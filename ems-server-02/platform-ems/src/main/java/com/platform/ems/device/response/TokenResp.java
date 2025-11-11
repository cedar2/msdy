package com.platform.ems.device.response;

import lombok.Data;

/**
 * @author Straw
 * @since 2023/3/24
 */
@Data
public class TokenResp extends DeviceResp {

    Token data;

    public String getTokenOrThrow() {
        requireSuccess();
        return data.token;
    }

    static class Token {
        String token;
    }

}
