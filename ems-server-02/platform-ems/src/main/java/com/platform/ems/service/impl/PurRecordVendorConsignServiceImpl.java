package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasVendor;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.mapper.BasVendorMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.PurRecordVendorConsignMapper;
import com.platform.ems.domain.PurRecordVendorConsign;
import com.platform.ems.service.IPurRecordVendorConsignService;

/**
 * s_pur_record_vendor_consignService业务层处理
 *
 * @author linhongwei
 * @date 2021-06-23
 */
@Service
@SuppressWarnings("all")
public class PurRecordVendorConsignServiceImpl extends ServiceImpl<PurRecordVendorConsignMapper,PurRecordVendorConsign>  implements IPurRecordVendorConsignService {
    @Autowired
    private PurRecordVendorConsignMapper purRecordVendorConsignMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;

    private static final String LOCK_KEY = "Vendor_lock";

    private static final String TITLE = "s_pur_record_vendor_consign";


    /**
     * 查询s_pur_record_vendor_consign
     *
     * @param recordVendorConsignSid s_pur_record_vendor_consignID
     * @return s_pur_record_vendor_consign
     */
    @Override
    public PurRecordVendorConsign selectPurRecordVendorConsignById(Long recordVendorConsignSid) {
        PurRecordVendorConsign purRecordVendorConsign = purRecordVendorConsignMapper.selectPurRecordVendorConsignById(recordVendorConsignSid);
        MongodbUtil.find(purRecordVendorConsign);
        return  purRecordVendorConsign;
    }

    /**
     * 查询s_pur_record_vendor_consign列表
     *
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return s_pur_record_vendor_consign
     */
    @Override
    public List<PurRecordVendorConsign> selectPurRecordVendorConsignList(PurRecordVendorConsign purRecordVendorConsign) {
        return purRecordVendorConsignMapper.selectPurRecordVendorConsignList(purRecordVendorConsign);
    }

