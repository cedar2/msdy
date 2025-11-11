package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.plug.mapper.ConPlantJixinliangEnterModeMapper;
import com.platform.common.core.domain.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.domain.ConPlantJixinliangEnterMode;
import com.platform.ems.plug.service.IConPlantJixinliangEnterModeService;

/**
 * 工厂计薪量录入方式Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-07-14
 */
@Service
@SuppressWarnings("all")
public class ConPlantJixinliangEnterModeServiceImpl extends ServiceImpl<ConPlantJixinliangEnterModeMapper, ConPlantJixinliangEnterMode> implements IConPlantJixinliangEnterModeService {
    @Autowired
    private ConPlantJixinliangEnterModeMapper conPlantJixinliangEnterModeMapper;

    private static final String TITLE = "工厂计薪量录入方式";

    /**
     * 查询工厂计薪量录入方式
     *
     * @param sid 工厂计薪量录入方式ID
     * @return 工厂计薪量录入方式
     */
    @Override
    public ConPlantJixinliangEnterMode selectConPlantJixinliangEnterModeById(Long sid) {
        ConPlantJixinliangEnterMode conPlantJixinliangEnterMode = conPlantJixinliangEnterModeMapper.selectConPlantJixinliangEnterModeById(sid);
        MongodbUtil.find(conPlantJixinliangEnterMode);
        return conPlantJixinliangEnterMode;
    }

    /**
     * 查询工厂计薪量录入方式列表
     *
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 工厂计薪量录入方式
     */
    @Override
    public List<ConPlantJixinliangEnterMode> selectConPlantJixinliangEnterModeList(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        return conPlantJixinliangEnterModeMapper.selectConPlantJixinliangEnterModeList(conPlantJixinliangEnterMode);
    }

