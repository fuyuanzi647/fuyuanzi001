package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ShipmentItemVO {

    private Long id;

    private Long orderId;

    private Long manufacturerId;

    private String manufacturerName;

    private Long productId;

    private String productName;

    private BigDecimal quantity;

    private String batchNo;

    private BigDecimal amount;
}
