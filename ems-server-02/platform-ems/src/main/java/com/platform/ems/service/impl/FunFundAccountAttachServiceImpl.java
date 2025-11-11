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
import com.platform.ems.mapper.FunFundAccountAttachMapper;
import com.platform.ems.domain.FunFundAccountAttach;
import com.platform.ems.service.IFunFundAccountAttachService;

/**
 * 资金账户信息-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class FunFundAccountAttachServiceImpl extends ServiceImpl<FunFundAccountAttachMapper, FunFundAccountAttach> implements IFunFundAccountAttachService {
    @Autowired
    private FunFundAccountAttachMapper funFundAccountAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "资金账户信息-附件";

    /**
     * 查询资金账户信息-附件
     *
     * @param fundAccountAttachSid 资金账户信息-附件ID
     * @return 资金账户信息-附件
     */
    @Override
    public FunFundAccountAttach selectFunFundAccountAttachById(Long fundAccountAttachSid) {
        FunFundAccountAttach funFundAccountAttach = funFundAccountAttachMapper.selectFunFundAccountAttachById(fundAccountAttachSid);
        MongodbUtil.find(funFundAccountAttach);
        return funFundAccountAttach;
    }

    /**
     * 查询资金账户信息-附件列表
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 资金账户信息-附件
     */
    @Override
    public List<FunFundAccountAttach> selectFunFundAccountAttachList(FunFundAccountAttach funFundAccountAttach) {
        return funFundAccountAttachMapper.selectFunFundAccountAttachList(funFundAccountAttach);
    }

    /**
     * 新增资金账户信息-附件
     * 需要注意编码重复校验
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFunFundAccountAttach(FunFundAccountAttach funFundAccountAttach) {
        int row = funFundAccountAttachMapper.insert(funFundAccountAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FunFundAccountAttach(), funFundAccountAttach);
            MongodbUtil.insertUserLog(funFundAccountAttach.getFundAccountAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改资金账户信息-附件
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFunFundAccountAttach(FunFundAccountAttach funFundAccountAttach) {
        FunFundAccountAttach response = funFundAccountAttachMapper.selectFunFundAccountAttachById(funFundAccountAttach.getFundAccountAttachSid());
        int row = funFundAccountAttachMapper.updateById(funFundAccountAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundAccountAttach.getFundAccountAttachSid(), BusinessType.UPDATE.getValue(), response, funFundAccountAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更资金账户信息-附件
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFunFundAccountAttach(FunFundAccountAttach funFundAccountAttach) {
        FunFundAccountAttach response = funFundAccountAttachMapper.selectFunFundAccountAttachById(funFundAccountAttach.getFundAccountAttachSid());
        int row = funFundAccountAttachMapper.updateAllById(funFundAccountAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundAccountAttach.getFundAccountAttachSid(), BusinessType.CHANGE.getValue(), response, funFundAccountAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除资金账户信息-附件
     *
     * @param fundAccountAttachSids 需要删除的资金账户信息-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFunFundAccountAttachByIds(List<Long> fundAccountAttachSids) {
        fundAccountAttachSids.forEach(sid -> {
            FunFundAccountAttach funFundAccountAttach = funFundAccountAttachMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(funFundAccountAttach, new FunFundAccountAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return funFundAccountAttachMapper.deleteBatchIds(fundAccountAttachSids);
    }

}
