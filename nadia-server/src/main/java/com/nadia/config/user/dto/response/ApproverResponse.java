package com.nadia.config.user.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-16 10:40
 */
@Data
public class ApproverResponse {
    private List<String> approvers;
    private List<Approver> approverOptions;

    @Data
    public class Approver {
        private Long id;
        private String name;
    }
}
