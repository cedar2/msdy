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
import com.platform.ems.plug.domain.ConContactBusinessTypeC;
import com.platform.ems.plug.mapper.ConContactBusinessTypeCMapper;
import com.platform.ems.plug.service.IConContactBusinessTypeCService;
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
 * 对接业务类型_客户Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConContactBusinessTypeCServiceImpl extends ServiceImpl<ConContactBusinessTypeCMapper,ConContactBusinessTypeC>  implements IConContactBusinessTypeCService {
    @Autowired
    private ConContactBusinessTypeCMapper conContactBusinessTypeCMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "对接业务类型_客户";
    /**
     * 查询对接业务类型_客户
     *
     * @param sid 对接业务类型_客户ID
     * @return 对接业务类型_客户
     */
    @Override
    public ConContactBusinessTypeC selectConContactBusinessTypeCById(Long sid) {
        ConContactBusinessTypeC conContactBusinessTypeC = conContactBusinessTypeCMapper.selectConContactBusinessTypeCById(sid);
        MongodbUtil.find(conContactBusinessTypeC);
        return  conContactBusinessTypeC;
    }

    /**
     * 查询对接业务类型_客户列表
     *
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 对接业务类型_客户
     */
    @Override
    public List<ConContactBusinessTypeC> selectConContactBusinessTypeCList(ConContactBusinessTypeC conContactBusinessTypeC) {
        return conContactBusinessTypeCMapper.selectConContactBusinessTypeCList(conContactBusinessTypeC);
    }

    /**
     * 新增对接业务类型_客户
     * 需要注意编码重复校验
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConContactBusinessTypeC(ConContactBusinessTypeC conContactBusinessTypeC) {
        List<ConContactBusinessTypeC> codeList = conContactBusinessTypeCMapper.selectList(new QueryWrapper<ConContactBusinessTypeC>().lambda()
                .eq(ConContactBusinessTypeC::getCode, conContactBusinessTypeC.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConContactBusinessTypeC> nameList = conContactBusinessTypeCMapper.selectList(new QueryWrapper<ConContactBusinessTypeC>().lambda()
                .eq(ConContactBusinessTypeC::getName, conContactBusinessTypeC.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conContactBusinessTypeCMapper.insert(conContactBusinessTypeC);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conContactBusinessTypeC.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改对接业务类型_客户
     *
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConContactBusinessTypeC(ConContactBusinessTypeC conContactBusinessTypeC) {
        ConContactBusinessTypeC response = conContactBusinessTypeCMapper.selectConContactBusinessTypeCById(conContactBusinessTypeC.getSid());
        int row=conContactBusinessTypeCMapper.updateById(conContactBusinessTypeC);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conContactBusinessTypeC.getSid(), BusinessType.UPDATE.getValue(), response,conContactBusinessTypeC,TITLE);
        }
        return row;
    }

    /**
     * 变更对接业务类型_客户
     *
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConContactBusinessTypeC(ConContactBusinessTypeC conContactBusinessTypeC) {
        List<ConContactBusinessTypeC> nameList = conContactBusinessTypeCMapper.selectList(new QueryWrapper<ConContactBusinessTypeC>().lambda()
                .eq(ConContactBusinessTypeC::getName, conContactBusinessTypeC.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conContactBusinessTypeC.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conContactBusinessTypeC.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConContactBusinessTypeC response = conContactBusinessTypeCMapper.selectConContactBusinessTypeCById(conContactBusinessTypeC.getSid());
        int row = conContactBusinessTypeCMapper.updateAllById(conContactBusinessTypeC);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conContactBusinessTypeC.getSid(), BusinessType.CHANGE.getValue(), response, conContactBusinessTypeC, TITLE);
        }
        return row;
    }

    /**
     * 批量删除对接业务类型_客户
     *
     * @param sids 需要删除的对接业务类型_客户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConContactBusinessTypeCByIds(List<Long> sids) {
        return conContactBusinessTypeCMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conContactBusinessTypeC
    * @return
    */
    @Override
    public int changeStatus(ConContactBusinessTypeC conContactBusinessTypeC){
        int row=0;
        Long[] sids=conContactBusinessTypeC.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conContactBusinessTypeC.setSid(id);
                row=conContactBusinessTypeCMapper.updateById( conContactBusinessTypeC);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conContactBusinessTypeC.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conContactBusinessTypeC.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conContactBusinessTypeC
     * @return
     */
    @Override
    public int check(ConContactBusinessTypeC conContactBusinessTypeC){
        int row=0;
        Long[] sids=conContactBusinessTypeC.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conContactBusinessTypeC.setSid(id);
                row=conContactBusinessTypeCMapper.updateById( conContactBusinessTypeC);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conContactBusinessTypeC.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
