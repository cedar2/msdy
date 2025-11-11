package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManWeekManufacturePlanAttach;
import com.platform.ems.mapper.ManWeekManufacturePlanAttachMapper;
import com.platform.ems.service.IManWeekManufacturePlanAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产周计划-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-10
 */
@Service
@SuppressWarnings("all")
public class ManWeekManufacturePlanAttachServiceImpl extends ServiceImpl<ManWeekManufacturePlanAttachMapper, ManWeekManufacturePlanAttach> implements IManWeekManufacturePlanAttachService {
    @Autowired
    private ManWeekManufacturePlanAttachMapper manWeekManufacturePlanAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产周计划-附件";

    /**
     * 查询生产周计划-附件
     *
     * @param manufacturePlanAttachSid 生产周计划-附件ID
     * @return 生产周计划-附件
     */
    @Override
    public ManWeekManufacturePlanAttach selectManWeekManufacturePlanAttachById(Long manufacturePlanAttachSid) {
        ManWeekManufacturePlanAttach manWeekManufacturePlanAttach = manWeekManufacturePlanAttachMapper.selectManWeekManufacturePlanAttachById(manufacturePlanAttachSid);
        MongodbUtil.find(manWeekManufacturePlanAttach);
        return manWeekManufacturePlanAttach;
    }

    /**
     * 查询生产周计划-附件列表
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 生产周计划-附件
     */
    @Override
    public List<ManWeekManufacturePlanAttach> selectManWeekManufacturePlanAttachList(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach) {
        return manWeekManufacturePlanAttachMapper.selectManWeekManufacturePlanAttachList(manWeekManufacturePlanAttach);
    }

    /**
     * 新增生产周计划-附件
     * 需要注意编码重复校验
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManWeekManufacturePlanAttach(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach) {
        int row = manWeekManufacturePlanAttachMapper.insert(manWeekManufacturePlanAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manWeekManufacturePlanAttach.getManufacturePlanAttachSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产周计划-附件
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManWeekManufacturePlanAttach(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach) {
        ManWeekManufacturePlanAttach response = manWeekManufacturePlanAttachMapper.selectManWeekManufacturePlanAttachById(manWeekManufacturePlanAttach.getManufacturePlanAttachSid());
        int row = manWeekManufacturePlanAttachMapper.updateById(manWeekManufacturePlanAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manWeekManufacturePlanAttach.getManufacturePlanAttachSid(), BusinessType.UPDATE.ordinal(), response, manWeekManufacturePlanAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更生产周计划-附件
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManWeekManufacturePlanAttach(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach) {
        ManWeekManufacturePlanAttach response = manWeekManufacturePlanAttachMapper.selectManWeekManufacturePlanAttachById(manWeekManufacturePlanAttach.getManufacturePlanAttachSid());
        int row = manWeekManufacturePlanAttachMapper.updateAllById(manWeekManufacturePlanAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manWeekManufacturePlanAttach.getManufacturePlanAttachSid(), BusinessType.CHANGE.ordinal(), response, manWeekManufacturePlanAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产周计划-附件
     *
     * @param manufacturePlanAttachSids 需要删除的生产周计划-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManWeekManufacturePlanAttachByIds(List<Long> manufacturePlanAttachSids) {
        return manWeekManufacturePlanAttachMapper.deleteBatchIds(manufacturePlanAttachSids);
    }
}
