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
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.mapper.ConSpecialStockMapper;
import com.platform.ems.plug.service.IConSpecialStockService;
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
 * 特殊库存Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConSpecialStockServiceImpl extends ServiceImpl<ConSpecialStockMapper,ConSpecialStock>  implements IConSpecialStockService {
    @Autowired
    private ConSpecialStockMapper conSpecialStockMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "特殊库存";
    /**
     * 查询特殊库存
     *
     * @param sid 特殊库存ID
     * @return 特殊库存
     */
    @Override
    public ConSpecialStock selectConSpecialStockById(Long sid) {
        ConSpecialStock conSpecialStock = conSpecialStockMapper.selectConSpecialStockById(sid);
        MongodbUtil.find(conSpecialStock);
        return  conSpecialStock;
    }

    @Override
    public List<ConSpecialStock> getList() {
        return conSpecialStockMapper.getList();
    }

    /**
     * 查询特殊库存列表
     *
     * @param conSpecialStock 特殊库存
     * @return 特殊库存
     */
    @Override
    public List<ConSpecialStock> selectConSpecialStockList(ConSpecialStock conSpecialStock) {
        return conSpecialStockMapper.selectConSpecialStockList(conSpecialStock);
    }

    /**
     * 新增特殊库存
     * 需要注意编码重复校验
     * @param conSpecialStock 特殊库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSpecialStock(ConSpecialStock conSpecialStock) {
        List<ConSpecialStock> codeList = conSpecialStockMapper.selectList(new QueryWrapper<ConSpecialStock>().lambda()
                .eq(ConSpecialStock::getCode, conSpecialStock.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConSpecialStock> nameList = conSpecialStockMapper.selectList(new QueryWrapper<ConSpecialStock>().lambda()
                .eq(ConSpecialStock::getName, conSpecialStock.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conSpecialStockMapper.insert(conSpecialStock);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conSpecialStock.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改特殊库存
     *
     * @param conSpecialStock 特殊库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSpecialStock(ConSpecialStock conSpecialStock) {
        ConSpecialStock response = conSpecialStockMapper.selectConSpecialStockById(conSpecialStock.getSid());
        int row=conSpecialStockMapper.updateById(conSpecialStock);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conSpecialStock.getSid(), BusinessType.UPDATE.getValue(), response,conSpecialStock,TITLE);
        }
        return row;
    }

    /**
     * 变更特殊库存
     *
     * @param conSpecialStock 特殊库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSpecialStock(ConSpecialStock conSpecialStock) {
        List<ConSpecialStock> nameList = conSpecialStockMapper.selectList(new QueryWrapper<ConSpecialStock>().lambda()
                .eq(ConSpecialStock::getName, conSpecialStock.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conSpecialStock.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conSpecialStock.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConSpecialStock response = conSpecialStockMapper.selectConSpecialStockById(conSpecialStock.getSid());
        int row = conSpecialStockMapper.updateAllById(conSpecialStock);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSpecialStock.getSid(), BusinessType.CHANGE.getValue(), response, conSpecialStock, TITLE);
        }
        return row;
    }

    /**
     * 批量删除特殊库存
     *
     * @param sids 需要删除的特殊库存ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSpecialStockByIds(List<Long> sids) {
        return conSpecialStockMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conSpecialStock
    * @return
    */
    @Override
    public int changeStatus(ConSpecialStock conSpecialStock){
        int row=0;
        Long[] sids=conSpecialStock.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conSpecialStock.setSid(id);
                row=conSpecialStockMapper.updateById( conSpecialStock);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conSpecialStock.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conSpecialStock.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conSpecialStock
     * @return
     */
    @Override
    public int check(ConSpecialStock conSpecialStock){
        int row=0;
        Long[] sids=conSpecialStock.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conSpecialStock.setSid(id);
                row=conSpecialStockMapper.updateById( conSpecialStock);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conSpecialStock.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
