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
import com.platform.ems.plug.domain.ConReasonTypeStorage;
import com.platform.ems.plug.mapper.ConReasonTypeStorageMapper;
import com.platform.ems.plug.service.IConReasonTypeStorageService;
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
 * 原因类型(库存管理)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConReasonTypeStorageServiceImpl extends ServiceImpl<ConReasonTypeStorageMapper,ConReasonTypeStorage>  implements IConReasonTypeStorageService {
    @Autowired
    private ConReasonTypeStorageMapper conReasonTypeStorageMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "原因类型(库存管理)";
    /**
     * 查询原因类型(库存管理)
     *
     * @param sid 原因类型(库存管理)ID
     * @return 原因类型(库存管理)
     */
    @Override
    public ConReasonTypeStorage selectConReasonTypeStorageById(Long sid) {
        ConReasonTypeStorage conReasonTypeStorage = conReasonTypeStorageMapper.selectConReasonTypeStorageById(sid);
        MongodbUtil.find(conReasonTypeStorage);
        return  conReasonTypeStorage;
    }

    @Override
    public List<ConReasonTypeStorage> getList() {
        return conReasonTypeStorageMapper.getList();
    }

    /**
     * 查询原因类型(库存管理)列表
     *
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 原因类型(库存管理)
     */
    @Override
    public List<ConReasonTypeStorage> selectConReasonTypeStorageList(ConReasonTypeStorage conReasonTypeStorage) {
        return conReasonTypeStorageMapper.selectConReasonTypeStorageList(conReasonTypeStorage);
    }

    /**
     * 新增原因类型(库存管理)
     * 需要注意编码重复校验
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConReasonTypeStorage(ConReasonTypeStorage conReasonTypeStorage) {
        List<ConReasonTypeStorage> codeList = conReasonTypeStorageMapper.selectList(new QueryWrapper<ConReasonTypeStorage>().lambda()
                .eq(ConReasonTypeStorage::getCode, conReasonTypeStorage.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConReasonTypeStorage> nameList = conReasonTypeStorageMapper.selectList(new QueryWrapper<ConReasonTypeStorage>().lambda()
                .eq(ConReasonTypeStorage::getName, conReasonTypeStorage.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conReasonTypeStorageMapper.insert(conReasonTypeStorage);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conReasonTypeStorage.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改原因类型(库存管理)
     *
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConReasonTypeStorage(ConReasonTypeStorage conReasonTypeStorage) {
        ConReasonTypeStorage response = conReasonTypeStorageMapper.selectConReasonTypeStorageById(conReasonTypeStorage.getSid());
        int row=conReasonTypeStorageMapper.updateById(conReasonTypeStorage);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conReasonTypeStorage.getSid(), BusinessType.UPDATE.getValue(), response,conReasonTypeStorage,TITLE);
        }
        return row;
    }

    /**
     * 变更原因类型(库存管理)
     *
     * @param conReasonTypeStorage 原因类型(库存管理)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConReasonTypeStorage(ConReasonTypeStorage conReasonTypeStorage) {
        List<ConReasonTypeStorage> nameList = conReasonTypeStorageMapper.selectList(new QueryWrapper<ConReasonTypeStorage>().lambda()
                .eq(ConReasonTypeStorage::getName, conReasonTypeStorage.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conReasonTypeStorage.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conReasonTypeStorage.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConReasonTypeStorage response = conReasonTypeStorageMapper.selectConReasonTypeStorageById(conReasonTypeStorage.getSid());
        int row = conReasonTypeStorageMapper.updateAllById(conReasonTypeStorage);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conReasonTypeStorage.getSid(), BusinessType.CHANGE.getValue(), response, conReasonTypeStorage, TITLE);
        }
        return row;
    }

    /**
     * 批量删除原因类型(库存管理)
     *
     * @param sids 需要删除的原因类型(库存管理)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConReasonTypeStorageByIds(List<Long> sids) {
        return conReasonTypeStorageMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conReasonTypeStorage
    * @return
    */
    @Override
    public int changeStatus(ConReasonTypeStorage conReasonTypeStorage){
        int row=0;
        Long[] sids=conReasonTypeStorage.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReasonTypeStorage.setSid(id);
                row=conReasonTypeStorageMapper.updateById( conReasonTypeStorage);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conReasonTypeStorage.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conReasonTypeStorage.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conReasonTypeStorage
     * @return
     */
    @Override
    public int check(ConReasonTypeStorage conReasonTypeStorage){
        int row=0;
        Long[] sids=conReasonTypeStorage.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReasonTypeStorage.setSid(id);
                row=conReasonTypeStorageMapper.updateById( conReasonTypeStorage);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conReasonTypeStorage.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
