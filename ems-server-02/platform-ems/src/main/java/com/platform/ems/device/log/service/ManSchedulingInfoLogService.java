package com.platform.ems.device.log.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.device.log.domain.ManSchedulingInfoLog;
import com.platform.ems.device.log.mapper.ManSchedulingInfoLogMapper;
import org.springframework.stereotype.Service;

/**
 * 生产排程信息日志
 * @author chenkw
 * @since 2023/6/02
 */
@Service
public class ManSchedulingInfoLogService extends ServiceImpl<ManSchedulingInfoLogMapper, ManSchedulingInfoLog> {
}
