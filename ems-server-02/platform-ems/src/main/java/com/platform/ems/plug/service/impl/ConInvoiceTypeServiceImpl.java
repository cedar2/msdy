package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;

import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConInvoiceTypeMapper;
import com.platform.ems.plug.domain.ConInvoiceType;
import com.platform.ems.plug.service.IConInvoiceTypeService;

/**
 * 发票类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceTypeServiceImpl extends ServiceImpl<ConInvoiceTypeMapper,ConInvoiceType>  implements IConInvoiceTypeService {
    @Autowired
    private ConInvoiceTypeMapper conInvoiceTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "发票类型";
    /**
     * 查询发票类型
     *
     * @param sid 发票类型ID
     * @return 发票类型
     */
    @Override
    public ConInvoiceType selectConInvoiceTypeById(Long sid) {
        ConInvoiceType conInvoiceType = conInvoiceTypeMapper.selectConInvoiceTypeById(sid);
        MongodbUtil.find(conInvoiceType);
        return  conInvoiceType;
    }

    /**
     * 查询发票类型列表
     *
     * @param conInvoiceType 发票类型
     * @return 发票类型
     */
    @Override
    public List<ConInvoiceType> selectConInvoiceTypeList(ConInvoiceType conInvoiceType) {
        return conInvoiceTypeMapper.selectConInvoiceTypeList(conInvoiceType);
    }

    /**
     * 新增发票类型
     * 需要注意编码重复校验
     * @param conInvoiceType 发票类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceType(ConInvoiceType conInvoiceType) {
        List<ConInvoiceType> codeList = conInvoiceTypeMapper.selectList(new QueryWrapper<ConInvoiceType>().lambda()
                .eq(ConInvoiceType::getCode, conInvoiceType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConInvoiceType> nameList = conInvoiceTypeMapper.selectList(new QueryWrapper<ConInvoiceType>().lambda()
                .eq(ConInvoiceType::getName, conInvoiceType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conInvoiceTypeMapper.insert(conInvoiceType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改发票类型
     *
     * @param conInvoiceType 发票类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceType(ConInvoiceType conInvoiceType) {
        List<ConInvoiceType> nameList = conInvoiceTypeMapper.selectList(new QueryWrapper<ConInvoiceType>().lambda()
                .eq(ConInvoiceType::getName, conInvoiceType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conInvoiceType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conInvoiceType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConInvoiceType response = conInvoiceTypeMapper.selectConInvoiceTypeById(conInvoiceType.getSid());
        int row=conInvoiceTypeMapper.updateById(conInvoiceType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceType.getSid(), BusinessType.UPDATE.getValue(), response,conInvoiceType,TITLE);
        }
        return row;
    }

    /**
     * 变更发票类型
     *
     * @param conInvoiceType 发票类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceType(ConInvoiceType conInvoiceType) {
        ConInvoiceType response = conInvoiceTypeMapper.selectConInvoiceTypeById(conInvoiceType.getSid());
        ConInvoiceType tempCode = conInvoiceTypeMapper.selectOne(new QueryWrapper<ConInvoiceType>().lambda().eq(ConInvoiceType::getCode,conInvoiceType.getCode()));
        if (tempCode != null && !conInvoiceType.getSid().equals(tempCode.getSid())){
            throw new CustomException(conInvoiceType.getCode()+"：编码已存在");
        }
        ConInvoiceType tempName = conInvoiceTypeMapper.selectOne(new QueryWrapper<ConInvoiceType>().lambda().eq(ConInvoiceType::getName,conInvoiceType.getName()));
        if (tempName != null && !conInvoiceType.getSid().equals(tempName.getSid())){
            throw new CustomException(conInvoiceType.getName()+"：名称已存在");
        }
        int row=conInvoiceTypeMapper.updateAllById(conInvoiceType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceType.getSid(), BusinessType.CHANGE.getValue(), response,conInvoiceType,TITLE);
        }
        return row;
    }

    /**
     * 批量删除发票类型
     *
     * @param sids 需要删除的发票类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceTypeByIds(List<Long> sids) {
        return conInvoiceTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conInvoiceType
    * @return
    */
    @Override
    public int changeStatus(ConInvoiceType conInvoiceType){
        int row=0;
        Long[] sids=conInvoiceType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conInvoiceType.setSid(id);
                row=conInvoiceTypeMapper.updateById( conInvoiceType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conInvoiceType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conInvoiceType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }

    /**
     *更改确认状态
     * @param conInvoiceType
     * @return
     */
    @Override
    public int check(ConInvoiceType conInvoiceType){
        int row=0;
        Long[] sids=conInvoiceType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conInvoiceType.setSid(id);
                row=conInvoiceTypeMapper.updateById( conInvoiceType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conInvoiceType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConInvoiceType> getConInvoiceTypeList(ConInvoiceType conInvoiceType) {
        return conInvoiceTypeMapper.getConInvoiceTypeList(conInvoiceType);
    }
}
