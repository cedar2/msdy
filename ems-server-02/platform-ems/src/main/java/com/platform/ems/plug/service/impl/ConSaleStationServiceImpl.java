package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.enums.HandleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.plug.mapper.ConSaleStationMapper;
import com.platform.ems.plug.domain.ConSaleStation;
import com.platform.ems.plug.service.IConSaleStationService;

/**
 * 销售站点Service业务层处理
 *
 * @author chenkw
 * @date 2023-01-02
 */
@Service
@SuppressWarnings("all")
public class ConSaleStationServiceImpl extends ServiceImpl<ConSaleStationMapper, ConSaleStation> implements IConSaleStationService {
    @Autowired
    private ConSaleStationMapper conSaleStationMapper;

    private static final String TITLE = "销售站点/网店";

    /**
     * 查询销售站点
     *
     * @param sid 销售站点ID
     * @return 销售站点
     */
    @Override
    public ConSaleStation selectConSaleStationById(Long sid) {
        ConSaleStation conSaleStation = conSaleStationMapper.selectConSaleStationById(sid);
        MongodbUtil.find(conSaleStation);
        return conSaleStation;
    }

    /**
     * 查询销售站点列表
     *
     * @param conSaleStation 销售站点
     * @return 销售站点
     */
    @Override
    public List<ConSaleStation> selectConSaleStationList(ConSaleStation conSaleStation) {
        return conSaleStationMapper.selectConSaleStationList(conSaleStation);
    }

    /**
     * 编码名称重复校验
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    private void judge(ConSaleStation conSaleStation) {
        QueryWrapper<ConSaleStation> queryWrapper = new QueryWrapper<>();
        // 编码
        queryWrapper.lambda().eq(ConSaleStation::getCode, conSaleStation.getCode());
        if (conSaleStation.getSid() != null) {
            queryWrapper.lambda().ne(ConSaleStation::getSid, conSaleStation.getSid());
        }
        if (CollectionUtil.isNotEmpty(conSaleStationMapper.selectList(queryWrapper))) {
            throw new BaseException("销售站点/网店编码已存在，请核实！");
        }
        queryWrapper = new QueryWrapper<>();
        // 名称
        queryWrapper.lambda().eq(ConSaleStation::getName, conSaleStation.getName());
        if (conSaleStation.getSid() != null) {
            queryWrapper.lambda().ne(ConSaleStation::getSid, conSaleStation.getSid());
        }
        if (CollectionUtil.isNotEmpty(conSaleStationMapper.selectList(queryWrapper))) {
            throw new BaseException("销售站点/网店名称已存在，请核实！");
        }
    }

    /**
     * 新增销售站点
     * 需要注意编码重复校验
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSaleStation(ConSaleStation conSaleStation) {
        // 校验编码和名称不能重复
        this.judge(conSaleStation);
        // 写入确认信息
        if (HandleStatus.CONFIRMED.getCode().equals(conSaleStation.getHandleStatus())) {
            conSaleStation.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conSaleStationMapper.insert(conSaleStation);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConSaleStation(), conSaleStation);
            MongodbDeal.insert(conSaleStation.getSid(), conSaleStation.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改销售站点
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSaleStation(ConSaleStation conSaleStation) {
        ConSaleStation original = conSaleStationMapper.selectConSaleStationById(conSaleStation.getSid());
        // 校验编码和名称不能重复
        this.judge(conSaleStation);
        // 写入确认信息
        if (HandleStatus.CONFIRMED.getCode().equals(conSaleStation.getHandleStatus())) {
            conSaleStation.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, conSaleStation);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conSaleStation.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = conSaleStationMapper.updateAllById(conSaleStation);
        if (row > 0) {
            //插入日志
            MongodbDeal.update(conSaleStation.getSid(), original.getHandleStatus(), conSaleStation.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更销售站点
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSaleStation(ConSaleStation conSaleStation) {
        ConSaleStation response = conSaleStationMapper.selectConSaleStationById(conSaleStation.getSid());
        // 校验编码和名称不能重复
        this.judge(conSaleStation);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, conSaleStation);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conSaleStation.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = conSaleStationMapper.updateAllById(conSaleStation);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleStation.getSid(), BusinessType.CHANGE.getValue(), response, conSaleStation, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售站点
     *
     * @param sids 需要删除的销售站点ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSaleStationByIds(List<Long> sids) {
        List<ConSaleStation> list = conSaleStationMapper.selectList(new QueryWrapper<ConSaleStation>()
                .lambda().in(ConSaleStation::getSid, sids));
        int row = conSaleStationMapper.deleteBatchIds(sids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConSaleStation());
                MongodbUtil.insertUserLog(o.getSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conSaleStation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConSaleStation conSaleStation) {
        int row = 0;
        Long[] sids = conSaleStation.getSidList();
        if (sids != null && sids.length > 0) {
            row = conSaleStationMapper.update(null, new UpdateWrapper<ConSaleStation>().lambda().set(ConSaleStation::getStatus, conSaleStation.getStatus())
                    .in(ConSaleStation::getSid, sids));
            if (row == 0) {
                throw new BaseException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                //插入日志
                MongodbDeal.status(id, conSaleStation.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param conSaleStation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConSaleStation conSaleStation) {
        int row = 0;
        Long[] sids = conSaleStation.getSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ConSaleStation> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ConSaleStation::getSid, sids);
            updateWrapper.set(ConSaleStation::getHandleStatus, conSaleStation.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(conSaleStation.getHandleStatus())) {
                updateWrapper.set(ConSaleStation::getConfirmDate, new Date());
                updateWrapper.set(ConSaleStation::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = conSaleStationMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, conSaleStation.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
