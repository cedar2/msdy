package com.platform.ems.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.MaterialBottomsActionRequest;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.BasCompanyBrandMapper;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.ems.mapper.BasMaterialBottomsMapper;
import com.platform.ems.mapper.BasSkuMapper;
import com.platform.ems.service.IBasMaterialBottomsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 商品-上下装尺码对照Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-14
 */
@Service
@SuppressWarnings("all")
public class BasMaterialBottomsServiceImpl extends ServiceImpl<BasMaterialBottomsMapper, BasMaterialBottoms>  implements IBasMaterialBottomsService {
    @Autowired
    private BasMaterialBottomsMapper basMaterialBottomsMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasCompanyMapper  basCompanyMapper;
    @Autowired
    private BasCompanyBrandMapper basCompanyBrandMapper;

    /**
     * 查询商品-上下装尺码对照
     *
     * @param clientId 商品-上下装尺码对照ID
     * @return 商品-上下装尺码对照
     */
    @Override
    public BasMaterialBottoms selectBasMaterialBottomsById(Long id) {
        BasMaterialBottoms basMaterialBottoms = basMaterialBottomsMapper.selectById(id);
        return basMaterialBottoms;
    }

    /**
     * 查询商品-上下装尺码对照列表
     *
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 商品-上下装尺码对照
     */
    @Override
    public List<BasMaterialBottoms> selectBasMaterialBottomsList(BasMaterialBottoms request) {
        List<BasMaterialBottoms> list = basMaterialBottomsMapper.selectBasMaterialBottomsList(request);
        return list;
    }

    /**
     * 新增商品-上下装尺码对照
     * 需要注意编码重复校验
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialBottoms(BasMaterialBottoms basMaterialBottoms) {
        //获取状态
        String changeHandleStatus=basMaterialBottoms.getHandleStatus();
        if(HandleStatus.SAVE.getCode().equals(changeHandleStatus)) {
            return basMaterialBottomsMapper.insert(basMaterialBottoms);
        }else{
            //注入确认人 确认时间
//            basMaterialBottoms.setConfirmDate(new Date());
//            basMaterialBottoms.setConfirmerAccount(SecurityUtils.getLoginUser().getUsername());
            return basMaterialBottomsMapper.insert(basMaterialBottoms);
        }
    }

    /**
     * 修改商品-上下装尺码对照
     *
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateBasMaterialBottoms(BasMaterialBottoms basMaterialBottoms) {
        BasMaterialBottoms response = basMaterialBottomsMapper.selectById(basMaterialBottoms.getBottomsSkuSid());
        //当前状态
        String status = response.getHandleStatus();
        //要改变的状态
        String changeHandleStatus=basMaterialBottoms.getHandleStatus();
        if (HandleStatus.SAVE.getCode().equals(status) ){
            if(changeHandleStatus.equals(HandleStatus.SAVE.getCode())) {
                basMaterialBottomsMapper.updateAllById(basMaterialBottoms);
                return AjaxResult.success("修改上下装尺码对照信息成功");
            }else{
                //注入确认人 确认时间
//                basMaterialBottoms.setConfirmerAccount(SecurityUtils.getLoginUser().getUsername());
//                basMaterialBottoms.setConfirmDate(new Date());
                basMaterialBottomsMapper.updateAllById(basMaterialBottoms);
                return AjaxResult.success("修改上下装尺码对照信息成功");
            }
        }
        return AjaxResult.error("仅保存状态下才可修改");

    }
    /**
     * 变更商品-上下装尺码对照
     *
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult changeBasMaterialBottoms(BasMaterialBottoms basMaterialBottoms) {
        BasMaterialBottoms response = basMaterialBottomsMapper.selectById(basMaterialBottoms.getBottomsSkuSid());
        String status = response.getHandleStatus();
        if (HandleStatus.CONFIRMED.getCode().equals(status)){
            basMaterialBottomsMapper.updateAllById(basMaterialBottoms);
            return AjaxResult.success("变更上下装尺码对照信息成功");
        }
        return AjaxResult.error("仅确认状态才下可变更");

    }

    /**
     * 批量删除商品-上下装尺码对照
     *
     * @param bottomsSkuSids 需要删除的商品-上下装尺码对照ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteBasMaterialBottomsByIds(List<Long> bottomsSkuSids) {
        QueryWrapper<BasMaterialBottoms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handle_status",HandleStatus.SAVE.getCode())
                .in("bottoms_sku_sid",bottomsSkuSids);
        int size=basMaterialBottomsMapper.selectCount(queryWrapper);
         if(size==bottomsSkuSids.size()){
          int count= basMaterialBottomsMapper.deleteBatchIds(bottomsSkuSids);
             return AjaxResult.success("删除成功，删除"+count+"条上下装尺码对照数据信息");
         }
        return AjaxResult.error("仅保存状态下，才可删除");
    }
    /**
     * 批量 启用/停用-上下装尺码对照
     *
     * @param bottomsSkuSids 需要删除的商品-上下装尺码对照ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateValidStatus(MaterialBottomsActionRequest materialBottomsActionRequest ) {
        Long[] BottomsSkuSid = materialBottomsActionRequest.getBottomsSkuSid();
        QueryWrapper<BasMaterialBottoms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handle_status",HandleStatus.CONFIRMED.getCode())
                .in("bottoms_sku_sid",BottomsSkuSid);
        int size=basMaterialBottomsMapper.selectCount(queryWrapper);
        //获取要改变的状态
        String correctStatus = materialBottomsActionRequest.getStatus();
        //判断是否都符合条件
        if (size == BottomsSkuSid.length) {
            UpdateWrapper<BasMaterialBottoms> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status", correctStatus)
                    .in("bottoms_sku_sid", BottomsSkuSid);
            BasMaterialBottoms basMaterialBottoms = new BasMaterialBottoms();
            basMaterialBottomsMapper.update(basMaterialBottoms, updateWrapper);
            if (Status.ENABLE.getCode().equals(correctStatus)) {
                return AjaxResult.success("启用成功");
            } else {
                return AjaxResult.success("停用成功");
            }
        }else{
            return AjaxResult.error("仅确认状态下才可启用/停用");
        }

    }
    /**
     * 确认-上下装尺码对照
     *
     * @param materialBottomsActionRequest
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirm(MaterialBottomsActionRequest materialBottomsActionRequest){
        //获取要改变的状态
        String correctHandleStatus = materialBottomsActionRequest.getHandleStatus();
        Long[] BottomsSkuSid = materialBottomsActionRequest.getBottomsSkuSid();
        UpdateWrapper<BasMaterialBottoms> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("handle_status", correctHandleStatus)
                .in("bottoms_sku_sid", BottomsSkuSid)
                .set("confirm_date",new Date())
                .set("confirmer_account",SecurityUtils.getLoginUser().getUsername());
        BasMaterialBottoms basMaterialBottoms = new BasMaterialBottoms();
        int count=basMaterialBottomsMapper.update(basMaterialBottoms, updateWrapper);
        return AjaxResult.success("确认成功,确认"+count+"条上下装尺码对照的数据");
    }


}
