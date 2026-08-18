package com.fuyuanzi.flow.module.config.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.module.config.dto.PositionSaveDTO;
import com.fuyuanzi.flow.module.config.dto.SalesmanSaveDTO;
import com.fuyuanzi.flow.module.config.dto.UserSaveDTO;
import com.fuyuanzi.flow.module.config.vo.PositionVO;
import com.fuyuanzi.flow.module.config.vo.SalesmanVO;
import com.fuyuanzi.flow.module.config.vo.UserVO;

import java.util.List;

public interface UserConfigService {

    IPage<UserVO> userPage(long current, long size, String name, String username, Integer status, Long departmentId, Long positionId);

    UserVO userDetail(Long id);

    void userCreate(UserSaveDTO dto);

    void userUpdate(UserSaveDTO dto);

    void userDelete(Long id);

    void userTransfer(Long id, Long positionId, Long departmentId);

    String userExportCsv();

    void userImportCsv(String csv);

    List<PositionVO> positionList();

    IPage<PositionVO> positionPage(long current, long size, String name, Integer status);

    void positionCreate(PositionSaveDTO dto);

    void positionUpdate(PositionSaveDTO dto);

    void positionDelete(Long id);

    String positionExportCsv();

    void positionImportCsv(String csv);

    IPage<SalesmanVO> salesmanPage(long current, long size, String name, Integer status, Long departmentId);

    void salesmanCreate(SalesmanSaveDTO dto);

    void salesmanUpdate(SalesmanSaveDTO dto);

    void salesmanDelete(Long id);

    String salesmanExportCsv();

    void salesmanImportCsv(String csv);
}
