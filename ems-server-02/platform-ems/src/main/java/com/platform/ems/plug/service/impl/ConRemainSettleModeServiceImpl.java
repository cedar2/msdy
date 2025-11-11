package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConRemainSettleModeMapper;
import com.platform.ems.plug.domain.ConRemainSettleMode;
import com.platform.ems.plug.service.IConRemainSettleModeService;

/**
 * 尾款结算方式Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConRemainSettleModeServiceImpl extends ServiceImpl<ConRemainSettleModeMapper, ConRemainSettleMode> implements IConRemainSettleModeService {
    @Autowired
    private ConRemainSettleModeMapper conRemainSettleModeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "尾款结算方式";

    /**
     * 查询尾款结算方式
     *
     * @param sid 尾款结算方式ID
     * @return 尾款结算方式
     */
    @Override
    public ConRemainSettleMode selectConRemainSettleModeById(Long sid) {
        ConRemainSettleMode conRemainSettleMode = conRemainSettleModeMapper.selectConRemainSettleModeById(sid);
        MongodbUtil.find(conRemainSettleMode);
        return conRemainSettleMode;
    }

    /**
     * 查询尾款结算方式列表
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 尾款结算方式
     */
    @Override
    public List<ConRemainSettleMode> selectConRemainSettleModeList(ConRemainSettleMode conRemainSettleMode) {
        return conRemainSettleModeMapper.selectConRemainSettleModeList(conRemainSettleMode);
    }

    /**
     * 新增尾款结算方式
     * 需要注意编码重复校验
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConRemainSettleMode(ConRemainSettleMode conRemainSettleMode) {
        List<ConRemainSettleMode> codeList = conRemainSettleModeMapper.selectList(new QueryWrapper<ConRemainSettleMode>().lambda()
                .eq(ConRemainSettleMode::getCode, conRemainSettleMode.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConRemainSettleMode> nameList = conRemainSettleModeMapper.selectList(new QueryWrapper<ConRemainSettleMode>().lambda()
                .eq(ConRemainSettleMode::getName, conRemainSettleMode.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conRemainSettleModeMapper.insert(conRemainSettleMode);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conRemainSettleMode.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改尾款结算方式
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConRemainSettleMode(ConRemainSettleMode conRemainSettleMode) {
        ConRemainSettleMode response = conRemainSettleModeMapper.selectConRemainSettleModeById(conRemainSettleMode.getSid());
        int row = conRemainSettleModeMapper.updateById(conRemainSettleMode);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conRemainSettleMode.getSid(), BusinessType.UPDATE.getValue(), response, conRemainSettleMode, TITLE);
        }
        return row;
    }

    /**
     * 变更尾款结算方式
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConRemainSettleMode(ConRemainSettleMode conRemainSettleMode) {
        ConRemainSettleMode response = conRemainSettleModeMapper.selectConRemainSettleModeById(conRemainSettleMode.getSid());
        List<ConRemainSettleMode> nameList = conRemainSettleModeMapper.selectList(new QueryWrapper<ConRemainSettleMode>().lambda()
                .eq(ConRemainSettleMode::getName, conRemainSettleMode.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conRemainSettleMode.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conRemainSettleMode.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conRemainSettleModeMapper.updateAllById(conRemainSettleMode);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conRemainSettleMode.getSid(), BusinessType.CHANGE.getValue(), response, conRemainSettleMode, TITLE);
        }
        return row;
    }

    /**
     * 批量删除尾款结算方式
     *
     * @param sids 需要删除的尾款结算方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConRemainSettleModeByIds(List<Long> sids) {
        return conRemainSettleModeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conRemainSettleMode
     * @return
     */
    @Override
    public int changeStatus(ConRemainSettleMode conRemainSettleMode) {
        int row = 0;
        Long[] sids = conRemainSettleMode.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conRemainSettleMode.setSid(id);
                row = conRemainSettleModeMapper.updateById(conRemainSettleMode);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conRemainSettleMode.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conRemainSettleMode.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conRemainSettleMode
     * @return
     */
    @Override
    public int check(ConRemainSettleMode conRemainSettleMode) {
        int row = 0;
        Long[] sids = conRemainSettleMode.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conRemainSettleMode.setSid(id);
                row = conRemainSettleModeMapper.updateById(conRemainSettleMode);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conRemainSettleMode.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConRemainSettleMode> getConRemainSettleModeList() {
        return conRemainSettleModeMapper.getConRemainSettleModeList();
    }
}
