package com.fuyuanzi.flow.module.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("shipment_order")
public class ShipmentOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long businessId;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal paidQuantity;

    private LocalDate shipDate;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
