package com.fuyuanzi.flow.module.desktop.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/desktop")
public class DesktopController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("desktop")
                .name("系统桌面")
                .description("工作台入口，集中展示审批、备忘与消息提醒")
                .features(List.of("审批流程", "备忘录", "消息提醒"))
                .build();
        return Result.ok(info);
    }
}
