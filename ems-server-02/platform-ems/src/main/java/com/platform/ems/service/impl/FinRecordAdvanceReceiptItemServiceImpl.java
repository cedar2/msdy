package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.enums.HandleStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.FinRecordAdvanceReceiptItem;
import com.platform.ems.mapper.FinRecordAdvanceReceiptItemMapper;
import com.platform.ems.service.IFinRecordAdvanceReceiptItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 客户业务台账-明细-预收Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@Service
@SuppressWarnings("all")
public class FinRecordAdvanceReceiptItemServiceImpl extends ServiceImpl<FinRecordAdvanceReceiptItemMapper,FinRecordAdvanceReceiptItem>  implements IFinRecordAdvanceReceiptItemService {
    @Autowired
    private FinRecordAdvanceReceiptItemMapper finRecordAdvanceReceiptItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户业务台账-明细-预收";
    /**
     * 查询客户业务台账-明细-预收
     *
     * @param recordAdvanceReceiptItemSid 客户业务台账-明细-预收ID
     * @return 客户业务台账-明细-预收
     */
    @Override
    public FinRecordAdvanceReceiptItem selectFinRecordAdvanceReceiptItemById(Long recordAdvanceReceiptItemSid) {
        FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem = finRecordAdvanceReceiptItemMapper.selectFinRecordAdvanceReceiptItemById(recordAdvanceReceiptItemSid);
        MongodbUtil.find(finRecordAdvanceReceiptItem);
        return  finRecordAdvanceReceiptItem;
    }

    /**
     * 查询客户业务台账-明细-预收列表
     *
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 客户业务台账-明细-预收
     */
    @Override
    public List<FinRecordAdvanceReceiptItem> selectFinRecordAdvanceReceiptItemList(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem) {
        return finRecordAdvanceReceiptItemMapper.selectFinRecordAdvanceReceiptItemList(finRecordAdvanceReceiptItem);
    }

    /**
     * 新增客户业务台账-明细-预收
     * 需要注意编码重复校验
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinRecordAdvanceReceiptItem(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem) {
        int row= finRecordAdvanceReceiptItemMapper.insert(finRecordAdvanceReceiptItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改客户业务台账-明细-预收
     *
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinRecordAdvanceReceiptItem(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem) {
        FinRecordAdvanceReceiptItem response = finRecordAdvanceReceiptItemMapper.selectFinRecordAdvanceReceiptItemById(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid());
        int row=finRecordAdvanceReceiptItemMapper.updateById(finRecordAdvanceReceiptItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid(), BusinessType.UPDATE.ordinal(), response,finRecordAdvanceReceiptItem,TITLE);
        }
        return row;
    }

    /**
     * 变更客户业务台账-明细-预收
     *
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinRecordAdvanceReceiptItem(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem) {
        FinRecordAdvanceReceiptItem response = finRecordAdvanceReceiptItemMapper.selectFinRecordAdvanceReceiptItemById(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid());
                                                                        int row=finRecordAdvanceReceiptItemMapper.updateAllById(finRecordAdvanceReceiptItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid(), BusinessType.CHANGE.ordinal(), response,finRecordAdvanceReceiptItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户业务台账-明细-预收
     *
     * @param recordAdvanceReceiptItemSids 需要删除的客户业务台账-明细-预收ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinRecordAdvanceReceiptItemByIds(List<Long> recordAdvanceReceiptItemSids) {
        return finRecordAdvanceReceiptItemMapper.deleteBatchIds(recordAdvanceReceiptItemSids);
    }

    /**
    * 启用/停用
    * @param finRecordAdvanceReceiptItem
    * @return
    */
    @Override
    public int changeStatus(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem){
        int row=0;
        Long[] sids=finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvanceReceiptItem.setRecordAdvanceReceiptItemSid(id);
                row=finRecordAdvanceReceiptItemMapper.updateById( finRecordAdvanceReceiptItem);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finRecordAdvanceReceiptItem.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,"");
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finRecordAdvanceReceiptItem
     * @return
     */
    @Override
    public int check(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem){
        int row=0;
        Long[] sids=finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvanceReceiptItem.setRecordAdvanceReceiptItemSid(id);
                row=finRecordAdvanceReceiptItemMapper.updateById( finRecordAdvanceReceiptItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finRecordAdvanceReceiptItem.getRecordAdvanceReceiptItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 设置到期日
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setValidDate(FinRecordAdvanceReceiptItem request){
        if (request.getAccountValidDate() == null){
            throw new BaseException("请选择到期日");
        }
        int row = 0;
        List<FinRecordAdvanceReceiptItem> itemList = finRecordAdvanceReceiptItemMapper.selectFinRecordAdvanceReceiptItemList(
                new FinRecordAdvanceReceiptItem().setRecordAdvanceReceiptItemSidList(request.getRecordAdvanceReceiptItemSidList())
                        .setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX,ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode()}));
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != request.getRecordAdvanceReceiptItemSidList().length){
            throw new BaseException("仅核销状态为“未核销”或“部分核销”，处理状态为“已确认”的数据可以点击该按钮");
        }
        UpdateWrapper<FinRecordAdvanceReceiptItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(FinRecordAdvanceReceiptItem::getRecordAdvanceReceiptItemSid, request.getRecordAdvanceReceiptItemSidList());
        if (request.getAccountValidDate() != null){
            updateWrapper.lambda().set(FinRecordAdvanceReceiptItem::getAccountValidDate, request.getAccountValidDate());
        }
        else {
            updateWrapper.lambda().set(FinRecordAdvanceReceiptItem::getAccountValidDate, null);
        }
        row = finRecordAdvanceReceiptItemMapper.update(null, updateWrapper);
        return row;
    }

}
