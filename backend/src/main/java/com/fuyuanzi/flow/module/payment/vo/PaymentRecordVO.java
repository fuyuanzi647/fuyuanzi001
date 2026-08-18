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

    /** 订单下所有产品名称（逗号分隔） */
    private String productName;

    private BigDecimal amount;

    private BigDecimal quantity;

    private LocalDate payDate;

    private LocalDate officeDate;

    private String payMethod;

    private String remark;

    private LocalDateTime createTime;
}
