package com.fuyuanzi.flow.module.config.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentVO {

    private Long id;

    private String name;

    private Long parentId;

    /** 上级部门名称 */
    private String parentName;

    private String type;

    private Integer sort;

    /** 状态:0停用1启用 */
    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
