package com.platform.ems.service;

import com.platform.ems.domain.RepBusinessRemind;

/**
 * 看板数据Service接口
 *
 * @author chenkw
 * @date 2022-04-26
 */
public interface IRepService {

    /**
     * 已逾期与即将逾期看板数据Service接口
     *
     * @author chenkw
     * @date 2022-04-26
     */
    RepBusinessRemind getBusinessRemind();

    /**
     * 财务状况看板数据接口Service接口
     *
     * @author chenkw
     * @date 2022-05-07
     */
    RepBusinessRemind getFinanceStatus();

}
