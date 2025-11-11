package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.ManDayManufacturePlanItemMapper;
import com.platform.ems.domain.ManDayManufacturePlanItem;
import com.platform.ems.service.IManDayManufacturePlanItemService;

/**
 * 生产日计划-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@Service
@SuppressWarnings("all")
public class ManDayManufacturePlanItemServiceImpl extends ServiceImpl<ManDayManufacturePlanItemMapper,ManDayManufacturePlanItem>  implements IManDayManufacturePlanItemService {
    @Autowired
    private ManDayManufacturePlanItemMapper manDayManufacturePlanItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产日计划-明细";
    /**
     * 查询生产日计划-明细

     *
     * @param dayManufacturePlanItemSid 生产日计划-明细ID
     * @return 生产日计划-明细

     */
    @Override
    public ManDayManufacturePlanItem selectManDayManufacturePlanItemById(Long dayManufacturePlanItemSid) {
        ManDayManufacturePlanItem manDayManufacturePlanItem = manDayManufacturePlanItemMapper.selectManDayManufacturePlanItemById(dayManufacturePlanItemSid);
        MongodbUtil.find(manDayManufacturePlanItem);
        return  manDayManufacturePlanItem;
    }

    /**
     * 查询生产日计划-明细列表
     *
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 生产日计划-明细

     */
    @Override
    public List<ManDayManufacturePlanItem> selectManDayManufacturePlanItemList(ManDayManufacturePlanItem manDayManufacturePlanItem) {
        return manDayManufacturePlanItemMapper.selectManDayManufacturePlanItemList(manDayManufacturePlanItem);
    }

    /**
     * 新增生产日计划-明细

     * 需要注意编码重复校验
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManDayManufacturePlanItem(ManDayManufacturePlanItem manDayManufacturePlanItem) {
        int row= manDayManufacturePlanItemMapper.insert(manDayManufacturePlanItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(manDayManufacturePlanItem.getDayManufacturePlanItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改生产日计划-明细

     *
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManDayManufacturePlanItem(ManDayManufacturePlanItem manDayManufacturePlanItem) {
        ManDayManufacturePlanItem response = manDayManufacturePlanItemMapper.selectManDayManufacturePlanItemById(manDayManufacturePlanItem.getDayManufacturePlanItemSid());
        int row=manDayManufacturePlanItemMapper.updateById(manDayManufacturePlanItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manDayManufacturePlanItem.getDayManufacturePlanItemSid(), BusinessType.UPDATE.ordinal(), response,manDayManufacturePlanItem,TITLE);
        }
        return row;
    }

    /**
     * 变更生产日计划-明细

     *
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManDayManufacturePlanItem(ManDayManufacturePlanItem manDayManufacturePlanItem) {
        ManDayManufacturePlanItem response = manDayManufacturePlanItemMapper.selectManDayManufacturePlanItemById(manDayManufacturePlanItem.getDayManufacturePlanItemSid());
        int row=manDayManufacturePlanItemMapper.updateAllById(manDayManufacturePlanItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manDayManufacturePlanItem.getDayManufacturePlanItemSid(), BusinessType.CHANGE.ordinal(), response,manDayManufacturePlanItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产日计划-明细

     *
     * @param dayManufacturePlanItemSids 需要删除的生产日计划-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManDayManufacturePlanItemByIds(List<Long> dayManufacturePlanItemSids) {
        return manDayManufacturePlanItemMapper.deleteBatchIds(dayManufacturePlanItemSids);
    }

    /**
     *更改确认状态
     * @param manDayManufacturePlanItem
     * @return
     */
    @Override
    public int check(ManDayManufacturePlanItem manDayManufacturePlanItem){
        int row=0;
        Long[] sids=manDayManufacturePlanItem.getDayManufacturePlanItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                manDayManufacturePlanItem.setDayManufacturePlanItemSid(id);
                row=manDayManufacturePlanItemMapper.updateById( manDayManufacturePlanItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(manDayManufacturePlanItem.getDayManufacturePlanItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
