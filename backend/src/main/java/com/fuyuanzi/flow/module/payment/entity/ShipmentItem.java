package com.fuyuanzi.flow.module.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shipment_item")
public class ShipmentItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long manufacturerId;

    private Long productId;

    private BigDecimal quantity;

    private String batchNo;

    private BigDecimal amount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
