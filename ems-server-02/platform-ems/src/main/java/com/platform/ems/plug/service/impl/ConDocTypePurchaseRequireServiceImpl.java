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
import com.platform.ems.plug.domain.ConDocTypePurchaseRequire;
import com.platform.ems.plug.mapper.ConDocTypePurchaseRequireMapper;
import com.platform.ems.plug.service.IConDocTypePurchaseRequireService;
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
 * 单据类型_申购单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypePurchaseRequireServiceImpl extends ServiceImpl<ConDocTypePurchaseRequireMapper,ConDocTypePurchaseRequire>  implements IConDocTypePurchaseRequireService {
    @Autowired
    private ConDocTypePurchaseRequireMapper conDocTypePurchaseRequireMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_申购单";
    /**
     * 查询单据类型_申购单
     *
     * @param sid 单据类型_申购单ID
     * @return 单据类型_申购单
     */
    @Override
    public ConDocTypePurchaseRequire selectConDocTypePurchaseRequireById(Long sid) {
        ConDocTypePurchaseRequire conDocTypePurchaseRequire = conDocTypePurchaseRequireMapper.selectConDocTypePurchaseRequireById(sid);
        MongodbUtil.find(conDocTypePurchaseRequire);
        return  conDocTypePurchaseRequire;
    }

    /**
     * 查询单据类型_申购单列表
     *
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 单据类型_申购单
     */
    @Override
    public List<ConDocTypePurchaseRequire> selectConDocTypePurchaseRequireList(ConDocTypePurchaseRequire conDocTypePurchaseRequire) {
        return conDocTypePurchaseRequireMapper.selectConDocTypePurchaseRequireList(conDocTypePurchaseRequire);
    }

    /**
     * 新增单据类型_申购单
     * 需要注意编码重复校验
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypePurchaseRequire(ConDocTypePurchaseRequire conDocTypePurchaseRequire) {
        List<ConDocTypePurchaseRequire> codeList = conDocTypePurchaseRequireMapper.selectList(new QueryWrapper<ConDocTypePurchaseRequire>().lambda()
                .eq(ConDocTypePurchaseRequire::getCode, conDocTypePurchaseRequire.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypePurchaseRequire> nameList = conDocTypePurchaseRequireMapper.selectList(new QueryWrapper<ConDocTypePurchaseRequire>().lambda()
                .eq(ConDocTypePurchaseRequire::getName, conDocTypePurchaseRequire.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypePurchaseRequireMapper.insert(conDocTypePurchaseRequire);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypePurchaseRequire.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_申购单
     *
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypePurchaseRequire(ConDocTypePurchaseRequire conDocTypePurchaseRequire) {
        ConDocTypePurchaseRequire response = conDocTypePurchaseRequireMapper.selectConDocTypePurchaseRequireById(conDocTypePurchaseRequire.getSid());
        int row=conDocTypePurchaseRequireMapper.updateById(conDocTypePurchaseRequire);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePurchaseRequire.getSid(), BusinessType.UPDATE.ordinal(), response,conDocTypePurchaseRequire,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_申购单
     *
     * @param conDocTypePurchaseRequire 单据类型_申购单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypePurchaseRequire(ConDocTypePurchaseRequire conDocTypePurchaseRequire) {
        List<ConDocTypePurchaseRequire> nameList = conDocTypePurchaseRequireMapper.selectList(new QueryWrapper<ConDocTypePurchaseRequire>().lambda()
                .eq(ConDocTypePurchaseRequire::getName, conDocTypePurchaseRequire.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypePurchaseRequire.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypePurchaseRequire.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypePurchaseRequire response = conDocTypePurchaseRequireMapper.selectConDocTypePurchaseRequireById(conDocTypePurchaseRequire.getSid());
        int row = conDocTypePurchaseRequireMapper.updateAllById(conDocTypePurchaseRequire);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePurchaseRequire.getSid(), BusinessType.CHANGE.ordinal(), response, conDocTypePurchaseRequire, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_申购单
     *
     * @param sids 需要删除的单据类型_申购单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypePurchaseRequireByIds(List<Long> sids) {
        return conDocTypePurchaseRequireMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypePurchaseRequire
    * @return
    */
    @Override
    public int changeStatus(ConDocTypePurchaseRequire conDocTypePurchaseRequire){
        int row=0;
        Long[] sids=conDocTypePurchaseRequire.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePurchaseRequire.setSid(id);
                row=conDocTypePurchaseRequireMapper.updateById( conDocTypePurchaseRequire);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypePurchaseRequire.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypePurchaseRequire.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypePurchaseRequire
     * @return
     */
    @Override
    public int check(ConDocTypePurchaseRequire conDocTypePurchaseRequire){
        int row=0;
        Long[] sids=conDocTypePurchaseRequire.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePurchaseRequire.setSid(id);
                row=conDocTypePurchaseRequireMapper.updateById( conDocTypePurchaseRequire);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypePurchaseRequire.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
