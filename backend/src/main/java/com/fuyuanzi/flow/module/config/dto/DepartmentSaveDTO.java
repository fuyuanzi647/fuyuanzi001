package com.fuyuanzi.flow.module.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentSaveDTO {

    private Long id;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    @NotNull(message = "上级部门不能为空")
    private Long parentId;

    @NotBlank(message = "部门类型不能为空")
    private String type;

    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
