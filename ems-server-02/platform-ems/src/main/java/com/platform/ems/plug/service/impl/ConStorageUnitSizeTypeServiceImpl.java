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
import com.platform.ems.plug.domain.ConStorageUnitSizeType;
import com.platform.ems.plug.mapper.ConStorageUnitSizeTypeMapper;
import com.platform.ems.plug.service.IConStorageUnitSizeTypeService;
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
 * 托盘规格类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConStorageUnitSizeTypeServiceImpl extends ServiceImpl<ConStorageUnitSizeTypeMapper,ConStorageUnitSizeType>  implements IConStorageUnitSizeTypeService {
    @Autowired
    private ConStorageUnitSizeTypeMapper conStorageUnitSizeTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "托盘规格类型";
    /**
     * 查询托盘规格类型
     *
     * @param sid 托盘规格类型ID
     * @return 托盘规格类型
     */
    @Override
    public ConStorageUnitSizeType selectConStorageUnitSizeTypeById(Long sid) {
        ConStorageUnitSizeType conStorageUnitSizeType = conStorageUnitSizeTypeMapper.selectConStorageUnitSizeTypeById(sid);
        MongodbUtil.find(conStorageUnitSizeType);
        return  conStorageUnitSizeType;
    }

    /**
     * 查询托盘规格类型列表
     *
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 托盘规格类型
     */
    @Override
    public List<ConStorageUnitSizeType> selectConStorageUnitSizeTypeList(ConStorageUnitSizeType conStorageUnitSizeType) {
        return conStorageUnitSizeTypeMapper.selectConStorageUnitSizeTypeList(conStorageUnitSizeType);
    }

    /**
     * 新增托盘规格类型
     * 需要注意编码重复校验
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConStorageUnitSizeType(ConStorageUnitSizeType conStorageUnitSizeType) {
        List<ConStorageUnitSizeType> codeList = conStorageUnitSizeTypeMapper.selectList(new QueryWrapper<ConStorageUnitSizeType>().lambda()
                .eq(ConStorageUnitSizeType::getCode, conStorageUnitSizeType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConStorageUnitSizeType> nameList = conStorageUnitSizeTypeMapper.selectList(new QueryWrapper<ConStorageUnitSizeType>().lambda()
                .eq(ConStorageUnitSizeType::getName, conStorageUnitSizeType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conStorageUnitSizeTypeMapper.insert(conStorageUnitSizeType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conStorageUnitSizeType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改托盘规格类型
     *
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConStorageUnitSizeType(ConStorageUnitSizeType conStorageUnitSizeType) {
        ConStorageUnitSizeType response = conStorageUnitSizeTypeMapper.selectConStorageUnitSizeTypeById(conStorageUnitSizeType.getSid());
        int row=conStorageUnitSizeTypeMapper.updateById(conStorageUnitSizeType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conStorageUnitSizeType.getSid(), BusinessType.UPDATE.getValue(), response,conStorageUnitSizeType,TITLE);
        }
        return row;
    }

    /**
     * 变更托盘规格类型
     *
     * @param conStorageUnitSizeType 托盘规格类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConStorageUnitSizeType(ConStorageUnitSizeType conStorageUnitSizeType) {
        List<ConStorageUnitSizeType> nameList = conStorageUnitSizeTypeMapper.selectList(new QueryWrapper<ConStorageUnitSizeType>().lambda()
                .eq(ConStorageUnitSizeType::getName, conStorageUnitSizeType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conStorageUnitSizeType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conStorageUnitSizeType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConStorageUnitSizeType response = conStorageUnitSizeTypeMapper.selectConStorageUnitSizeTypeById(conStorageUnitSizeType.getSid());
        int row = conStorageUnitSizeTypeMapper.updateAllById(conStorageUnitSizeType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conStorageUnitSizeType.getSid(), BusinessType.CHANGE.getValue(), response, conStorageUnitSizeType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除托盘规格类型
     *
     * @param sids 需要删除的托盘规格类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConStorageUnitSizeTypeByIds(List<Long> sids) {
        return conStorageUnitSizeTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conStorageUnitSizeType
    * @return
    */
    @Override
    public int changeStatus(ConStorageUnitSizeType conStorageUnitSizeType){
        int row=0;
        Long[] sids=conStorageUnitSizeType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageUnitSizeType.setSid(id);
                row=conStorageUnitSizeTypeMapper.updateById( conStorageUnitSizeType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conStorageUnitSizeType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conStorageUnitSizeType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conStorageUnitSizeType
     * @return
     */
    @Override
    public int check(ConStorageUnitSizeType conStorageUnitSizeType){
        int row=0;
        Long[] sids=conStorageUnitSizeType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageUnitSizeType.setSid(id);
                row=conStorageUnitSizeTypeMapper.updateById( conStorageUnitSizeType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conStorageUnitSizeType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
