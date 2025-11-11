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
import com.platform.ems.plug.domain.ConLocationType;
import com.platform.ems.plug.mapper.ConLocationTypeMapper;
import com.platform.ems.plug.service.IConLocationTypeService;
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
 * 库位类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConLocationTypeServiceImpl extends ServiceImpl<ConLocationTypeMapper,ConLocationType>  implements IConLocationTypeService {
    @Autowired
    private ConLocationTypeMapper conLocationTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "库位类型";
    /**
     * 查询库位类型
     *
     * @param sid 库位类型ID
     * @return 库位类型
     */
    @Override
    public ConLocationType selectConLocationTypeById(Long sid) {
        ConLocationType conLocationType = conLocationTypeMapper.selectConLocationTypeById(sid);
        MongodbUtil.find(conLocationType);
        return  conLocationType;
    }

    /**
     * 查询库位类型列表
     *
     * @param conLocationType 库位类型
     * @return 库位类型
     */
    @Override
    public List<ConLocationType> selectConLocationTypeList(ConLocationType conLocationType) {
        return conLocationTypeMapper.selectConLocationTypeList(conLocationType);
    }

    /**
     * 新增库位类型
     * 需要注意编码重复校验
     * @param conLocationType 库位类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConLocationType(ConLocationType conLocationType) {
        List<ConLocationType> codeList = conLocationTypeMapper.selectList(new QueryWrapper<ConLocationType>().lambda()
                .eq(ConLocationType::getCode, conLocationType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConLocationType> nameList = conLocationTypeMapper.selectList(new QueryWrapper<ConLocationType>().lambda()
                .eq(ConLocationType::getName, conLocationType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conLocationTypeMapper.insert(conLocationType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conLocationType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改库位类型
     *
     * @param conLocationType 库位类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConLocationType(ConLocationType conLocationType) {
        ConLocationType response = conLocationTypeMapper.selectConLocationTypeById(conLocationType.getSid());
        int row=conLocationTypeMapper.updateById(conLocationType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conLocationType.getSid(), BusinessType.UPDATE.getValue(), response,conLocationType,TITLE);
        }
        return row;
    }

    /**
     * 变更库位类型
     *
     * @param conLocationType 库位类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConLocationType(ConLocationType conLocationType) {
        List<ConLocationType> nameList = conLocationTypeMapper.selectList(new QueryWrapper<ConLocationType>().lambda()
                .eq(ConLocationType::getName, conLocationType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conLocationType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conLocationType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConLocationType response = conLocationTypeMapper.selectConLocationTypeById(conLocationType.getSid());
        int row = conLocationTypeMapper.updateAllById(conLocationType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conLocationType.getSid(), BusinessType.CHANGE.getValue(), response, conLocationType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除库位类型
     *
     * @param sids 需要删除的库位类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConLocationTypeByIds(List<Long> sids) {
        return conLocationTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conLocationType
    * @return
    */
    @Override
    public int changeStatus(ConLocationType conLocationType){
        int row=0;
        Long[] sids=conLocationType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conLocationType.setSid(id);
                row=conLocationTypeMapper.updateById( conLocationType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conLocationType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conLocationType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conLocationType
     * @return
     */
    @Override
    public int check(ConLocationType conLocationType){
        int row=0;
        Long[] sids=conLocationType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conLocationType.setSid(id);
                row=conLocationTypeMapper.updateById( conLocationType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conLocationType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
