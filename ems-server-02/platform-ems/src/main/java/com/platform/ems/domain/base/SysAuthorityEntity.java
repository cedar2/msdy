package com.platform.ems.domain.base;

/**
 * @author Straw
 * @date 2023/1/16
 */
public interface SysAuthorityEntity extends HandleStatusInfo {
    String getCreateSource();

    SysAuthorityEntity setCreateSource(String createSource);

}
