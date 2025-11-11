package com.platform.api.service.impl;

import com.platform.api.service.RemoteEmsService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.FileType;
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.ems.controller.BasStaffController;
import com.platform.ems.controller.SysDeployFormController;
import com.platform.ems.controller.SysFormProcessController;
import com.platform.ems.controller.SysUserAgencyController;
import com.platform.ems.plug.controller.ConFileTypeController;
import com.platform.system.domain.SysDeployForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author xfzz
 */
@Service
@SuppressWarnings("all")
public class RemoteEmsServiceImpl implements RemoteEmsService {

    @Autowired
    private ConFileTypeController conFileTypeController;
    @Autowired
    private SysFormProcessController sysFormProcessController;
    @Autowired
    private SysDeployFormController sysDeployFormController;
    @Autowired
    private BasStaffController basStaffController;
    @Autowired
    private SysUserAgencyController sysUserAgencyController;

    @Override
    public R<FileType> fileType(Long sid) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        conFileTypeController = context.getBean(ConFileTypeController.class);
        FileType fileType = (FileType) conFileTypeController.getInfo(sid).get(AjaxResult.DATA_TAG);
        return R.ok(fileType);
    }

    @Override
    public AjaxResult addDeployForm(SysDeployForm sysDeployForm) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        sysDeployFormController = context.getBean(SysDeployFormController.class);
        return sysDeployFormController.add(sysDeployForm);
    }

}
