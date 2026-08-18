package com.fuyuanzi.flow.module.analysis.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("analysis")
                .name("数据分析")
                .description("对发货回款应收款与流向数据进行多维分析")
                .features(List.of("发货回款应收款数据分析", "流向分析"))
                .build();
        return Result.ok(info);
    }
}
