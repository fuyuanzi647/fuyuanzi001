package com.fuyuanzi.flow.module.payment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.common.ModuleInfo;
import com.fuyuanzi.flow.common.PageResult;
import com.fuyuanzi.flow.common.Result;
import com.fuyuanzi.flow.module.payment.dto.PaymentSaveDTO;
import com.fuyuanzi.flow.module.payment.dto.ShipmentSaveDTO;
import com.fuyuanzi.flow.module.payment.service.ShipmentService;
import com.fuyuanzi.flow.module.payment.vo.PaymentRecordVO;
import com.fuyuanzi.flow.module.payment.vo.ShipmentOrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ShipmentService shipmentService;

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

    @GetMapping("/shipment/page")
    public Result<PageResult<ShipmentOrderVO>> page(@RequestParam(defaultValue = "1") long current,
                                                    @RequestParam(defaultValue = "10") long size,
                                                    @RequestParam(required = false) String orderNo,
                                                    @RequestParam(required = false) Long businessId,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shipDateStart,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shipDateEnd,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "createDesc") String sort) {
        IPage<ShipmentOrderVO> page = shipmentService.page(
                current, size, orderNo, businessId, shipDateStart, shipDateEnd, status, sort);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/shipment/{id}")
    public Result<ShipmentOrderVO> detail(@PathVariable Long id) {
        return Result.ok(shipmentService.getDetail(id));
    }

    @PostMapping("/shipment")
    public Result<Void> create(@Valid @RequestBody ShipmentSaveDTO dto) {
        shipmentService.create(dto);
        return Result.ok();
    }

    @PutMapping("/shipment")
    public Result<Void> update(@Valid @RequestBody ShipmentSaveDTO dto) {
        shipmentService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/shipment/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        shipmentService.delete(id);
        return Result.ok();
    }

    @PostMapping("/shipment/payment")
    public Result<Void> addPayment(@Valid @RequestBody PaymentSaveDTO dto) {
        shipmentService.addPayment(dto);
        return Result.ok();
    }

    @GetMapping("/shipment/{orderId}/payment")
    public Result<List<PaymentRecordVO>> listPayments(@PathVariable Long orderId) {
        return Result.ok(shipmentService.listPayments(orderId));
    }

    @GetMapping("/record/page")
    public Result<PageResult<PaymentRecordVO>> recordPage(@RequestParam(defaultValue = "1") long current,
                                                          @RequestParam(defaultValue = "10") long size,
                                                          @RequestParam(required = false) String orderNo,
                                                          @RequestParam(required = false) Long businessId,
                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payDateStart,
                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payDateEnd,
                                                          @RequestParam(required = false) String payMethod,
                                                          @RequestParam(defaultValue = "payDateDesc") String sort) {
        IPage<PaymentRecordVO> page = shipmentService.recordPage(
                current, size, orderNo, businessId, payDateStart, payDateEnd, payMethod, sort);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/options")
    public Result<Map<String, List<?>>> options() {
        return Result.ok(shipmentService.options());
    }
}
