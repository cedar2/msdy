package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBuTypeManufactureOutsourceSettle;
import com.platform.ems.plug.mapper.ConBuTypeManufactureOutsourceSettleMapper;
import com.platform.ems.plug.service.IConBuTypeManufactureOutsourceSettleService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_外发加工费结算单Service业务层处理
 *
 * @author c
 * @date 2021-11-25
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeManufactureOutsourceSettleServiceImpl extends ServiceImpl<ConBuTypeManufactureOutsourceSettleMapper, ConBuTypeManufactureOutsourceSettle> implements IConBuTypeManufactureOutsourceSettleService {
    @Autowired
    private ConBuTypeManufactureOutsourceSettleMapper conBuTypeOutsourceSettleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "业务类型_外发加工费结算单";

    /**
     * 查询业务类型_外发加工费结算单
     *
     * @param sid 业务类型_外发加工费结算单ID
     * @return 业务类型_外发加工费结算单
     */
    @Override
    public ConBuTypeManufactureOutsourceSettle selectConBuTypeManOutsourceSettleById(Long sid) {
        ConBuTypeManufactureOutsourceSettle outsourceSettle = conBuTypeOutsourceSettleMapper.selectConBuTypeManOutsourceSettleById(sid);
        MongodbUtil.find(outsourceSettle);
        return outsourceSettle;
    }

    /**
     * 查询业务类型_外发加工费结算单列表
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 业务类型_外发加工费结算单
     */
    @Override
    public List<ConBuTypeManufactureOutsourceSettle> selectConBuTypeManOutsourceSettleList(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return conBuTypeOutsourceSettleMapper.selectConBuTypeManOutsourceSettleList(outsourceSettle);
    }

    /**
     * 新增业务类型_外发加工费结算单
     * 需要注意编码重复校验
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeManOutsourceSettle(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        // 编码唯一性校验
        judgeCode(outsourceSettle);
        // 名称唯一性校验
        judgeName(outsourceSettle);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(outsourceSettle.getHandleStatus())) {
            outsourceSettle.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conBuTypeOutsourceSettleMapper.insert(outsourceSettle);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(outsourceSettle.getSid(), outsourceSettle.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 编码唯一性校验
     * @param settle
     */
    private void judgeCode(ConBuTypeManufactureOutsourceSettle settle) {
        if (StrUtil.isNotBlank(settle.getCode())) {
            QueryWrapper<ConBuTypeManufactureOutsourceSettle> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConBuTypeManufactureOutsourceSettle::getCode, settle.getCode());
            if (settle.getSid() != null) {
                queryWrapper.lambda().ne(ConBuTypeManufactureOutsourceSettle::getSid, settle.getSid());
            }
            List<ConBuTypeManufactureOutsourceSettle> list = conBuTypeOutsourceSettleMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("业务类型编码已存在，请核实！");
            }
        }
    }

    /**
     * 编码唯一性校验
     * @param settle
     */
    private void judgeName(ConBuTypeManufactureOutsourceSettle settle) {
        if (StrUtil.isNotBlank(settle.getName())) {
            QueryWrapper<ConBuTypeManufactureOutsourceSettle> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConBuTypeManufactureOutsourceSettle::getName, settle.getName());
            if (settle.getSid() != null) {
                queryWrapper.lambda().ne(ConBuTypeManufactureOutsourceSettle::getSid, settle.getSid());
            }
            List<ConBuTypeManufactureOutsourceSettle> list = conBuTypeOutsourceSettleMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("业务类型名称已存在，请核实！");
            }
        }
    }

    /**
     * 修改业务类型_外发加工费结算单
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeManOutsourceSettle(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        ConBuTypeManufactureOutsourceSettle response = conBuTypeOutsourceSettleMapper.selectConBuTypeManOutsourceSettleById(outsourceSettle.getSid());
        // 校验编号不能重复
        if (StrUtil.isNotBlank(outsourceSettle.getCode()) && !outsourceSettle.getCode().equals(response.getCode())) {
            judgeCode(outsourceSettle);
        }
        // 校验名称不能重复
        if (StrUtil.isNotBlank(outsourceSettle.getName()) && !outsourceSettle.getName().equals(response.getName())) {
            this.judgeName(outsourceSettle);
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(outsourceSettle.getHandleStatus())) {
            outsourceSettle.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, outsourceSettle);
        if (CollectionUtil.isNotEmpty(msgList)) {
            outsourceSettle.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conBuTypeOutsourceSettleMapper.updateAllById(outsourceSettle);
        if (row > 0) {
            //插入日志
            MongodbDeal.update(outsourceSettle.getSid(), response.getHandleStatus(), outsourceSettle.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更业务类型_外发加工费结算单
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeManOutsourceSettle(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        ConBuTypeManufactureOutsourceSettle response = conBuTypeOutsourceSettleMapper.selectConBuTypeManOutsourceSettleById(outsourceSettle.getSid());
        // 校验编号不能重复
        if (StrUtil.isNotBlank(outsourceSettle.getCode()) && !outsourceSettle.getCode().equals(response.getCode())) {
            judgeCode(outsourceSettle);
        }
        // 校验名称不能重复
        if (StrUtil.isNotBlank(outsourceSettle.getName()) && !outsourceSettle.getName().equals(response.getName())) {
            this.judgeName(outsourceSettle);
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, outsourceSettle);
        if (CollectionUtil.isNotEmpty(msgList)) {
            outsourceSettle.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conBuTypeOutsourceSettleMapper.updateAllById(outsourceSettle);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(outsourceSettle.getSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_外发加工费结算单
     *
     * @param sids 需要删除的业务类型_外发加工费结算单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeManOutsourceSettleByIds(List<Long> sids) {
        return conBuTypeOutsourceSettleMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param outsourceSettle
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        int row = 0;
        Long[] sids = outsourceSettle.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeOutsourceSettleMapper.update(null, new UpdateWrapper<ConBuTypeManufactureOutsourceSettle>().lambda()
                    .set(ConBuTypeManufactureOutsourceSettle::getStatus, outsourceSettle.getStatus())
                    .in(ConBuTypeManufactureOutsourceSettle::getSid, sids));
            for (Long id : sids) {
                int success = 0;
                outsourceSettle.setSid(id);
                success = conBuTypeOutsourceSettleMapper.updateById(outsourceSettle);
                if (success > 0) {
                    //插入日志
                    MongodbDeal.status(outsourceSettle.getSid(), outsourceSettle.getStatus(), null, TITLE, null);
                }
                row = row + success;
            }
        }
        return row;
    }

    /**
     * 批量确认
     *
     * @param outsourceSettle
     * @return
     */
    @Override
    public int check(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        int row = 0;
        Long[] sids = outsourceSettle.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeOutsourceSettleMapper.update(null, new UpdateWrapper<ConBuTypeManufactureOutsourceSettle>().lambda()
                    .set(ConBuTypeManufactureOutsourceSettle::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeManufactureOutsourceSettle::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 下拉框查询
     *
     * @param outsourceSettle
     * @return
     */
    @Override
    public List<ConBuTypeManufactureOutsourceSettle> getOutsourceSettleList(ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return conBuTypeOutsourceSettleMapper.getOutsourceSettleList(outsourceSettle);
    }
}
