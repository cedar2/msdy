package com.platform.api.service.impl;

import com.platform.api.service.RemoteFileService;
import com.platform.common.core.domain.R;
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.file.controller.SysFileController;
import com.platform.framework.web.domain.server.SysFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xfzz
 */
@Service
@SuppressWarnings("all")
public class RemoteFileServiceImpl  implements RemoteFileService {

    @Autowired
    private SysFileController sysFileController;

    @Override
    public R<SysFile> upload(MultipartFile file, Long sid) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        sysFileController = context.getBean(SysFileController.class);
        return sysFileController.upload(file, sid);
    }

    @Override
    public R<SysFile> upload(MultipartFile file) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        sysFileController = context.getBean(SysFileController.class);
        return sysFileController.upload(file, null);
    }

    @Override
    public R<SysFile> uploadTemplate(MultipartFile file, String fileName) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        sysFileController = context.getBean(SysFileController.class);
        return sysFileController.uploadTemplate(file, null);
    }
}
