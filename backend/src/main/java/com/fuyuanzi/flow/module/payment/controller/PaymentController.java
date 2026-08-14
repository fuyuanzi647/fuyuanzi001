package com.fuyuanzi.flow.module.payment.controller;

import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @GetMapping("/summary")
    public Result<ModuleInfo> summary() {
        ModuleInfo info = ModuleInfo.builder()
                .code("payment")
                .name("货款管理")
                .description("管理发货订单、回款记录、应收与应付账款")
                .features(List.of("发货订单", "回款记录", "应收管理", "应付管理"))
                .build();
        return Result.ok(info);
    }
}
