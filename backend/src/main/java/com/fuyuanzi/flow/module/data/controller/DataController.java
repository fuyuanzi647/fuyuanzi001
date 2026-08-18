package com.fuyuanzi.flow.module.data.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("data")
                .name("数据中心")
                .description("集中管理流向数据、核算数据、库存与退换货")
                .features(List.of("流向数据", "核算数据", "库存管理", "退换货管理"))
                .build();
        return Result.ok(info);
    }
}
