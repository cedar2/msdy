package com.platform.ems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.exception.CustomException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ITecMaterialSizeService;
import com.platform.common.core.domain.entity.SysUser;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 商品尺寸Service业务层处理
 *
 * @author olive
 * @date 2021-02-21
 */
@Service
@SuppressWarnings("all")
public class TecMaterialSizeServiceImpl implements ITecMaterialSizeService {

    @Autowired
    private TecMaterialSizeMapper tecMaterialSizeMapper;
    @Autowired
    private TecMaterialPosInforMapper posInforMapper;
    @Autowired
    private TecMaterialPosInforDownMapper posInforDownMapper;
    @Autowired
    private TecMaterialPosSizeMapper posSizeMapper;
    @Autowired
    private TecMaterialPosSizeDownMapper posSizeDownMapper;

    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "MATERIALSIZE_STOCK";

    /**
     * 查询商品尺寸
     *
     * @param clientId 商品尺寸ID
     * @return 商品尺寸
     */
    @Override
    public TecMaterialSize selectTecMaterialSizeById(Long materialSizeSid) {
        return tecMaterialSizeMapper.selectTecMaterialSizeById(materialSizeSid);
    }

    /**
     * 查询商品尺寸列表
     *
     * @param tecMaterialSize 商品尺寸
     * @return 商品尺寸
     */
    @Override
    public List<TecMaterialSize> selectTecMaterialSizeList(TecMaterialSize tecMaterialSize) {
        return tecMaterialSizeMapper.selectTecMaterialSizeList(tecMaterialSize);
    }

    @Override
    public List<BasMaterial> selectBasMaterialList(){
        return tecMaterialSizeMapper.selectBasMaterialList();
    }


    /**
     * 新增商品尺寸
     *
     * @param tecMaterialSize 商品尺寸
     * @return 结果
     */
    @Override
    public int insertTecMaterialSize(TecMaterialSize tecMaterialSize) {
        return tecMaterialSizeMapper.insert(tecMaterialSize);
    }

    /**
     * 修改商品尺寸
     *
     * @param tecMaterialSize 商品尺寸
     * @return 结果
     */
    @Override
    public int updateTecMaterialSize(TecMaterialSize tecMaterialSize) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        int row = 0;
        try{
            SysUser sysUser= ApiThreadLocalUtil.get().getSysUser();
            String username= sysUser.getUserName();
            Date date=new Date();
            tecMaterialSize.setUpdateDate(date);
            tecMaterialSize.setUpdaterAccount(username);
            tecMaterialSize.setUpdateDate(new Date());
            if (HandleStatus.CONFIRMED.getCode().equals(tecMaterialSize.getHandleStatus())){
                tecMaterialSize.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                tecMaterialSize.setConfirmDate(new Date());
            }
            row=tecMaterialSizeMapper.updateTecMaterialSize(tecMaterialSize);
            if(row>0){
                //删除上装
                List<Long> posInforIds=posInforMapper.selectPosInforIds(tecMaterialSize.getMaterialSizeSid());
                Map<String,Object> deleteParams=new HashMap<>();
                deleteParams.put("material_size_sid", tecMaterialSize.getMaterialSizeSid());
                int row1=posInforMapper.deleteByMap(deleteParams);
                deleteParams=new HashMap<>();
                for(Long inforid:posInforIds){
                    deleteParams.put("material_pos_infor_sid", inforid);
                    int row2= posSizeMapper.deleteByMap(deleteParams);
                }
                //删除下装
                QueryWrapper<TecMaterialPosInforDown> posInforDownQueryWrapper= new QueryWrapper<TecMaterialPosInforDown>();
                posInforDownQueryWrapper.lambda().eq(TecMaterialPosInforDown::getMaterialSizeSid, tecMaterialSize.getMaterialSizeSid());
                List<TecMaterialPosInforDown> posInforDownList=posInforDownMapper.selectList(posInforDownQueryWrapper);
                posInforDownList.forEach(down->{
                    //删除下装尺寸
                    posSizeDownMapper.delete(new QueryWrapper<TecMaterialPosSizeDown>().lambda().eq(TecMaterialPosSizeDown::getMaterialPosInforSid, down.getMaterialPosInforSid()));
                });
                posInforDownMapper.delete(posInforDownQueryWrapper);

                //重新插入新数据
                List<TecMaterialPosInfor> productPosInfoRequests = tecMaterialSize.getPosInforList();
                for (TecMaterialPosInfor materialPosInfor : productPosInfoRequests) {
                    String inforSid="";
                    if(StrUtil.isEmpty(materialPosInfor.getMaterialPosInforSid())){
                        inforSid=IdWorker.getIdStr();
                    }else {
                        inforSid= materialPosInfor.getMaterialPosInforSid();
                    }
                    materialPosInfor.setMaterialPosInforSid(inforSid);
                    materialPosInfor.setUpdateDate(date);
                    materialPosInfor.setUpdaterAccount(username);
                    materialPosInfor.setMaterialSizeSid(tecMaterialSize.getMaterialSizeSid());
                    posInforMapper.insert(materialPosInfor);
                    //商品尺寸部位尺寸表
                    List<TecMaterialPosSize> productPosSizeRequests = materialPosInfor.getPosSizeList();
                    for (TecMaterialPosSize materialPosSize : productPosSizeRequests) {
                        materialPosSize.setMaterialPosInforSid(inforSid);
                        materialPosSize.setUpdateDate(date);
                        materialPosSize.setUpdaterAccount(username);
                        posSizeMapper.insert(materialPosSize);
                    }
                }
                //下装
                List<TecMaterialPosInforDown> materialPosInforDownList = tecMaterialSize.getPosInforDownList();
                Optional<List<TecMaterialPosInforDown>> downList = Optional.ofNullable(materialPosInforDownList);
                downList.ifPresent(d ->{
                    d.forEach(down ->{
                        Long downSid;
                        if(down.getMaterialPosInforSid()==null){
                            downSid=IdWorker.getId();
                        }else {
                            downSid=down.getMaterialPosInforSid();
                        }
                        down.setMaterialPosInforSid(downSid);
                        down.setMaterialSizeSid(Long.valueOf(tecMaterialSize.getMaterialSizeSid()));
                        down.setUpdateDate(new Date());
                        down.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        posInforDownMapper.insert(down);
                        List<TecMaterialPosSizeDown> posSizeDownList = down.getPosSizeDownList();
                        posSizeDownList.forEach(size ->{
                            size.setMaterialPosInforSid(downSid);
                            size.setUpdateDate(new Date());
                            size.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            posSizeDownMapper.insert(size);
                        });
                    });
                });
            }
        }catch (CustomException e){
            throw new CustomException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return row;
    }

