package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManProductDefectAttach;
import com.platform.ems.domain.dto.request.ManProductDefectRequest;
import com.platform.ems.mapper.ManProductDefectAttachMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProductDefectMapper;
import com.platform.ems.domain.ManProductDefect;
import com.platform.ems.service.IManProductDefectService;

/**
 * 生产产品缺陷登记Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-08-04
 */
@Service
@SuppressWarnings("all")
public class ManProductDefectServiceImpl extends ServiceImpl<ManProductDefectMapper, ManProductDefect> implements IManProductDefectService {
    @Autowired
    private ManProductDefectMapper manProductDefectMapper;

    @Autowired
    private ManProductDefectAttachMapper manProductDefectAttachMapper;

    private static final String TITLE = "生产产品缺陷登记";

    /**
     * 查询生产产品缺陷登记
     *
     * @param productDefectSid 生产产品缺陷登记ID
     * @return 生产产品缺陷登记
     */
    @Override
    public ManProductDefect selectManProductDefectById(Long productDefectSid) {
        ManProductDefect manProductDefect = manProductDefectMapper.selectManProductDefectById(productDefectSid);
        if (ObjectUtil.isNotEmpty(manProductDefect)) {
            ManProductDefectAttach manProductDefectAttach = new ManProductDefectAttach();
            manProductDefectAttach.setProductDefectSid(productDefectSid);
            List<ManProductDefectAttach> list = manProductDefectAttachMapper.selectManProductDefectAttachList(manProductDefectAttach);
            manProductDefect.setManProductDefectAttachList(list);
        }
        MongodbUtil.find(manProductDefect);
        return manProductDefect;
    }

    /**
     * 查询生产产品缺陷登记列表
     *
     * @param manProductDefect 生产产品缺陷登记
     * @return 生产产品缺陷登记
     */
    @Override
    public List<ManProductDefect> selectManProductDefectList(ManProductDefect manProductDefect) {
        return manProductDefectMapper.selectManProductDefectList(manProductDefect);
    }
    //设置值
    @Override
    public int updateStatus(ManProductDefectRequest request){
        return manProductDefectMapper.updateStatus(request);
    }

    /**
     * 新增生产产品缺陷登记
     * 需要注意编码重复校验
     *
     * @param manProductDefect 生产产品缺陷登记
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProductDefect(ManProductDefect manProductDefect) {
        manProductDefect.setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setConfirmDate(new Date());
        int row = manProductDefectMapper.insert(manProductDefect);
        setConfirm(manProductDefect);
        if (row > 0) {
            List<ManProductDefectAttach> list = manProductDefect.getManProductDefectAttachList();
            if (ObjectUtil.isNotEmpty(list)) {
                list.forEach((val) -> {
                    val.setProductDefectSid(manProductDefect.getProductDefectSid())
                            .setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                });
                int insertCount = manProductDefectAttachMapper.inserts(list);
                if (insertCount != list.size()) {
                    throw new CustomException("系统异常，请联系管理员!(新增附加异常)");
                }
            }

            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProductDefect(), manProductDefect);
            MongodbDeal.insert(manProductDefect.getProductDefectSid(), manProductDefect.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改生产产品缺陷登记
     *
     * @param manProductDefect 生产产品缺陷登记
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProductDefect(ManProductDefect manProductDefect) {
        ManProductDefect original = manProductDefectMapper.selectManProductDefectById(manProductDefect.getProductDefectSid());
        int row = manProductDefectMapper.updateById(manProductDefect);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProductDefect);
            MongodbDeal.update(manProductDefect.getProductDefectSid(), original.getHandleStatus(), manProductDefect.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }
    public void setConfirm(ManProductDefect manProductDefect){
        if(ConstantsEms.CHECK_STATUS.equals(manProductDefect.getHandleStatus())){
            manProductDefect.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                    .setConfirmDate(new Date());
        }
    }

    /**
     * 变更生产产品缺陷登记
     *
     * @param manProductDefect 生产产品缺陷登记
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProductDefect(ManProductDefect manProductDefect) {
        ManProductDefect response = manProductDefectMapper.selectManProductDefectById(manProductDefect.getProductDefectSid());
        setConfirm(manProductDefect);
        int row = manProductDefectMapper.updateAllById(manProductDefect);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manProductDefect.getProductDefectSid(), BusinessType.CHANGE.getValue(), response, manProductDefect, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产产品缺陷登记
     *
     * @param productDefectSids 需要删除的生产产品缺陷登记ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProductDefectByIds(List<Long> productDefectSids) {
        List<ManProductDefect> list = manProductDefectMapper.selectList(new QueryWrapper<ManProductDefect>()
                .lambda().in(ManProductDefect::getProductDefectSid, productDefectSids));
        int row = manProductDefectMapper.deleteBatchIds(productDefectSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProductDefect());
                MongodbUtil.insertUserLog(o.getProductDefectSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param manProductDefect
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ManProductDefect manProductDefect) {
        int row = 0;
//        Long[] sids = manProductDefect.getProductDefectSidList();
//        if (sids != null && sids.length > 0) {
//            row = manProductDefectMapper.update(null, new UpdateWrapper<ManProductDefect>().lambda().set(ManProductDefect::getStatus, manProductDefect.getStatus())
//                    .in(ManProductDefect::getProductDefectSid, sids));
//            for (Long id : sids) {
//                manProductDefect.setProductDefectSid(id);
//                row = manProductDefectMapper.updateById(manProductDefect);
//                if (row == 0) {
//                    throw new CustomException(id + "更改状态失败,请联系管理员");
//                }
//                //插入日志
//                MongodbDeal.status(manProductDefect.getProductDefectSid(), "1", null, TITLE, null);
//            }
//        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manProductDefect
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManProductDefect manProductDefect) {
        int row = 0;
        Long[] sids = manProductDefect.getProductDefectSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ManProductDefect> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ManProductDefect::getProductDefectSid, sids);
            updateWrapper.set(ManProductDefect::getHandleStatus, manProductDefect.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manProductDefect.getHandleStatus())) {
                updateWrapper.set(ManProductDefect::getConfirmDate, new Date());
                updateWrapper.set(ManProductDefect::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = manProductDefectMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, manProductDefect.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
