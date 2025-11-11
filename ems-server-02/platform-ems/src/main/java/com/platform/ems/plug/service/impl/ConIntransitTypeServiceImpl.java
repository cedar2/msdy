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
import com.platform.ems.plug.domain.ConIntransitType;
import com.platform.ems.plug.mapper.ConIntransitTypeMapper;
import com.platform.ems.plug.service.IConIntransitTypeService;
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
 * 在途类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConIntransitTypeServiceImpl extends ServiceImpl<ConIntransitTypeMapper,ConIntransitType>  implements IConIntransitTypeService {
    @Autowired
    private ConIntransitTypeMapper conIntransitTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "在途类型";
    /**
     * 查询在途类型
     *
     * @param sid 在途类型ID
     * @return 在途类型
     */
    @Override
    public ConIntransitType selectConIntransitTypeById(Long sid) {
        ConIntransitType conIntransitType = conIntransitTypeMapper.selectConIntransitTypeById(sid);
        MongodbUtil.find(conIntransitType);
        return  conIntransitType;
    }

    /**
     * 查询在途类型列表
     *
     * @param conIntransitType 在途类型
     * @return 在途类型
     */
    @Override
    public List<ConIntransitType> selectConIntransitTypeList(ConIntransitType conIntransitType) {
        return conIntransitTypeMapper.selectConIntransitTypeList(conIntransitType);
    }

    /**
     * 新增在途类型
     * 需要注意编码重复校验
     * @param conIntransitType 在途类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConIntransitType(ConIntransitType conIntransitType) {
        List<ConIntransitType> codeList = conIntransitTypeMapper.selectList(new QueryWrapper<ConIntransitType>().lambda()
                .eq(ConIntransitType::getCode, conIntransitType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConIntransitType> nameList = conIntransitTypeMapper.selectList(new QueryWrapper<ConIntransitType>().lambda()
                .eq(ConIntransitType::getName, conIntransitType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conIntransitTypeMapper.insert(conIntransitType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conIntransitType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改在途类型
     *
     * @param conIntransitType 在途类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConIntransitType(ConIntransitType conIntransitType) {
        ConIntransitType response = conIntransitTypeMapper.selectConIntransitTypeById(conIntransitType.getSid());
        int row=conIntransitTypeMapper.updateById(conIntransitType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conIntransitType.getSid(), BusinessType.UPDATE.getValue(), response,conIntransitType,TITLE);
        }
        return row;
    }

    /**
     * 变更在途类型
     *
     * @param conIntransitType 在途类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConIntransitType(ConIntransitType conIntransitType) {
        List<ConIntransitType> nameList = conIntransitTypeMapper.selectList(new QueryWrapper<ConIntransitType>().lambda()
                .eq(ConIntransitType::getName, conIntransitType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conIntransitType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conIntransitType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConIntransitType response = conIntransitTypeMapper.selectConIntransitTypeById(conIntransitType.getSid());
        int row = conIntransitTypeMapper.updateAllById(conIntransitType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conIntransitType.getSid(), BusinessType.CHANGE.getValue(), response, conIntransitType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除在途类型
     *
     * @param sids 需要删除的在途类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConIntransitTypeByIds(List<Long> sids) {
        return conIntransitTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conIntransitType
    * @return
    */
    @Override
    public int changeStatus(ConIntransitType conIntransitType){
        int row=0;
        Long[] sids=conIntransitType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conIntransitType.setSid(id);
                row=conIntransitTypeMapper.updateById( conIntransitType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conIntransitType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conIntransitType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conIntransitType
     * @return
     */
    @Override
    public int check(ConIntransitType conIntransitType){
        int row=0;
        Long[] sids=conIntransitType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conIntransitType.setSid(id);
                row=conIntransitTypeMapper.updateById( conIntransitType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conIntransitType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
