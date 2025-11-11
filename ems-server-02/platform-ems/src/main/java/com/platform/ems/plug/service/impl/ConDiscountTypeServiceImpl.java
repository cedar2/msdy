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
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.mapper.ConDiscountTypeMapper;
import com.platform.ems.plug.service.IConDiscountTypeService;
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
 * 折扣类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConDiscountTypeServiceImpl extends ServiceImpl<ConDiscountTypeMapper,ConDiscountType>  implements IConDiscountTypeService {
    @Autowired
    private ConDiscountTypeMapper conDiscountTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "折扣类型";
    /**
     * 查询折扣类型
     *
     * @param sid 折扣类型ID
     * @return 折扣类型
     */
    @Override
    public ConDiscountType selectConDiscountTypeById(Long sid) {
        ConDiscountType conDiscountType = conDiscountTypeMapper.selectConDiscountTypeById(sid);
        MongodbUtil.find(conDiscountType);
        return  conDiscountType;
    }

    /**
     * 查询折扣类型列表
     *
     * @param conDiscountType 折扣类型
     * @return 折扣类型
     */
    @Override
    public List<ConDiscountType> selectConDiscountTypeList(ConDiscountType conDiscountType) {
        return conDiscountTypeMapper.selectConDiscountTypeList(conDiscountType);
    }

    /**
     * 新增折扣类型
     * 需要注意编码重复校验
     * @param conDiscountType 折扣类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDiscountType(ConDiscountType conDiscountType) {
        List<ConDiscountType> codeList = conDiscountTypeMapper.selectList(new QueryWrapper<ConDiscountType>().lambda()
                .eq(ConDiscountType::getCode, conDiscountType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDiscountType> nameList = conDiscountTypeMapper.selectList(new QueryWrapper<ConDiscountType>().lambda()
                .eq(ConDiscountType::getName, conDiscountType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDiscountTypeMapper.insert(conDiscountType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDiscountType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改折扣类型
     *
     * @param conDiscountType 折扣类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDiscountType(ConDiscountType conDiscountType) {
        List<ConDiscountType> nameList = conDiscountTypeMapper.selectList(new QueryWrapper<ConDiscountType>().lambda()
                .eq(ConDiscountType::getName, conDiscountType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDiscountType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        ConDiscountType response = conDiscountTypeMapper.selectConDiscountTypeById(conDiscountType.getSid());
        int row=conDiscountTypeMapper.updateById(conDiscountType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDiscountType.getSid(), BusinessType.UPDATE.getValue(), response,conDiscountType,TITLE);
        }
        return row;
    }

    /**
     * 变更折扣类型
     *
     * @param conDiscountType 折扣类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDiscountType(ConDiscountType conDiscountType) {
        List<ConDiscountType> nameList = conDiscountTypeMapper.selectList(new QueryWrapper<ConDiscountType>().lambda()
                .eq(ConDiscountType::getName, conDiscountType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDiscountType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDiscountType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDiscountType response = conDiscountTypeMapper.selectConDiscountTypeById(conDiscountType.getSid());
        int row = conDiscountTypeMapper.updateAllById(conDiscountType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDiscountType.getSid(), BusinessType.CHANGE.getValue(), response, conDiscountType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除折扣类型
     *
     * @param sids 需要删除的折扣类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDiscountTypeByIds(List<Long> sids) {
        return conDiscountTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDiscountType
    * @return
    */
    @Override
    public int changeStatus(ConDiscountType conDiscountType){
        int row=0;
        Long[] sids=conDiscountType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDiscountType.setSid(id);
                row=conDiscountTypeMapper.updateById( conDiscountType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDiscountType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDiscountType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDiscountType
     * @return
     */
    @Override
    public int check(ConDiscountType conDiscountType){
        int row=0;
        Long[] sids=conDiscountType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDiscountType.setSid(id);
                row=conDiscountTypeMapper.updateById( conDiscountType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDiscountType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDiscountType> getConDiscountTypeList() {
        return conDiscountTypeMapper.getConDiscountTypeList();
    }
}
