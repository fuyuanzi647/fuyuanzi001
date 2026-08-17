package com.fuyuanzi.flow.module.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentSaveDTO {

    @NotNull(message = "发货订单不能为空")
    private Long orderId;

    @NotNull(message = "回款金额不能为空")
    @DecimalMin(value = "0.01", message = "回款金额必须大于0")
    private BigDecimal amount;

    private BigDecimal quantity;

    @NotNull(message = "回款日期不能为空")
    private LocalDate payDate;

    private String remark;
}
