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
import com.platform.ems.plug.domain.ConDocTypeManufactureOutsourceSettle;
import com.platform.ems.plug.mapper.ConDocTypeManufactureOutsourceSettleMapper;
import com.platform.ems.plug.service.IConDocTypeManufactureOutsourceSettleService;
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
 * 单据类型_外发加工费结算单Service业务层处理
 *
 * @author c
 * @date 2021-11-25
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeManufactureOutsourceSettleServiceImpl extends ServiceImpl<ConDocTypeManufactureOutsourceSettleMapper, ConDocTypeManufactureOutsourceSettle> implements IConDocTypeManufactureOutsourceSettleService {
    @Autowired
    private ConDocTypeManufactureOutsourceSettleMapper conDocTypeOutsourceSettleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_外发加工费结算单";

    /**
     * 查询单据类型_外发加工费结算单
     *
     * @param sid 单据类型_外发加工费结算单ID
     * @return 单据类型_外发加工费结算单
     */
    @Override
    public ConDocTypeManufactureOutsourceSettle selectConDocTypeManOutsourceSettleById(Long sid) {
        ConDocTypeManufactureOutsourceSettle outsourceSettle =
                conDocTypeOutsourceSettleMapper.selectConDocTypeManOutsourceSettleById(sid);
        MongodbUtil.find(outsourceSettle);
        return outsourceSettle;
    }

    /**
     * 查询单据类型_外发加工费结算单列表
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 单据类型_外发加工费结算单
     */
    @Override
    public List<ConDocTypeManufactureOutsourceSettle> selectConDocTypeManOutsourceSettleList(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return conDocTypeOutsourceSettleMapper.selectConDocTypeManOutsourceSettleList(outsourceSettle);
    }

    /**
     * 新增单据类型_外发加工费结算单
     * 需要注意编码重复校验
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeManOutsourceSettle(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        // 编码唯一性校验
        judgeCode(outsourceSettle);
        // 名称唯一性校验
        judgeName(outsourceSettle);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(outsourceSettle.getHandleStatus())) {
            outsourceSettle.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conDocTypeOutsourceSettleMapper.insert(outsourceSettle);
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
    private void judgeCode(ConDocTypeManufactureOutsourceSettle settle) {
        if (StrUtil.isNotBlank(settle.getCode())) {
            QueryWrapper<ConDocTypeManufactureOutsourceSettle> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConDocTypeManufactureOutsourceSettle::getCode, settle.getCode());
            if (settle.getSid() != null) {
                queryWrapper.lambda().ne(ConDocTypeManufactureOutsourceSettle::getSid, settle.getSid());
            }
            List<ConDocTypeManufactureOutsourceSettle> list = conDocTypeOutsourceSettleMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("单据类型编码已存在，请核实！");
            }
        }
    }

    /**
     * 编码唯一性校验
     * @param settle
     */
    private void judgeName(ConDocTypeManufactureOutsourceSettle settle) {
        if (StrUtil.isNotBlank(settle.getName())) {
            QueryWrapper<ConDocTypeManufactureOutsourceSettle> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConDocTypeManufactureOutsourceSettle::getName, settle.getName());
            if (settle.getSid() != null) {
                queryWrapper.lambda().ne(ConDocTypeManufactureOutsourceSettle::getSid, settle.getSid());
            }
            List<ConDocTypeManufactureOutsourceSettle> list = conDocTypeOutsourceSettleMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("单据类型名称已存在，请核实！");
            }
        }
    }

    /**
     * 修改单据类型_外发加工费结算单
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeManOutsourceSettle(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        ConDocTypeManufactureOutsourceSettle response = conDocTypeOutsourceSettleMapper.selectConDocTypeManOutsourceSettleById(outsourceSettle.getSid());
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
        int row = conDocTypeOutsourceSettleMapper.updateAllById(outsourceSettle);
        if (row > 0) {
            //插入日志
            MongodbDeal.update(outsourceSettle.getSid(), response.getHandleStatus(), outsourceSettle.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更单据类型_外发加工费结算单
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeManOutsourceSettle(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        ConDocTypeManufactureOutsourceSettle response = conDocTypeOutsourceSettleMapper.selectConDocTypeManOutsourceSettleById(outsourceSettle.getSid());
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
        int row = conDocTypeOutsourceSettleMapper.updateAllById(outsourceSettle);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(outsourceSettle.getSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_外发加工费结算单
     *
     * @param sids 需要删除的单据类型_外发加工费结算单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeManOutsourceSettleByIds(List<Long> sids) {
        return conDocTypeOutsourceSettleMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param outsourceSettle
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        int row = 0;
        Long[] sids = outsourceSettle.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeOutsourceSettleMapper.update(null, new UpdateWrapper<ConDocTypeManufactureOutsourceSettle>().lambda()
                    .set(ConDocTypeManufactureOutsourceSettle::getStatus, outsourceSettle.getStatus())
                    .in(ConDocTypeManufactureOutsourceSettle::getSid, sids));
            for (Long id : sids) {
                int success = 0;
                outsourceSettle.setSid(id);
                success = conDocTypeOutsourceSettleMapper.updateById(outsourceSettle);
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
     * 更改确认状态
     *
     * @param outsourceSettle
     * @return
     */
    @Override
    public int check(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        int row = 0;
        Long[] sids = outsourceSettle.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeOutsourceSettleMapper.update(null, new UpdateWrapper<ConDocTypeManufactureOutsourceSettle>().lambda()
                    .set(ConDocTypeManufactureOutsourceSettle::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeManufactureOutsourceSettle::getSid, sids));
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
    public List<ConDocTypeManufactureOutsourceSettle> getDocOutsourceSettleList(ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return conDocTypeOutsourceSettleMapper.getDocOutsourceSettleList(outsourceSettle);
    }
}
