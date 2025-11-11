package com.platform.ems.plug.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConSampleRequisitionUsageMapper;
import com.platform.ems.plug.domain.ConSampleRequisitionUsage;
import com.platform.ems.plug.service.IConSampleRequisitionUsageService;

/**
 * 样品出库用途Service业务层处理
 *
 * @author yangqz
 * @date 2022-04-24
 */
@Service
@SuppressWarnings("all")
public class ConSampleRequisitionUsageServiceImpl extends ServiceImpl<ConSampleRequisitionUsageMapper,ConSampleRequisitionUsage>  implements IConSampleRequisitionUsageService {
    @Autowired
    private ConSampleRequisitionUsageMapper conSampleRequisitionUsageMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "样品出库用途";
    /**
     * 查询样品出库用途
     *
     * @param sid 样品出库用途ID
     * @return 样品出库用途
     */
    @Override
    public ConSampleRequisitionUsage selectConSampleRequisitionUsageById(Long sid) {
        ConSampleRequisitionUsage conSampleRequisitionUsage = conSampleRequisitionUsageMapper.selectConSampleRequisitionUsageById(sid);
        MongodbUtil.find(conSampleRequisitionUsage);
        return  conSampleRequisitionUsage;
    }

    /**
     * 查询样品出库用途列表
     *
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 样品出库用途
     */
    @Override
    public List<ConSampleRequisitionUsage> selectConSampleRequisitionUsageList(ConSampleRequisitionUsage conSampleRequisitionUsage) {
        return conSampleRequisitionUsageMapper.selectConSampleRequisitionUsageList(conSampleRequisitionUsage);
    }

    /**
     * 查询样品出库用途下拉列表
     */
    @Override
    public List<ConSampleRequisitionUsage> getList(ConSampleRequisitionUsage conSampleRequisitionUsage) {
        return conSampleRequisitionUsageMapper.getList(conSampleRequisitionUsage);
    }
    /**
     * 新增样品出库用途
     * 需要注意编码重复校验
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSampleRequisitionUsage(ConSampleRequisitionUsage conSampleRequisitionUsage) {
        int row= conSampleRequisitionUsageMapper.insert(conSampleRequisitionUsage);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conSampleRequisitionUsage.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改样品出库用途
     *
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSampleRequisitionUsage(ConSampleRequisitionUsage conSampleRequisitionUsage) {
        ConSampleRequisitionUsage response = conSampleRequisitionUsageMapper.selectConSampleRequisitionUsageById(conSampleRequisitionUsage.getSid());
        int row=conSampleRequisitionUsageMapper.updateById(conSampleRequisitionUsage);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conSampleRequisitionUsage.getSid(), BusinessType.UPDATE.ordinal(), response,conSampleRequisitionUsage,TITLE);
        }
        return row;
    }

    /**
     * 变更样品出库用途
     *
     * @param conSampleRequisitionUsage 样品出库用途
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSampleRequisitionUsage(ConSampleRequisitionUsage conSampleRequisitionUsage) {
        ConSampleRequisitionUsage response = conSampleRequisitionUsageMapper.selectConSampleRequisitionUsageById(conSampleRequisitionUsage.getSid());
                                                                    int row=conSampleRequisitionUsageMapper.updateAllById(conSampleRequisitionUsage);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conSampleRequisitionUsage.getSid(), BusinessType.CHANGE.ordinal(), response,conSampleRequisitionUsage,TITLE);
        }
        return row;
    }

    /**
     * 批量删除样品出库用途
     *
     * @param sids 需要删除的样品出库用途ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSampleRequisitionUsageByIds(List<Long> sids) {
        return conSampleRequisitionUsageMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conSampleRequisitionUsage
    * @return
    */
    @Override
    public int changeStatus(ConSampleRequisitionUsage conSampleRequisitionUsage){
        int row=0;
        Long[] sids=conSampleRequisitionUsage.getSidList();
        if(sids!=null&&sids.length>0){
            row=conSampleRequisitionUsageMapper.update(null, new UpdateWrapper<ConSampleRequisitionUsage>().lambda().set(ConSampleRequisitionUsage::getStatus ,conSampleRequisitionUsage.getStatus() )
                    .in(ConSampleRequisitionUsage::getSid,sids));
            for(Long id:sids){
                conSampleRequisitionUsage.setSid(id);
                row=conSampleRequisitionUsageMapper.updateById( conSampleRequisitionUsage);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conSampleRequisitionUsage.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conSampleRequisitionUsage.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conSampleRequisitionUsage
     * @return
     */
    @Override
    public int check(ConSampleRequisitionUsage conSampleRequisitionUsage){
        int row=0;
        Long[] sids=conSampleRequisitionUsage.getSidList();
        if(sids!=null&&sids.length>0){
            row=conSampleRequisitionUsageMapper.update(null,new UpdateWrapper<ConSampleRequisitionUsage>().lambda().set(ConSampleRequisitionUsage::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConSampleRequisitionUsage::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
