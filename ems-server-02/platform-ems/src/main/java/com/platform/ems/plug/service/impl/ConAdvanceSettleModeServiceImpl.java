package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConAdvanceSettleMode;
import com.platform.ems.plug.mapper.ConAdvanceSettleModeMapper;
import com.platform.ems.plug.service.IConAdvanceSettleModeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预收款/预付款付款方式Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConAdvanceSettleModeServiceImpl extends ServiceImpl<ConAdvanceSettleModeMapper, ConAdvanceSettleMode> implements IConAdvanceSettleModeService {
    @Autowired
    private ConAdvanceSettleModeMapper conAdvanceSettleModeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "预收款/预付款付款方式";

    /**
     * 查询预收款/预付款付款方式
     *
     * @param sid 预收款/预付款付款方式ID
     * @return 预收款/预付款付款方式
     */
    @Override
    public ConAdvanceSettleMode selectConAdvanceSettleModeById(Long sid) {
        ConAdvanceSettleMode conAdvanceSettleMode = conAdvanceSettleModeMapper.selectConAdvanceSettleModeById(sid);
        MongodbUtil.find(conAdvanceSettleMode);
        return conAdvanceSettleMode;
    }

    /**
     * 查询预收款/预付款付款方式列表
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 预收款/预付款付款方式
     */
    @Override
    public List<ConAdvanceSettleMode> selectConAdvanceSettleModeList(ConAdvanceSettleMode conAdvanceSettleMode) {
        return conAdvanceSettleModeMapper.selectConAdvanceSettleModeList(conAdvanceSettleMode);
    }

    /**
     * 新增预收款/预付款付款方式
     * 需要注意编码重复校验
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConAdvanceSettleMode(ConAdvanceSettleMode conAdvanceSettleMode) {
        List<ConAdvanceSettleMode> codeList = conAdvanceSettleModeMapper.selectList(new QueryWrapper<ConAdvanceSettleMode>().lambda()
                .eq(ConAdvanceSettleMode::getCode, conAdvanceSettleMode.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConAdvanceSettleMode> nameList = conAdvanceSettleModeMapper.selectList(new QueryWrapper<ConAdvanceSettleMode>().lambda()
                .eq(ConAdvanceSettleMode::getName, conAdvanceSettleMode.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conAdvanceSettleModeMapper.insert(conAdvanceSettleMode);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conAdvanceSettleMode.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改预收款/预付款付款方式
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConAdvanceSettleMode(ConAdvanceSettleMode conAdvanceSettleMode) {
        ConAdvanceSettleMode response = conAdvanceSettleModeMapper.selectConAdvanceSettleModeById(conAdvanceSettleMode.getSid());
        int row = conAdvanceSettleModeMapper.updateById(conAdvanceSettleMode);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAdvanceSettleMode.getSid(), BusinessType.UPDATE.getValue(), response, conAdvanceSettleMode, TITLE);
        }
        return row;
    }

    /**
     * 变更预收款/预付款付款方式
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConAdvanceSettleMode(ConAdvanceSettleMode conAdvanceSettleMode) {
        List<ConAdvanceSettleMode> nameList = conAdvanceSettleModeMapper.selectList(new QueryWrapper<ConAdvanceSettleMode>().lambda()
                .eq(ConAdvanceSettleMode::getName, conAdvanceSettleMode.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conAdvanceSettleMode.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conAdvanceSettleMode.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConAdvanceSettleMode response = conAdvanceSettleModeMapper.selectConAdvanceSettleModeById(conAdvanceSettleMode.getSid());
        int row = conAdvanceSettleModeMapper.updateAllById(conAdvanceSettleMode);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAdvanceSettleMode.getSid(), BusinessType.CHANGE.getValue(), response, conAdvanceSettleMode, TITLE);
        }
        return row;
    }

    /**
     * 批量删除预收款/预付款付款方式
     *
     * @param sids 需要删除的预收款/预付款付款方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConAdvanceSettleModeByIds(List<Long> sids) {
        return conAdvanceSettleModeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conAdvanceSettleMode
     * @return
     */
    @Override
    public int changeStatus(ConAdvanceSettleMode conAdvanceSettleMode) {
        int row = 0;
        Long[] sids = conAdvanceSettleMode.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAdvanceSettleMode.setSid(id);
                row = conAdvanceSettleModeMapper.updateById(conAdvanceSettleMode);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conAdvanceSettleMode.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conAdvanceSettleMode.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conAdvanceSettleMode
     * @return
     */
    @Override
    public int check(ConAdvanceSettleMode conAdvanceSettleMode) {
        int row = 0;
        Long[] sids = conAdvanceSettleMode.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAdvanceSettleMode.setSid(id);
                row = conAdvanceSettleModeMapper.updateById(conAdvanceSettleMode);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conAdvanceSettleMode.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConAdvanceSettleMode> getConAdvanceSettleModeList() {
        return conAdvanceSettleModeMapper.getConAdvanceSettleModeList();
    }
}
