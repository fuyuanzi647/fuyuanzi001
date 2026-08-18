package com.fuyuanzi.flow.module.payment.mapper;

import com.fuyuanzi.flow.module.payment.vo.OptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BaseOptionMapper {

    @Select("SELECT id, name FROM base_business WHERE deleted = 0 AND status = 1 ORDER BY id")
    List<OptionVO> selectBusinessOptions();

    @Select("SELECT id, name FROM base_manufacturer WHERE deleted = 0 AND status = 1 ORDER BY id")
    List<OptionVO> selectManufacturerOptions();

    @Select("SELECT id, name FROM base_product WHERE deleted = 0 AND status = 1 ORDER BY id")
    List<OptionVO> selectProductOptions();
}
