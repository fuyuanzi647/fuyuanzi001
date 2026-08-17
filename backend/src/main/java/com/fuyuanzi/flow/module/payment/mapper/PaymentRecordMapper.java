package com.fuyuanzi.flow.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuyuanzi.flow.module.payment.entity.PaymentRecord;
import com.fuyuanzi.flow.module.payment.vo.PaymentRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    List<PaymentRecordVO> selectRecordsByOrderId(@Param("orderId") Long orderId);
}
