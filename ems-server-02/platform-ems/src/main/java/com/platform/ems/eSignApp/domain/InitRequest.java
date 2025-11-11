package com.platform.ems.eSignApp.domain;

import lombok.Data;

import java.util.Map;

/**
 * @author duya
 * @since 2022-09-19
 *
 * @update lgt
 * @since 2024-01-28
 */
@Data
public class InitRequest {

    private Map<String, Object>  orgAuthConfig;

    private Map<String, Object>  authorizeConfig;

    private Map<String, Object> redirectConfig;


}
