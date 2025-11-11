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
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConBookTypeMapper;
import com.platform.ems.plug.domain.ConBookType;
import com.platform.ems.plug.service.IConBookTypeService;

/**
 * 流水类型_财务Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Service
@SuppressWarnings("all")
public class ConBookTypeServiceImpl extends ServiceImpl<ConBookTypeMapper,ConBookType>  implements IConBookTypeService {
    @Autowired
    private ConBookTypeMapper conBookTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "流水类型_财务";
    /**
     * 查询流水类型_财务
     *
     * @param sid 流水类型_财务ID
     * @return 流水类型_财务
     */
    @Override
    public ConBookType selectConBookTypeById(Long sid) {
        ConBookType conBookType = conBookTypeMapper.selectConBookTypeById(sid);
        MongodbUtil.find(conBookType);
        return  conBookType;
    }

    /**
     * 查询流水类型_财务列表
     *
     * @param conBookType 流水类型_财务
     * @return 流水类型_财务
     */
    @Override
    public List<ConBookType> selectConBookTypeList(ConBookType conBookType) {
        return conBookTypeMapper.selectConBookTypeList(conBookType);
    }

    /**
     * 新增流水类型_财务
     * 需要注意编码重复校验
     * @param conBookType 流水类型_财务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBookType(ConBookType conBookType) {
        List<ConBookType> codeList = conBookTypeMapper.selectList(new QueryWrapper<ConBookType>().lambda()
                .eq(ConBookType::getCode, conBookType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBookType> nameList = conBookTypeMapper.selectList(new QueryWrapper<ConBookType>().lambda()
                .eq(ConBookType::getName, conBookType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBookTypeMapper.insert(conBookType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBookType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改流水类型_财务
     *
     * @param conBookType 流水类型_财务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBookType(ConBookType conBookType) {
        ConBookType response = conBookTypeMapper.selectConBookTypeById(conBookType.getSid());
        checkNameUnique(conBookType);
        int row=conBookTypeMapper.updateById(conBookType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBookType.getSid(), BusinessType.UPDATE.getValue(), response,conBookType,TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(ConBookType conBookType) {
        List<ConBookType> nameList = conBookTypeMapper.selectList(new QueryWrapper<ConBookType>().lambda()
                .eq(ConBookType::getName, conBookType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBookType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
    }

    /**
     * 变更流水类型_财务
     *
     * @param conBookType 流水类型_财务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBookType(ConBookType conBookType) {
        ConBookType response = conBookTypeMapper.selectConBookTypeById(conBookType.getSid());
        checkNameUnique(conBookType);
        conBookType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row=conBookTypeMapper.updateAllById(conBookType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBookType.getSid(), BusinessType.CHANGE.getValue(), response,conBookType,TITLE);
        }
        return row;
    }

    /**
     * 批量删除流水类型_财务
     *
     * @param sids 需要删除的流水类型_财务ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBookTypeByIds(List<Long> sids) {
        return conBookTypeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     * @param conBookType
     * @return
     */
    @Override
    public int changeStatus(ConBookType conBookType){
        int row=0;
        Long[] sids=conBookType.getSidList();
        if(sids!=null&&sids.length>0){
            row=conBookTypeMapper.update(null, new UpdateWrapper<ConBookType>().lambda().set(ConBookType::getStatus ,conBookType.getStatus() )
                    .in(ConBookType::getSid,sids));
            for(Long id:sids){
                conBookType.setSid(id);
                row=conBookTypeMapper.updateById( conBookType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBookType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBookType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBookType
     * @return
     */
    @Override
    public int check(ConBookType conBookType){
        int row=0;
        Long[] sids=conBookType.getSidList();
        if(sids!=null&&sids.length>0){
            row=conBookTypeMapper.update(null,new UpdateWrapper<ConBookType>().lambda().set(ConBookType::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConBookType::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 流水类型财务拉框列表
     */
    @Override
    public List<ConBookType> getConBookTypeList(ConBookType conBookType) {
        return conBookTypeMapper.getConBookTypeList(conBookType);
    }
}
