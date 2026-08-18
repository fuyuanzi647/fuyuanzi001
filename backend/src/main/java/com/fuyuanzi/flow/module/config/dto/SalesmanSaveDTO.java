package com.fuyuanzi.flow.module.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesmanSaveDTO {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    private Long positionId;

    private Long departmentId;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
