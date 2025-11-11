package com.platform.ems.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.exception.CheckedException;
import com.platform.ems.domain.SBasProductSeason;
import com.platform.ems.domain.dto.request.EditHandleStatusRequest;
import com.platform.ems.domain.dto.request.ListSeasonRequest;
import com.platform.ems.domain.dto.response.ExportSeasonResponse;
import com.platform.ems.domain.dto.response.ListSeasonResponse;
import com.platform.ems.mapper.SBasProductSeasonMapper;
import com.platform.ems.service.ISBasProductSeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 产品季档案Service业务层处理
 *
 * @author shakeflags
 * @date 2021-01-21
 */
@Service
@SuppressWarnings("all")
public class SBasProductSeasonServiceImpl implements ISBasProductSeasonService
{
    @Autowired
    private SBasProductSeasonMapper sBasProductSeasonMapper;

    /**
     * 查询产品季档案
     *
     * @param productSeasonSid 产品季档案ID
     * @return 产品季档案
     */
    @Override
    public ListSeasonResponse selectSBasProductSeasonById(Long productSeasonSid)
    {
        return sBasProductSeasonMapper.selectSBasProductSeasonById(productSeasonSid);
    }

    /**
     * 查询产品季档案列表
     *
     * @param listSeasonRequest 产品季档案
     * @return 产品季档案
     */
    @Override
    public List<ListSeasonResponse> selectSBasProductSeasonList(ListSeasonRequest listSeasonRequest)
    {
        Date endTime = listSeasonRequest.getCreateDateEnd();
        if (endTime != null){
            System.out.println("---------------------"+endTime);
            Date endTimeAdd = new Date(endTime.getTime()+24*3600*1000);
            System.out.println("-----------------------"+endTimeAdd);
            listSeasonRequest.setCreateDateEnd(endTimeAdd);
        }
        List<ListSeasonResponse> list = sBasProductSeasonMapper.selectSBasProductSeasonList(listSeasonRequest);

        return list;
    }

    /**
     * 新增产品季档案
     *
     * @param sBasProductSeason 产品季档案
     * @return 结果
     */
    @Override
    public int insertSBasProductSeason(SBasProductSeason sBasProductSeason)
    {
        SBasProductSeason query=new SBasProductSeason();
        query.setProductSeasonCode(sBasProductSeason.getProductSeasonCode());
        QueryWrapper<SBasProductSeason> wrapper=new QueryWrapper<>();
        wrapper.setEntity(query);
        query=sBasProductSeasonMapper.selectOne(wrapper);
        if(query!=null){
            throw new CheckedException("产品季编码重复，请查看");
        }
        sBasProductSeason.setProductSeasonSid(IdWorker.getId());
        return sBasProductSeasonMapper.insertSBasProductSeason(sBasProductSeason);
    }



    /**
     * 批量删除产品季档案
     *
     * @param productSeasonSids 需要删除的产品季档案ID
     * @return 结果
     */
    @Override
    public int deleteSBasProductSeasonByIds(String productSeasonSids)
    {
        return sBasProductSeasonMapper.deleteSBasProductSeasonByIds(productSeasonSids);
    }

    /**
     * 删除产品季档案信息
     *
     * @param productSeasonSid 产品季档案ID
     * @return 结果
     */
    @Override
    public int deleteSBasProductSeasonById(String productSeasonSid)
    {
        return sBasProductSeasonMapper.deleteSBasProductSeasonByIds(productSeasonSid);
    }



    @Override
    public int updateHandleStatus(EditHandleStatusRequest editHandleStatusRequest) {
        String confirmerAccount = editHandleStatusRequest.getConfirmerAccount();
        editHandleStatusRequest.setConfirmerAccount(null);
        String updaterAccount = editHandleStatusRequest.getUpdaterAccount();
        editHandleStatusRequest.setUpdaterAccount(null);
        HashSet<String> set = sBasProductSeasonMapper.selectHandleStatusById(editHandleStatusRequest.getProductSeasonSid());

        int newHandleStatus = Integer.parseInt(editHandleStatusRequest.getHandleStatus());
        if (set.size() != 1){
            throw new CheckedException("所选择的列状态可能已被更新！");
        }

        int oldHandleStatus = Integer.parseInt(set.iterator().next());
        if (oldHandleStatus == 1){
            if (newHandleStatus == 2){
                editHandleStatusRequest.setUpdaterAccount(updaterAccount);
            }else {
                throw new CheckedException("不可进行此操作");
            }


        }else if (oldHandleStatus == 2){
            if (newHandleStatus == 4 || newHandleStatus == 1){
                editHandleStatusRequest.setUpdaterAccount(updaterAccount);
            }else if (newHandleStatus == 3){

                editHandleStatusRequest.setConfirmerAccount(confirmerAccount);
            };

        }else if (oldHandleStatus == 4){
            if (newHandleStatus == 2){
                editHandleStatusRequest.setUpdaterAccount(updaterAccount);
            }else {
                throw new CheckedException("不可进行此操作");
            }

        }else{
            throw new CheckedException("不可进行此操作");
        }

        return sBasProductSeasonMapper.updateHandleStatus(editHandleStatusRequest);
    }

    @Override
    public int updateValidStatus(String productSeasonSid, String validStatus) {
        return sBasProductSeasonMapper.updateValidStatus(productSeasonSid,validStatus);
    }

    @Override
    public List<ExportSeasonResponse> getAllList() {
        return sBasProductSeasonMapper.getAllList();
    }


    @Override
    public List<ListSeasonResponse> seasonExport(ListSeasonRequest listSeasonRequest, String productSeasonSid) {
        if (productSeasonSid !=null && productSeasonSid .equals("")){
            return sBasProductSeasonMapper.selectSBasProductSeasonByIds(productSeasonSid);
        }else {
            return sBasProductSeasonMapper.selectSBasProductSeasonList(listSeasonRequest);
        }

    }
    /**
     * 修改产品季档案
     *
     * @param request 产品季档案
     * @return 结果
     */
    @Override
    public int updateSBasProductSeason(SBasProductSeason request)
    {
        ListSeasonResponse response = sBasProductSeasonMapper.selectSBasProductSeasonById(request.getProductSeasonSid());
        String status = response.getHandleStatus();
        if (status.equals("1") ){
            return sBasProductSeasonMapper.updateSBasProductSeason(request);
        }
        throw new CheckedException("该数据不可编辑");

    }

    @Override
    public int changeSBasProductSeason(SBasProductSeason request) {
        ListSeasonResponse response = sBasProductSeasonMapper.selectSBasProductSeasonById(request.getProductSeasonSid());


        String status = response.getHandleStatus();
        if (status.equals("3") ){

            return sBasProductSeasonMapper.updateSBasProductSeason(request);
        }
        throw new CheckedException("该数据不可变更");
    }

    /**
     * 产品季档案下拉框列表
     */
    @Override
    public List<SBasProductSeason> getProductSeasonList() {
        return sBasProductSeasonMapper.getProductSeasonList();
    }
}
