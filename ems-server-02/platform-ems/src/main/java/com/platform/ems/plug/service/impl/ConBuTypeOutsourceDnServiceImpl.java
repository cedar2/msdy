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
import com.platform.ems.plug.domain.ConBuTypeOutsourceDn;
import com.platform.ems.plug.mapper.ConBuTypeOutsourceDnMapper;
import com.platform.ems.plug.service.IConBuTypeOutsourceDnService;
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
 * 业务类型_外发加工交货单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeOutsourceDnServiceImpl extends ServiceImpl<ConBuTypeOutsourceDnMapper,ConBuTypeOutsourceDn>  implements IConBuTypeOutsourceDnService {
    @Autowired
    private ConBuTypeOutsourceDnMapper conBuTypeOutsourceDnMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_外发加工交货单";
    /**
     * 查询业务类型_外发加工交货单
     *
     * @param sid 业务类型_外发加工交货单ID
     * @return 业务类型_外发加工交货单
     */
    @Override
    public ConBuTypeOutsourceDn selectConBuTypeOutsourceDnById(Long sid) {
        ConBuTypeOutsourceDn conBuTypeOutsourceDn = conBuTypeOutsourceDnMapper.selectConBuTypeOutsourceDnById(sid);
        MongodbUtil.find(conBuTypeOutsourceDn);
        return  conBuTypeOutsourceDn;
    }

    /**
     * 查询业务类型_外发加工交货单列表
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 业务类型_外发加工交货单
     */
    @Override
    public List<ConBuTypeOutsourceDn> selectConBuTypeOutsourceDnList(ConBuTypeOutsourceDn conBuTypeOutsourceDn) {
        return conBuTypeOutsourceDnMapper.selectConBuTypeOutsourceDnList(conBuTypeOutsourceDn);
    }

    /**
     * 新增业务类型_外发加工交货单
     * 需要注意编码重复校验
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeOutsourceDn(ConBuTypeOutsourceDn conBuTypeOutsourceDn) {
        List<ConBuTypeOutsourceDn> codeList = conBuTypeOutsourceDnMapper.selectList(new QueryWrapper<ConBuTypeOutsourceDn>().lambda()
                .eq(ConBuTypeOutsourceDn::getCode, conBuTypeOutsourceDn.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeOutsourceDn> nameList = conBuTypeOutsourceDnMapper.selectList(new QueryWrapper<ConBuTypeOutsourceDn>().lambda()
                .eq(ConBuTypeOutsourceDn::getName, conBuTypeOutsourceDn.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeOutsourceDnMapper.insert(conBuTypeOutsourceDn);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeOutsourceDn.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_外发加工交货单
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeOutsourceDn(ConBuTypeOutsourceDn conBuTypeOutsourceDn) {
        ConBuTypeOutsourceDn response = conBuTypeOutsourceDnMapper.selectConBuTypeOutsourceDnById(conBuTypeOutsourceDn.getSid());
        int row=conBuTypeOutsourceDnMapper.updateById(conBuTypeOutsourceDn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeOutsourceDn.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeOutsourceDn,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_外发加工交货单
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeOutsourceDn(ConBuTypeOutsourceDn conBuTypeOutsourceDn) {
        List<ConBuTypeOutsourceDn> nameList = conBuTypeOutsourceDnMapper.selectList(new QueryWrapper<ConBuTypeOutsourceDn>().lambda()
                .eq(ConBuTypeOutsourceDn::getName, conBuTypeOutsourceDn.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeOutsourceDn.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeOutsourceDn.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeOutsourceDn response = conBuTypeOutsourceDnMapper.selectConBuTypeOutsourceDnById(conBuTypeOutsourceDn.getSid());
        int row = conBuTypeOutsourceDnMapper.updateAllById(conBuTypeOutsourceDn);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeOutsourceDn.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeOutsourceDn, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_外发加工交货单
     *
     * @param sids 需要删除的业务类型_外发加工交货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeOutsourceDnByIds(List<Long> sids) {
        return conBuTypeOutsourceDnMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeOutsourceDn
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeOutsourceDn conBuTypeOutsourceDn){
        int row=0;
        Long[] sids=conBuTypeOutsourceDn.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeOutsourceDn.setSid(id);
                row=conBuTypeOutsourceDnMapper.updateById( conBuTypeOutsourceDn);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeOutsourceDn.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeOutsourceDn.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeOutsourceDn
     * @return
     */
    @Override
    public int check(ConBuTypeOutsourceDn conBuTypeOutsourceDn){
        int row=0;
        Long[] sids=conBuTypeOutsourceDn.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeOutsourceDn.setSid(id);
                row=conBuTypeOutsourceDnMapper.updateById( conBuTypeOutsourceDn);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeOutsourceDn.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConBuTypeOutsourceDn> getConBuTypeOutsourceDnList() {
        return conBuTypeOutsourceDnMapper.getConBuTypeOutsourceDnList();
    }
}
