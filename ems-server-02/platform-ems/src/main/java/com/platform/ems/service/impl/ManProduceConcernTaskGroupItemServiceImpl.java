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
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProduceConcernTaskGroupItemMapper;
import com.platform.ems.domain.ManProduceConcernTaskGroupItem;
import com.platform.ems.service.IManProduceConcernTaskGroupItemService;

/**
 * 生产关注事项组-明细Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-08-02
 */
@Service
@SuppressWarnings("all")
public class ManProduceConcernTaskGroupItemServiceImpl extends ServiceImpl<ManProduceConcernTaskGroupItemMapper, ManProduceConcernTaskGroupItem> implements IManProduceConcernTaskGroupItemService {
    @Autowired
    private ManProduceConcernTaskGroupItemMapper manProduceConcernTaskGroupItemMapper;

    private static final String TITLE = "生产关注事项组-明细";

    /**
     * 查询生产关注事项组-明细
     *
     * @param concernTaskGroupItemSid 生产关注事项组-明细ID
     * @return 生产关注事项组-明细
     */
    @Override
    public ManProduceConcernTaskGroupItem selectManProduceConcernTaskGroupItemById(Long concernTaskGroupItemSid) {
        ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem = manProduceConcernTaskGroupItemMapper.selectManProduceConcernTaskGroupItemById(concernTaskGroupItemSid);
        MongodbUtil.find(manProduceConcernTaskGroupItem);
        return manProduceConcernTaskGroupItem;
    }

    /**
     * 查询生产关注事项组-明细列表
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 生产关注事项组-明细
     */
    @Override
    public List<ManProduceConcernTaskGroupItem> selectManProduceConcernTaskGroupItemList(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
        return manProduceConcernTaskGroupItemMapper.selectManProduceConcernTaskGroupItemList(manProduceConcernTaskGroupItem);
    }

    /**
     * 新增生产关注事项组-明细
     * 需要注意编码重复校验
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProduceConcernTaskGroupItem(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
        int row = manProduceConcernTaskGroupItemMapper.insert(manProduceConcernTaskGroupItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProduceConcernTaskGroupItem(), manProduceConcernTaskGroupItem);
            MongodbDeal.insert(manProduceConcernTaskGroupItem.getConcernTaskGroupItemSid(), "5" , msgList, TITLE, null);
        }
        return row;
    }


    /**
     * 变更生产关注事项组-明细
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 结果
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int changeManProduceConcernTaskGroupItem(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        ManProduceConcernTaskGroupItem response = manProduceConcernTaskGroupItemMapper.selectManProduceConcernTaskGroupItemById(manProduceConcernTaskGroupItem.getConcernTaskGroupItemSid());
//        int row = manProduceConcernTaskGroupItemMapper.updateAllById(manProduceConcernTaskGroupItem);
//        if (row > 0) {
//            //插入日志
//            MongodbUtil.insertUserLog(manProduceConcernTaskGroupItem.getConcernTaskGroupItemSid(), BusinessType.CHANGE.getValue(), response, manProduceConcernTaskGroupItem, TITLE);
//        }
//        return row;
//    }

    /**
     * 批量删除生产关注事项组-明细
     *
     * @param concernTaskGroupItemSids 需要删除的生产关注事项组-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProduceConcernTaskGroupItemByIds(List<Long> concernTaskGroupItemSids) {
        List<ManProduceConcernTaskGroupItem> list = manProduceConcernTaskGroupItemMapper.selectList(new QueryWrapper<ManProduceConcernTaskGroupItem>()
                .lambda().in(ManProduceConcernTaskGroupItem::getConcernTaskGroupItemSid, concernTaskGroupItemSids));
        int row = manProduceConcernTaskGroupItemMapper.deleteBatchIds(concernTaskGroupItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProduceConcernTaskGroupItem());
                MongodbUtil.insertUserLog(o.getConcernTaskGroupItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param manProduceConcernTaskGroupItem
     * @return
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int changeStatus(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        int row = 0;
////        Long[] sids = manProduceConcernTaskGroupItem.getConcernTaskGroupItemSidList();
////        if (sids != null && sids.length > 0) {
////            row = manProduceConcernTaskGroupItemMapper.update(null, new UpdateWrapper<ManProduceConcernTaskGroupItem>().lambda().set(ManProduceConcernTaskGroupItem::getStatus, manProduceConcernTaskGroupItem.getStatus())
////                    .in(ManProduceConcernTaskGroupItem::getConcernTaskGroupItemSid, sids));
////            for (Long id : sids) {
////                manProduceConcernTaskGroupItem.setConcernTaskGroupItemSid(id);
////                row = manProduceConcernTaskGroupItemMapper.updateById(manProduceConcernTaskGroupItem);
////                if (row == 0) {
////                    throw new CustomException(id + "更改状态失败,请联系管理员");
////                }
////                //插入日志
////                MongodbDeal.status(manProduceConcernTaskGroupItem.getConcernTaskGroupItemSid(), ConstantsEms.SAVA_STATUS, null, TITLE, null);
////            }
////        }
//        return row;
//    }

    /**
     * 更改确认状态
     *
     * @param manProduceConcernTaskGroupItem
     * @return
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int check(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        int row = 0;
////        Long[] sids = manProduceConcernTaskGroupItem.getConcernTaskGroupItemSidList();
////        if (sids != null && sids.length > 0) {
////            LambdaUpdateWrapper<ManProduceConcernTaskGroupItem> updateWrapper = new LambdaUpdateWrapper<>();
////            updateWrapper.in(ManProduceConcernTaskGroupItem::getConcernTaskGroupItemSid, sids);
////            updateWrapper.set(ManProduceConcernTaskGroupItem::getHandleStatus, manProduceConcernTaskGroupItem.getHandleStatus());
////            if (ConstantsEms.CHECK_STATUS.equals(manProduceConcernTaskGroupItem.getHandleStatus())) {
////                updateWrapper.set(ManProduceConcernTaskGroupItem::getConfirmDate, new Date());
////                updateWrapper.set(ManProduceConcernTaskGroupItem::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
////            }
////            row = manProduceConcernTaskGroupItemMapper.update(null, updateWrapper);
////            if (row > 0) {
////                for (Long id : sids) {
////                    //插入日志
////                    MongodbDeal.check(id, manProduceConcernTaskGroupItem.getHandleStatus(), null, TITLE);
////                }
////            }
////        }
//        return row;
//    }

}
