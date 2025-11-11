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
import com.platform.ems.plug.domain.ConPartnerType;
import com.platform.ems.plug.mapper.ConPartnerTypeMapper;
import com.platform.ems.plug.service.IConPartnerTypeService;
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
 * 类型_业务合作伙伴Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConPartnerTypeServiceImpl extends ServiceImpl<ConPartnerTypeMapper,ConPartnerType>  implements IConPartnerTypeService {
    @Autowired
    private ConPartnerTypeMapper conPartnerTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "类型_业务合作伙伴";
    /**
     * 查询类型_业务合作伙伴
     *
     * @param sid 类型_业务合作伙伴ID
     * @return 类型_业务合作伙伴
     */
    @Override
    public ConPartnerType selectConPartnerTypeById(Long sid) {
        ConPartnerType conPartnerType = conPartnerTypeMapper.selectConPartnerTypeById(sid);
        MongodbUtil.find(conPartnerType);
        return  conPartnerType;
    }

    /**
     * 查询类型_业务合作伙伴列表
     *
     * @param conPartnerType 类型_业务合作伙伴
     * @return 类型_业务合作伙伴
     */
    @Override
    public List<ConPartnerType> selectConPartnerTypeList(ConPartnerType conPartnerType) {
        return conPartnerTypeMapper.selectConPartnerTypeList(conPartnerType);
    }

    /**
     * 新增类型_业务合作伙伴
     * 需要注意编码重复校验
     * @param conPartnerType 类型_业务合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPartnerType(ConPartnerType conPartnerType) {
        List<ConPartnerType> codeList = conPartnerTypeMapper.selectList(new QueryWrapper<ConPartnerType>().lambda()
                .eq(ConPartnerType::getCode, conPartnerType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPartnerType> nameList = conPartnerTypeMapper.selectList(new QueryWrapper<ConPartnerType>().lambda()
                .eq(ConPartnerType::getName, conPartnerType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conPartnerTypeMapper.insert(conPartnerType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conPartnerType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改类型_业务合作伙伴
     *
     * @param conPartnerType 类型_业务合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPartnerType(ConPartnerType conPartnerType) {
        ConPartnerType response = conPartnerTypeMapper.selectConPartnerTypeById(conPartnerType.getSid());
        int row=conPartnerTypeMapper.updateById(conPartnerType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conPartnerType.getSid(), BusinessType.UPDATE.getValue(), response,conPartnerType,TITLE);
        }
        return row;
    }

    /**
     * 变更类型_业务合作伙伴
     *
     * @param conPartnerType 类型_业务合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPartnerType(ConPartnerType conPartnerType) {
        List<ConPartnerType> nameList = conPartnerTypeMapper.selectList(new QueryWrapper<ConPartnerType>().lambda()
                .eq(ConPartnerType::getName, conPartnerType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPartnerType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPartnerType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPartnerType response = conPartnerTypeMapper.selectConPartnerTypeById(conPartnerType.getSid());
        int row = conPartnerTypeMapper.updateAllById(conPartnerType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPartnerType.getSid(), BusinessType.CHANGE.getValue(), response, conPartnerType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除类型_业务合作伙伴
     *
     * @param sids 需要删除的类型_业务合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPartnerTypeByIds(List<Long> sids) {
        return conPartnerTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conPartnerType
    * @return
    */
    @Override
    public int changeStatus(ConPartnerType conPartnerType){
        int row=0;
        Long[] sids=conPartnerType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPartnerType.setSid(id);
                row=conPartnerTypeMapper.updateById( conPartnerType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conPartnerType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conPartnerType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conPartnerType
     * @return
     */
    @Override
    public int check(ConPartnerType conPartnerType){
        int row=0;
        Long[] sids=conPartnerType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPartnerType.setSid(id);
                row=conPartnerTypeMapper.updateById( conPartnerType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conPartnerType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
