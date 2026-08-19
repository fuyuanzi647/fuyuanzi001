package com.fuyuanzi.flow.module.payment.vo;

import lombok.Data;

import java.util.List;

@Data
public class ReceivablePageVO {

    private long total;

    private List<ReceivableVO> records;

    /** 本页合计 */
    private ReceivableTotalVO pageTotal;

    /** 本次查询合计 */
    private ReceivableTotalVO queryTotal;
}
