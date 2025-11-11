package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.FunFundRecordAttachMapper;
import com.platform.ems.domain.FunFundRecordAttach;
import com.platform.ems.service.IFunFundRecordAttachService;

/**
 * 资金流水-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class FunFundRecordAttachServiceImpl extends ServiceImpl<FunFundRecordAttachMapper, FunFundRecordAttach> implements IFunFundRecordAttachService {
    @Autowired
    private FunFundRecordAttachMapper funFundRecordAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "资金流水-附件";

    /**
     * 查询资金流水-附件
     *
     * @param fundRecordAttachSid 资金流水-附件ID
     * @return 资金流水-附件
     */
    @Override
    public FunFundRecordAttach selectFunFundRecordAttachById(Long fundRecordAttachSid) {
        FunFundRecordAttach funFundRecordAttach = funFundRecordAttachMapper.selectFunFundRecordAttachById(fundRecordAttachSid);
        MongodbUtil.find(funFundRecordAttach);
        return funFundRecordAttach;
    }

    /**
     * 查询资金流水-附件列表
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 资金流水-附件
     */
    @Override
    public List<FunFundRecordAttach> selectFunFundRecordAttachList(FunFundRecordAttach funFundRecordAttach) {
        return funFundRecordAttachMapper.selectFunFundRecordAttachList(funFundRecordAttach);
    }

    /**
     * 新增资金流水-附件
     * 需要注意编码重复校验
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFunFundRecordAttach(FunFundRecordAttach funFundRecordAttach) {
        int row = funFundRecordAttachMapper.insert(funFundRecordAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FunFundRecordAttach(), funFundRecordAttach);
            MongodbUtil.insertUserLog(funFundRecordAttach.getFundRecordAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改资金流水-附件
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFunFundRecordAttach(FunFundRecordAttach funFundRecordAttach) {
        FunFundRecordAttach response = funFundRecordAttachMapper.selectFunFundRecordAttachById(funFundRecordAttach.getFundRecordAttachSid());
        int row = funFundRecordAttachMapper.updateById(funFundRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundRecordAttach.getFundRecordAttachSid(), BusinessType.UPDATE.getValue(), response, funFundRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更资金流水-附件
     *
     * @param funFundRecordAttach 资金流水-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFunFundRecordAttach(FunFundRecordAttach funFundRecordAttach) {
        FunFundRecordAttach response = funFundRecordAttachMapper.selectFunFundRecordAttachById(funFundRecordAttach.getFundRecordAttachSid());
        int row = funFundRecordAttachMapper.updateAllById(funFundRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundRecordAttach.getFundRecordAttachSid(), BusinessType.CHANGE.getValue(), response, funFundRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除资金流水-附件
     *
     * @param fundRecordAttachSids 需要删除的资金流水-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFunFundRecordAttachByIds(List<Long> fundRecordAttachSids) {
        fundRecordAttachSids.forEach(sid -> {
            FunFundRecordAttach funFundRecordAttach = funFundRecordAttachMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(funFundRecordAttach, new FunFundRecordAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return funFundRecordAttachMapper.deleteBatchIds(fundRecordAttachSids);
    }

}
