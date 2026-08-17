package com.fuyuanzi.flow.module.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.module.payment.dto.PaymentSaveDTO;
import com.fuyuanzi.flow.module.payment.dto.ShipmentSaveDTO;
import com.fuyuanzi.flow.module.payment.vo.PaymentRecordVO;
import com.fuyuanzi.flow.module.payment.vo.ShipmentOrderVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ShipmentService {

    IPage<ShipmentOrderVO> page(long current, long size, String orderNo, Long businessId,
                                LocalDate shipDateStart, LocalDate shipDateEnd,
                                Integer status, String sort);

    ShipmentOrderVO getDetail(Long id);

    void create(ShipmentSaveDTO dto);

    void update(ShipmentSaveDTO dto);

    void delete(Long id);

    void addPayment(PaymentSaveDTO dto);

    List<PaymentRecordVO> listPayments(Long orderId);

    Map<String, List<?>> options();
}
