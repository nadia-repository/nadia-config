package com.nadia.config.meta.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class MetadataRequest {
    private List<String> applications;
}
