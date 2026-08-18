package com.fuyuanzi.flow.module.config.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("config")
                .name("运营配置")
                .description("配置人员、权限、性质分类与部门区域等运营基础")
                .features(List.of("人员配置", "权限分配", "性质分类", "部门区域"))
                .build();
        return Result.ok(info);
    }
}
