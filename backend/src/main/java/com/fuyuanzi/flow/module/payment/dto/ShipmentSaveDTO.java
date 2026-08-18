package com.fuyuanzi.flow.module.payment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ShipmentSaveDTO {

    private Long id;

    @NotEmpty(message = "发货单号不能为空")
    private String orderNo;

    @NotNull(message = "商业公司不能为空")
    private Long businessId;

    private LocalDate shipDate;

    private String remark;

    @Valid
    @NotEmpty(message = "发货明细不能为空")
    private List<ShipmentItemDTO> items;

    @Data
    public static class ShipmentItemDTO {

        private Long id;

        @NotNull(message = "出库厂家不能为空")
        private Long manufacturerId;

        @NotNull(message = "产品不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private BigDecimal quantity;

        private String batchNo;

        @NotNull(message = "金额不能为空")
        private BigDecimal amount;
    }
}
