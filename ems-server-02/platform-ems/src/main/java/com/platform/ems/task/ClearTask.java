package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.mapper.SysBusinessBcstMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 业务动态通知：清除7天前动态通知
 *
 * @author hjj
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class ClearTask {

    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;

    @Scheduled(cron = "00 00 00 * * *")
    public void earlyWarning() {
        log.info("=====>开始清除7天前动态通知");
        List<SysBusinessBcst> sysBusinessBcsts = sysBusinessBcstMapper.selectAll();
        if (CollectionUtil.isNotEmpty(sysBusinessBcsts)) {
            Long[] businessBcstSids = sysBusinessBcsts.stream()
                    .filter(sysBusinessBcst -> DateUtil.format(sysBusinessBcst.getNoticeDate(), "yyyy-MM-dd")
                            .compareTo(DateUtil.format(DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -7), "yyyy-MM-dd")) <= 0 )
                    .map(SysBusinessBcst::getBusinessBcstSid).toArray(Long[]::new);
            sysBusinessBcstMapper.deleteAll(new SysBusinessBcst().setBusinessBcstSidList(businessBcstSids));
        }
        log.info("==>处理结束");
    }
}
