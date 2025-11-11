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
import com.platform.ems.plug.domain.ConShelfType;
import com.platform.ems.plug.mapper.ConShelfTypeMapper;
import com.platform.ems.plug.service.IConShelfTypeService;
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
 * 货架类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConShelfTypeServiceImpl extends ServiceImpl<ConShelfTypeMapper,ConShelfType>  implements IConShelfTypeService {
    @Autowired
    private ConShelfTypeMapper conShelfTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "货架类型";
    /**
     * 查询货架类型
     *
     * @param sid 货架类型ID
     * @return 货架类型
     */
    @Override
    public ConShelfType selectConShelfTypeById(Long sid) {
        ConShelfType conShelfType = conShelfTypeMapper.selectConShelfTypeById(sid);
        MongodbUtil.find(conShelfType);
        return  conShelfType;
    }

    /**
     * 查询货架类型列表
     *
     * @param conShelfType 货架类型
     * @return 货架类型
     */
    @Override
    public List<ConShelfType> selectConShelfTypeList(ConShelfType conShelfType) {
        return conShelfTypeMapper.selectConShelfTypeList(conShelfType);
    }

    /**
     * 新增货架类型
     * 需要注意编码重复校验
     * @param conShelfType 货架类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConShelfType(ConShelfType conShelfType) {
        List<ConShelfType> codeList = conShelfTypeMapper.selectList(new QueryWrapper<ConShelfType>().lambda()
                .eq(ConShelfType::getCode, conShelfType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConShelfType> nameList = conShelfTypeMapper.selectList(new QueryWrapper<ConShelfType>().lambda()
                .eq(ConShelfType::getName, conShelfType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conShelfTypeMapper.insert(conShelfType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conShelfType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改货架类型
     *
     * @param conShelfType 货架类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConShelfType(ConShelfType conShelfType) {
        ConShelfType response = conShelfTypeMapper.selectConShelfTypeById(conShelfType.getSid());
        int row=conShelfTypeMapper.updateById(conShelfType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conShelfType.getSid(), BusinessType.UPDATE.getValue(), response,conShelfType,TITLE);
        }
        return row;
    }

    /**
     * 变更货架类型
     *
     * @param conShelfType 货架类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConShelfType(ConShelfType conShelfType) {
        List<ConShelfType> nameList = conShelfTypeMapper.selectList(new QueryWrapper<ConShelfType>().lambda()
                .eq(ConShelfType::getName, conShelfType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conShelfType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conShelfType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConShelfType response = conShelfTypeMapper.selectConShelfTypeById(conShelfType.getSid());
        int row = conShelfTypeMapper.updateAllById(conShelfType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conShelfType.getSid(), BusinessType.CHANGE.getValue(), response, conShelfType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除货架类型
     *
     * @param sids 需要删除的货架类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConShelfTypeByIds(List<Long> sids) {
        return conShelfTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conShelfType
    * @return
    */
    @Override
    public int changeStatus(ConShelfType conShelfType){
        int row=0;
        Long[] sids=conShelfType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShelfType.setSid(id);
                row=conShelfTypeMapper.updateById( conShelfType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conShelfType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conShelfType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conShelfType
     * @return
     */
    @Override
    public int check(ConShelfType conShelfType){
        int row=0;
        Long[] sids=conShelfType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShelfType.setSid(id);
                row=conShelfTypeMapper.updateById( conShelfType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conShelfType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
