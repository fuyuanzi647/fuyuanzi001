package com.fuyuanzi.flow.module.config.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PositionVO {

    private Long id;

    private String name;

    private String code;

    private Integer sort;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
