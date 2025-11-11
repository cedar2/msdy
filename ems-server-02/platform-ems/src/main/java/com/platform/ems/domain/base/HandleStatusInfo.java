package com.platform.ems.domain.base;

import java.util.Date;

/**
 * @author Straw
 * @date 2023/1/18
 */
public interface HandleStatusInfo extends CreatorInfo {

    HandleStatusInfo setConfirmDate(Date date);

    HandleStatusInfo setConfirmerAccount(String userName);

    HandleStatusInfo setUpdateDate(Date date);

    HandleStatusInfo setUpdaterAccount(String userName);

    String getHandleStatus();

}
