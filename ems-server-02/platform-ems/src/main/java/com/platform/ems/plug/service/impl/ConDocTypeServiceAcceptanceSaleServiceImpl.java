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
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptanceSale;
import com.platform.ems.plug.mapper.ConDocTypeServiceAcceptanceSaleMapper;
import com.platform.ems.plug.service.IConDocTypeServiceAcceptanceSaleService;
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
 * 单据类型_服务销售验收单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeServiceAcceptanceSaleServiceImpl extends ServiceImpl<ConDocTypeServiceAcceptanceSaleMapper,ConDocTypeServiceAcceptanceSale>  implements IConDocTypeServiceAcceptanceSaleService {
    @Autowired
    private ConDocTypeServiceAcceptanceSaleMapper conDocTypeServiceAcceptanceSaleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_服务销售验收单";
    /**
     * 查询单据类型_服务销售验收单
     *
     * @param sid 单据类型_服务销售验收单ID
     * @return 单据类型_服务销售验收单
     */
    @Override
    public ConDocTypeServiceAcceptanceSale selectConDocTypeServiceAcceptanceSaleById(Long sid) {
        ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale = conDocTypeServiceAcceptanceSaleMapper.selectConDocTypeServiceAcceptanceSaleById(sid);
        MongodbUtil.find(conDocTypeServiceAcceptanceSale);
        return  conDocTypeServiceAcceptanceSale;
    }

    /**
     * 查询单据类型_服务销售验收单列表
     *
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 单据类型_服务销售验收单
     */
    @Override
    public List<ConDocTypeServiceAcceptanceSale> selectConDocTypeServiceAcceptanceSaleList(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        return conDocTypeServiceAcceptanceSaleMapper.selectConDocTypeServiceAcceptanceSaleList(conDocTypeServiceAcceptanceSale);
    }

    /**
     * 新增单据类型_服务销售验收单
     * 需要注意编码重复校验
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeServiceAcceptanceSale(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        List<ConDocTypeServiceAcceptanceSale> codeList = conDocTypeServiceAcceptanceSaleMapper.selectList(new QueryWrapper<ConDocTypeServiceAcceptanceSale>().lambda()
                .eq(ConDocTypeServiceAcceptanceSale::getCode, conDocTypeServiceAcceptanceSale.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeServiceAcceptanceSale> nameList = conDocTypeServiceAcceptanceSaleMapper.selectList(new QueryWrapper<ConDocTypeServiceAcceptanceSale>().lambda()
                .eq(ConDocTypeServiceAcceptanceSale::getName, conDocTypeServiceAcceptanceSale.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeServiceAcceptanceSaleMapper.insert(conDocTypeServiceAcceptanceSale);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeServiceAcceptanceSale.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_服务销售验收单
     *
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeServiceAcceptanceSale(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        ConDocTypeServiceAcceptanceSale response = conDocTypeServiceAcceptanceSaleMapper.selectConDocTypeServiceAcceptanceSaleById(conDocTypeServiceAcceptanceSale.getSid());
        int row=conDocTypeServiceAcceptanceSaleMapper.updateById(conDocTypeServiceAcceptanceSale);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeServiceAcceptanceSale.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeServiceAcceptanceSale,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_服务销售验收单
     *
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeServiceAcceptanceSale(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        List<ConDocTypeServiceAcceptanceSale> nameList = conDocTypeServiceAcceptanceSaleMapper.selectList(new QueryWrapper<ConDocTypeServiceAcceptanceSale>().lambda()
                .eq(ConDocTypeServiceAcceptanceSale::getName, conDocTypeServiceAcceptanceSale.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeServiceAcceptanceSale.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeServiceAcceptanceSale.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeServiceAcceptanceSale response = conDocTypeServiceAcceptanceSaleMapper.selectConDocTypeServiceAcceptanceSaleById(conDocTypeServiceAcceptanceSale.getSid());
        int row = conDocTypeServiceAcceptanceSaleMapper.updateAllById(conDocTypeServiceAcceptanceSale);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeServiceAcceptanceSale.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeServiceAcceptanceSale, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_服务销售验收单
     *
     * @param sids 需要删除的单据类型_服务销售验收单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeServiceAcceptanceSaleByIds(List<Long> sids) {
        return conDocTypeServiceAcceptanceSaleMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeServiceAcceptanceSale
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale){
        int row=0;
        Long[] sids=conDocTypeServiceAcceptanceSale.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeServiceAcceptanceSale.setSid(id);
                row=conDocTypeServiceAcceptanceSaleMapper.updateById( conDocTypeServiceAcceptanceSale);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeServiceAcceptanceSale.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeServiceAcceptanceSale.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeServiceAcceptanceSale
     * @return
     */
    @Override
    public int check(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale){
        int row=0;
        Long[] sids=conDocTypeServiceAcceptanceSale.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeServiceAcceptanceSale.setSid(id);
                row=conDocTypeServiceAcceptanceSaleMapper.updateById( conDocTypeServiceAcceptanceSale);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeServiceAcceptanceSale.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
