package com.fuyuanzi.flow.module.assessment.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("assessment")
                .name("考核管理")
                .description("负责竞标考核与批量考核发布、结果管理")
                .features(List.of("竞标考核", "批量考核"))
                .build();
        return Result.ok(info);
    }
}
