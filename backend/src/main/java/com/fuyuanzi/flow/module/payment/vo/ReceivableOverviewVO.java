package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceivableOverviewVO {

    private String region;

    private Long businessId;

    private String businessName;

    /** 订单数量 */
    private Long orderCount;

    /** 发货数量 */
    private BigDecimal shipQuantity;

    /** 发货金额 */
    private BigDecimal shipAmount;

    /** 回款金额 */
    private BigDecimal paidAmount;

    /** 期内还款金额（60 天内回款） */
    private BigDecimal periodPayAmount;

    /** 应收总额 = 发货金额 - 回款金额 */
    private BigDecimal receivableAmount;

    /** 应收数量 = 发货数量 - 回款数量 */
    private BigDecimal receivableQuantity;
}
