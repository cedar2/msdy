package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.domain.FinHuipiaoRecordAttach;
import com.platform.ems.mapper.FinHuipiaoRecordAttachMapper;
import com.platform.ems.service.IFinHuipiaoRecordAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 汇票台账-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class FinHuipiaoRecordAttachServiceImpl extends ServiceImpl<FinHuipiaoRecordAttachMapper, FinHuipiaoRecordAttach> implements IFinHuipiaoRecordAttachService {
    @Autowired
    private FinHuipiaoRecordAttachMapper finHuipiaoRecordAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "汇票台账-附件";

    /**
     * 查询汇票台账-附件
     *
     * @param huipiaoRecordAttachSid 汇票台账-附件ID
     * @return 汇票台账-附件
     */
    @Override
    public FinHuipiaoRecordAttach selectFinHuipiaoRecordAttachById(Long huipiaoRecordAttachSid) {
        FinHuipiaoRecordAttach finHuipiaoRecordAttach = finHuipiaoRecordAttachMapper.selectFinHuipiaoRecordAttachById(huipiaoRecordAttachSid);
        MongodbUtil.find(finHuipiaoRecordAttach);
        return finHuipiaoRecordAttach;
    }

    /**
     * 查询汇票台账-附件列表
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 汇票台账-附件
     */
    @Override
    public List<FinHuipiaoRecordAttach> selectFinHuipiaoRecordAttachList(FinHuipiaoRecordAttach finHuipiaoRecordAttach) {
        return finHuipiaoRecordAttachMapper.selectFinHuipiaoRecordAttachList(finHuipiaoRecordAttach);
    }

    /**
     * 新增汇票台账-附件
     * 需要注意编码重复校验
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinHuipiaoRecordAttach(FinHuipiaoRecordAttach finHuipiaoRecordAttach) {
        int row = finHuipiaoRecordAttachMapper.insert(finHuipiaoRecordAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinHuipiaoRecordAttach(), finHuipiaoRecordAttach);
            MongodbUtil.insertUserLog(finHuipiaoRecordAttach.getHuipiaoRecordAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改汇票台账-附件
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinHuipiaoRecordAttach(FinHuipiaoRecordAttach finHuipiaoRecordAttach) {
        FinHuipiaoRecordAttach response = finHuipiaoRecordAttachMapper.selectFinHuipiaoRecordAttachById(finHuipiaoRecordAttach.getHuipiaoRecordAttachSid());
        int row = finHuipiaoRecordAttachMapper.updateById(finHuipiaoRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finHuipiaoRecordAttach.getHuipiaoRecordAttachSid(), BusinessType.UPDATE.getValue(), response, finHuipiaoRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更汇票台账-附件
     *
     * @param finHuipiaoRecordAttach 汇票台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinHuipiaoRecordAttach(FinHuipiaoRecordAttach finHuipiaoRecordAttach) {
        FinHuipiaoRecordAttach response = finHuipiaoRecordAttachMapper.selectFinHuipiaoRecordAttachById(finHuipiaoRecordAttach.getHuipiaoRecordAttachSid());
        int row = finHuipiaoRecordAttachMapper.updateAllById(finHuipiaoRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finHuipiaoRecordAttach.getHuipiaoRecordAttachSid(), BusinessType.CHANGE.getValue(), response, finHuipiaoRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除汇票台账-附件
     *
     * @param huipiaoRecordAttachSids 需要删除的汇票台账-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinHuipiaoRecordAttachByIds(List<Long> huipiaoRecordAttachSids) {
        huipiaoRecordAttachSids.forEach(sid -> {
            FinHuipiaoRecordAttach finHuipiaoRecordAttach = finHuipiaoRecordAttachMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(finHuipiaoRecordAttach, new FinHuipiaoRecordAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return finHuipiaoRecordAttachMapper.deleteBatchIds(huipiaoRecordAttachSids);
    }

}
