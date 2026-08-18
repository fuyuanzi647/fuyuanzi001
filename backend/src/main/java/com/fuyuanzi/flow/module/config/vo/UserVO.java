package com.fuyuanzi.flow.module.config.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    /** 状态:0离职1在职 */
    private Integer status;

    private LocalDate hireDate;

    private Long positionId;

    /** 当前岗位名称 */
    private String positionName;

    private Long departmentId;

    /** 当前部门名称 */
    private String departmentName;

    private String employeeType;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
