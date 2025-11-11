package com.platform.ems.plug.service.impl;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConAccountCategory;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConInvoiceDimensionMapper;
import com.platform.ems.plug.domain.ConInvoiceDimension;
import com.platform.ems.plug.service.IConInvoiceDimensionService;

/**
 * 发票维度Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-11
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceDimensionServiceImpl extends ServiceImpl<ConInvoiceDimensionMapper,ConInvoiceDimension>  implements IConInvoiceDimensionService {
    @Autowired
    private ConInvoiceDimensionMapper conInvoiceDimensionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "发票维度";
    /**
     * 查询发票维度
     *
     * @param sid 发票维度ID
     * @return 发票维度
     */
    @Override
    public ConInvoiceDimension selectConInvoiceDimensionById(Long sid) {
        ConInvoiceDimension conInvoiceDimension = conInvoiceDimensionMapper.selectConInvoiceDimensionById(sid);
        MongodbUtil.find(conInvoiceDimension);
        return  conInvoiceDimension;
    }

    /**
     * 查询发票维度列表
     *
     * @param conInvoiceDimension 发票维度
     * @return 发票维度
     */
    @Override
    public List<ConInvoiceDimension> selectConInvoiceDimensionList(ConInvoiceDimension conInvoiceDimension) {
        return conInvoiceDimensionMapper.selectConInvoiceDimensionList(conInvoiceDimension);
    }

    /**
     * 新增发票维度
     * 需要注意编码重复校验
     * @param conInvoiceDimension 发票维度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceDimension(ConInvoiceDimension conInvoiceDimension) {
        List<ConInvoiceDimension> codeList = conInvoiceDimensionMapper.selectList(new QueryWrapper<ConInvoiceDimension>().lambda()
                .eq(ConInvoiceDimension::getCode, conInvoiceDimension.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConInvoiceDimension> nameList = conInvoiceDimensionMapper.selectList(new QueryWrapper<ConInvoiceDimension>().lambda()
                .eq(ConInvoiceDimension::getName, conInvoiceDimension.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conInvoiceDimensionMapper.insert(conInvoiceDimension);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceDimension.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改发票维度
     *
     * @param conInvoiceDimension 发票维度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceDimension(ConInvoiceDimension conInvoiceDimension) {
        ConInvoiceDimension response = conInvoiceDimensionMapper.selectConInvoiceDimensionById(conInvoiceDimension.getSid());
        int row=conInvoiceDimensionMapper.updateById(conInvoiceDimension);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceDimension.getSid(), BusinessType.UPDATE.getValue(), response,conInvoiceDimension,TITLE);
        }
        return row;
    }

    /**
     * 变更发票维度
     *
     * @param conInvoiceDimension 发票维度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceDimension(ConInvoiceDimension conInvoiceDimension) {
        ConInvoiceDimension response = conInvoiceDimensionMapper.selectConInvoiceDimensionById(conInvoiceDimension.getSid());
        List<ConInvoiceDimension> nameList = conInvoiceDimensionMapper.selectList(new QueryWrapper<ConInvoiceDimension>().lambda()
                .eq(ConInvoiceDimension::getName, conInvoiceDimension.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conInvoiceDimension.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conInvoiceDimension.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row=conInvoiceDimensionMapper.updateAllById(conInvoiceDimension);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceDimension.getSid(), BusinessType.CHANGE.getValue(), response,conInvoiceDimension,TITLE);
        }
        return row;
    }

    /**
     * 批量删除发票维度
     *
     * @param sids 需要删除的发票维度ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceDimensionByIds(List<Long> sids) {
        return conInvoiceDimensionMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     * @param conInvoiceDimension
     * @return
     */
    @Override
    public int changeStatus(ConInvoiceDimension conInvoiceDimension){
        int row=0;
        Long[] sids=conInvoiceDimension.getSidList();
        if(sids!=null&&sids.length>0){
            row=conInvoiceDimensionMapper.update(null, new UpdateWrapper<ConInvoiceDimension>().lambda().set(ConInvoiceDimension::getStatus ,conInvoiceDimension.getStatus() )
                    .in(ConInvoiceDimension::getSid,sids));
            for(Long id:sids){
                conInvoiceDimension.setSid(id);
                row=conInvoiceDimensionMapper.updateById( conInvoiceDimension);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conInvoiceDimension.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conInvoiceDimension.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conInvoiceDimension
     * @return
     */
    @Override
    public int check(ConInvoiceDimension conInvoiceDimension){
        int row=0;
        Long[] sids=conInvoiceDimension.getSidList();
        if(sids!=null&&sids.length>0){
            row=conInvoiceDimensionMapper.update(null,new UpdateWrapper<ConInvoiceDimension>().lambda().set(ConInvoiceDimension::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConInvoiceDimension::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 开票维度下拉框列表
     */
    @Override
    public List<ConInvoiceDimension> getInvoiceDimensionList() {
        return conInvoiceDimensionMapper.getInvoiceDimensionList();
    }
}
