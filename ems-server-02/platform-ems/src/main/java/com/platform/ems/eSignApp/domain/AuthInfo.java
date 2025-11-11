package com.platform.ems.eSignApp.domain;

/**
 * @author duya
 * @since 2022-09-19
 */
public class AuthInfo {

    private String orgId;

    private String psnId;

    private String authShortUrl;

    private String authStatus;

    private Integer orgRealnameStatus;

    private Integer psnRealnameStatus;

    private Long authEffectiveTime;

    private Long authExpireTime;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getPsnId() {
        return psnId;
    }

    public void setPsnId(String psnId) {
        this.psnId = psnId;
    }

    public String getAuthShortUrl() {
        return authShortUrl;
    }

    public void setAuthShortUrl(String authShortUrl) {
        this.authShortUrl = authShortUrl;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public Integer getOrgRealnameStatus() {
        return orgRealnameStatus;
    }

    public void setOrgRealnameStatus(Integer orgRealnameStatus) {
        this.orgRealnameStatus = orgRealnameStatus;
    }

    public Integer getPsnRealnameStatus() {
        return psnRealnameStatus;
    }

    public void setPsnRealnameStatus(Integer psnRealnameStatus) {
        this.psnRealnameStatus = psnRealnameStatus;
    }

    public Long getAuthEffectiveTime() {
        return authEffectiveTime;
    }

    public void setAuthEffectiveTime(Long authEffectiveTime) {
        this.authEffectiveTime = authEffectiveTime;
    }

    public Long getAuthExpireTime() {
        return authExpireTime;
    }

    public void setAuthExpireTime(Long authExpireTime) {
        this.authExpireTime = authExpireTime;
    }
}
