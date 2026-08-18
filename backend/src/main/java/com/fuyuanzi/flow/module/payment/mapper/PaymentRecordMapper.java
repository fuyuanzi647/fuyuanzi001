package com.fuyuanzi.flow.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuyuanzi.flow.module.payment.entity.PaymentRecord;
import com.fuyuanzi.flow.module.payment.vo.PaymentRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    List<PaymentRecordVO> selectRecordsByOrderId(@Param("orderId") Long orderId);

    IPage<PaymentRecordVO> selectRecordPage(Page<PaymentRecordVO> page,
                                            @Param("orderNo") String orderNo,
                                            @Param("businessId") Long businessId,
                                            @Param("payDateStart") LocalDate payDateStart,
                                            @Param("payDateEnd") LocalDate payDateEnd,
                                            @Param("payMethod") String payMethod,
                                            @Param("sort") String sort);
}
