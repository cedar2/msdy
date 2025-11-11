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
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptanceSale;
import com.platform.ems.plug.mapper.ConBuTypeServiceAcceptanceSaleMapper;
import com.platform.ems.plug.service.IConBuTypeServiceAcceptanceSaleService;
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
 * 业务类型_服务销售验收单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeServiceAcceptanceSaleServiceImpl extends ServiceImpl<ConBuTypeServiceAcceptanceSaleMapper,ConBuTypeServiceAcceptanceSale>  implements IConBuTypeServiceAcceptanceSaleService {
    @Autowired
    private ConBuTypeServiceAcceptanceSaleMapper conBuTypeServiceAcceptanceSaleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_服务销售验收单";
    /**
     * 查询业务类型_服务销售验收单
     *
     * @param sid 业务类型_服务销售验收单ID
     * @return 业务类型_服务销售验收单
     */
    @Override
    public ConBuTypeServiceAcceptanceSale selectConBuTypeServiceAcceptanceSaleById(Long sid) {
        ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale = conBuTypeServiceAcceptanceSaleMapper.selectConBuTypeServiceAcceptanceSaleById(sid);
        MongodbUtil.find(conBuTypeServiceAcceptanceSale);
        return  conBuTypeServiceAcceptanceSale;
    }

    /**
     * 查询业务类型_服务销售验收单列表
     *
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 业务类型_服务销售验收单
     */
    @Override
    public List<ConBuTypeServiceAcceptanceSale> selectConBuTypeServiceAcceptanceSaleList(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale) {
        return conBuTypeServiceAcceptanceSaleMapper.selectConBuTypeServiceAcceptanceSaleList(conBuTypeServiceAcceptanceSale);
    }

    /**
     * 新增业务类型_服务销售验收单
     * 需要注意编码重复校验
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeServiceAcceptanceSale(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale) {
        List<ConBuTypeServiceAcceptanceSale> codeList = conBuTypeServiceAcceptanceSaleMapper.selectList(new QueryWrapper<ConBuTypeServiceAcceptanceSale>().lambda()
                .eq(ConBuTypeServiceAcceptanceSale::getCode, conBuTypeServiceAcceptanceSale.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeServiceAcceptanceSale> nameList = conBuTypeServiceAcceptanceSaleMapper.selectList(new QueryWrapper<ConBuTypeServiceAcceptanceSale>().lambda()
                .eq(ConBuTypeServiceAcceptanceSale::getName, conBuTypeServiceAcceptanceSale.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeServiceAcceptanceSaleMapper.insert(conBuTypeServiceAcceptanceSale);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeServiceAcceptanceSale.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_服务销售验收单
     *
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeServiceAcceptanceSale(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale) {
        ConBuTypeServiceAcceptanceSale response = conBuTypeServiceAcceptanceSaleMapper.selectConBuTypeServiceAcceptanceSaleById(conBuTypeServiceAcceptanceSale.getSid());
        int row=conBuTypeServiceAcceptanceSaleMapper.updateById(conBuTypeServiceAcceptanceSale);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeServiceAcceptanceSale.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeServiceAcceptanceSale,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_服务销售验收单
     *
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeServiceAcceptanceSale(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale) {
        List<ConBuTypeServiceAcceptanceSale> nameList = conBuTypeServiceAcceptanceSaleMapper.selectList(new QueryWrapper<ConBuTypeServiceAcceptanceSale>().lambda()
                .eq(ConBuTypeServiceAcceptanceSale::getName, conBuTypeServiceAcceptanceSale.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeServiceAcceptanceSale.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeServiceAcceptanceSale.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeServiceAcceptanceSale response = conBuTypeServiceAcceptanceSaleMapper.selectConBuTypeServiceAcceptanceSaleById(conBuTypeServiceAcceptanceSale.getSid());
        int row = conBuTypeServiceAcceptanceSaleMapper.updateAllById(conBuTypeServiceAcceptanceSale);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeServiceAcceptanceSale.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeServiceAcceptanceSale, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_服务销售验收单
     *
     * @param sids 需要删除的业务类型_服务销售验收单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeServiceAcceptanceSaleByIds(List<Long> sids) {
        return conBuTypeServiceAcceptanceSaleMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeServiceAcceptanceSale
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale){
        int row=0;
        Long[] sids=conBuTypeServiceAcceptanceSale.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeServiceAcceptanceSale.setSid(id);
                row=conBuTypeServiceAcceptanceSaleMapper.updateById( conBuTypeServiceAcceptanceSale);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeServiceAcceptanceSale.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeServiceAcceptanceSale.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeServiceAcceptanceSale
     * @return
     */
    @Override
    public int check(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale){
        int row=0;
        Long[] sids=conBuTypeServiceAcceptanceSale.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeServiceAcceptanceSale.setSid(id);
                row=conBuTypeServiceAcceptanceSaleMapper.updateById( conBuTypeServiceAcceptanceSale);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeServiceAcceptanceSale.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
