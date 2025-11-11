package com.platform.ems.device.log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import lombok.Data;

import java.util.Date;

@Data
@TableName("s_sys_log_workstation")
public class SysLogWorkstation {
    /**
     * 租户ID
     */
    String clientId;

    /**
     * 工位档案日志SID
     */
    @TableId(type = IdType.AUTO)
    Long logWorkstationSid;

    /**
     * 系统SID-工位档案
     */
    Long workstationSid;

    /**
     * 工位编号（人工编码）
     */
    String workstationCode;

    /**
     * 工位名称
     */
    String workstationName;

    /**
     * 隶属公司sid
     */
    Long companySid;

    /**
     * 隶属公司编码
     */
    String companyCode;

    /**
     * 隶属工厂sid
     */
    Long plantSid;

    /**
     * 隶属工厂编码
     */
    String plantCode;

    /**
     * 隶属工作中心(班组)sid
     */
    Long workCenterSid;

    /**
     * 隶属工作中心(班组)编码
     */
    String workCenterCode;

    /**
     * 隶属操作部门sid
     */
    Long departmentSid;

    /**
     * 隶属操作部门编码
     */
    String departmentCode;

    /**
     * 推送人账号（用户账号）
     */
    String sendAccount = ApiThreadLocalUtil.getLoginUserUserName();

    /**
     * 推送时间
     */
    Date sendDate = new Date();

    /**
     * 目标系统（数据字典的键值或配置档案的编码）
     */
    String dataTargetSys;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    String dataSourceSys;
}

