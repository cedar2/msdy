package com.platform.ems.eSignApp.domain;

/**
 * @author duya
 * @since 2022-09-19
 */
public class InitData {

    private String jsSdkTicket;

    private AuthInfo authInfo;

    public String getJsSdkTicket() {
        return jsSdkTicket;
    }

    public void setJsSdkTicket(String jsSdkTicket) {
        this.jsSdkTicket = jsSdkTicket;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }
}
