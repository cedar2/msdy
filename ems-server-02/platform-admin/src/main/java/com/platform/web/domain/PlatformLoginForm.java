package com.platform.web.domain;

import lombok.Data;

/**
 * @author Straw
 * @date 2023/1/6
 */
@Data
public class PlatformLoginForm {

    String username;
    String password;
    String clientId;
    String code;
    String uuid;

    PlatformInfo platform;

    @Data
    public static class PlatformInfo {
        String name;
        String code;
    }

}