    /**
     * 批量删除商品尺寸
     *
     * @param materialSizeSids 需要删除的商品尺寸ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecMaterialSizeByIds(List<Long> materialSizeSids) {
        //删除关联表
        int row=tecMaterialSizeMapper.deleteBatchIds(materialSizeSids);
        if(row>0){
            for(Long id:materialSizeSids){
                List<Long> posInforIds=posInforMapper.selectPosInforIds(id);
                Map<String,Object> deleteParams=new HashMap<>();
                deleteParams.put("material_size_sid", id);
                posInforMapper.deleteByMap(deleteParams);
                deleteParams=new HashMap<>();
                for(Long inforid:posInforIds){
                    deleteParams.put("material_pos_infor_sid", inforid);
                    posSizeMapper.deleteByMap(deleteParams);
                }

                //删除下装
                QueryWrapper<TecMaterialPosInforDown> posInforDownQueryWrapper= new QueryWrapper<TecMaterialPosInforDown>();
                posInforDownQueryWrapper.lambda().eq(TecMaterialPosInforDown::getMaterialSizeSid, id);
                List<TecMaterialPosInforDown> posInforDownList=posInforDownMapper.selectList(posInforDownQueryWrapper);
                posInforDownList.forEach(down->{
                    //删除下装尺寸
                    posSizeDownMapper.delete(new QueryWrapper<TecMaterialPosSizeDown>().lambda().eq(TecMaterialPosSizeDown::getMaterialPosInforSid, down.getMaterialPosInforSid()));
                });
                posInforDownMapper.delete(posInforDownQueryWrapper);
            }
        }
        return row;
    }

    /**
     * 删除商品尺寸信息
     *
     * @param clientId 商品尺寸ID
     * @return 结果
     */
    @Override
    public int deleteTecMaterialSizeById(Long materialSid) {
        return tecMaterialSizeMapper.deleteTecMaterialSizeById(materialSid);
    }

    @Override
    public boolean isExist(Long materialSid){
        Map<String,Object> params=new HashMap<>();
        params.put("material_sid", materialSid);
        List<TecMaterialSize> materialSize=tecMaterialSizeMapper.selectByMap(params);
        if(materialSize!=null&&materialSize.size()>0){
            return true;
        }
        return false;
    }

    @Override
    public String getHandleStatus(Long sId) {
        return tecMaterialSizeMapper.getHandleStatus(sId);
    }

    @Override
    public String putHandleStatus(Long sId, String handleStatus) {
        return tecMaterialSizeMapper.putHandleStatus(sId, handleStatus);
    }

    @Override
    public String getStatus(Long sId) {
        return tecMaterialSizeMapper.getStatus(sId);
    }

    @Override
    public String putStatus(Long sId, String validStatus) {
        return tecMaterialSizeMapper.putStatus(sId, validStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(TecMaterialSize materialSize){
        int row=0;
        Long[] sids=materialSize.getMaterialSizeSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                materialSize.setMaterialSizeSid(id);
                row=tecMaterialSizeMapper.updateById(materialSize);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
            }
        }
        return row;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecMaterialSize materialSize){
        int row=0;
        Long[] sids=materialSize.getMaterialSizeSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                materialSize.setMaterialSizeSid(id);
                row=tecMaterialSizeMapper.updateById(materialSize);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
            }
        }
        return row;
    }
}
