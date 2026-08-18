package com.fuyuanzi.flow.module.config.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fuyuanzi.flow.module.config.dto.DepartmentSaveDTO;
import com.fuyuanzi.flow.module.config.vo.DepartmentVO;

import java.util.List;

public interface DepartmentService {

    IPage<DepartmentVO> page(long current, long size, String name, String type, Integer status);

    List<DepartmentVO> listAll();

    DepartmentVO getDetail(Long id);

    void create(DepartmentSaveDTO dto);

    void update(DepartmentSaveDTO dto);

    void delete(Long id);

    String exportCsv();

    void importCsv(String csv);
}
