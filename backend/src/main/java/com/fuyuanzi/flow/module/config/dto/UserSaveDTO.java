package com.fuyuanzi.flow.module.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSaveDTO {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "登录账户不能为空")
    private String username;

    private String password;

    private String phone;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private LocalDate hireDate;

    private Long positionId;

    private Long departmentId;

    private String employeeType;

    private String remark;
}
