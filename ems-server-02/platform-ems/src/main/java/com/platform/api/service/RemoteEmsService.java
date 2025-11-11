package com.platform.api.service;

import com.platform.common.core.domain.entity.FileType;

import com.platform.common.core.domain.R;
import com.platform.common.core.domain.AjaxResult;
import com.platform.system.domain.SysDeployForm;

/**
 * 业务模块
 *
 * @author c
 */
public interface RemoteEmsService {

    R<FileType> fileType(Long sid);

    public AjaxResult addDeployForm(SysDeployForm sysDeployForm);

}
