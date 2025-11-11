package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConPurchaseType;
import com.platform.ems.plug.mapper.ConPurchaseTypeMapper;
import com.platform.ems.plug.service.IConPurchaseTypeService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.mapper.SysClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 采购类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConPurchaseTypeServiceImpl extends ServiceImpl<ConPurchaseTypeMapper,ConPurchaseType>  implements IConPurchaseTypeService {
    @Autowired
    private ConPurchaseTypeMapper conPurchaseTypeMapper;
    @Autowired
    private SysClientMapper sysClientMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购类型";
    /**
     * 查询采购类型
     *
     * @param sid 采购类型ID
     * @return 采购类型
     */
    @Override
    public ConPurchaseType selectConPurchaseTypeById(Long sid) {
        ConPurchaseType conPurchaseType = conPurchaseTypeMapper.selectConPurchaseTypeById(sid);
        MongodbUtil.find(conPurchaseType);
        return  conPurchaseType;
    }

    /**
     * 查询采购类型列表
     *
     * @param conPurchaseType 采购类型
     * @return 采购类型
     */
    @Override
    public List<ConPurchaseType> selectConPurchaseTypeList(ConPurchaseType conPurchaseType) {
        return conPurchaseTypeMapper.selectConPurchaseTypeList(conPurchaseType);
    }

    /**
     * 新增采购类型
     * 需要注意编码重复校验
     * @param conPurchaseType 采购类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPurchaseType(ConPurchaseType conPurchaseType) {
        List<ConPurchaseType> codeList = conPurchaseTypeMapper.selectList(new QueryWrapper<ConPurchaseType>().lambda()
                .eq(ConPurchaseType::getCode, conPurchaseType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPurchaseType> nameList = conPurchaseTypeMapper.selectList(new QueryWrapper<ConPurchaseType>().lambda()
                .eq(ConPurchaseType::getName, conPurchaseType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conPurchaseTypeMapper.insert(conPurchaseType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conPurchaseType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改采购类型
     *
     * @param conPurchaseType 采购类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPurchaseType(ConPurchaseType conPurchaseType) {
        ConPurchaseType response = conPurchaseTypeMapper.selectConPurchaseTypeById(conPurchaseType.getSid());
        int row=conPurchaseTypeMapper.updateById(conPurchaseType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseType.getSid(), BusinessType.UPDATE.getValue(), response,conPurchaseType,TITLE);
        }
        return row;
    }

    /**
     * 变更采购类型
     *
     * @param conPurchaseType 采购类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPurchaseType(ConPurchaseType conPurchaseType) {
        List<ConPurchaseType> nameList = conPurchaseTypeMapper.selectList(new QueryWrapper<ConPurchaseType>().lambda()
                .eq(ConPurchaseType::getName, conPurchaseType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPurchaseType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPurchaseType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPurchaseType response = conPurchaseTypeMapper.selectConPurchaseTypeById(conPurchaseType.getSid());
        int row = conPurchaseTypeMapper.updateAllById(conPurchaseType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseType.getSid(), BusinessType.CHANGE.getValue(), response, conPurchaseType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购类型
     *
     * @param sids 需要删除的采购类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPurchaseTypeByIds(List<Long> sids) {
        return conPurchaseTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conPurchaseType
    * @return
    */
    @Override
    public int changeStatus(ConPurchaseType conPurchaseType){
        int row=0;
        Long[] sids=conPurchaseType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseType.setSid(id);
                row=conPurchaseTypeMapper.updateById( conPurchaseType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conPurchaseType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conPurchaseType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conPurchaseType
     * @return
     */
    @Override
    public int check(ConPurchaseType conPurchaseType){
        int row=0;
        Long[] sids=conPurchaseType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseType.setSid(id);
                row=conPurchaseTypeMapper.updateById( conPurchaseType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conPurchaseType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConPurchaseType> getConPurchaseTypeList() {
        return conPurchaseTypeMapper.getConPurchaseTypeList();
    }

    @Override
    public List<ConPurchaseType> getList(ConPurchaseType conPurchaseType) {
        //根据当前用户的租户类型过滤
        SysClient sysClient = sysClientMapper.selectSysClientById(ApiThreadLocalUtil.get().getClientId());
        if (sysClient != null && StrUtil.isNotEmpty(sysClient.getClientType())) {
            if (!ConstantsEms.CLIENT_TYPE_GMYT.equals(sysClient.getClientType())) {
                conPurchaseType.setClientType(sysClient.getClientType());
            }
        }
        return conPurchaseTypeMapper.getList(conPurchaseType);
    }
}