    /**
     * 新增工厂计薪量录入方式
     * 需要注意编码重复校验
     *
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPlantJixinliangEnterMode(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
//        List<ConPlantJixinliangEnterMode> list = conPlantJixinliangEnterModeMapper.selectConPlantJixinliangEnterModeList(
//                new ConPlantJixinliangEnterMode().setProductPriceType(conPlantJixinliangEnterMode.getProductPriceType())
//                        .setJixinWangongType(conPlantJixinliangEnterMode.getJixinWangongType())
//                        .setPlantSid(conPlantJixinliangEnterMode.getPlantSid()));

        List<ConPlantJixinliangEnterMode> list = conPlantJixinliangEnterModeMapper
                .selectList(Wrappers.lambdaQuery(ConPlantJixinliangEnterMode.class)
                        .eq(ConPlantJixinliangEnterMode::getProductPriceType, conPlantJixinliangEnterMode.getProductPriceType())
                        .eq(ConPlantJixinliangEnterMode::getJixinWangongType, conPlantJixinliangEnterMode.getJixinWangongType())
                        .eq(ConPlantJixinliangEnterMode::getPlantSid, conPlantJixinliangEnterMode.getPlantSid()));

        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("该组合的配置管理已存在，请检查！");
        }
        LoginUser loginUser = ApiThreadLocalUtil.get();
        conPlantJixinliangEnterMode.setClientId(loginUser.getSysUser().getClientId())
                                   .setCreatorAccount(loginUser.getUsername())
                                   .setCreateDate(new Date())
                                   .setUpdaterAccount(loginUser.getUsername())
                                   .setUpdateDate(new Date());
        if (StrUtil.equals(conPlantJixinliangEnterMode.getHandleStatus() , "5")) {
            conPlantJixinliangEnterMode.setConfirmDate(new Date())
                                       .setConfirmerAccount(loginUser.getUsername());
        }
        int row = conPlantJixinliangEnterModeMapper.insert(conPlantJixinliangEnterMode);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConPlantJixinliangEnterMode(), conPlantJixinliangEnterMode);
            MongodbDeal.insert(conPlantJixinliangEnterMode.getSid(), conPlantJixinliangEnterMode.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改工厂计薪量录入方式
     *
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPlantJixinliangEnterMode(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        conPlantJixinliangEnterMode.setUpdaterAccount(loginUser.getUsername())
                                   .setUpdateDate(new Date());
        if (StrUtil.equals(conPlantJixinliangEnterMode.getHandleStatus() , "5")) {
            conPlantJixinliangEnterMode.setConfirmDate(new Date())
                    .setConfirmerAccount(loginUser.getUsername());
        }
        int row = conPlantJixinliangEnterModeMapper.updateById(conPlantJixinliangEnterMode);
        if (row > 0) {
            //插入日志
            ConPlantJixinliangEnterMode original = conPlantJixinliangEnterModeMapper.selectConPlantJixinliangEnterModeById(conPlantJixinliangEnterMode.getSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, conPlantJixinliangEnterMode);
            MongodbDeal.update(conPlantJixinliangEnterMode.getSid(), original.getHandleStatus(), conPlantJixinliangEnterMode.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更工厂计薪量录入方式
     *
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPlantJixinliangEnterMode(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        ConPlantJixinliangEnterMode response = conPlantJixinliangEnterModeMapper.selectConPlantJixinliangEnterModeById(conPlantJixinliangEnterMode.getSid());
        LoginUser loginUser = ApiThreadLocalUtil.get();
        conPlantJixinliangEnterMode.setUpdateDate(new Date()).setUpdaterAccount(loginUser.getUsername()).setClientId(loginUser.getSysUser().getClientId());
        int row = conPlantJixinliangEnterModeMapper.updateAllById(conPlantJixinliangEnterMode);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPlantJixinliangEnterMode.getSid(), BusinessType.CHANGE.getValue(), response, conPlantJixinliangEnterMode, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工厂计薪量录入方式
     *
     * @param sids 需要删除的工厂计薪量录入方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPlantJixinliangEnterModeByIds(List<Long> sids) {
        List<ConPlantJixinliangEnterMode> list = conPlantJixinliangEnterModeMapper.selectList(new QueryWrapper<ConPlantJixinliangEnterMode>()
                .lambda().in(ConPlantJixinliangEnterMode::getSid, sids));
        int row = conPlantJixinliangEnterModeMapper.deleteBatchIds(sids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConPlantJixinliangEnterMode());
                MongodbUtil.insertUserLog(o.getSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conPlantJixinliangEnterMode
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        int row = 0;
        Long[] sids = conPlantJixinliangEnterMode.getSidList();
        if (sids != null && sids.length > 0) {
            row = conPlantJixinliangEnterModeMapper.update(null, new UpdateWrapper<ConPlantJixinliangEnterMode>().lambda().set(ConPlantJixinliangEnterMode::getStatus, conPlantJixinliangEnterMode.getStatus())
                    .in(ConPlantJixinliangEnterMode::getSid, sids));
            for (Long id : sids) {
                conPlantJixinliangEnterMode.setSid(id);
                row = conPlantJixinliangEnterModeMapper.updateById(conPlantJixinliangEnterMode);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.status(conPlantJixinliangEnterMode.getSid(), conPlantJixinliangEnterMode.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param conPlantJixinliangEnterMode
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        int row = 0;
        Long[] sids = conPlantJixinliangEnterMode.getSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ConPlantJixinliangEnterMode> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ConPlantJixinliangEnterMode::getSid, sids);
            updateWrapper.set(ConPlantJixinliangEnterMode::getHandleStatus, conPlantJixinliangEnterMode.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(conPlantJixinliangEnterMode.getHandleStatus())) {
                updateWrapper.set(ConPlantJixinliangEnterMode::getConfirmDate, new Date());
                updateWrapper.set(ConPlantJixinliangEnterMode::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = conPlantJixinliangEnterModeMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, conPlantJixinliangEnterMode.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
