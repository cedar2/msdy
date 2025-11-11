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
import com.platform.ems.mapper.DelOutsourceMaterialIssueNoteItemMapper;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteItem;
import com.platform.ems.service.IDelOutsourceMaterialIssueNoteItemService;

/**
 * 外发加工发料单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class DelOutsourceMaterialIssueNoteItemServiceImpl extends ServiceImpl<DelOutsourceMaterialIssueNoteItemMapper,DelOutsourceMaterialIssueNoteItem>  implements IDelOutsourceMaterialIssueNoteItemService {
    @Autowired
    private DelOutsourceMaterialIssueNoteItemMapper delOutsourceMaterialIssueNoteItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工发料单-明细";
    /**
     * 查询外发加工发料单-明细
     *
     * @param issueNoteItemSid 外发加工发料单-明细ID
     * @return 外发加工发料单-明细
     */
    @Override
    public DelOutsourceMaterialIssueNoteItem selectDelOutsourceMaterialIssueNoteItemById(Long issueNoteItemSid) {
        DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem = delOutsourceMaterialIssueNoteItemMapper.selectDelOutsourceMaterialIssueNoteItemById(issueNoteItemSid);
        MongodbUtil.find(delOutsourceMaterialIssueNoteItem);
        return  delOutsourceMaterialIssueNoteItem;
    }

    /**
     * 查询外发加工发料单-明细列表
     *
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 外发加工发料单-明细
     */
    @Override
    public List<DelOutsourceMaterialIssueNoteItem> selectDelOutsourceMaterialIssueNoteItemList(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) {
        return delOutsourceMaterialIssueNoteItemMapper.selectDelOutsourceMaterialIssueNoteItemList(delOutsourceMaterialIssueNoteItem);
    }

    /**
     * 新增外发加工发料单-明细
     * 需要注意编码重复校验
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) {
        int row= delOutsourceMaterialIssueNoteItemMapper.insert(delOutsourceMaterialIssueNoteItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteItem.getIssueNoteItemSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工发料单-明细
     *
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) {
        DelOutsourceMaterialIssueNoteItem response = delOutsourceMaterialIssueNoteItemMapper.selectDelOutsourceMaterialIssueNoteItemById(delOutsourceMaterialIssueNoteItem.getIssueNoteItemSid());
        int row=delOutsourceMaterialIssueNoteItemMapper.updateById(delOutsourceMaterialIssueNoteItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteItem.getIssueNoteItemSid(), BusinessType.UPDATE.getValue(), response,delOutsourceMaterialIssueNoteItem,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工发料单-明细
     *
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) {
        DelOutsourceMaterialIssueNoteItem response = delOutsourceMaterialIssueNoteItemMapper.selectDelOutsourceMaterialIssueNoteItemById(delOutsourceMaterialIssueNoteItem.getIssueNoteItemSid());
                                                                                            int row=delOutsourceMaterialIssueNoteItemMapper.updateAllById(delOutsourceMaterialIssueNoteItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteItem.getIssueNoteItemSid(), BusinessType.CHANGE.getValue(), response,delOutsourceMaterialIssueNoteItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工发料单-明细
     *
     * @param issueNoteItemSids 需要删除的外发加工发料单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelOutsourceMaterialIssueNoteItemByIds(List<Long> issueNoteItemSids) {
        return delOutsourceMaterialIssueNoteItemMapper.deleteBatchIds(issueNoteItemSids);
    }

    /**
     *更改确认状态
     * @param delOutsourceMaterialIssueNoteItem
     * @return
     */
    @Override
    public int check(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem){
        int row=0;
        Long[] sids=delOutsourceMaterialIssueNoteItem.getIssueNoteItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                delOutsourceMaterialIssueNoteItem.setIssueNoteItemSid(id);
                row=delOutsourceMaterialIssueNoteItemMapper.updateById( delOutsourceMaterialIssueNoteItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteItem.getIssueNoteItemSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 外发加工发料单明细报表
     */
    @Override
    public List<DelOutsourceMaterialIssueNoteItem> getItemList(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) {
        return delOutsourceMaterialIssueNoteItemMapper.getItemList(delOutsourceMaterialIssueNoteItem);
    }
}
