package com.fuyuanzi.flow.module.payment.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuyuanzi.flow.module.payment.vo.ReceivableOverviewVO;
import com.fuyuanzi.flow.module.payment.vo.ReceivableTotalVO;
import com.fuyuanzi.flow.module.payment.vo.ReceivableVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReceivableMapper {

    IPage<ReceivableVO> selectReceivablePage(Page<ReceivableVO> page,
                                             @Param("orderNo") String orderNo,
                                             @Param("businessId") Long businessId,
                                             @Param("region") String region,
                                             @Param("shipDateStart") LocalDate shipDateStart,
                                             @Param("shipDateEnd") LocalDate shipDateEnd,
                                             @Param("sort") String sort);

    ReceivableTotalVO selectReceivableTotal(@Param("orderNo") String orderNo,
                                            @Param("businessId") Long businessId,
                                            @Param("region") String region,
                                            @Param("shipDateStart") LocalDate shipDateStart,
                                            @Param("shipDateEnd") LocalDate shipDateEnd);

    List<ReceivableOverviewVO> selectReceivableOverview(@Param("orderNo") String orderNo,
                                                        @Param("businessId") Long businessId,
                                                        @Param("region") String region,
                                                        @Param("shipDateStart") LocalDate shipDateStart,
                                                        @Param("shipDateEnd") LocalDate shipDateEnd);

    ReceivableTotalVO selectOverviewTotal(@Param("orderNo") String orderNo,
                                          @Param("businessId") Long businessId,
                                          @Param("region") String region,
                                          @Param("shipDateStart") LocalDate shipDateStart,
                                          @Param("shipDateEnd") LocalDate shipDateEnd);
}
