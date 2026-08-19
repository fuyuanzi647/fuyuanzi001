package com.fuyuanzi.flow.module.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.module.payment.dto.PaymentSaveDTO;
import com.fuyuanzi.flow.module.payment.dto.ShipmentSaveDTO;
import com.fuyuanzi.flow.module.payment.vo.PaymentRecordVO;
import com.fuyuanzi.flow.module.payment.vo.ReceivableOverviewVO;
import com.fuyuanzi.flow.module.payment.vo.ReceivablePageVO;
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

    void updateRemark(Long id, String remark);

    void delete(Long id);

    void addPayment(PaymentSaveDTO dto);

    List<PaymentRecordVO> listPayments(Long orderId);

    IPage<PaymentRecordVO> recordPage(long current, long size, String orderNo, Long businessId,
                                      LocalDate payDateStart, LocalDate payDateEnd,
                                      String payMethod, String sort);

    Map<String, List<?>> options();

    ReceivablePageVO receivablePage(long current, long size, String orderNo, Long businessId,
                                    String region, LocalDate shipDateStart, LocalDate shipDateEnd,
                                    String sort);

    List<ReceivableOverviewVO> receivableOverview(String orderNo, Long businessId, String region,
                                                  LocalDate shipDateStart, LocalDate shipDateEnd);
}
