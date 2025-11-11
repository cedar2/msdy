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
import com.platform.ems.plug.domain.ConBuTypeVendorAccountAdjust;
import com.platform.ems.plug.mapper.ConBuTypeVendorAccountAdjustMapper;
import com.platform.ems.plug.service.IConBuTypeVendorAccountAdjustService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_供应商调账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeVendorAccountAdjustServiceImpl extends ServiceImpl<ConBuTypeVendorAccountAdjustMapper, ConBuTypeVendorAccountAdjust> implements IConBuTypeVendorAccountAdjustService {
    @Autowired
    private ConBuTypeVendorAccountAdjustMapper conBuTypeVendorAccountAdjustMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_供应商调账单";

    /**
     * 查询业务类型_供应商调账单
     *
     * @param sid 业务类型_供应商调账单ID
     * @return 业务类型_供应商调账单
     */
    @Override
    public ConBuTypeVendorAccountAdjust selectConBuTypeVendorAccountAdjustById(Long sid) {
        ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust = conBuTypeVendorAccountAdjustMapper.selectConBuTypeVendorAccountAdjustById(sid);
        MongodbUtil.find(conBuTypeVendorAccountAdjust);
        return conBuTypeVendorAccountAdjust;
    }

    /**
     * 查询业务类型_供应商调账单列表
     *
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 业务类型_供应商调账单
     */
    @Override
    public List<ConBuTypeVendorAccountAdjust> selectConBuTypeVendorAccountAdjustList(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust) {
        return conBuTypeVendorAccountAdjustMapper.selectConBuTypeVendorAccountAdjustList(conBuTypeVendorAccountAdjust);
    }

    /**
     * 新增业务类型_供应商调账单
     * 需要注意编码重复校验
     *
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeVendorAccountAdjust(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust) {
        List<ConBuTypeVendorAccountAdjust> codeList = conBuTypeVendorAccountAdjustMapper.selectList(new QueryWrapper<ConBuTypeVendorAccountAdjust>().lambda()
                .eq(ConBuTypeVendorAccountAdjust::getCode, conBuTypeVendorAccountAdjust.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeVendorAccountAdjust> nameList = conBuTypeVendorAccountAdjustMapper.selectList(new QueryWrapper<ConBuTypeVendorAccountAdjust>().lambda()
                .eq(ConBuTypeVendorAccountAdjust::getName, conBuTypeVendorAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBuTypeVendorAccountAdjustMapper.insert(conBuTypeVendorAccountAdjust);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeVendorAccountAdjust.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_供应商调账单
     *
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeVendorAccountAdjust(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust) {
        String name = conBuTypeVendorAccountAdjust.getName();
        ConBuTypeVendorAccountAdjust item = conBuTypeVendorAccountAdjustMapper.selectOne(new QueryWrapper<ConBuTypeVendorAccountAdjust>().lambda()
                .eq(ConBuTypeVendorAccountAdjust::getName, name)
        );
        if (item != null && !item.getSid().equals(conBuTypeVendorAccountAdjust.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConBuTypeVendorAccountAdjust response = conBuTypeVendorAccountAdjustMapper.selectConBuTypeVendorAccountAdjustById(conBuTypeVendorAccountAdjust.getSid());
        int row = conBuTypeVendorAccountAdjustMapper.updateById(conBuTypeVendorAccountAdjust);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorAccountAdjust.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeVendorAccountAdjust, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_供应商调账单
     *
     * @param conBuTypeVendorAccountAdjust 业务类型_供应商调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeVendorAccountAdjust(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust) {
        List<ConBuTypeVendorAccountAdjust> nameList = conBuTypeVendorAccountAdjustMapper.selectList(new QueryWrapper<ConBuTypeVendorAccountAdjust>().lambda()
                .eq(ConBuTypeVendorAccountAdjust::getName, conBuTypeVendorAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeVendorAccountAdjust.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeVendorAccountAdjust.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeVendorAccountAdjust response = conBuTypeVendorAccountAdjustMapper.selectConBuTypeVendorAccountAdjustById(conBuTypeVendorAccountAdjust.getSid());
        int row = conBuTypeVendorAccountAdjustMapper.updateAllById(conBuTypeVendorAccountAdjust);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorAccountAdjust.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeVendorAccountAdjust, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_供应商调账单
     *
     * @param sids 需要删除的业务类型_供应商调账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeVendorAccountAdjustByIds(List<Long> sids) {
        return conBuTypeVendorAccountAdjustMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorAccountAdjust
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust) {
        int row = 0;
        Long[] sids = conBuTypeVendorAccountAdjust.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBuTypeVendorAccountAdjust.setSid(id);
                row = conBuTypeVendorAccountAdjustMapper.updateById(conBuTypeVendorAccountAdjust);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeVendorAccountAdjust.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeVendorAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorAccountAdjust
     * @return
     */
    @Override
    public int check(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust) {
        int row = 0;
        Long[] sids = conBuTypeVendorAccountAdjust.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBuTypeVendorAccountAdjust.setSid(id);
                row = conBuTypeVendorAccountAdjustMapper.updateById(conBuTypeVendorAccountAdjust);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeVendorAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