    /**
     * 新增s_pur_record_vendor_consign
     * 需要注意编码重复校验
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurRecordVendorConsign(PurRecordVendorConsign purRecordVendorConsign) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock();
        int row= 0;
        try {
            String vendorName=null;
            String materialName=null;
            BasVendor basVendor = basVendorMapper.selectById(purRecordVendorConsign.getVendorSid());
            if(basVendor!=null){
                vendorName=basVendor.getVendorName();
            }
            BasMaterial basMaterial = basMaterialMapper.selectById(purRecordVendorConsign.getMaterialSid());
            if(basMaterial!=null){
                materialName=basMaterial.getMaterialName();
            }
            //判断是否存在供应商寄售待结算台账
            PurRecordVendorConsign purRecordVendorConsignOld = exitVendorConsign(purRecordVendorConsign);
            if(purRecordVendorConsignOld!=null){
                //出库 减少库存
                if(purRecordVendorConsign.getType().equals(ConstantsEms.CHU_KU)){
                    BigDecimal subtract = purRecordVendorConsignOld.getQuantity().subtract(purRecordVendorConsign.getQuantity());
                    if(subtract.compareTo(new  BigDecimal(0))==-1){
                        if(purRecordVendorConsign.getSku2Name()!=null){
                            throw new CustomException("物料为"+materialName+",sku1为"+purRecordVendorConsign.getSku1Name()+",sku2为"+purRecordVendorConsign.getSku2Name()+",供应商为"+vendorName+"，扣减量大于待结算量，不允许操作");
                        }else{
                            throw new CustomException("物料为"+materialName+",sku1为"+purRecordVendorConsign.getSku1Name()+",供应商为"+vendorName+"，扣减量大于待结算量，不允许操作");
                        }
                    }
                    purRecordVendorConsignOld.setQuantity(subtract);
                }else {
                    purRecordVendorConsignOld.setQuantity(purRecordVendorConsignOld.getQuantity().add(purRecordVendorConsign.getQuantity()));
                }
                if(!ConstantsEms.YES.equals(purRecordVendorConsign.getIsSkipInsert())){
                    purRecordVendorConsignMapper.updateById(purRecordVendorConsignOld);
                }

            }else {
                if(!ConstantsEms.YES.equals(purRecordVendorConsign.getIsSkipInsert())){
                    //出库 数量负数
                    if(purRecordVendorConsign.getType().equals(ConstantsEms.CHU_KU)){
                        purRecordVendorConsign.setQuantity(purRecordVendorConsign.getQuantity().multiply(new BigDecimal(-1)));
                        add(row, purRecordVendorConsign);
                    }else{
                        add(row, purRecordVendorConsign);
                    }
                }
            }

        } catch (CustomException e) {
           throw new CustomException(e.getMessage());
        } finally {
            lock.unlock();
        }

        return row;
    }
    public void add(int row,PurRecordVendorConsign purRecordVendorConsign){
        row = purRecordVendorConsignMapper.insert(purRecordVendorConsign);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(purRecordVendorConsign.getRecordVendorConsignSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
    }
    public PurRecordVendorConsign exitVendorConsign(PurRecordVendorConsign purRecordVendorConsign){
        PurRecordVendorConsign VendorConsign = purRecordVendorConsignMapper.selectOne(new QueryWrapper<PurRecordVendorConsign>().lambda()
                .eq(PurRecordVendorConsign::getBarcodeSid, purRecordVendorConsign.getBarcodeSid())
                .eq(PurRecordVendorConsign::getVendorSid, purRecordVendorConsign.getVendorSid()));
        return VendorConsign;
    }


    /**
     * 修改s_pur_record_vendor_consign
     *
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurRecordVendorConsign(PurRecordVendorConsign purRecordVendorConsign) {
        PurRecordVendorConsign response = purRecordVendorConsignMapper.selectPurRecordVendorConsignById(purRecordVendorConsign.getRecordVendorConsignSid());
        int row=purRecordVendorConsignMapper.updateById(purRecordVendorConsign);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(purRecordVendorConsign.getRecordVendorConsignSid(), BusinessType.UPDATE.ordinal(), response,purRecordVendorConsign,TITLE);
        }
        return row;
    }

    /**
     * 变更s_pur_record_vendor_consign
     *
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurRecordVendorConsign(PurRecordVendorConsign purRecordVendorConsign) {
        PurRecordVendorConsign response = purRecordVendorConsignMapper.selectPurRecordVendorConsignById(purRecordVendorConsign.getRecordVendorConsignSid());
                                                                                        int row=purRecordVendorConsignMapper.updateAllById(purRecordVendorConsign);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(purRecordVendorConsign.getRecordVendorConsignSid(), BusinessType.CHANGE.ordinal(), response,purRecordVendorConsign,TITLE);
        }
        return row;
    }

    /**
     * 批量删除s_pur_record_vendor_consign
     *
     * @param recordVendorConsignSids 需要删除的s_pur_record_vendor_consignID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurRecordVendorConsignByIds(List<Long> recordVendorConsignSids) {
        return purRecordVendorConsignMapper.deleteBatchIds(recordVendorConsignSids);
    }

    /**
    * 启用/停用
    * @param purRecordVendorConsign
    * @return
    */
    @Override
    public int changeStatus(PurRecordVendorConsign purRecordVendorConsign){
        int row=0;
        Long[] sids=purRecordVendorConsign.getRecordVendorConsignSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                purRecordVendorConsign.setRecordVendorConsignSid(id);
                row=purRecordVendorConsignMapper.updateById( purRecordVendorConsign);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(purRecordVendorConsign.getRecordVendorConsignSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,null);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param purRecordVendorConsign
     * @return
     */
    @Override
    public int check(PurRecordVendorConsign purRecordVendorConsign){
        int row=0;
        Long[] sids=purRecordVendorConsign.getRecordVendorConsignSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                purRecordVendorConsign.setRecordVendorConsignSid(id);
                row=purRecordVendorConsignMapper.updateById( purRecordVendorConsign);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(purRecordVendorConsign.getRecordVendorConsignSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
