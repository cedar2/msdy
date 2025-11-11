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
import com.platform.ems.plug.domain.ConDataObjectFileType;
import com.platform.ems.plug.mapper.ConDataObjectFileTypeMapper;
import com.platform.ems.plug.service.IConDataObjectFileTypeService;
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
 * 数据对象&附件类型对照Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConDataObjectFileTypeServiceImpl extends ServiceImpl<ConDataObjectFileTypeMapper,ConDataObjectFileType>  implements IConDataObjectFileTypeService {
    @Autowired
    private ConDataObjectFileTypeMapper conDataObjectFileTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "数据对象&附件类型对照";
    /**
     * 查询数据对象&附件类型对照
     *
     * @param sid 数据对象&附件类型对照ID
     * @return 数据对象&附件类型对照
     */
    @Override
    public ConDataObjectFileType selectConDataObjectFileTypeById(Long sid) {
        ConDataObjectFileType conDataObjectFileType = conDataObjectFileTypeMapper.selectConDataObjectFileTypeById(sid);
        MongodbUtil.find(conDataObjectFileType);
        return  conDataObjectFileType;
    }

    /**
     * 查询数据对象&附件类型对照列表
     *
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 数据对象&附件类型对照
     */
    @Override
    public List<ConDataObjectFileType> selectConDataObjectFileTypeList(ConDataObjectFileType conDataObjectFileType) {
        return conDataObjectFileTypeMapper.selectConDataObjectFileTypeList(conDataObjectFileType);
    }

    /**
     * 新增数据对象&附件类型对照
     * 需要注意编码重复校验
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDataObjectFileType(ConDataObjectFileType conDataObjectFileType) {
        List<ConDataObjectFileType> codeList = conDataObjectFileTypeMapper.selectList(new QueryWrapper<ConDataObjectFileType>().lambda()
                .eq(ConDataObjectFileType::getCode, conDataObjectFileType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDataObjectFileType> nameList = conDataObjectFileTypeMapper.selectList(new QueryWrapper<ConDataObjectFileType>().lambda()
                .eq(ConDataObjectFileType::getName, conDataObjectFileType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDataObjectFileTypeMapper.insert(conDataObjectFileType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDataObjectFileType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改数据对象&附件类型对照
     *
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDataObjectFileType(ConDataObjectFileType conDataObjectFileType) {
        ConDataObjectFileType response = conDataObjectFileTypeMapper.selectConDataObjectFileTypeById(conDataObjectFileType.getSid());
        int row=conDataObjectFileTypeMapper.updateById(conDataObjectFileType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDataObjectFileType.getSid(), BusinessType.UPDATE.getValue(), response,conDataObjectFileType,TITLE);
        }
        return row;
    }

    /**
     * 变更数据对象&附件类型对照
     *
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDataObjectFileType(ConDataObjectFileType conDataObjectFileType) {
        List<ConDataObjectFileType> nameList = conDataObjectFileTypeMapper.selectList(new QueryWrapper<ConDataObjectFileType>().lambda()
                .eq(ConDataObjectFileType::getName, conDataObjectFileType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDataObjectFileType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDataObjectFileType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDataObjectFileType response = conDataObjectFileTypeMapper.selectConDataObjectFileTypeById(conDataObjectFileType.getSid());
        int row = conDataObjectFileTypeMapper.updateAllById(conDataObjectFileType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDataObjectFileType.getSid(), BusinessType.CHANGE.getValue(), response, conDataObjectFileType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除数据对象&附件类型对照
     *
     * @param sids 需要删除的数据对象&附件类型对照ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDataObjectFileTypeByIds(List<Long> sids) {
        return conDataObjectFileTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDataObjectFileType
    * @return
    */
    @Override
    public int changeStatus(ConDataObjectFileType conDataObjectFileType){
        int row=0;
        Long[] sids=conDataObjectFileType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDataObjectFileType.setSid(id);
                row=conDataObjectFileTypeMapper.updateById( conDataObjectFileType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDataObjectFileType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDataObjectFileType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDataObjectFileType
     * @return
     */
    @Override
    public int check(ConDataObjectFileType conDataObjectFileType){
        int row=0;
        Long[] sids=conDataObjectFileType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDataObjectFileType.setSid(id);
                row=conDataObjectFileTypeMapper.updateById( conDataObjectFileType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDataObjectFileType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
