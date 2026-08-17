package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShipmentOrderVO {

    private Long id;

    private String orderNo;

    private Long businessId;

    private String businessName;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal paidQuantity;

    private LocalDate shipDate;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 回款状态:0未回款 1部分回款 2已回款 */
    private Integer status;

    /** 未回款天数（距记账日期） */
    private Long overdueDays;

    private List<ShipmentItemVO> items;
}
