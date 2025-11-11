package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.common.constant.ConstantsMsg;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ConTaskTemplateCompare;
import com.platform.ems.mapper.ConTaskTemplateCompareMapper;
import com.platform.ems.service.IConTaskTemplateCompareService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务模版对照关系ControllerService业务层处理
 *
 * @author platform
 * @date 2023-11-03
 */
@Service
@SuppressWarnings("all" )
public class ConTaskTemplateCompareServiceImpl extends ServiceImpl<ConTaskTemplateCompareMapper, ConTaskTemplateCompare> implements IConTaskTemplateCompareService {
    @Autowired
    private ConTaskTemplateCompareMapper conTaskTemplateCompareMapper;

    private static final String TITLE = "任务模版对照关系Controller" ;

    /**
     * 查询任务模版对照关系Controller
     *
     * @param taskTemplateCompareSid 任务模版对照关系ControllerID
     * @return 任务模版对照关系Controller
     */
    @Override
    public ConTaskTemplateCompare selectConTaskTemplateCompareById(Long taskTemplateCompareSid) {
        ConTaskTemplateCompare conTaskTemplateCompare =conTaskTemplateCompareMapper.selectConTaskTemplateCompareById(taskTemplateCompareSid);
        MongodbUtil.find(conTaskTemplateCompare);
        return conTaskTemplateCompare;
    }

    /**
     * 查询任务模版对照关系Controller列表
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 任务模版对照关系Controller
     */
    @Override
    public List<ConTaskTemplateCompare> selectConTaskTemplateCompareList(ConTaskTemplateCompare conTaskTemplateCompare) {
        return conTaskTemplateCompareMapper.selectConTaskTemplateCompareList(conTaskTemplateCompare);
    }

