package com.fuyuanzi.flow.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModuleInfo {

    private String code;
    private String name;
    private String description;
    private List<String> features;
}
