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
import com.platform.ems.plug.domain.ConAccountCategoryReceivableBill;
import com.platform.ems.plug.mapper.ConAccountCategoryReceivableBillMapper;
import com.platform.ems.plug.service.IConAccountCategoryReceivableBillService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 款项类别_收款单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConAccountCategoryReceivableBillServiceImpl extends ServiceImpl<ConAccountCategoryReceivableBillMapper, ConAccountCategoryReceivableBill> implements IConAccountCategoryReceivableBillService {
    @Autowired
    private ConAccountCategoryReceivableBillMapper conAccountCategoryReceivableBillMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "款项类别_收款单";

    /**
     * 查询款项类别_收款单
     *
     * @param sid 款项类别_收款单ID
     * @return 款项类别_收款单
     */
    @Override
    public ConAccountCategoryReceivableBill selectConAccountCategoryReceivableBillById(Long sid) {
        ConAccountCategoryReceivableBill conAccountCategoryReceivableBill = conAccountCategoryReceivableBillMapper.selectConAccountCategoryReceivableBillById(sid);
        MongodbUtil.find(conAccountCategoryReceivableBill);
        return conAccountCategoryReceivableBill;
    }

    /**
     * 查询款项类别_收款单列表
     *
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 款项类别_收款单
     */
    @Override
    public List<ConAccountCategoryReceivableBill> selectConAccountCategoryReceivableBillList(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        return conAccountCategoryReceivableBillMapper.selectConAccountCategoryReceivableBillList(conAccountCategoryReceivableBill);
    }

    /**
     * 新增款项类别_收款单
     * 需要注意编码重复校验
     *
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConAccountCategoryReceivableBill(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        List<ConAccountCategoryReceivableBill> codeList =
                conAccountCategoryReceivableBillMapper.selectList(new QueryWrapper<ConAccountCategoryReceivableBill>().lambda()
                        .eq(ConAccountCategoryReceivableBill::getCode, conAccountCategoryReceivableBill.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConAccountCategoryReceivableBill> nameList =
                conAccountCategoryReceivableBillMapper.selectList(new QueryWrapper<ConAccountCategoryReceivableBill>().lambda()
                        .eq(ConAccountCategoryReceivableBill::getName, conAccountCategoryReceivableBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conAccountCategoryReceivableBillMapper.insert(conAccountCategoryReceivableBill);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conAccountCategoryReceivableBill.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改款项类别_收款单
     *
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConAccountCategoryReceivableBill(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        ConAccountCategoryReceivableBill response = conAccountCategoryReceivableBillMapper.selectConAccountCategoryReceivableBillById(conAccountCategoryReceivableBill.getSid());
        int row = conAccountCategoryReceivableBillMapper.updateById(conAccountCategoryReceivableBill);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAccountCategoryReceivableBill.getSid(), BusinessType.UPDATE.getValue(), response, conAccountCategoryReceivableBill, TITLE);
        }
        return row;
    }

    /**
     * 变更款项类别_收款单
     *
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConAccountCategoryReceivableBill(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        List<ConAccountCategoryReceivableBill> nameList =
                conAccountCategoryReceivableBillMapper.selectList(new QueryWrapper<ConAccountCategoryReceivableBill>().lambda()
                        .eq(ConAccountCategoryReceivableBill::getName, conAccountCategoryReceivableBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conAccountCategoryReceivableBill.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conAccountCategoryReceivableBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConAccountCategoryReceivableBill response = conAccountCategoryReceivableBillMapper.selectConAccountCategoryReceivableBillById(conAccountCategoryReceivableBill.getSid());
        int row = conAccountCategoryReceivableBillMapper.updateAllById(conAccountCategoryReceivableBill);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAccountCategoryReceivableBill.getSid(), BusinessType.CHANGE.getValue(), response, conAccountCategoryReceivableBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除款项类别_收款单
     *
     * @param sids 需要删除的款项类别_收款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConAccountCategoryReceivableBillByIds(List<Long> sids) {
        return conAccountCategoryReceivableBillMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conAccountCategoryReceivableBill
     * @return
     */
    @Override
    public int changeStatus(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        int row = 0;
        Long[] sids = conAccountCategoryReceivableBill.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAccountCategoryReceivableBill.setSid(id);
                row = conAccountCategoryReceivableBillMapper.updateById(conAccountCategoryReceivableBill);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conAccountCategoryReceivableBill.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conAccountCategoryReceivableBill.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conAccountCategoryReceivableBill
     * @return
     */
    @Override
    public int check(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        int row = 0;
        Long[] sids = conAccountCategoryReceivableBill.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAccountCategoryReceivableBill.setSid(id);
                row = conAccountCategoryReceivableBillMapper.updateById(conAccountCategoryReceivableBill);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conAccountCategoryReceivableBill.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
