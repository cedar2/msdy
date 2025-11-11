package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProductDefectAttachMapper;
import com.platform.ems.domain.ManProductDefectAttach;
import com.platform.ems.service.IManProductDefectAttachService;

/**
 * 生产产品缺陷登记-附件Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-08-04
 */
@Service
@SuppressWarnings("all")
public class ManProductDefectAttachServiceImpl extends ServiceImpl<ManProductDefectAttachMapper, ManProductDefectAttach> implements IManProductDefectAttachService {
    @Autowired
    private ManProductDefectAttachMapper manProductDefectAttachMapper;

    private static final String TITLE = "生产产品缺陷登记-附件";

    /**
     * 查询生产产品缺陷登记-附件
     *
     * @param attachSid 生产产品缺陷登记-附件ID
     * @return 生产产品缺陷登记-附件
     */
    @Override
    public ManProductDefectAttach selectManProductDefectAttachById(Long attachSid) {
        ManProductDefectAttach manProductDefectAttach = manProductDefectAttachMapper.selectManProductDefectAttachById(attachSid);
        MongodbUtil.find(manProductDefectAttach);
        return manProductDefectAttach;
    }

    /**
     * 查询生产产品缺陷登记-附件列表
     *
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 生产产品缺陷登记-附件
     */
    @Override
    public List<ManProductDefectAttach> selectManProductDefectAttachList(ManProductDefectAttach manProductDefectAttach) {
        return manProductDefectAttachMapper.selectManProductDefectAttachList(manProductDefectAttach);
    }

    /**
     * 新增生产产品缺陷登记-附件
     * 需要注意编码重复校验
     *
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProductDefectAttach(ManProductDefectAttach manProductDefectAttach) {
        int row = manProductDefectAttachMapper.insert(manProductDefectAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProductDefectAttach(), manProductDefectAttach);
            MongodbDeal.insert(manProductDefectAttach.getAttachSid(), "5", msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改生产产品缺陷登记-附件
     *
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProductDefectAttach(ManProductDefectAttach manProductDefectAttach) {
        ManProductDefectAttach original = manProductDefectAttachMapper.selectManProductDefectAttachById(manProductDefectAttach.getAttachSid());
        int row = manProductDefectAttachMapper.updateById(manProductDefectAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProductDefectAttach);
            MongodbDeal.update(manProductDefectAttach.getAttachSid(), "5", "5", msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产产品缺陷登记-附件
     *
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProductDefectAttach(ManProductDefectAttach manProductDefectAttach) {
        ManProductDefectAttach response = manProductDefectAttachMapper.selectManProductDefectAttachById(manProductDefectAttach.getAttachSid());
        int row = manProductDefectAttachMapper.updateAllById(manProductDefectAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manProductDefectAttach.getAttachSid(), BusinessType.CHANGE.getValue(), response, manProductDefectAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产产品缺陷登记-附件
     *
     * @param attachSids 需要删除的生产产品缺陷登记-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProductDefectAttachByIds(List<Long> attachSids) {
        List<ManProductDefectAttach> list = manProductDefectAttachMapper.selectList(new QueryWrapper<ManProductDefectAttach>()
                .lambda().in(ManProductDefectAttach::getAttachSid, attachSids));
        int row = manProductDefectAttachMapper.deleteBatchIds(attachSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProductDefectAttach());
                MongodbUtil.insertUserLog(o.getAttachSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param manProductDefectAttach
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ManProductDefectAttach manProductDefectAttach) {
        int row = 0;
//        Long[] sids = manProductDefectAttach.getAttachSidList();
//        if (sids != null && sids.length > 0) {
//            row = manProductDefectAttachMapper.update(null, new UpdateWrapper<ManProductDefectAttach>().lambda().set(ManProductDefectAttach::getStatus, manProductDefectAttach.getStatus())
//                    .in(ManProductDefectAttach::getAttachSid, sids));
//            for (Long id : sids) {
//                manProductDefectAttach.setAttachSid(id);
//                row = manProductDefectAttachMapper.updateById(manProductDefectAttach);
//                if (row == 0) {
//                    throw new CustomException(id + "更改状态失败,请联系管理员");
//                }
//                //插入日志
//                MongodbDeal.status(manProductDefectAttach.getAttachSid(), "1", null, TITLE, null);
//            }
//        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manProductDefectAttach
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManProductDefectAttach manProductDefectAttach) {
        int row = 0;
//        Long[] sids = manProductDefectAttach.getAttachSidList();
//        if (sids != null && sids.length > 0) {
//            LambdaUpdateWrapper<ManProductDefectAttach> updateWrapper = new LambdaUpdateWrapper<>();
//            updateWrapper.in(ManProductDefectAttach::getAttachSid, sids);
//            updateWrapper.set(ManProductDefectAttach::getHandleStatus, manProductDefectAttach.getHandleStatus());
//            if (ConstantsEms.CHECK_STATUS.equals(manProductDefectAttach.getHandleStatus())) {
//                updateWrapper.set(ManProductDefectAttach::getConfirmDate, new Date());
//                updateWrapper.set(ManProductDefectAttach::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
//            }
//            row = manProductDefectAttachMapper.update(null, updateWrapper);
//            if (row > 0) {
//                for (Long id : sids) {
//                    //插入日志
//                    MongodbDeal.check(id, manProductDefectAttach.getHandleStatus(), null, TITLE);
//                }
//            }
//        }
        return row;
    }

}
