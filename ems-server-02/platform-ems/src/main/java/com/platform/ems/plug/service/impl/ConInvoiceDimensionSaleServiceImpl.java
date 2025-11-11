package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConInvoiceDimensionSale;
import com.platform.ems.plug.mapper.ConInvoiceDimensionSaleMapper;
import com.platform.ems.plug.service.IConInvoiceDimensionSaleService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 发票维度_销售Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceDimensionSaleServiceImpl extends ServiceImpl<ConInvoiceDimensionSaleMapper,ConInvoiceDimensionSale>  implements IConInvoiceDimensionSaleService {
    @Autowired
    private ConInvoiceDimensionSaleMapper conInvoiceDimensionSaleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "发票维度_销售";
    /**
     * 查询发票维度_销售
     *
     * @param sid 发票维度_销售ID
     * @return 发票维度_销售
     */
    @Override
    public ConInvoiceDimensionSale selectConInvoiceDimensionSaleById(Long sid) {
        ConInvoiceDimensionSale conInvoiceDimensionSale = conInvoiceDimensionSaleMapper.selectConInvoiceDimensionSaleById(sid);
        MongodbUtil.find(conInvoiceDimensionSale);
        return  conInvoiceDimensionSale;
    }

    /**
     * 查询发票维度_销售列表
     *
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 发票维度_销售
     */
    @Override
    public List<ConInvoiceDimensionSale> selectConInvoiceDimensionSaleList(ConInvoiceDimensionSale conInvoiceDimensionSale) {
        return conInvoiceDimensionSaleMapper.selectConInvoiceDimensionSaleList(conInvoiceDimensionSale);
    }

    /**
     * 新增发票维度_销售
     * 需要注意编码重复校验
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceDimensionSale(ConInvoiceDimensionSale conInvoiceDimensionSale) {
        String name = conInvoiceDimensionSale.getName();
        String code = conInvoiceDimensionSale.getCode();
        List<ConInvoiceDimensionSale> list = conInvoiceDimensionSaleMapper.selectList(new QueryWrapper<ConInvoiceDimensionSale>().lambda()
                .or().eq(ConInvoiceDimensionSale::getName, name)
                .or().eq(ConInvoiceDimensionSale::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conInvoiceDimensionSaleMapper.insert(conInvoiceDimensionSale);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceDimensionSale.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改发票维度_销售
     *
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceDimensionSale(ConInvoiceDimensionSale conInvoiceDimensionSale) {
        ConInvoiceDimensionSale response = conInvoiceDimensionSaleMapper.selectConInvoiceDimensionSaleById(conInvoiceDimensionSale.getSid());
        int row=conInvoiceDimensionSaleMapper.updateById(conInvoiceDimensionSale);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceDimensionSale.getSid(), BusinessType.UPDATE.ordinal(), response,conInvoiceDimensionSale,TITLE);
        }
        return row;
    }

    /**
     * 变更发票维度_销售
     *
     * @param conInvoiceDimensionSale 发票维度_销售
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceDimensionSale(ConInvoiceDimensionSale conInvoiceDimensionSale) {
        String name = conInvoiceDimensionSale.getName();
        ConInvoiceDimensionSale item = conInvoiceDimensionSaleMapper.selectOne(new QueryWrapper<ConInvoiceDimensionSale>().lambda()
                .eq(ConInvoiceDimensionSale::getName, name)
        );
        if (item != null && !item.getSid().equals(conInvoiceDimensionSale.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConInvoiceDimensionSale response = conInvoiceDimensionSaleMapper.selectConInvoiceDimensionSaleById(conInvoiceDimensionSale.getSid());
        int row = conInvoiceDimensionSaleMapper.updateAllById(conInvoiceDimensionSale);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceDimensionSale.getSid(), BusinessType.CHANGE.ordinal(), response, conInvoiceDimensionSale, TITLE);
        }
        return row;
    }

    /**
     * 批量删除发票维度_销售
     *
     * @param sids 需要删除的发票维度_销售ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceDimensionSaleByIds(List<Long> sids) {
        return conInvoiceDimensionSaleMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conInvoiceDimensionSale
    * @return
    */
    @Override
    public int changeStatus(ConInvoiceDimensionSale conInvoiceDimensionSale){
        int row=0;
        Long[] sids=conInvoiceDimensionSale.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conInvoiceDimensionSale.setSid(id);
                row=conInvoiceDimensionSaleMapper.updateById( conInvoiceDimensionSale);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conInvoiceDimensionSale.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conInvoiceDimensionSale.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conInvoiceDimensionSale
     * @return
     */
    @Override
    public int check(ConInvoiceDimensionSale conInvoiceDimensionSale){
        int row=0;
        Long[] sids=conInvoiceDimensionSale.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conInvoiceDimensionSale.setSid(id);
                row=conInvoiceDimensionSaleMapper.updateById( conInvoiceDimensionSale);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conInvoiceDimensionSale.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
