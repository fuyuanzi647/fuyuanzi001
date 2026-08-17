package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentRecordVO {

    private Long id;

    private Long orderId;

    private String orderNo;

    private Long businessId;

    private String businessName;

    private BigDecimal amount;

    private BigDecimal quantity;

    private LocalDate payDate;

    private String remark;

    private LocalDateTime createTime;
}
