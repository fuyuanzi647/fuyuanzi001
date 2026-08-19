package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceivableTotalVO {

    /** 发货数量合计 */
    private BigDecimal shipQuantity = BigDecimal.ZERO;

    /** 发货金额合计 */
    private BigDecimal shipAmount = BigDecimal.ZERO;

    /** 订单金额合计 */
    private BigDecimal orderAmount = BigDecimal.ZERO;

    /** 回款金额合计 */
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /** 期内还款金额合计 */
    private BigDecimal periodPayAmount = BigDecimal.ZERO;

    /** 应收数量合计 */
    private BigDecimal receivableQuantity = BigDecimal.ZERO;

    /** 应收总额合计 */
    private BigDecimal receivableAmount = BigDecimal.ZERO;
}
