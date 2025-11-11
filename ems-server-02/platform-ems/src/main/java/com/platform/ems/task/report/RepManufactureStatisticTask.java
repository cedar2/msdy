package com.platform.ems.task.report;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.system.mapper.SysClientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产统计报表看板指标数据统计分析定时作业
 * @author chenkw
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
@Service
public class RepManufactureStatisticTask {

    @Autowired
    private RepManufactureStatusDaipaiMapper manufactureStatusDaipaiMapper;
    @Autowired
    private RepManufactureStatusZaichaMapper manufactureStatusZaichaMapper;
    @Autowired
    private RepManufactureStatusCipinMapper manufactureStatusCipinMapper;
    @Autowired
    private RepManufactureStatisticMapper manufactureStatisticMapper;
    @Autowired
    private SysClientMapper clientMapper;

    @Transactional(rollbackFor = Exception.class)
    public void setRepManufactureStatistic(){
        manufactureStatisticMapper.delete(new UpdateWrapper<RepManufactureStatistic>());
        this.setStatistic();
    }

    /**
     *  开始统计生产状况
     *
     * @author chenkw
     */
    @Transactional(rollbackFor = Exception.class)
    public void setStatistic() {
        log.info("=====>开始统计生产状况<=====");
        List<SysClient> clientList = clientMapper.selectList(new QueryWrapper<>());
        manufactureStatusDaipaiMapper.delete(new UpdateWrapper<RepManufactureStatusDaipai>());
        manufactureStatusZaichaMapper.delete(new UpdateWrapper<RepManufactureStatusZaicha>());
        manufactureStatusCipinMapper.delete(new UpdateWrapper<RepManufactureStatusCipin>());
        for (SysClient client : clientList) {
            List<RepManufactureStatistic> statisticList = new ArrayList<>();
            //*****财务状况-生产状况-待排产量
            List<RepManufactureStatusDaipai> repManufactureStatusDaipaiList = manufactureStatusDaipaiMapper.getRepManufactureStatusDaipaiList(
                    new RepManufactureStatusDaipai().setClientId(client.getClientId()).setHandleStatus(ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(repManufactureStatusDaipaiList)) {
                int daipai = manufactureStatusDaipaiMapper.inserts(repManufactureStatusDaipaiList);

            }
            //*****财务状况-生产状况-在产
            List<RepManufactureStatusZaicha> repManufactureStatusZaichaList = manufactureStatusZaichaMapper.getRepManufactureStatusZaichaList(
                    new RepManufactureStatusZaicha().setClientId(client.getClientId()).setHandleStatus(ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(repManufactureStatusZaichaList)) {
                int daipai = manufactureStatusZaichaMapper.inserts(repManufactureStatusZaichaList);

            }
            //*****财务状况-生产状况-次品
            List<RepManufactureStatusCipin> repManufactureStatusCipinList = manufactureStatusCipinMapper.getRepManufactureStatusCipinList(
                    new RepManufactureStatusCipin().setClientId(client.getClientId()).setHandleStatus(ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(repManufactureStatusCipinList)) {
                int daipai = manufactureStatusCipinMapper.inserts(repManufactureStatusCipinList);

            }
        }


    }


}
