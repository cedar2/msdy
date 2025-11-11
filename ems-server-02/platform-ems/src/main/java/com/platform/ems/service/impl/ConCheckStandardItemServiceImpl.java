package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ConCheckItem;
import com.platform.ems.domain.ConCheckStandardItem;
import com.platform.ems.mapper.ConCheckStandardItemMapper;
import com.platform.ems.service.IConCheckStandardItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测标准/项目关联Service业务层处理
 *
 * @author qhq
 * @date 2021-11-01
 */
@Service
@SuppressWarnings("all")
public class ConCheckStandardItemServiceImpl extends ServiceImpl<ConCheckStandardItemMapper,ConCheckStandardItem>  implements IConCheckStandardItemService {
    @Autowired
    private ConCheckStandardItemMapper conCheckStandardItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "检测标准/项目关联";
    /**
     * 查询检测标准/项目关联
     *
     * @param checkStandardItemSid 检测标准/项目关联ID
     * @return 检测标准/项目关联
     */
    @Override
    public ConCheckStandardItem selectConCheckStandardItemById(Long checkStandardItemSid) {
        ConCheckStandardItem conCheckStandardItem = conCheckStandardItemMapper.selectConCheckStandardItemById(checkStandardItemSid);
        MongodbUtil.find(conCheckStandardItem);
        return  conCheckStandardItem;
    }

    /**
     * 查询检测标准/项目关联列表
     *
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 检测标准/项目关联
     */
    @Override
    public List<ConCheckStandardItem> selectConCheckStandardItemList(ConCheckStandardItem conCheckStandardItem) {
        return conCheckStandardItemMapper.selectConCheckStandardItemList(conCheckStandardItem);
    }

    /**
     * 新增检测标准/项目关联
     * 需要注意编码重复校验
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCheckStandardItem(ConCheckStandardItem conCheckStandardItem) {
        matching(conCheckStandardItem);
        int row= conCheckStandardItemMapper.insert(conCheckStandardItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCheckStandardItem.getCheckStandardItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改检测标准/项目关联
     *
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCheckStandardItem(ConCheckStandardItem conCheckStandardItem) {
        ConCheckStandardItem response = conCheckStandardItemMapper.selectConCheckStandardItemById(conCheckStandardItem.getCheckStandardItemSid());
        int row=conCheckStandardItemMapper.updateById(conCheckStandardItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckStandardItem.getCheckStandardItemSid(), BusinessType.UPDATE.ordinal(), response,conCheckStandardItem,TITLE);
        }
        return row;
    }

    /**
     * 变更检测标准/项目关联
     *
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCheckStandardItem(ConCheckStandardItem conCheckStandardItem) {
        ConCheckStandardItem response = conCheckStandardItemMapper.selectConCheckStandardItemById(conCheckStandardItem.getCheckStandardItemSid());
        int row=conCheckStandardItemMapper.updateAllById(conCheckStandardItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckStandardItem.getCheckStandardItemSid(), BusinessType.CHANGE.ordinal(), response,conCheckStandardItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除检测标准/项目关联
     *
     * @param checkStandardItemSids 需要删除的检测标准/项目关联ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCheckStandardItemByIds(List<Long> checkStandardItemSids) {
        return conCheckStandardItemMapper.deleteBatchIds(checkStandardItemSids);
    }

    public void matching(ConCheckStandardItem conCheckStandardItem){
        QueryWrapper<ConCheckStandardItem> standardItemQueryWrapper = new QueryWrapper<>();
        standardItemQueryWrapper.eq("check_item_sid",conCheckStandardItem.getCheckItemSid());
        standardItemQueryWrapper.eq("check_item_code",conCheckStandardItem.getCheckItemCode());
        standardItemQueryWrapper.eq("check_standard_sid",conCheckStandardItem.getCheckStandardSid());
        standardItemQueryWrapper.eq("check_standard_code",conCheckStandardItem.getCheckStandardCode());
        int codeMatching = conCheckStandardItemMapper.selectList(standardItemQueryWrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同组合，请重新选择！");
        }
    }

    /**
    * 启用/停用
    * @param conCheckStandardItem
    * @return

    @Override
    public int changeStatus(ConCheckStandardItem conCheckStandardItem){
        int row=0;
        Long[] sids=conCheckStandardItem.getCheckStandardItemSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckStandardItemMapper.update(null, new UpdateWrapper<ConCheckStandardItem>().lambda().set(ConCheckStandardItem::getStatus ,conCheckStandardItem.getStatus() )
                    .in(ConCheckStandardItem::getCheckStandardItemSid,sids));
            for(Long id:sids){
                conCheckStandardItem.setCheckStandardItemSid(id);
                row=conCheckStandardItemMapper.updateById( conCheckStandardItem);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCheckStandardItem.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCheckStandardItem.getCheckStandardItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }*/


    /**
     *更改确认状态
     * @param conCheckStandardItem
     * @return

    @Override
    public int check(ConCheckStandardItem conCheckStandardItem){
        int row=0;
        Long[] sids=conCheckStandardItem.getCheckStandardItemSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckStandardItemMapper.update(null,new UpdateWrapper<ConCheckStandardItem>().lambda().set(ConCheckStandardItem::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConCheckStandardItem::getCheckStandardItemSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }*/


}
