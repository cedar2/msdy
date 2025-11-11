package com.platform.flowable.domain;

import lombok.Data;

@Data
public class AssigneeQuery {
    Long userId;
    String processInstanceId;
}
