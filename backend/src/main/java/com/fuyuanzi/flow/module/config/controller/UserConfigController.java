package com.fuyuanzi.flow.module.config.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.common.PageResult;
import com.fuyuanzi.flow.common.Result;
import com.fuyuanzi.flow.module.config.dto.PositionSaveDTO;
import com.fuyuanzi.flow.module.config.dto.SalesmanSaveDTO;
import com.fuyuanzi.flow.module.config.dto.UserSaveDTO;
import com.fuyuanzi.flow.module.config.service.UserConfigService;
import com.fuyuanzi.flow.module.config.vo.PositionVO;
import com.fuyuanzi.flow.module.config.vo.SalesmanVO;
import com.fuyuanzi.flow.module.config.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class UserConfigController {

    private final UserConfigService userConfigService;

    @GetMapping("/user/page")
    public Result<PageResult<UserVO>> userPage(@RequestParam(defaultValue = "1") long current,
                                               @RequestParam(defaultValue = "10") long size,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) String username,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Long departmentId,
                                               @RequestParam(required = false) Long positionId) {
        IPage<UserVO> page = userConfigService.userPage(current, size, name, username, status, departmentId, positionId);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/user/{id}")
    public Result<UserVO> userDetail(@PathVariable Long id) {
        return Result.ok(userConfigService.userDetail(id));
    }

    @PostMapping("/user")
    public Result<Void> userCreate(@Valid @RequestBody UserSaveDTO dto) {
        userConfigService.userCreate(dto);
        return Result.ok();
    }

    @PutMapping("/user")
    public Result<Void> userUpdate(@Valid @RequestBody UserSaveDTO dto) {
        userConfigService.userUpdate(dto);
        return Result.ok();
    }

    @DeleteMapping("/user/{id}")
    public Result<Void> userDelete(@PathVariable Long id) {
        userConfigService.userDelete(id);
        return Result.ok();
    }

    @PutMapping("/user/{id}/transfer")
    public Result<Void> userTransfer(@PathVariable Long id,
                                     @RequestParam Long positionId,
                                     @RequestParam Long departmentId) {
        userConfigService.userTransfer(id, positionId, departmentId);
        return Result.ok();
    }

    @GetMapping("/user/export")
    public Result<Map<String, String>> userExport() {
        return Result.ok(Map.of("fileName", "user.csv", "content", userConfigService.userExportCsv()));
    }

    @PostMapping("/user/import")
    public Result<Void> userImport(@RequestBody Map<String, String> body) {
        userConfigService.userImportCsv(body.get("content"));
        return Result.ok();
    }

    @GetMapping("/position/list")
    public Result<List<PositionVO>> positionList() {
        return Result.ok(userConfigService.positionList());
    }

    @GetMapping("/position/page")
    public Result<PageResult<PositionVO>> positionPage(@RequestParam(defaultValue = "1") long current,
                                                       @RequestParam(defaultValue = "10") long size,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer status) {
        IPage<PositionVO> page = userConfigService.positionPage(current, size, name, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/position")
    public Result<Void> positionCreate(@Valid @RequestBody PositionSaveDTO dto) {
        userConfigService.positionCreate(dto);
        return Result.ok();
    }

    @PutMapping("/position")
    public Result<Void> positionUpdate(@Valid @RequestBody PositionSaveDTO dto) {
        userConfigService.positionUpdate(dto);
        return Result.ok();
    }

    @DeleteMapping("/position/{id}")
    public Result<Void> positionDelete(@PathVariable Long id) {
        userConfigService.positionDelete(id);
        return Result.ok();
    }

    @GetMapping("/position/export")
    public Result<Map<String, String>> positionExport() {
        return Result.ok(Map.of("fileName", "position.csv", "content", userConfigService.positionExportCsv()));
    }

    @PostMapping("/position/import")
    public Result<Void> positionImport(@RequestBody Map<String, String> body) {
        userConfigService.positionImportCsv(body.get("content"));
        return Result.ok();
    }

    @GetMapping("/salesman/page")
    public Result<PageResult<SalesmanVO>> salesmanPage(@RequestParam(defaultValue = "1") long current,
                                                       @RequestParam(defaultValue = "10") long size,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(required = false) Long departmentId) {
        IPage<SalesmanVO> page = userConfigService.salesmanPage(current, size, name, status, departmentId);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/salesman")
    public Result<Void> salesmanCreate(@Valid @RequestBody SalesmanSaveDTO dto) {
        userConfigService.salesmanCreate(dto);
        return Result.ok();
    }

    @PutMapping("/salesman")
    public Result<Void> salesmanUpdate(@Valid @RequestBody SalesmanSaveDTO dto) {
        userConfigService.salesmanUpdate(dto);
        return Result.ok();
    }

    @DeleteMapping("/salesman/{id}")
    public Result<Void> salesmanDelete(@PathVariable Long id) {
        userConfigService.salesmanDelete(id);
        return Result.ok();
    }

    @GetMapping("/salesman/export")
    public Result<Map<String, String>> salesmanExport() {
        return Result.ok(Map.of("fileName", "salesman.csv", "content", userConfigService.salesmanExportCsv()));
    }

    @PostMapping("/salesman/import")
    public Result<Void> salesmanImport(@RequestBody Map<String, String> body) {
        userConfigService.salesmanImportCsv(body.get("content"));
        return Result.ok();
    }
}
