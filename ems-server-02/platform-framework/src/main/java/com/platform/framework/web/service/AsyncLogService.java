package com.platform.framework.web.service;

import com.platform.system.domain.SysOperLog;
import com.platform.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步调用日志服务
 *
 * @author platform
 */
@Service
public class AsyncLogService {
    @Autowired
    private ISysOperLogService operLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog) {
        operLogService.insertOperlog(sysOperLog);
    }
}
