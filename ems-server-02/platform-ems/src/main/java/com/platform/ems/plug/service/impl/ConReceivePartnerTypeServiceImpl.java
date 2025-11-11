package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConReceivePartnerType;
import com.platform.ems.plug.mapper.ConReceivePartnerTypeMapper;
import com.platform.ems.plug.service.IConReceivePartnerTypeService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 收货方类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConReceivePartnerTypeServiceImpl extends ServiceImpl<ConReceivePartnerTypeMapper,ConReceivePartnerType>  implements IConReceivePartnerTypeService {
    @Autowired
    private ConReceivePartnerTypeMapper conReceivePartnerTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "收货方类型";
    /**
     * 查询收货方类型
     *
     * @param sid 收货方类型ID
     * @return 收货方类型
     */
    @Override
    public ConReceivePartnerType selectConReceivePartnerTypeById(Long sid) {
        ConReceivePartnerType conReceivePartnerType = conReceivePartnerTypeMapper.selectConReceivePartnerTypeById(sid);
        MongodbUtil.find(conReceivePartnerType);
        return  conReceivePartnerType;
    }

    @Override
    public List<ConReceivePartnerType> getList() {
        return conReceivePartnerTypeMapper.getList();
    }

    /**
     * 查询收货方类型列表
     *
     * @param conReceivePartnerType 收货方类型
     * @return 收货方类型
     */
    @Override
    public List<ConReceivePartnerType> selectConReceivePartnerTypeList(ConReceivePartnerType conReceivePartnerType) {
        return conReceivePartnerTypeMapper.selectConReceivePartnerTypeList(conReceivePartnerType);
    }

    /**
     * 新增收货方类型
     * 需要注意编码重复校验
     * @param conReceivePartnerType 收货方类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConReceivePartnerType(ConReceivePartnerType conReceivePartnerType) {
        List<ConReceivePartnerType> codeList = conReceivePartnerTypeMapper.selectList(new QueryWrapper<ConReceivePartnerType>().lambda()
                .eq(ConReceivePartnerType::getCode, conReceivePartnerType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConReceivePartnerType> nameList = conReceivePartnerTypeMapper.selectList(new QueryWrapper<ConReceivePartnerType>().lambda()
                .eq(ConReceivePartnerType::getName, conReceivePartnerType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conReceivePartnerTypeMapper.insert(conReceivePartnerType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conReceivePartnerType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改收货方类型
     *
     * @param conReceivePartnerType 收货方类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConReceivePartnerType(ConReceivePartnerType conReceivePartnerType) {
        ConReceivePartnerType response = conReceivePartnerTypeMapper.selectConReceivePartnerTypeById(conReceivePartnerType.getSid());
        int row=conReceivePartnerTypeMapper.updateById(conReceivePartnerType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conReceivePartnerType.getSid(), BusinessType.UPDATE.getValue(), response,conReceivePartnerType,TITLE);
        }
        return row;
    }

    /**
     * 变更收货方类型
     *
     * @param conReceivePartnerType 收货方类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConReceivePartnerType(ConReceivePartnerType conReceivePartnerType) {
        List<ConReceivePartnerType> nameList = conReceivePartnerTypeMapper.selectList(new QueryWrapper<ConReceivePartnerType>().lambda()
                .eq(ConReceivePartnerType::getName, conReceivePartnerType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conReceivePartnerType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conReceivePartnerType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConReceivePartnerType response = conReceivePartnerTypeMapper.selectConReceivePartnerTypeById(conReceivePartnerType.getSid());
        int row = conReceivePartnerTypeMapper.updateAllById(conReceivePartnerType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conReceivePartnerType.getSid(), BusinessType.CHANGE.getValue(), response, conReceivePartnerType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收货方类型
     *
     * @param sids 需要删除的收货方类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConReceivePartnerTypeByIds(List<Long> sids) {
        return conReceivePartnerTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conReceivePartnerType
    * @return
    */
    @Override
    public int changeStatus(ConReceivePartnerType conReceivePartnerType){
        int row=0;
        Long[] sids=conReceivePartnerType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReceivePartnerType.setSid(id);
                row=conReceivePartnerTypeMapper.updateById( conReceivePartnerType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conReceivePartnerType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conReceivePartnerType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conReceivePartnerType
     * @return
     */
    @Override
    public int check(ConReceivePartnerType conReceivePartnerType){
        int row=0;
        Long[] sids=conReceivePartnerType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReceivePartnerType.setSid(id);
                row=conReceivePartnerTypeMapper.updateById( conReceivePartnerType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conReceivePartnerType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
