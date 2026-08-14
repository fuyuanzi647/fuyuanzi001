package com.fuyuanzi.flow.module.bid.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("bid")
                .name("竞标管理")
                .description("管理竞标全生命周期：目录、申请、公示、奖惩、考核、档案、合同")
                .features(List.of("竞标目录", "申请竞标", "竞标公示", "竞标奖惩", "竞标考核", "竞标档案", "竞标合同", "后台管理"))
                .build();
        return Result.ok(info);
    }
}
