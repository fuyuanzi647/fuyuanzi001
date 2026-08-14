package com.fuyuanzi.flow.module.collection.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("collection")
                .name("采集中心")
                .description("自动实时爬取流向数据，管理流向上报任务与识别策略")
                .features(List.of("数据采集", "流向计划", "识别策略", "数据工具"))
                .build();
        return Result.ok(info);
    }
}