    /**
     * 新增任务模版对照关系Controller
     * 需要注意编码重复校验
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConTaskTemplateCompare(ConTaskTemplateCompare conTaskTemplateCompare) {

        // 判断“项目类型+品牌+经营模式+加盟模式+线下门店定位类型”组合是否已存在对照关系
        LambdaQueryWrapper<ConTaskTemplateCompare> queryWrapper = new LambdaQueryWrapper<>();
        if (conTaskTemplateCompare.getProjectType() == null){
            queryWrapper.isNull(ConTaskTemplateCompare::getProjectType);
        }else {
            queryWrapper.eq(ConTaskTemplateCompare::getProjectType, conTaskTemplateCompare.getProjectType());
        }
        if (conTaskTemplateCompare.getBrand() == null){
            queryWrapper.isNull(ConTaskTemplateCompare::getBrand);
        }else {
            queryWrapper.eq(ConTaskTemplateCompare::getBrand, conTaskTemplateCompare.getBrand());
        }
        if (conTaskTemplateCompare.getOperateMode() == null){
            queryWrapper.isNull(ConTaskTemplateCompare::getOperateMode);
        }else{
            queryWrapper.eq(ConTaskTemplateCompare::getOperateMode, conTaskTemplateCompare.getOperateMode());
        }
        if (conTaskTemplateCompare.getJoinMode() == null){
            queryWrapper.isNull(ConTaskTemplateCompare::getJoinMode);
        }else{
            queryWrapper.eq(ConTaskTemplateCompare::getJoinMode, conTaskTemplateCompare.getJoinMode());
        }
        if (conTaskTemplateCompare.getStoreCategory() == null){
            queryWrapper.isNull(ConTaskTemplateCompare::getStoreCategory);
        }else {
            queryWrapper.eq(ConTaskTemplateCompare::getStoreCategory, conTaskTemplateCompare.getStoreCategory());
        }
        List<ConTaskTemplateCompare> compareList = conTaskTemplateCompareMapper.selectList(queryWrapper);

        if(CollectionUtil.isNotEmpty(compareList)){
            throw new BaseException(ConstantsMsg.CON_TASK_TEMPLATE_COMPARE_IS_EXIST);
        }

        // 设置处理状态创建人创建日期
        conTaskTemplateCompare.setHandleStatus(ConstantsEms.CHECK_STATUS);
        conTaskTemplateCompare.setCreateDate(new Date());
        conTaskTemplateCompare.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());

        int row = conTaskTemplateCompareMapper.insert(conTaskTemplateCompare);
        if (row > 0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConTaskTemplateCompare(), conTaskTemplateCompare);
            MongodbDeal.insert(conTaskTemplateCompare.getTaskTemplateCompareSid(), conTaskTemplateCompare.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改任务模版对照关系Controller
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConTaskTemplateCompare(ConTaskTemplateCompare conTaskTemplateCompare) {
        ConTaskTemplateCompare original = conTaskTemplateCompareMapper.selectConTaskTemplateCompareById(conTaskTemplateCompare.getTaskTemplateCompareSid());
        // 写入确认人
//        if (ConstantsEms.CHECK_STATUS.equals(conTaskTemplateCompare.getHandleStatus())) {
//            conTaskTemplateCompare.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
//        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, conTaskTemplateCompare);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conTaskTemplateCompare.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conTaskTemplateCompareMapper.updateAllById(conTaskTemplateCompare);
        if (row > 0){
            //插入日志
            MongodbDeal.update(conTaskTemplateCompare.getTaskTemplateCompareSid(), original.getHandleStatus(),
                    conTaskTemplateCompare.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更任务模版对照关系Controller
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConTaskTemplateCompare(ConTaskTemplateCompare conTaskTemplateCompare) {
        ConTaskTemplateCompare response = conTaskTemplateCompareMapper.selectConTaskTemplateCompareById(conTaskTemplateCompare.getTaskTemplateCompareSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, conTaskTemplateCompare);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conTaskTemplateCompare.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conTaskTemplateCompareMapper.updateAllById(conTaskTemplateCompare);
        if (row > 0){
            //插入日志
            MongodbUtil.insertUserLog(conTaskTemplateCompare.getTaskTemplateCompareSid(), BusinessType.CHANGE.getValue(), response, conTaskTemplateCompare, TITLE);
        }
        return row;
    }

    /**
     * 批量删除任务模版对照关系Controller
     *
     * @param taskTemplateCompareSids 需要删除的任务模版对照关系ControllerID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConTaskTemplateCompareByIds(List<Long> taskTemplateCompareSids) {
        List<ConTaskTemplateCompare> list = conTaskTemplateCompareMapper.selectList(new QueryWrapper<ConTaskTemplateCompare>()
                .lambda().in(ConTaskTemplateCompare::getTaskTemplateCompareSid, taskTemplateCompareSids));
        int row = conTaskTemplateCompareMapper.deleteBatchIds(taskTemplateCompareSids);
        if (row > 0){
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConTaskTemplateCompare());
                MongodbUtil.insertUserLog(o.getTaskTemplateCompareSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     * @param conTaskTemplateCompare
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConTaskTemplateCompare conTaskTemplateCompare) {
        int row = 0;
        Long[] sids =conTaskTemplateCompare.getTaskTemplateCompareSidList();
        if (sids != null && sids.length > 0) {
            row = conTaskTemplateCompareMapper.update(null, new UpdateWrapper<ConTaskTemplateCompare>().lambda()
//                    .set(ConTaskTemplateCompare::getStatus,conTaskTemplateCompare.getStatus() )
                    .in(ConTaskTemplateCompare::getTaskTemplateCompareSid, sids));
            if (row == 0) {
                throw new CheckedException("更改状态失败,请联系管理员" );
            }
            for (Long id : sids) {
                //插入日志
//                UserOperUtil.status(id, conTaskTemplateCompare.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     *更改确认状态
     * @param conTaskTemplateCompare
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConTaskTemplateCompare conTaskTemplateCompare) {
        Long[] sids =conTaskTemplateCompare.getTaskTemplateCompareSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<ConTaskTemplateCompare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ConTaskTemplateCompare::getTaskTemplateCompareSid, sids);
        updateWrapper.set(ConTaskTemplateCompare::getHandleStatus, conTaskTemplateCompare.getHandleStatus());
//        if (ConstantsEms.CHECK_STATUS.equals(conTaskTemplateCompare.getHandleStatus())) {
//            updateWrapper.set(ConTaskTemplateCompare::getConfirmDate, new Date());
//            updateWrapper.set(ConTaskTemplateCompare::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
//        }
        int row = conTaskTemplateCompareMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, conTaskTemplateCompare.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

}
