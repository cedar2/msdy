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
import com.platform.ems.plug.domain.ConDocTypeOutsourceMi;
import com.platform.ems.plug.mapper.ConDocTypeOutsourceMiMapper;
import com.platform.ems.plug.service.IConDocTypeOutsourceMiService;
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
 * 单据类型_外发加工发料单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeOutsourceMiServiceImpl extends ServiceImpl<ConDocTypeOutsourceMiMapper,ConDocTypeOutsourceMi>  implements IConDocTypeOutsourceMiService {
    @Autowired
    private ConDocTypeOutsourceMiMapper conDocTypeOutsourceMiMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_外发加工发料单";
    /**
     * 查询单据类型_外发加工发料单
     *
     * @param sid 单据类型_外发加工发料单ID
     * @return 单据类型_外发加工发料单
     */
    @Override
    public ConDocTypeOutsourceMi selectConDocTypeOutsourceMiById(Long sid) {
        ConDocTypeOutsourceMi conDocTypeOutsourceMi = conDocTypeOutsourceMiMapper.selectConDocTypeOutsourceMiById(sid);
        MongodbUtil.find(conDocTypeOutsourceMi);
        return  conDocTypeOutsourceMi;
    }

    /**
     * 查询单据类型_外发加工发料单列表
     *
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 单据类型_外发加工发料单
     */
    @Override
    public List<ConDocTypeOutsourceMi> selectConDocTypeOutsourceMiList(ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        return conDocTypeOutsourceMiMapper.selectConDocTypeOutsourceMiList(conDocTypeOutsourceMi);
    }

    /**
     * 新增单据类型_外发加工发料单
     * 需要注意编码重复校验
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeOutsourceMi(ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        List<ConDocTypeOutsourceMi> codeList = conDocTypeOutsourceMiMapper.selectList(new QueryWrapper<ConDocTypeOutsourceMi>().lambda()
                .eq(ConDocTypeOutsourceMi::getCode, conDocTypeOutsourceMi.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeOutsourceMi> nameList = conDocTypeOutsourceMiMapper.selectList(new QueryWrapper<ConDocTypeOutsourceMi>().lambda()
                .eq(ConDocTypeOutsourceMi::getName, conDocTypeOutsourceMi.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeOutsourceMiMapper.insert(conDocTypeOutsourceMi);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeOutsourceMi.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_外发加工发料单
     *
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeOutsourceMi(ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        ConDocTypeOutsourceMi response = conDocTypeOutsourceMiMapper.selectConDocTypeOutsourceMiById(conDocTypeOutsourceMi.getSid());
        int row=conDocTypeOutsourceMiMapper.updateById(conDocTypeOutsourceMi);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeOutsourceMi.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeOutsourceMi,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_外发加工发料单
     *
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeOutsourceMi(ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        List<ConDocTypeOutsourceMi> nameList = conDocTypeOutsourceMiMapper.selectList(new QueryWrapper<ConDocTypeOutsourceMi>().lambda()
                .eq(ConDocTypeOutsourceMi::getName, conDocTypeOutsourceMi.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeOutsourceMi.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeOutsourceMi.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeOutsourceMi response = conDocTypeOutsourceMiMapper.selectConDocTypeOutsourceMiById(conDocTypeOutsourceMi.getSid());
        int row = conDocTypeOutsourceMiMapper.updateAllById(conDocTypeOutsourceMi);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeOutsourceMi.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeOutsourceMi, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_外发加工发料单
     *
     * @param sids 需要删除的单据类型_外发加工发料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeOutsourceMiByIds(List<Long> sids) {
        return conDocTypeOutsourceMiMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeOutsourceMi
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeOutsourceMi conDocTypeOutsourceMi){
        int row=0;
        Long[] sids=conDocTypeOutsourceMi.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeOutsourceMi.setSid(id);
                row=conDocTypeOutsourceMiMapper.updateById( conDocTypeOutsourceMi);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeOutsourceMi.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeOutsourceMi.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeOutsourceMi
     * @return
     */
    @Override
    public int check(ConDocTypeOutsourceMi conDocTypeOutsourceMi){
        int row=0;
        Long[] sids=conDocTypeOutsourceMi.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeOutsourceMi.setSid(id);
                row=conDocTypeOutsourceMiMapper.updateById( conDocTypeOutsourceMi);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeOutsourceMi.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
