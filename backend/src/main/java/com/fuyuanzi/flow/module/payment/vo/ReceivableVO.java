package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceivableVO {

    private Long orderId;

    private String orderNo;

    /** 区域（来自商业公司） */
    private String region;

    private Long businessId;

    private String businessName;

    /** 产品名称（合并展示） */
    private String productName;

    /** 发货数量（最小售卖单位） */
    private BigDecimal shipQuantity;

    /** 发货金额（元） */
    private BigDecimal shipAmount;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 回款金额 */
    private BigDecimal paidAmount;

    /** 期内还款金额（60 天内回款） */
    private BigDecimal periodPayAmount;

    /** 应收数量 = 发货数量 - 回款数量 */
    private BigDecimal receivableQuantity;

    /** 应收总额 = 发货总额 - 回款总额 */
    private BigDecimal receivableAmount;

    /** 应收款天数 = 系统当天 - 记账日期 + 1 */
    private Long receivableDays;

    private LocalDate shipDate;
}
