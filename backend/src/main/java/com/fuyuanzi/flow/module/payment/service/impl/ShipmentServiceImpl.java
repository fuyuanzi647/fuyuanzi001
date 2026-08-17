package com.fuyuanzi.flow.module.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuyuanzi.flow.common.BusinessException;
import com.fuyuanzi.flow.module.payment.dto.PaymentSaveDTO;
import com.fuyuanzi.flow.module.payment.dto.ShipmentSaveDTO;
import com.fuyuanzi.flow.module.payment.entity.PaymentRecord;
import com.fuyuanzi.flow.module.payment.entity.ShipmentItem;
import com.fuyuanzi.flow.module.payment.entity.ShipmentOrder;
import com.fuyuanzi.flow.module.payment.mapper.BaseOptionMapper;
import com.fuyuanzi.flow.module.payment.mapper.PaymentRecordMapper;
import com.fuyuanzi.flow.module.payment.mapper.ShipmentItemMapper;
import com.fuyuanzi.flow.module.payment.mapper.ShipmentOrderMapper;
import com.fuyuanzi.flow.module.payment.service.ShipmentService;
import com.fuyuanzi.flow.module.payment.vo.PaymentRecordVO;
import com.fuyuanzi.flow.module.payment.vo.ShipmentItemVO;
import com.fuyuanzi.flow.module.payment.vo.ShipmentOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentOrderMapper shipmentOrderMapper;
    private final ShipmentItemMapper shipmentItemMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final BaseOptionMapper baseOptionMapper;

    @Override
    public IPage<ShipmentOrderVO> page(long current, long size, String orderNo, Long businessId,
                                       LocalDate shipDateStart, LocalDate shipDateEnd,
                                       Integer status, String sort) {
        Page<ShipmentOrderVO> page = new Page<>(current, size);
        IPage<ShipmentOrderVO> result = shipmentOrderMapper.selectOrderPage(
                page, orderNo, businessId, shipDateStart, shipDateEnd, status, sort);
        List<ShipmentOrderVO> records = result.getRecords();
        if (records != null && !records.isEmpty()) {
            List<Long> orderIds = records.stream().map(ShipmentOrderVO::getId).collect(Collectors.toList());
            List<ShipmentItemVO> items = shipmentOrderMapper.selectItemsByOrderIds(orderIds);
            Map<Long, List<ShipmentItemVO>> itemMap = items.stream()
                    .collect(Collectors.groupingBy(ShipmentItemVO::getOrderId));
            for (ShipmentOrderVO vo : records) {
                vo.setItems(itemMap.getOrDefault(vo.getId(), new ArrayList<>()));
            }
        }
        return result;
    }

    @Override
    public ShipmentOrderVO getDetail(Long id) {
        ShipmentOrder order = shipmentOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("发货订单不存在");
        }
        ShipmentOrderVO vo = shipmentOrderMapper.selectOrderById(id);
        if (vo == null) {
            throw new BusinessException("发货订单不存在");
        }
        List<ShipmentItemVO> items = shipmentOrderMapper.selectItemsByOrderIds(List.of(id));
        vo.setItems(items);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ShipmentSaveDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("发货明细不能为空");
        }
        ShipmentOrder order = new ShipmentOrder();
        order.setOrderNo(dto.getOrderNo());
        order.setBusinessId(dto.getBusinessId());
        order.setShipDate(dto.getShipDate());
        order.setRemark(dto.getRemark());
        order.setTotalAmount(sumAmount(dto));
        order.setPaidAmount(BigDecimal.ZERO);
        order.setPaidQuantity(BigDecimal.ZERO);
        shipmentOrderMapper.insert(order);
        saveItems(order.getId(), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShipmentSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("缺少订单ID");
        }
        ShipmentOrder order = shipmentOrderMapper.selectById(dto.getId());
        if (order == null) {
            throw new BusinessException("发货订单不存在");
        }
        order.setOrderNo(dto.getOrderNo());
        order.setBusinessId(dto.getBusinessId());
        order.setShipDate(dto.getShipDate());
        order.setRemark(dto.getRemark());
        order.setTotalAmount(sumAmount(dto));
        shipmentOrderMapper.updateById(order);

        // 删除旧明细重新插入
        shipmentItemMapper.delete(new LambdaQueryWrapper<ShipmentItem>().eq(ShipmentItem::getOrderId, dto.getId()));
        saveItems(dto.getId(), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ShipmentOrder order = shipmentOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("发货订单不存在");
        }
        shipmentOrderMapper.deleteById(id);
        shipmentItemMapper.delete(new LambdaQueryWrapper<ShipmentItem>().eq(ShipmentItem::getOrderId, id));
        paymentRecordMapper.delete(new LambdaQueryWrapper<PaymentRecord>().eq(PaymentRecord::getOrderId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPayment(PaymentSaveDTO dto) {
        ShipmentOrder order = shipmentOrderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("发货订单不存在");
        }
        BigDecimal remain = order.getTotalAmount().subtract(order.getPaidAmount());
        if (remain.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("该订单已全部回款");
        }
        if (dto.getAmount().compareTo(remain) > 0) {
            throw new BusinessException("回款金额不能大于未回款金额 " + remain);
        }

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(dto.getOrderId());
        record.setBusinessId(order.getBusinessId());
        record.setAmount(dto.getAmount());
        record.setQuantity(dto.getQuantity() == null ? BigDecimal.ZERO : dto.getQuantity());
        record.setPayDate(dto.getPayDate());
        record.setPayMethod(dto.getPayMethod());
        record.setRemark(dto.getRemark());
        paymentRecordMapper.insert(record);

        order.setPaidAmount(order.getPaidAmount().add(dto.getAmount()));
        order.setPaidQuantity(order.getPaidQuantity().add(record.getQuantity()));
        shipmentOrderMapper.updateById(order);
    }

    @Override
    public List<PaymentRecordVO> listPayments(Long orderId) {
        return paymentRecordMapper.selectRecordsByOrderId(orderId);
    }

    @Override
    public Map<String, List<?>> options() {
        Map<String, List<?>> map = new HashMap<>();
        map.put("businesses", baseOptionMapper.selectBusinessOptions());
        map.put("manufacturers", baseOptionMapper.selectManufacturerOptions());
        map.put("products", baseOptionMapper.selectProductOptions());
        return map;
    }

    private void saveItems(Long orderId, ShipmentSaveDTO dto) {
        for (ShipmentSaveDTO.ShipmentItemDTO item : dto.getItems()) {
            ShipmentItem entity = new ShipmentItem();
            entity.setOrderId(orderId);
            entity.setManufacturerId(item.getManufacturerId());
            entity.setProductId(item.getProductId());
            entity.setQuantity(item.getQuantity());
            entity.setBatchNo(item.getBatchNo());
            entity.setAmount(item.getAmount());
            shipmentItemMapper.insert(entity);
        }
    }

    private BigDecimal sumAmount(ShipmentSaveDTO dto) {
        return dto.getItems().stream()
                .map(ShipmentSaveDTO.ShipmentItemDTO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
