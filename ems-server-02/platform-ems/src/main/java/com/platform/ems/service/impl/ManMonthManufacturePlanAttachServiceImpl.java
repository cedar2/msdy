package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManMonthManufacturePlanAttach;
import com.platform.ems.mapper.ManMonthManufacturePlanAttachMapper;
import com.platform.ems.service.IManMonthManufacturePlanAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产月计划-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-10
 */
@Service
@SuppressWarnings("all")
public class ManMonthManufacturePlanAttachServiceImpl extends ServiceImpl<ManMonthManufacturePlanAttachMapper, ManMonthManufacturePlanAttach> implements IManMonthManufacturePlanAttachService {
    @Autowired
    private ManMonthManufacturePlanAttachMapper manMonthManufacturePlanAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产月计划-附件";

    /**
     * 查询生产月计划-附件
     *
     * @param manufacturePlanAttachSid 生产月计划-附件ID
     * @return 生产月计划-附件
     */
    @Override
    public ManMonthManufacturePlanAttach selectManMonthManufacturePlanAttachById(Long manufacturePlanAttachSid) {
        ManMonthManufacturePlanAttach manMonthManufacturePlanAttach = manMonthManufacturePlanAttachMapper.selectManMonthManufacturePlanAttachById(manufacturePlanAttachSid);
        MongodbUtil.find(manMonthManufacturePlanAttach);
        return manMonthManufacturePlanAttach;
    }

    /**
     * 查询生产月计划-附件列表
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 生产月计划-附件
     */
    @Override
    public List<ManMonthManufacturePlanAttach> selectManMonthManufacturePlanAttachList(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach) {
        return manMonthManufacturePlanAttachMapper.selectManMonthManufacturePlanAttachList(manMonthManufacturePlanAttach);
    }

    /**
     * 新增生产月计划-附件
     * 需要注意编码重复校验
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManMonthManufacturePlanAttach(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach) {
        int row = manMonthManufacturePlanAttachMapper.insert(manMonthManufacturePlanAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manMonthManufacturePlanAttach.getManufacturePlanAttachSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产月计划-附件
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManMonthManufacturePlanAttach(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach) {
        ManMonthManufacturePlanAttach response = manMonthManufacturePlanAttachMapper.selectManMonthManufacturePlanAttachById(manMonthManufacturePlanAttach.getManufacturePlanAttachSid());
        int row = manMonthManufacturePlanAttachMapper.updateById(manMonthManufacturePlanAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manMonthManufacturePlanAttach.getManufacturePlanAttachSid(), BusinessType.UPDATE.ordinal(), response, manMonthManufacturePlanAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更生产月计划-附件
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManMonthManufacturePlanAttach(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach) {
        ManMonthManufacturePlanAttach response = manMonthManufacturePlanAttachMapper.selectManMonthManufacturePlanAttachById(manMonthManufacturePlanAttach.getManufacturePlanAttachSid());
        int row = manMonthManufacturePlanAttachMapper.updateAllById(manMonthManufacturePlanAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manMonthManufacturePlanAttach.getManufacturePlanAttachSid(), BusinessType.CHANGE.ordinal(), response, manMonthManufacturePlanAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产月计划-附件
     *
     * @param manufacturePlanAttachSids 需要删除的生产月计划-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManMonthManufacturePlanAttachByIds(List<Long> manufacturePlanAttachSids) {
        return manMonthManufacturePlanAttachMapper.deleteBatchIds(manufacturePlanAttachSids);
    }
}
