package com.fuyuanzi.flow.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuyuanzi.flow.module.payment.entity.ShipmentOrder;
import com.fuyuanzi.flow.module.payment.vo.ShipmentItemVO;
import com.fuyuanzi.flow.module.payment.vo.ShipmentOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ShipmentOrderMapper extends BaseMapper<ShipmentOrder> {

    IPage<ShipmentOrderVO> selectOrderPage(Page<ShipmentOrderVO> page,
                                           @Param("orderNo") String orderNo,
                                           @Param("businessId") Long businessId,
                                           @Param("shipDateStart") LocalDate shipDateStart,
                                           @Param("shipDateEnd") LocalDate shipDateEnd,
                                           @Param("status") Integer status,
                                           @Param("sort") String sort);

    ShipmentOrderVO selectOrderById(@Param("id") Long id);

    List<ShipmentItemVO> selectItemsByOrderIds(@Param("orderIds") List<Long> orderIds);
}
