package com.fuyuanzi.flow.module.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuyuanzi.flow.common.BusinessException;
import com.fuyuanzi.flow.module.config.dto.DepartmentSaveDTO;
import com.fuyuanzi.flow.module.config.entity.ConfigDepartment;
import com.fuyuanzi.flow.module.config.mapper.ConfigDepartmentMapper;
import com.fuyuanzi.flow.module.config.service.DepartmentService;
import com.fuyuanzi.flow.module.config.vo.DepartmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final ConfigDepartmentMapper departmentMapper;

    @Override
    public IPage<DepartmentVO> page(long current, long size, String name, String type, Integer status) {
        Page<ConfigDepartment> page = new Page<>(current, size);
        LambdaQueryWrapper<ConfigDepartment> wrapper = new LambdaQueryWrapper<ConfigDepartment>()
                .like(name != null && !name.isBlank(), ConfigDepartment::getName, name)
                .eq(type != null && !type.isBlank(), ConfigDepartment::getType, type)
                .eq(status != null, ConfigDepartment::getStatus, status)
                .orderByAsc(ConfigDepartment::getParentId)
                .orderByAsc(ConfigDepartment::getSort)
                .orderByAsc(ConfigDepartment::getId);
        IPage<ConfigDepartment> result = departmentMapper.selectPage(page, wrapper);
        Map<Long, String> nameMap = loadNameMap();
        Page<DepartmentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(d -> toVO(d, nameMap))
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<DepartmentVO> listAll() {
        List<ConfigDepartment> all = departmentMapper.selectList(
                new LambdaQueryWrapper<ConfigDepartment>()
                        .orderByAsc(ConfigDepartment::getParentId)
                        .orderByAsc(ConfigDepartment::getSort)
                        .orderByAsc(ConfigDepartment::getId));
        Map<Long, String> nameMap = loadNameMap();
        return all.stream().map(d -> toVO(d, nameMap)).collect(Collectors.toList());
    }

    @Override
    public DepartmentVO getDetail(Long id) {
        ConfigDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        return toVO(department, loadNameMap());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DepartmentSaveDTO dto) {
        checkParent(dto.getParentId());
        ConfigDepartment department = new ConfigDepartment();
        copyToEntity(dto, department);
        departmentMapper.insert(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DepartmentSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("缺少部门ID");
        }
        ConfigDepartment department = departmentMapper.selectById(dto.getId());
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        if (Objects.equals(dto.getId(), dto.getParentId())) {
            throw new BusinessException("上级部门不能选择自身");
        }
        checkParent(dto.getParentId());
        copyToEntity(dto, department);
        departmentMapper.updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ConfigDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        Long childCount = departmentMapper.selectCount(
                new LambdaQueryWrapper<ConfigDepartment>().eq(ConfigDepartment::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("存在下级部门，请先删除下级部门");
        }
        departmentMapper.deleteById(id);
    }

    @Override
    public String exportCsv() {
        List<ConfigDepartment> all = departmentMapper.selectList(
                new LambdaQueryWrapper<ConfigDepartment>()
                        .orderByAsc(ConfigDepartment::getParentId)
                        .orderByAsc(ConfigDepartment::getSort)
                        .orderByAsc(ConfigDepartment::getId));
        Map<Long, String> nameMap = loadNameMap();
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append("部门名称,部门类型,状态,同级排序,上级部门,备注\n");
        for (ConfigDepartment d : all) {
            String statusText = Objects.equals(d.getStatus(), 1) ? "启用" : "停用";
            String parentName = d.getParentId() != null && d.getParentId() != 0
                    ? nameMap.getOrDefault(d.getParentId(), "")
                    : "";
            sb.append(escapeCsv(d.getName())).append(',')
                    .append(escapeCsv(d.getType())).append(',')
                    .append(statusText).append(',')
                    .append(d.getSort() == null ? 0 : d.getSort()).append(',')
                    .append(escapeCsv(parentName)).append(',')
                    .append(escapeCsv(d.getRemark())).append('\n');
        }
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new BusinessException("导入内容为空");
        }
        String content = csv;
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }
        String[] lines = content.split("\r?\n");
        if (lines.length <= 1) {
            throw new BusinessException("导入文件无数据");
        }
        Map<String, Long> nameToId = new LinkedHashMap<>(loadNameToId());
        List<ConfigDepartment> toInsert = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            List<String> cols = parseCsvLine(line);
            if (cols.size() < 1 || cols.get(0).isBlank()) {
                throw new BusinessException("第 " + (i + 1) + " 行数据格式错误");
            }
            ConfigDepartment d = new ConfigDepartment();
            d.setName(cols.get(0).trim());
            d.setType(cols.size() > 1 && !cols.get(1).isBlank() ? cols.get(1).trim() : "区域");
            String statusText = cols.size() > 2 ? cols.get(2).trim() : "启用";
            d.setStatus("停用".equals(statusText) ? 0 : 1);
            d.setSort(cols.size() > 3 && !cols.get(3).isBlank() ? Integer.parseInt(cols.get(3).trim()) : 0);
            String parentName = cols.size() > 4 ? cols.get(4).trim() : "";
            d.setParentId(parentName.isBlank() ? 0L : nameToId.getOrDefault(parentName, 0L));
            d.setRemark(cols.size() > 5 ? cols.get(5).trim() : null);
            toInsert.add(d);
        }
        for (ConfigDepartment d : toInsert) {
            departmentMapper.insert(d);
            if (d.getId() != null) {
                nameToId.putIfAbsent(d.getName(), d.getId());
            }
        }
    }

    private void checkParent(Long parentId) {
        if (parentId == null || parentId == 0) {
            return;
        }
        if (departmentMapper.selectById(parentId) == null) {
            throw new BusinessException("上级部门不存在");
        }
    }

    private void copyToEntity(DepartmentSaveDTO dto, ConfigDepartment department) {
        department.setName(dto.getName());
        department.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        department.setType(dto.getType());
        department.setSort(dto.getSort() == null ? 0 : dto.getSort());
        department.setStatus(dto.getStatus());
        department.setRemark(dto.getRemark());
    }

    private DepartmentVO toVO(ConfigDepartment d, Map<Long, String> nameMap) {
        DepartmentVO vo = new DepartmentVO();
        vo.setId(d.getId());
        vo.setName(d.getName());
        vo.setParentId(d.getParentId());
        vo.setParentName(d.getParentId() != null && d.getParentId() != 0
                ? nameMap.getOrDefault(d.getParentId(), "")
                : "");
        vo.setType(d.getType());
        vo.setSort(d.getSort());
        vo.setStatus(d.getStatus());
        vo.setRemark(d.getRemark());
        vo.setCreateTime(d.getCreateTime());
        vo.setUpdateTime(d.getUpdateTime());
        return vo;
    }

    private Map<Long, String> loadNameMap() {
        List<ConfigDepartment> all = departmentMapper.selectList(null);
        return all.stream().filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ConfigDepartment::getId, ConfigDepartment::getName, (a, b) -> a));
    }

    private Map<String, Long> loadNameToId() {
        List<ConfigDepartment> all = departmentMapper.selectList(null);
        Map<String, Long> map = new LinkedHashMap<>();
        for (ConfigDepartment d : all) {
            if (d.getName() != null) {
                map.putIfAbsent(d.getName(), d.getId());
            }
        }
        return map;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    result.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        result.add(sb.toString());
        return result;
    }
}
