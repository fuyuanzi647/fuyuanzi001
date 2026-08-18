package com.fuyuanzi.flow.module.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuyuanzi.flow.common.BusinessException;
import com.fuyuanzi.flow.module.config.dto.PositionSaveDTO;
import com.fuyuanzi.flow.module.config.dto.SalesmanSaveDTO;
import com.fuyuanzi.flow.module.config.dto.UserSaveDTO;
import com.fuyuanzi.flow.module.config.entity.ConfigDepartment;
import com.fuyuanzi.flow.module.config.entity.ConfigPosition;
import com.fuyuanzi.flow.module.config.entity.ConfigSalesman;
import com.fuyuanzi.flow.module.config.entity.ConfigUser;
import com.fuyuanzi.flow.module.config.mapper.ConfigDepartmentMapper;
import com.fuyuanzi.flow.module.config.mapper.ConfigPositionMapper;
import com.fuyuanzi.flow.module.config.mapper.ConfigSalesmanMapper;
import com.fuyuanzi.flow.module.config.mapper.ConfigUserMapper;
import com.fuyuanzi.flow.module.config.service.UserConfigService;
import com.fuyuanzi.flow.module.config.vo.PositionVO;
import com.fuyuanzi.flow.module.config.vo.SalesmanVO;
import com.fuyuanzi.flow.module.config.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserConfigServiceImpl implements UserConfigService {

    private final ConfigUserMapper userMapper;
    private final ConfigPositionMapper positionMapper;
    private final ConfigSalesmanMapper salesmanMapper;
    private final ConfigDepartmentMapper departmentMapper;

    @Override
    public IPage<UserVO> userPage(long current, long size, String name, String username,
                                  Integer status, Long departmentId, Long positionId) {
        Page<ConfigUser> page = new Page<>(current, size);
        LambdaQueryWrapper<ConfigUser> wrapper = new LambdaQueryWrapper<ConfigUser>()
                .like(name != null && !name.isBlank(), ConfigUser::getRealName, name)
                .like(username != null && !username.isBlank(), ConfigUser::getUsername, username)
                .eq(status != null, ConfigUser::getStatus, status)
                .eq(departmentId != null, ConfigUser::getDepartmentId, departmentId)
                .eq(positionId != null, ConfigUser::getPositionId, positionId)
                .orderByDesc(ConfigUser::getCreateTime);
        IPage<ConfigUser> result = userMapper.selectPage(page, wrapper);
        Map<Long, String> positionMap = loadPositionMap();
        Map<Long, String> departmentMap = loadDepartmentMap();
        Page<UserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(u -> toUserVO(u, positionMap, departmentMap))
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public UserVO userDetail(Long id) {
        ConfigUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserVO(user, loadPositionMap(), loadDepartmentMap());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userCreate(UserSaveDTO dto) {
        checkUsernameUnique(dto.getUsername(), null);
        ConfigUser user = new ConfigUser();
        copyToUser(dto, user);
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userUpdate(UserSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("缺少用户ID");
        }
        ConfigUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        checkUsernameUnique(dto.getUsername(), dto.getId());
        copyToUser(dto, user);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userDelete(Long id) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userTransfer(Long id, Long positionId, Long departmentId) {
        ConfigUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPositionId(positionId);
        user.setDepartmentId(departmentId);
        userMapper.updateById(user);
    }

    @Override
    public String userExportCsv() {
        List<ConfigUser> all = userMapper.selectList(
                new LambdaQueryWrapper<ConfigUser>().orderByDesc(ConfigUser::getCreateTime));
        Map<Long, String> positionMap = loadPositionMap();
        Map<Long, String> departmentMap = loadDepartmentMap();
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append("姓名,登录账户,手机号,状态,入职日期,当前岗位,当前部门,员工属性,备注\n");
        for (ConfigUser u : all) {
            String statusText = Objects.equals(u.getStatus(), 1) ? "在职" : "离职";
            sb.append(escapeCsv(u.getRealName())).append(',')
                    .append(escapeCsv(u.getUsername())).append(',')
                    .append(escapeCsv(u.getPhone())).append(',')
                    .append(statusText).append(',')
                    .append(u.getHireDate() == null ? "" : u.getHireDate()).append(',')
                    .append(escapeCsv(u.getPositionId() == null ? "" : positionMap.getOrDefault(u.getPositionId(), ""))).append(',')
                    .append(escapeCsv(u.getDepartmentId() == null ? "" : departmentMap.getOrDefault(u.getDepartmentId(), ""))).append(',')
                    .append(escapeCsv(u.getEmployeeType())).append(',')
                    .append(escapeCsv(u.getRemark())).append('\n');
        }
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userImportCsv(String csv) {
        List<ConfigUser> toInsert = new ArrayList<>();
        Map<String, Long> positionMap = loadPositionIdByName();
        Map<String, Long> departmentMap = loadDepartmentIdByName();
        for (String[] cols : parseCsvRows(csv)) {
            if (cols.length < 1 || cols[0].isBlank()) {
                continue;
            }
            ConfigUser u = new ConfigUser();
            u.setRealName(cols[0].trim());
            u.setUsername(cols.length > 1 ? cols[1].trim() : "");
            u.setPhone(cols.length > 2 ? cols[2].trim() : null);
            String statusText = cols.length > 3 ? cols[3].trim() : "在职";
            u.setStatus("离职".equals(statusText) ? 0 : 1);
            u.setHireDate(cols.length > 4 && !cols[4].isBlank()
                    ? java.time.LocalDate.parse(cols[4].trim()) : null);
            String positionName = cols.length > 5 ? cols[5].trim() : "";
            u.setPositionId(positionName.isBlank() ? null : positionMap.getOrDefault(positionName, null));
            String departmentName = cols.length > 6 ? cols[6].trim() : "";
            u.setDepartmentId(departmentName.isBlank() ? null : departmentMap.getOrDefault(departmentName, null));
            u.setEmployeeType(cols.length > 7 ? cols[7].trim() : null);
            u.setRemark(cols.length > 8 ? cols[8].trim() : null);
            if (u.getUsername() != null && !u.getUsername().isBlank()) {
                checkUsernameUnique(u.getUsername(), null);
            }
            toInsert.add(u);
        }
        if (toInsert.isEmpty()) {
            throw new BusinessException("导入文件无有效数据");
        }
        for (ConfigUser u : toInsert) {
            userMapper.insert(u);
        }
    }

    @Override
    public List<PositionVO> positionList() {
        return positionMapper.selectList(
                        new LambdaQueryWrapper<ConfigPosition>().orderByAsc(ConfigPosition::getSort))
                .stream().map(this::toPositionVO).collect(Collectors.toList());
    }

    @Override
    public IPage<PositionVO> positionPage(long current, long size, String name, Integer status) {
        Page<ConfigPosition> page = new Page<>(current, size);
        LambdaQueryWrapper<ConfigPosition> wrapper = new LambdaQueryWrapper<ConfigPosition>()
                .like(name != null && !name.isBlank(), ConfigPosition::getName, name)
                .eq(status != null, ConfigPosition::getStatus, status)
                .orderByAsc(ConfigPosition::getSort);
        IPage<ConfigPosition> result = positionMapper.selectPage(page, wrapper);
        Page<PositionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toPositionVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void positionCreate(PositionSaveDTO dto) {
        ConfigPosition position = new ConfigPosition();
        copyToPosition(dto, position);
        positionMapper.insert(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void positionUpdate(PositionSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("缺少岗位ID");
        }
        ConfigPosition position = positionMapper.selectById(dto.getId());
        if (position == null) {
            throw new BusinessException("岗位不存在");
        }
        copyToPosition(dto, position);
        positionMapper.updateById(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void positionDelete(Long id) {
        if (positionMapper.selectById(id) == null) {
            throw new BusinessException("岗位不存在");
        }
        positionMapper.deleteById(id);
    }

    @Override
    public String positionExportCsv() {
        List<ConfigPosition> all = positionMapper.selectList(
                new LambdaQueryWrapper<ConfigPosition>().orderByAsc(ConfigPosition::getSort));
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append("岗位名称,岗位编码,状态,排序,备注\n");
        for (ConfigPosition p : all) {
            String statusText = Objects.equals(p.getStatus(), 1) ? "启用" : "停用";
            sb.append(escapeCsv(p.getName())).append(',')
                    .append(escapeCsv(p.getCode())).append(',')
                    .append(statusText).append(',')
                    .append(p.getSort() == null ? 0 : p.getSort()).append(',')
                    .append(escapeCsv(p.getRemark())).append('\n');
        }
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void positionImportCsv(String csv) {
        List<ConfigPosition> toInsert = new ArrayList<>();
        for (String[] cols : parseCsvRows(csv)) {
            if (cols.length < 1 || cols[0].isBlank()) {
                continue;
            }
            ConfigPosition p = new ConfigPosition();
            p.setName(cols[0].trim());
            p.setCode(cols.length > 1 ? cols[1].trim() : null);
            String statusText = cols.length > 2 ? cols[2].trim() : "启用";
            p.setStatus("停用".equals(statusText) ? 0 : 1);
            p.setSort(cols.length > 3 && !cols[3].isBlank() ? Integer.parseInt(cols[3].trim()) : 0);
            p.setRemark(cols.length > 4 ? cols[4].trim() : null);
            toInsert.add(p);
        }
        if (toInsert.isEmpty()) {
            throw new BusinessException("导入文件无有效数据");
        }
        for (ConfigPosition p : toInsert) {
            positionMapper.insert(p);
        }
    }

    @Override
    public IPage<SalesmanVO> salesmanPage(long current, long size, String name, Integer status, Long departmentId) {
        Page<ConfigSalesman> page = new Page<>(current, size);
        LambdaQueryWrapper<ConfigSalesman> wrapper = new LambdaQueryWrapper<ConfigSalesman>()
                .like(name != null && !name.isBlank(), ConfigSalesman::getName, name)
                .eq(status != null, ConfigSalesman::getStatus, status)
                .eq(departmentId != null, ConfigSalesman::getDepartmentId, departmentId)
                .orderByDesc(ConfigSalesman::getCreateTime);
        IPage<ConfigSalesman> result = salesmanMapper.selectPage(page, wrapper);
        Map<Long, String> positionMap = loadPositionMap();
        Map<Long, String> departmentMap = loadDepartmentMap();
        Page<SalesmanVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(s -> toSalesmanVO(s, positionMap, departmentMap))
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void salesmanCreate(SalesmanSaveDTO dto) {
        ConfigSalesman salesman = new ConfigSalesman();
        copyToSalesman(dto, salesman);
        salesmanMapper.insert(salesman);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void salesmanUpdate(SalesmanSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("缺少业务员ID");
        }
        ConfigSalesman salesman = salesmanMapper.selectById(dto.getId());
        if (salesman == null) {
            throw new BusinessException("业务员不存在");
        }
        copyToSalesman(dto, salesman);
        salesmanMapper.updateById(salesman);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void salesmanDelete(Long id) {
        if (salesmanMapper.selectById(id) == null) {
            throw new BusinessException("业务员不存在");
        }
        salesmanMapper.deleteById(id);
    }

    @Override
    public String salesmanExportCsv() {
        List<ConfigSalesman> all = salesmanMapper.selectList(
                new LambdaQueryWrapper<ConfigSalesman>().orderByDesc(ConfigSalesman::getCreateTime));
        Map<Long, String> positionMap = loadPositionMap();
        Map<Long, String> departmentMap = loadDepartmentMap();
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append("姓名,手机号,当前岗位,当前部门,状态,备注\n");
        for (ConfigSalesman s : all) {
            String statusText = Objects.equals(s.getStatus(), 1) ? "在职" : "离职";
            sb.append(escapeCsv(s.getName())).append(',')
                    .append(escapeCsv(s.getPhone())).append(',')
                    .append(escapeCsv(s.getPositionId() == null ? "" : positionMap.getOrDefault(s.getPositionId(), ""))).append(',')
                    .append(escapeCsv(s.getDepartmentId() == null ? "" : departmentMap.getOrDefault(s.getDepartmentId(), ""))).append(',')
                    .append(statusText).append(',')
                    .append(escapeCsv(s.getRemark())).append('\n');
        }
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void salesmanImportCsv(String csv) {
        List<ConfigSalesman> toInsert = new ArrayList<>();
        Map<String, Long> positionMap = loadPositionIdByName();
        Map<String, Long> departmentMap = loadDepartmentIdByName();
        for (String[] cols : parseCsvRows(csv)) {
            if (cols.length < 1 || cols[0].isBlank()) {
                continue;
            }
            ConfigSalesman s = new ConfigSalesman();
            s.setName(cols[0].trim());
            s.setPhone(cols.length > 1 ? cols[1].trim() : null);
            String positionName = cols.length > 2 ? cols[2].trim() : "";
            s.setPositionId(positionName.isBlank() ? null : positionMap.getOrDefault(positionName, null));
            String departmentName = cols.length > 3 ? cols[3].trim() : "";
            s.setDepartmentId(departmentName.isBlank() ? null : departmentMap.getOrDefault(departmentName, null));
            String statusText = cols.length > 4 ? cols[4].trim() : "在职";
            s.setStatus("离职".equals(statusText) ? 0 : 1);
            s.setRemark(cols.length > 5 ? cols[5].trim() : null);
            toInsert.add(s);
        }
        if (toInsert.isEmpty()) {
            throw new BusinessException("导入文件无有效数据");
        }
        for (ConfigSalesman s : toInsert) {
            salesmanMapper.insert(s);
        }
    }

    private void checkUsernameUnique(String username, Long excludeId) {
        if (username == null || username.isBlank()) {
            return;
        }
        ConfigUser exist = userMapper.selectOne(new LambdaQueryWrapper<ConfigUser>()
                .eq(ConfigUser::getUsername, username)
                .ne(excludeId != null, ConfigUser::getId, excludeId));
        if (exist != null) {
            throw new BusinessException("登录账户 " + username + " 已存在");
        }
    }

    private void copyToUser(UserSaveDTO dto, ConfigUser user) {
        user.setRealName(dto.getRealName());
        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
        }
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus());
        user.setHireDate(dto.getHireDate());
        user.setPositionId(dto.getPositionId());
        user.setDepartmentId(dto.getDepartmentId());
        user.setEmployeeType(dto.getEmployeeType());
        user.setRemark(dto.getRemark());
    }

    private void copyToPosition(PositionSaveDTO dto, ConfigPosition position) {
        position.setName(dto.getName());
        position.setCode(dto.getCode());
        position.setSort(dto.getSort() == null ? 0 : dto.getSort());
        position.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        position.setRemark(dto.getRemark());
    }

    private void copyToSalesman(SalesmanSaveDTO dto, ConfigSalesman salesman) {
        salesman.setName(dto.getName());
        salesman.setPhone(dto.getPhone());
        salesman.setPositionId(dto.getPositionId());
        salesman.setDepartmentId(dto.getDepartmentId());
        salesman.setStatus(dto.getStatus());
        salesman.setRemark(dto.getRemark());
    }

    private UserVO toUserVO(ConfigUser u, Map<Long, String> positionMap, Map<Long, String> departmentMap) {
        UserVO vo = new UserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setRealName(u.getRealName());
        vo.setPhone(u.getPhone());
        vo.setStatus(u.getStatus());
        vo.setHireDate(u.getHireDate());
        vo.setPositionId(u.getPositionId());
        vo.setPositionName(u.getPositionId() == null ? "" : positionMap.getOrDefault(u.getPositionId(), ""));
        vo.setDepartmentId(u.getDepartmentId());
        vo.setDepartmentName(u.getDepartmentId() == null ? "" : departmentMap.getOrDefault(u.getDepartmentId(), ""));
        vo.setEmployeeType(u.getEmployeeType());
        vo.setRemark(u.getRemark());
        vo.setCreateTime(u.getCreateTime());
        vo.setUpdateTime(u.getUpdateTime());
        return vo;
    }

    private SalesmanVO toSalesmanVO(ConfigSalesman s, Map<Long, String> positionMap, Map<Long, String> departmentMap) {
        SalesmanVO vo = new SalesmanVO();
        vo.setId(s.getId());
        vo.setName(s.getName());
        vo.setPhone(s.getPhone());
        vo.setPositionId(s.getPositionId());
        vo.setPositionName(s.getPositionId() == null ? "" : positionMap.getOrDefault(s.getPositionId(), ""));
        vo.setDepartmentId(s.getDepartmentId());
        vo.setDepartmentName(s.getDepartmentId() == null ? "" : departmentMap.getOrDefault(s.getDepartmentId(), ""));
        vo.setStatus(s.getStatus());
        vo.setRemark(s.getRemark());
        vo.setCreateTime(s.getCreateTime());
        vo.setUpdateTime(s.getUpdateTime());
        return vo;
    }

    private PositionVO toPositionVO(ConfigPosition p) {
        PositionVO vo = new PositionVO();
        vo.setId(p.getId());
        vo.setName(p.getName());
        vo.setCode(p.getCode());
        vo.setSort(p.getSort());
        vo.setStatus(p.getStatus());
        vo.setRemark(p.getRemark());
        vo.setCreateTime(p.getCreateTime());
        vo.setUpdateTime(p.getUpdateTime());
        return vo;
    }

    private Map<Long, String> loadPositionMap() {
        return positionMapper.selectList(null).stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(ConfigPosition::getId, ConfigPosition::getName, (a, b) -> a));
    }

    private Map<Long, String> loadDepartmentMap() {
        return departmentMapper.selectList(null).stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ConfigDepartment::getId, ConfigDepartment::getName, (a, b) -> a));
    }

    private Map<String, Long> loadPositionIdByName() {
        Map<String, Long> map = new java.util.LinkedHashMap<>();
        for (ConfigPosition p : positionMapper.selectList(null)) {
            if (p.getName() != null) {
                map.putIfAbsent(p.getName(), p.getId());
            }
        }
        return map;
    }

    private Map<String, Long> loadDepartmentIdByName() {
        Map<String, Long> map = new java.util.LinkedHashMap<>();
        for (ConfigDepartment d : departmentMapper.selectList(null)) {
            if (d.getName() != null) {
                map.putIfAbsent(d.getName(), d.getId());
            }
        }
        return map;
    }

    private List<String[]> parseCsvRows(String csv) {
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
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            rows.add(parseCsvLine(line));
        }
        return rows;
    }

    private String[] parseCsvLine(String line) {
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
        return result.toArray(new String[0]);
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
}
