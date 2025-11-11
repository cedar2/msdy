package com.platform.ems.domain.base;

import java.util.Date;

/**
 * @author Straw
 * @date 2023/2/3
 */
public interface CreatorInfo {
    CreatorInfo setClientId(String clientId);

    CreatorInfo setCreateDate(Date date);

    CreatorInfo setCreatorAccount(String userName);
}
