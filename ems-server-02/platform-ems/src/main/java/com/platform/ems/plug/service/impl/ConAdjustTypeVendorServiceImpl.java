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
import com.platform.ems.plug.domain.ConAdjustTypeVendor;
import com.platform.ems.plug.mapper.ConAdjustTypeVendorMapper;
import com.platform.ems.plug.service.IConAdjustTypeVendorService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 调账类型_供应商Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConAdjustTypeVendorServiceImpl extends ServiceImpl<ConAdjustTypeVendorMapper, ConAdjustTypeVendor> implements IConAdjustTypeVendorService {
    @Autowired
    private ConAdjustTypeVendorMapper conAdjustTypeVendorMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "调账类型_供应商";

    /**
     * 查询调账类型_供应商
     *
     * @param sid 调账类型_供应商ID
     * @return 调账类型_供应商
     */
    @Override
    public ConAdjustTypeVendor selectConAdjustTypeVendorById(Long sid) {
        ConAdjustTypeVendor conAdjustTypeVendor = conAdjustTypeVendorMapper.selectConAdjustTypeVendorById(sid);
        MongodbUtil.find(conAdjustTypeVendor);
        return conAdjustTypeVendor;
    }

    /**
     * 查询调账类型_供应商列表
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 调账类型_供应商
     */
    @Override
    public List<ConAdjustTypeVendor> selectConAdjustTypeVendorList(ConAdjustTypeVendor conAdjustTypeVendor) {
        return conAdjustTypeVendorMapper.selectConAdjustTypeVendorList(conAdjustTypeVendor);
    }

    /**
     * 新增调账类型_供应商
     * 需要注意编码重复校验
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConAdjustTypeVendor(ConAdjustTypeVendor conAdjustTypeVendor) {
        List<ConAdjustTypeVendor> codeList = conAdjustTypeVendorMapper.selectList(new QueryWrapper<ConAdjustTypeVendor>().lambda()
                .eq(ConAdjustTypeVendor::getCode, conAdjustTypeVendor.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConAdjustTypeVendor> nameList = conAdjustTypeVendorMapper.selectList(new QueryWrapper<ConAdjustTypeVendor>().lambda()
                .eq(ConAdjustTypeVendor::getName, conAdjustTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conAdjustTypeVendorMapper.insert(conAdjustTypeVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conAdjustTypeVendor.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改调账类型_供应商
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConAdjustTypeVendor(ConAdjustTypeVendor conAdjustTypeVendor) {
        ConAdjustTypeVendor response = conAdjustTypeVendorMapper.selectConAdjustTypeVendorById(conAdjustTypeVendor.getSid());
        int row = conAdjustTypeVendorMapper.updateById(conAdjustTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAdjustTypeVendor.getSid(), BusinessType.UPDATE.getValue(), response, conAdjustTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 变更调账类型_供应商
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConAdjustTypeVendor(ConAdjustTypeVendor conAdjustTypeVendor) {
        List<ConAdjustTypeVendor> nameList = conAdjustTypeVendorMapper.selectList(new QueryWrapper<ConAdjustTypeVendor>().lambda()
                .eq(ConAdjustTypeVendor::getName, conAdjustTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conAdjustTypeVendor.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conAdjustTypeVendor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConAdjustTypeVendor response = conAdjustTypeVendorMapper.selectConAdjustTypeVendorById(conAdjustTypeVendor.getSid());
        int row = conAdjustTypeVendorMapper.updateAllById(conAdjustTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAdjustTypeVendor.getSid(), BusinessType.CHANGE.getValue(), response, conAdjustTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除调账类型_供应商
     *
     * @param sids 需要删除的调账类型_供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConAdjustTypeVendorByIds(List<Long> sids) {
        return conAdjustTypeVendorMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conAdjustTypeVendor
     * @return
     */
    @Override
    public int changeStatus(ConAdjustTypeVendor conAdjustTypeVendor) {
        int row = 0;
        Long[] sids = conAdjustTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAdjustTypeVendor.setSid(id);
                row = conAdjustTypeVendorMapper.updateById(conAdjustTypeVendor);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conAdjustTypeVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conAdjustTypeVendor.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conAdjustTypeVendor
     * @return
     */
    @Override
    public int check(ConAdjustTypeVendor conAdjustTypeVendor) {
        int row = 0;
        Long[] sids = conAdjustTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAdjustTypeVendor.setSid(id);
                row = conAdjustTypeVendorMapper.updateById(conAdjustTypeVendor);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conAdjustTypeVendor.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 款项类别下拉框列表
     */
    @Override
    public List<ConAdjustTypeVendor> getConAdjustTypeVendorList() {
        return conAdjustTypeVendorMapper.getConAdjustTypeVendorList();
    }
}
