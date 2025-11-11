package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.enums.HandleStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.FinRecordAdvancePaymentItemMapper;
import com.platform.ems.domain.FinRecordAdvancePaymentItem;
import com.platform.ems.service.IFinRecordAdvancePaymentItemService;

/**
 * 供应商业务台账-明细-预付Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-29
 */
@Service
@SuppressWarnings("all")
public class FinRecordAdvancePaymentItemServiceImpl extends ServiceImpl<FinRecordAdvancePaymentItemMapper,FinRecordAdvancePaymentItem>  implements IFinRecordAdvancePaymentItemService {
    @Autowired
    private FinRecordAdvancePaymentItemMapper finRecordAdvancePaymentItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商业务台账-明细-预付";
    /**
     * 查询供应商业务台账-明细-预付
     *
     * @param recordAdvancePaymentItemSid 供应商业务台账-明细-预付ID
     * @return 供应商业务台账-明细-预付
     */
    @Override
    public FinRecordAdvancePaymentItem selectFinRecordAdvancePaymentItemById(Long recordAdvancePaymentItemSid) {
        FinRecordAdvancePaymentItem finRecordAdvancePaymentItem = finRecordAdvancePaymentItemMapper.selectFinRecordAdvancePaymentItemById(recordAdvancePaymentItemSid);
        MongodbUtil.find(finRecordAdvancePaymentItem);
        return  finRecordAdvancePaymentItem;
    }

    /**
     * 查询供应商业务台账-明细-预付列表
     *
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 供应商业务台账-明细-预付
     */
    @Override
    public List<FinRecordAdvancePaymentItem> selectFinRecordAdvancePaymentItemList(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem) {
        return finRecordAdvancePaymentItemMapper.selectFinRecordAdvancePaymentItemList(finRecordAdvancePaymentItem);
    }

    /**
     * 新增供应商业务台账-明细-预付
     * 需要注意编码重复校验
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinRecordAdvancePaymentItem(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem) {
        int row= finRecordAdvancePaymentItemMapper.insert(finRecordAdvancePaymentItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商业务台账-明细-预付
     *
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinRecordAdvancePaymentItem(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem) {
        FinRecordAdvancePaymentItem response = finRecordAdvancePaymentItemMapper.selectFinRecordAdvancePaymentItemById(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid());
        int row=finRecordAdvancePaymentItemMapper.updateById(finRecordAdvancePaymentItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid(), BusinessType.UPDATE.ordinal(), response,finRecordAdvancePaymentItem,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商业务台账-明细-预付
     *
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinRecordAdvancePaymentItem(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem) {
        FinRecordAdvancePaymentItem response = finRecordAdvancePaymentItemMapper.selectFinRecordAdvancePaymentItemById(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid());
                                                                        int row=finRecordAdvancePaymentItemMapper.updateAllById(finRecordAdvancePaymentItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid(), BusinessType.CHANGE.ordinal(), response,finRecordAdvancePaymentItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商业务台账-明细-预付
     *
     * @param recordAdvancePaymentItemSids 需要删除的供应商业务台账-明细-预付ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinRecordAdvancePaymentItemByIds(List<Long> recordAdvancePaymentItemSids) {
        return finRecordAdvancePaymentItemMapper.deleteBatchIds(recordAdvancePaymentItemSids);
    }

    /**
    * 启用/停用
    * @param finRecordAdvancePaymentItem
    * @return
    */
    @Override
    public int changeStatus(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem){
        int row=0;
        Long[] sids=finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvancePaymentItem.setRecordAdvancePaymentItemSid(id);
                row=finRecordAdvancePaymentItemMapper.updateById( finRecordAdvancePaymentItem);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finRecordAdvancePaymentItem.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
//                MongodbUtil.insertUserLog(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finRecordAdvancePaymentItem
     * @return
     */
    @Override
    public int check(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem){
        int row=0;
        Long[] sids=finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvancePaymentItem.setRecordAdvancePaymentItemSid(id);
                row=finRecordAdvancePaymentItemMapper.updateById( finRecordAdvancePaymentItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finRecordAdvancePaymentItem.getRecordAdvancePaymentItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
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
    public int setValidDate(FinRecordAdvancePaymentItem request){
        if (request.getAccountValidDate() == null){
            throw new BaseException("请选择到期日");
        }
        int row = 0;
        List<FinRecordAdvancePaymentItem> itemList = finRecordAdvancePaymentItemMapper.selectFinRecordAdvancePaymentItemList(
                new FinRecordAdvancePaymentItem().setRecordAdvancePaymentItemSidList(request.getRecordAdvancePaymentItemSidList())
                        .setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX,ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode()}));
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != request.getRecordAdvancePaymentItemSidList().length){
            throw new BaseException("仅核销状态为“未核销”或“部分核销”，处理状态为“已确认”的数据可以点击该按钮");
        }
        UpdateWrapper<FinRecordAdvancePaymentItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(FinRecordAdvancePaymentItem::getRecordAdvancePaymentItemSid, request.getRecordAdvancePaymentItemSidList());
        if (request.getAccountValidDate() != null){
            updateWrapper.lambda().set(FinRecordAdvancePaymentItem::getAccountValidDate, request.getAccountValidDate());
        }
        else {
            updateWrapper.lambda().set(FinRecordAdvancePaymentItem::getAccountValidDate, null);
        }
        row = finRecordAdvancePaymentItemMapper.update(null, updateWrapper);
        return row;
    }

}
