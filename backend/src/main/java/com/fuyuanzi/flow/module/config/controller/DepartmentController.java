package com.fuyuanzi.flow.module.config.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.common.PageResult;
import com.fuyuanzi.flow.common.Result;
import com.fuyuanzi.flow.module.config.dto.DepartmentSaveDTO;
import com.fuyuanzi.flow.module.config.service.DepartmentService;
import com.fuyuanzi.flow.module.config.vo.DepartmentVO;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/page")
    public Result<PageResult<DepartmentVO>> page(@RequestParam(defaultValue = "1") long current,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String type,
                                                 @RequestParam(required = false) Integer status) {
        IPage<DepartmentVO> page = departmentService.page(current, size, name, type, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<DepartmentVO>> listAll() {
        return Result.ok(departmentService.listAll());
    }

    @GetMapping("/{id}")
    public Result<DepartmentVO> detail(@PathVariable Long id) {
        return Result.ok(departmentService.getDetail(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody DepartmentSaveDTO dto) {
        departmentService.create(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody DepartmentSaveDTO dto) {
        departmentService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.ok();
    }

    @GetMapping("/export")
    public Result<Map<String, String>> export() {
        String csv = departmentService.exportCsv();
        return Result.ok(Map.of("fileName", "department.csv", "content", csv));
    }

    @PostMapping("/import")
    public Result<Void> importCsv(@RequestBody Map<String, String> body) {
        departmentService.importCsv(body.get("content"));
        return Result.ok();
    }
}
