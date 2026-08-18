package com.fuyuanzi.flow.module.report.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("report")
                .name("报表管理")
                .description("生成终端表、空间表、空间工资表等业务报表")
                .features(List.of("终端表", "空间表", "空间工资表"))
                .build();
        return Result.ok(info);
    }
}
