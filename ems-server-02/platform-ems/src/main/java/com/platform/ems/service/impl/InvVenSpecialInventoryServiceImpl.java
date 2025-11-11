package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasStorehouse;
import com.platform.ems.domain.InvCusSpecialInventory;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.InvInventoryLocation;
import com.platform.ems.mapper.BasStorehouseMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.InvVenSpecialInventoryMapper;
import com.platform.ems.domain.InvVenSpecialInventory;
import com.platform.ems.service.IInvVenSpecialInventoryService;

/**
 * 供应商特殊库存（寄售/甲供料）Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@Service
@SuppressWarnings("all")
public class InvVenSpecialInventoryServiceImpl extends ServiceImpl<InvVenSpecialInventoryMapper,InvVenSpecialInventory>  implements IInvVenSpecialInventoryService {
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private InvCusSpecialInventoryServiceImpl invCusSpecialInventoryServiceImpl;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;

    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商特殊库存（寄售/甲供料）";
    /**
     * 查询供应商特殊库存（寄售/甲供料）
     *
     * @param vendorSpecialStockSid 供应商特殊库存（寄售/甲供料）ID
     * @return 供应商特殊库存（寄售/甲供料）
     */
    @Override
    public InvVenSpecialInventory selectInvVenSpecialInventoryById(Long vendorSpecialStockSid) {
        InvVenSpecialInventory invVenSpecialInventory = invVenSpecialInventoryMapper.selectInvVenSpecialInventoryById(vendorSpecialStockSid);
        MongodbUtil.find(invVenSpecialInventory);
        return  invVenSpecialInventory;
    }

    /**
     * 查询供应商特殊库存（寄售/甲供料）列表
     *
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 供应商特殊库存（寄售/甲供料）
     */
    @Override
    public List<InvVenSpecialInventory> selectInvVenSpecialInventoryList(InvVenSpecialInventory invVenSpecialInventory) {
        return invVenSpecialInventoryMapper.selectInvVenSpecialInventoryList(invVenSpecialInventory);
    }

    @Override
    public int judgeAdd(List<InvVenSpecialInventory> list){
        list.forEach(item->{
            InvVenSpecialInventory invVenSpecialInventory = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                    .eq(InvVenSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvVenSpecialInventory::getSpecialStock,ConstantsEms.VEN_RA)
                    .eq(InvVenSpecialInventory::getVendorSid,item.getVendorSid())
                    .eq(InvVenSpecialInventory::getStorehouseLocationSid,item.getStorehouseLocationSid())
            );
            BasStorehouse basStorehouse = basStorehouseMapper.selectById(item.getStorehouseSid());
            if(invVenSpecialInventory==null){
                if(item.getSku2Name()!=null){
                    throw new CustomException(basStorehouse.getStorehouseName()+",sku1为"+item.getSku1Name()+"、sku2为"+item.getSku2Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"不存在库存，无法添加");
                }else{
                    throw new CustomException(basStorehouse.getStorehouseName()+",sku为"+item.getSku1Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"不存在库存，无法添加");
                }
            }else{
                BigDecimal unlimitedQuantity = invVenSpecialInventory.getUnlimitedQuantity();
                if(unlimitedQuantity.compareTo(BigDecimal.ZERO)!=1){
                    if(item.getSku2Name()!=null){
                        throw new CustomException(basStorehouse.getStorehouseName()+",sku1为"+item.getSku1Name()+"、sku2为"+item.getSku2Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"库存量为0，无法添加");
                    }else{
                        throw new CustomException(basStorehouse.getStorehouseName()+",sku为"+item.getSku1Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"库存量为0，无法添加");
                    }
                }
            }
        });
        return 1;
    }

    /**
     * 查询特殊库存报表明细
     *
     */
    @Override
    public List<InvVenSpecialInventory> report(InvVenSpecialInventory invVenSpecialInventory) {
        List<String> specialStockList = invVenSpecialInventory.getSpecialStockList();
        Long[] vendorSidList = invVenSpecialInventory.getVendorSidList();
        Long[] customerSidList = invVenSpecialInventory.getCustomerSidList();
        if(vendorSidList.length>0&&customerSidList.length>0){
            invVenSpecialInventory.setType(null);
        }else if(vendorSidList.length>0){
            invVenSpecialInventory.setType("1");
        }else if(customerSidList.length>0){
            invVenSpecialInventory.setType("2");
        }
        if (CollectionUtils.isNotEmpty(specialStockList)) {
            boolean judegVe = specialStockList.stream().anyMatch(o -> o.equals(ConstantsEms.VEN_CU) || o.equals(ConstantsEms.VEN_RA));
            boolean judegCu = specialStockList.stream().anyMatch(o -> o.equals(ConstantsEms.CUS_RA) || o.equals(ConstantsEms.CUS_VE));
            if (judegVe && judegCu == false) {
                invVenSpecialInventory.setType("1");
            } else if (judegVe == false && judegCu) {
                invVenSpecialInventory.setType("2");
            }
        }
            List<InvVenSpecialInventory> list = invVenSpecialInventoryMapper.selectInvVenSpecialInventoryList(invVenSpecialInventory);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(o -> {
                    String specialType = o.getSpecialStock();
                    if (specialType.equals(ConstantsEms.CUS_RA) || specialType.equals(ConstantsEms.CUS_VE)) {
                        String name = o.getVendorName();
                        if (name != null) {
                            o.setCustomerName(name);
                            o.setVendorName(null);
                        }
                    }
                });
            }
            return list;
    }

    //客户特殊库存拷贝到供应商库存
    public void Copy(InvVenSpecialInventory invVenSpecialInventory,List<InvVenSpecialInventory> invSpecialList){
        InvCusSpecialInventory invCusSpecialInventory = new InvCusSpecialInventory();
        BeanCopyUtils.copyProperties(invVenSpecialInventory,invCusSpecialInventory);
        List<InvCusSpecialInventory> invCusS = invCusSpecialInventoryServiceImpl.selectInvCusSpecialInventoryList(invCusSpecialInventory);
        invCusS.forEach(o->{
            InvVenSpecialInventory invVen = new InvVenSpecialInventory();
            BeanCopyUtils.copyProperties(o,invVen);
            invSpecialList.add(invVen);
        });
    }

    /**
     * 新增供应商特殊库存（寄售/甲供料）
     * 需要注意编码重复校验
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory) {
        int row= invVenSpecialInventoryMapper.insert(invVenSpecialInventory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invVenSpecialInventory.getVendorSpecialStockSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商特殊库存（寄售/甲供料）
     *
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory) {
        InvVenSpecialInventory response = invVenSpecialInventoryMapper.selectInvVenSpecialInventoryById(invVenSpecialInventory.getVendorSpecialStockSid());
        int row=invVenSpecialInventoryMapper.updateById(invVenSpecialInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invVenSpecialInventory.getVendorSpecialStockSid(), BusinessType.UPDATE.ordinal(), response,invVenSpecialInventory,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商特殊库存（寄售/甲供料）
     *
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory) {
        InvVenSpecialInventory response = invVenSpecialInventoryMapper.selectInvVenSpecialInventoryById(invVenSpecialInventory.getVendorSpecialStockSid());
                                                                                                                int row=invVenSpecialInventoryMapper.updateAllById(invVenSpecialInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invVenSpecialInventory.getVendorSpecialStockSid(), BusinessType.CHANGE.ordinal(), response,invVenSpecialInventory,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商特殊库存（寄售/甲供料）
     *
     * @param vendorSpecialStockSids 需要删除的供应商特殊库存（寄售/甲供料）ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvVenSpecialInventoryByIds(List<Long> vendorSpecialStockSids) {
        return invVenSpecialInventoryMapper.deleteBatchIds(vendorSpecialStockSids);
    }

    /**
    * 启用/停用
    * @param invVenSpecialInventory
    * @return
    */
    @Override
    public int changeStatus(InvVenSpecialInventory invVenSpecialInventory){
        int row=0;
        Long[] sids=invVenSpecialInventory.getVendorSpecialStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invVenSpecialInventory.setVendorSpecialStockSid(id);
                row=invVenSpecialInventoryMapper.updateById( invVenSpecialInventory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invVenSpecialInventory.getVendorSpecialStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param invVenSpecialInventory
     * @return
     */
    @Override
    public int check(InvVenSpecialInventory invVenSpecialInventory){
        int row=0;
        Long[] sids=invVenSpecialInventory.getVendorSpecialStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invVenSpecialInventory.setVendorSpecialStockSid(id);
                row=invVenSpecialInventoryMapper.updateById( invVenSpecialInventory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invVenSpecialInventory.getVendorSpecialStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
