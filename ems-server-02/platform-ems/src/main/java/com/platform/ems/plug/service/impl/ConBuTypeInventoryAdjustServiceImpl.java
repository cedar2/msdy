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
import com.platform.ems.plug.domain.ConBuTypeInventoryAdjust;
import com.platform.ems.plug.mapper.ConBuTypeInventoryAdjustMapper;
import com.platform.ems.plug.service.IConBuTypeInventoryAdjustService;
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
 * 业务类型_库存调整单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeInventoryAdjustServiceImpl extends ServiceImpl<ConBuTypeInventoryAdjustMapper,ConBuTypeInventoryAdjust>  implements IConBuTypeInventoryAdjustService {
    @Autowired
    private ConBuTypeInventoryAdjustMapper conBuTypeInventoryAdjustMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_库存调整单";
    /**
     * 查询业务类型_库存调整单
     *
     * @param sid 业务类型_库存调整单ID
     * @return 业务类型_库存调整单
     */
    @Override
    public ConBuTypeInventoryAdjust selectConBuTypeInventoryAdjustById(Long sid) {
        ConBuTypeInventoryAdjust conBuTypeInventoryAdjust = conBuTypeInventoryAdjustMapper.selectConBuTypeInventoryAdjustById(sid);
        MongodbUtil.find(conBuTypeInventoryAdjust);
        return  conBuTypeInventoryAdjust;
    }

    @Override
    public List<ConBuTypeInventoryAdjust> getList() {
        return conBuTypeInventoryAdjustMapper.getList();
    }

    /**
     * 查询业务类型_库存调整单列表
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 业务类型_库存调整单
     */
    @Override
    public List<ConBuTypeInventoryAdjust> selectConBuTypeInventoryAdjustList(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust) {
        return conBuTypeInventoryAdjustMapper.selectConBuTypeInventoryAdjustList(conBuTypeInventoryAdjust);
    }

    /**
     * 新增业务类型_库存调整单
     * 需要注意编码重复校验
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeInventoryAdjust(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust) {
        List<ConBuTypeInventoryAdjust> codeList = conBuTypeInventoryAdjustMapper.selectList(new QueryWrapper<ConBuTypeInventoryAdjust>().lambda()
                .eq(ConBuTypeInventoryAdjust::getCode, conBuTypeInventoryAdjust.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeInventoryAdjust> nameList = conBuTypeInventoryAdjustMapper.selectList(new QueryWrapper<ConBuTypeInventoryAdjust>().lambda()
                .eq(ConBuTypeInventoryAdjust::getName, conBuTypeInventoryAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeInventoryAdjustMapper.insert(conBuTypeInventoryAdjust);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeInventoryAdjust.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_库存调整单
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeInventoryAdjust(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust) {
        ConBuTypeInventoryAdjust response = conBuTypeInventoryAdjustMapper.selectConBuTypeInventoryAdjustById(conBuTypeInventoryAdjust.getSid());
        int row=conBuTypeInventoryAdjustMapper.updateById(conBuTypeInventoryAdjust);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInventoryAdjust.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeInventoryAdjust,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_库存调整单
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeInventoryAdjust(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust) {
        List<ConBuTypeInventoryAdjust> nameList = conBuTypeInventoryAdjustMapper.selectList(new QueryWrapper<ConBuTypeInventoryAdjust>().lambda()
                .eq(ConBuTypeInventoryAdjust::getName, conBuTypeInventoryAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeInventoryAdjust.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeInventoryAdjust.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeInventoryAdjust response = conBuTypeInventoryAdjustMapper.selectConBuTypeInventoryAdjustById(conBuTypeInventoryAdjust.getSid());
        int row = conBuTypeInventoryAdjustMapper.updateAllById(conBuTypeInventoryAdjust);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInventoryAdjust.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeInventoryAdjust, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_库存调整单
     *
     * @param sids 需要删除的业务类型_库存调整单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeInventoryAdjustByIds(List<Long> sids) {
        return conBuTypeInventoryAdjustMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeInventoryAdjust
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust){
        int row=0;
        Long[] sids=conBuTypeInventoryAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeInventoryAdjust.setSid(id);
                row=conBuTypeInventoryAdjustMapper.updateById( conBuTypeInventoryAdjust);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeInventoryAdjust.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeInventoryAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeInventoryAdjust
     * @return
     */
    @Override
    public int check(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust){
        int row=0;
        Long[] sids=conBuTypeInventoryAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeInventoryAdjust.setSid(id);
                row=conBuTypeInventoryAdjustMapper.updateById( conBuTypeInventoryAdjust);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeInventoryAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
