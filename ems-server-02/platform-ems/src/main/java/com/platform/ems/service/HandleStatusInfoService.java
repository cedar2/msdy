package com.platform.ems.service;

import com.platform.ems.domain.base.CreatorInfo;
import com.platform.ems.domain.base.HandleStatusInfo;
import com.platform.ems.enums.HandleStatus;

import java.util.Date;

import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserClientId;
import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserUserName;


/**
 * 模版方法
 * 子类只需要重写方法：setCreatorInfo，setUpdateInfo， setConfirmInfo <br>
 * 下面是【更新】或【编辑】一个实体类时，handleStatus状态对应的要更新的字段：<br>
 * <p>
 * 新建实体类时<br>
 * 暂存 --- 创建人/时间 <br>
 * 确认 --- 创建人+确认人/时间 <br>
 * </p>
 *
 * <p>
 * 编辑实体类时：<br>
 * 暂存 --- 更新人/时间 <br>
 * 确认 --- 更新人+确认人/时间 <br>
 * </p>
 *
 * @author Straw
 * @date 2023/1/18
 */
public interface HandleStatusInfoService {

    default void setHandleStatusInfoWhenUpdate(HandleStatusInfo obj) {
        // 更新实体类
        if (HandleStatus.isTemporarySave(obj.getHandleStatus())) {
            setUpdateInfo(obj);
        }
        if (HandleStatus.isConfirmed(obj.getHandleStatus())) {
            setUpdateInfo(obj);
            setConfirmInfo(obj);
        }
    }

    /**
     * 默认“租户ID”为当前操作用户的租户ID <br>
     * 默认“处理状态”为“保存”（1）<br>
     * 默认“创建人”为当前操作用户 <br>
     * 默认“创建日期”为当前操作时间 <br>
     */
    default void setHandleStatusInfoWhenNew(HandleStatusInfo obj) {
        // 新增实体类
        if (HandleStatus.isTemporarySave(obj.getHandleStatus())) {
            setCreatorInfo(obj);
        }
        if (HandleStatus.isConfirmed(obj.getHandleStatus())) {
            setCreatorInfo(obj);
            setConfirmInfo(obj);
        }
    }

    default void setCreatorInfo(CreatorInfo entity) {
        setClientId(entity);
        entity.setCreateDate(new Date());
        entity.setCreatorAccount(getLoginUserUserName());
    }

    default void setConfirmInfo(HandleStatusInfo entity) {
        entity.setConfirmDate(new Date());
        entity.setConfirmerAccount(getLoginUserUserName());
    }

    default void setUpdateInfo(HandleStatusInfo entity) {
        entity.setUpdateDate(new Date());
        entity.setUpdaterAccount(getLoginUserUserName());
    }

    default void setClientId(CreatorInfo entity) {
        entity.setClientId(getLoginUserClientId());
    }

}
