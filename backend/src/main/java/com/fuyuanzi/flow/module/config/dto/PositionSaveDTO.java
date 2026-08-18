package com.fuyuanzi.flow.module.config.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PositionSaveDTO {

    private Long id;

    @NotBlank(message = "岗位名称不能为空")
    private String name;

    private String code;

    private Integer sort;

    private Integer status;

    private String remark;
}
