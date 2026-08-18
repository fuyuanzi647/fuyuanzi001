package com.fuyuanzi.flow.module.config.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalesmanVO {

    private Long id;

    private String name;

    private String phone;

    private Long positionId;

    private String positionName;

    private Long departmentId;

    private String departmentName;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
