package com.fuyuanzi.flow.module.base.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/base")
public class BaseController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("base")
                .name("基础信息")
                .description("维护终端、商业、厂家、产品等基础档案")
                .features(List.of("终端信息", "商业信息", "厂家信息", "产品信息"))
                .build();
        return Result.ok(info);
    }
}
