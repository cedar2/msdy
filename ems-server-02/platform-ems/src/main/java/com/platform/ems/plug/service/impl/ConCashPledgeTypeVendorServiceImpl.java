package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConCashPledgeTypeVendor;
import com.platform.ems.plug.mapper.ConCashPledgeTypeVendorMapper;
import com.platform.ems.plug.service.IConCashPledgeTypeVendorService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 押金类型_供应商Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConCashPledgeTypeVendorServiceImpl extends ServiceImpl<ConCashPledgeTypeVendorMapper, ConCashPledgeTypeVendor> implements IConCashPledgeTypeVendorService {
    @Autowired
    private ConCashPledgeTypeVendorMapper conCashPledgeTypeVendorMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "押金类型_供应商";

    /**
     * 查询押金类型_供应商
     *
     * @param sid 押金类型_供应商ID
     * @return 押金类型_供应商
     */
    @Override
    public ConCashPledgeTypeVendor selectConCashPledgeTypeVendorById(Long sid) {
        ConCashPledgeTypeVendor conCashPledgeTypeVendor = conCashPledgeTypeVendorMapper.selectConCashPledgeTypeVendorById(sid);
        MongodbUtil.find(conCashPledgeTypeVendor);
        return conCashPledgeTypeVendor;
    }

    /**
     * 查询押金类型_供应商列表
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 押金类型_供应商
     */
    @Override
    public List<ConCashPledgeTypeVendor> selectConCashPledgeTypeVendorList(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return conCashPledgeTypeVendorMapper.selectConCashPledgeTypeVendorList(conCashPledgeTypeVendor);
    }

    /**
     * 新增押金类型_供应商
     * 需要注意编码重复校验
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCashPledgeTypeVendor(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        List<ConCashPledgeTypeVendor> codeList = conCashPledgeTypeVendorMapper.selectList(new QueryWrapper<ConCashPledgeTypeVendor>().lambda()
                .eq(ConCashPledgeTypeVendor::getCode, conCashPledgeTypeVendor.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConCashPledgeTypeVendor> nameList = conCashPledgeTypeVendorMapper.selectList(new QueryWrapper<ConCashPledgeTypeVendor>().lambda()
                .eq(ConCashPledgeTypeVendor::getName, conCashPledgeTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conCashPledgeTypeVendor);
        int row = conCashPledgeTypeVendorMapper.insert(conCashPledgeTypeVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conCashPledgeTypeVendor.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConCashPledgeTypeVendor o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改押金类型_供应商
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCashPledgeTypeVendor(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        ConCashPledgeTypeVendor response = conCashPledgeTypeVendorMapper.selectConCashPledgeTypeVendorById(conCashPledgeTypeVendor.getSid());
        int row = conCashPledgeTypeVendorMapper.updateById(conCashPledgeTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conCashPledgeTypeVendor.getSid(), BusinessType.UPDATE.getValue(), response, conCashPledgeTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 变更押金类型_供应商
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCashPledgeTypeVendor(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        List<ConCashPledgeTypeVendor> nameList = conCashPledgeTypeVendorMapper.selectList(new QueryWrapper<ConCashPledgeTypeVendor>().lambda()
                .eq(ConCashPledgeTypeVendor::getName, conCashPledgeTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conCashPledgeTypeVendor.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conCashPledgeTypeVendor);
        conCashPledgeTypeVendor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConCashPledgeTypeVendor response = conCashPledgeTypeVendorMapper.selectConCashPledgeTypeVendorById(conCashPledgeTypeVendor.getSid());
        int row = conCashPledgeTypeVendorMapper.updateAllById(conCashPledgeTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conCashPledgeTypeVendor.getSid(), BusinessType.CHANGE.getValue(), response, conCashPledgeTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除押金类型_供应商
     *
     * @param sids 需要删除的押金类型_供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCashPledgeTypeVendorByIds(List<Long> sids) {
        return conCashPledgeTypeVendorMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conCashPledgeTypeVendor
     * @return
     */
    @Override
    public int changeStatus(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        int row = 0;
        Long[] sids = conCashPledgeTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            row = conCashPledgeTypeVendorMapper.update(null, new UpdateWrapper<ConCashPledgeTypeVendor>().lambda().set(ConCashPledgeTypeVendor::getStatus, conCashPledgeTypeVendor.getStatus())
                    .in(ConCashPledgeTypeVendor::getSid, sids));
            for (Long id : sids) {
                conCashPledgeTypeVendor.setSid(id);
                row = conCashPledgeTypeVendorMapper.updateById(conCashPledgeTypeVendor);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conCashPledgeTypeVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conCashPledgeTypeVendor.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conCashPledgeTypeVendor
     * @return
     */
    @Override
    public int check(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        int row = 0;
        Long[] sids = conCashPledgeTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            row = conCashPledgeTypeVendorMapper.update(null, new UpdateWrapper<ConCashPledgeTypeVendor>().lambda().set(ConCashPledgeTypeVendor::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConCashPledgeTypeVendor::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 押金类型_供应商下拉框列表
     */
    @Override
    public List<ConCashPledgeTypeVendor> getList(ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return conCashPledgeTypeVendorMapper.getList(conCashPledgeTypeVendor);
    }
}
