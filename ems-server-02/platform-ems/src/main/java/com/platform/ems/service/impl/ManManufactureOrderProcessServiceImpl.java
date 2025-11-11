package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.ManManufactureOrderProcessSetRequest;
import com.platform.ems.domain.dto.response.form.ManManuOrderProcessTracking;
import com.platform.ems.mapper.BasStaffMapper;
import com.platform.ems.mapper.ManManufactureOrderProcessMapper;
import com.platform.ems.mapper.ManProcessMapper;
import com.platform.ems.service.IManManufactureOrderProcessService;
import com.platform.ems.util.data.ComUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.platform.ems.util.LightUtil.*;

/**
 * 生产订单-工序Service业务层处理
 *
 * @author qhq
 * @date 2021-04-13
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderProcessServiceImpl extends ServiceImpl<ManManufactureOrderProcessMapper, ManManufactureOrderProcess>  implements IManManufactureOrderProcessService {
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private ManProcessMapper manProcessMapper;


    /**
     * 即将到期报表-按工序
     * @param manManufactureOrderProcess
     * @return
     */
    @Override
    public List<ManManufactureOrderProcess> selectExpiringProcessForm(ManManufactureOrderProcess manManufactureOrderProcess) {
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessMapper.selectToexpireList(manManufactureOrderProcess);
        list.forEach(item->{
            // 图片视频
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddGx()));
        });
        return list;
    }

    /**
     * 已逾期生产报表-按工序
     * @param manManufactureOrderProcess
     * @return
     */
    @Override
    public List<ManManufactureOrderProcess> selectOverdueProcessForm(ManManufactureOrderProcess manManufactureOrderProcess) {
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessMapper.selectOverdueList(manManufactureOrderProcess);
        list.forEach(item -> {
            // 图片视频预警灯
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddGx()));
        });
        return list;
    }

    /**
     * 查询生产订单-工序
     *
     * @param manufactureOrderProcessSid 生产订单-工序ID
     * @return 生产订单-工序
     */
    @Override
    public ManManufactureOrderProcess selectManManufactureOrderProcessById(Long manufactureOrderProcessSid) {
        ManManufactureOrderProcess process = manManufactureOrderProcessMapper.selectManManufactureOrderProcessById(manufactureOrderProcessSid);
        if (process != null) {
            // 图片视频预警灯
            process.setPicturePathList(ComUtil.strToArr(process.getPicturePath()));
            process.setVideoPathList(ComUtil.strToArr(process.getVideoPath()));
        }
        return process;
    }

    /**
     * 查询生产订单-工序列表
     *
     * @param manManufactureOrderProcess 生产订单-工序
     * @return 生产订单-工序
     */
    @Override
    public List<ManManufactureOrderProcess> selectManManufactureOrderProcessList(ManManufactureOrderProcess manManufactureOrderProcess) {
        List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(manManufactureOrderProcess);
        processList.forEach(item->{
            // 图片视频预警灯
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
        });
        return processList;
    }

    /**
     * 新增生产订单-工序
     * 需要注意编码重复校验
     * @param manManufactureOrderProcess 生产订单-工序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOrderProcess(ManManufactureOrderProcess manManufactureOrderProcess) {
        return manManufactureOrderProcessMapper.insert(manManufactureOrderProcess);
    }

    /**
     * 修改生产订单-工序
     *
     * @param manManufactureOrderProcess 生产订单-工序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrderProcess(ManManufactureOrderProcess manManufactureOrderProcess) {
        return manManufactureOrderProcessMapper.updateById(manManufactureOrderProcess);
    }

    /**
     * 批量删除生产订单-工序
     *
     * @param manufactureOrderProcessSids 需要删除的生产订单-工序ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOrderProcessByIds(List<String> manufactureOrderProcessSids) {
        return manManufactureOrderProcessMapper.deleteBatchIds(manufactureOrderProcessSids);
    }

    /**
     * 生产订单工序明细报表
     */
    @Override
    public List<ManManufactureOrderProcess> getItemList(ManManufactureOrderProcess manManufactureOrderProcess) {
        List<ManManufactureOrderProcess> processitemList = manManufactureOrderProcessMapper.getItemList(manManufactureOrderProcess);
        if (CollectionUtil.isNotEmpty(processitemList)) {
            for (ManManufactureOrderProcess item : processitemList) {
                // 图片视频预警灯
                item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScdd()));
            }
        }
        return processitemList;
    }

    /**
     * 设置计划信息和进度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int concernSet(ManManufactureOrderProcessSetRequest request) {
        LambdaUpdateWrapper<ManManufactureOrderProcess> updateWrapper = new LambdaUpdateWrapper<>();
        if (request.getManufactureOrderProcessSidList().length == 0){
            throw new CustomException("请选择行！");
        }
        int row = 0, flag = 0;
        updateWrapper.in(ManManufactureOrderProcess::getManufactureOrderProcessSid, request.getManufactureOrderProcessSidList());
        // 设置计划信息
        if ("JH".equals(request.getSetType())) {
            if ("Y".equals(request.getPlantSidIsUpd())) {
                flag = 1;
                if (request.getPlantSid() == null) {
                    throw new CustomException("工厂(工序)不允许为空");
                }
                updateWrapper.set(ManManufactureOrderProcess::getPlantSid, request.getPlantSid());
            }
            if ("Y".equals(request.getHandlerSidIsUpd())) {
                flag = 1;
                String directorSids = null, directorCodes = null;
                if (ArrayUtil.isNotEmpty(request.getDirectorSidList())) {
                    List<BasStaff> staffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                            .in(BasStaff::getStaffSid, request.getDirectorSidList()));
                    if (CollectionUtil.isNotEmpty(staffList)) {
                        Map<Long, String> staffMap = staffList.stream()
                                .collect(Collectors.toMap(BasStaff::getStaffSid, BasStaff::getStaffCode, (existing, replacement) -> existing));
                        directorSids = ""; directorCodes = "";
                        for (Long key : staffMap.keySet()) {
                            directorSids = directorSids + String.valueOf(key) + ";";
                            directorCodes = directorCodes + String.valueOf(staffMap.get(key)) + ";";
                        }
                    }
                }
                //---------------------------------------------
                updateWrapper.set(ManManufactureOrderProcess::getDirectorSid, directorSids);
                updateWrapper.set(ManManufactureOrderProcess::getDirectorCode, directorCodes);
            }
            if ("Y".equals(request.getPlanStartDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderProcess::getPlanStartDate, request.getPlanStartDate());
            }
            if ("Y".equals(request.getPlanEndDateIsUpd())) {
                flag = 1;
                //---------------------------------------------
                updateWrapper.set(ManManufactureOrderProcess::getPlanEndDate, request.getPlanEndDate());
            }
            if ("Y".equals(request.getPlanQuantityIsUpd())) {
                flag = 1;
                if (request.getPlanQuantity() == null) {
                    throw new CustomException("计划产量不允许为空");
                }
                updateWrapper.set(ManManufactureOrderProcess::getQuantity, request.getPlanQuantity());
            }
        }
        // 设置进度信息
        else if ("JD".equals(request.getSetType())) {
            if ("Y".equals(request.getEndStatusIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderProcess::getEndStatus, request.getEndStatus());
            }
            //---------------------------------------------
            if ("Y".equals(request.getActualEndDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderProcess::getActualEndDate, request.getActualEndDate());
            }
            if ("Y".equals(request.getHandleCommentIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderProcess::getComment, request.getHandleComment());
            }
            //---------------------------------------------
            if ("Y".equals(request.getPicturePathListIsUpd())) {
                flag = 1;
                String picture = null;
                if (ArrayUtil.isNotEmpty(request.getPicturePathList())) {
                    picture = "";
                    for (int i = 0; i < request.getPicturePathList().length; i++) {
                        picture = picture + request.getPicturePathList()[i] + ";";
                    }
                }
                updateWrapper.set(ManManufactureOrderProcess::getPicturePath, picture);
            }
            if ("Y".equals(request.getVideoPathListIsUpd())) {
                flag = 1;
                String video = null;
                if (ArrayUtil.isNotEmpty(request.getVideoPathList())) {
                    video = "";
                    for (int i = 0; i < request.getVideoPathList().length; i++) {
                        video = video + request.getVideoPathList()[i] + ";";
                    }
                }
                updateWrapper.set(ManManufactureOrderProcess::getVideoPath, video);
            }
        }
        else {}
        if (flag == 1) {
            if (updateWrapper.getSqlSet() != null) {
                row = manManufactureOrderProcessMapper.update(null, updateWrapper);
            }
        }
        return row;
    }

    /**
     * 设置完工量校验参考工序
     *
     * @param manManufactureOrderProcess 完工量校验参考工序 ; 参考工序所引用数量类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setReferProcess(ManManufactureOrderProcess manManufactureOrderProcess){
        int row = 0;
        LambdaUpdateWrapper<ManManufactureOrderProcess> updateWrapper = new LambdaUpdateWrapper<>();
        if ((manManufactureOrderProcess.getQuantityReferProcessSid() == null && StrUtil.isNotBlank(manManufactureOrderProcess.getQuantityTypeReferProcess()))
                || (manManufactureOrderProcess.getQuantityReferProcessSid() != null && StrUtil.isBlank(manManufactureOrderProcess.getQuantityTypeReferProcess()))){
            throw new BaseException("“完工量校验参考工序、参考工序所引用数量类型”必须都为空，或都不为空");
        }
        if ((manManufactureOrderProcess.getManufactureOrderProcessSidList() == null || manManufactureOrderProcess.getManufactureOrderProcessSidList().length == 0 )
                && manManufactureOrderProcess.getManufactureOrderProcessSid() != null){
            manManufactureOrderProcess.setManufactureOrderProcessSidList(new Long[]{manManufactureOrderProcess.getManufactureOrderProcessSid()});
        }
        if (manManufactureOrderProcess.getManufactureOrderProcessSidList() == null || manManufactureOrderProcess.getManufactureOrderProcessSidList().length == 0){
            throw new BaseException("请选择行");
        }
        updateWrapper.in(ManManufactureOrderProcess::getManufactureOrderProcessSid, manManufactureOrderProcess.getManufactureOrderProcessSidList())
                .set(ManManufactureOrderProcess::getQuantityReferProcessSid, manManufactureOrderProcess.getQuantityReferProcessSid())
                .set(ManManufactureOrderProcess::getQuantityTypeReferProcess, manManufactureOrderProcess.getQuantityTypeReferProcess());
        if (manManufactureOrderProcess.getQuantityReferProcessSid() != null && StrUtil.isBlank(manManufactureOrderProcess.getQuantityReferProcessCode())){
            ManProcess process = manProcessMapper.selectById(manManufactureOrderProcess.getQuantityReferProcessSid());
            if (process != null){
                updateWrapper.set(ManManufactureOrderProcess::getQuantityReferProcessCode, process.getProcessCode());
            }
        }
        if (manManufactureOrderProcess.getQuantityReferProcessSid() == null){
            updateWrapper.set(ManManufactureOrderProcess::getQuantityReferProcessCode, null);
        }
        row = manManufactureOrderProcessMapper.update(null, updateWrapper);
        return row;
    }

    /*
     * 设置计划开始日期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPlanStart(ManManufactureOrderProcess manManufactureOrderProcess) {
        if (manManufactureOrderProcess.getManufactureOrderProcessSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderProcess> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (manManufactureOrderProcess.getPlanStartDate() == null) {
            manManufactureOrderProcess.setPlanStartDate(null);
        }
        //计划开始日期
        updateWrapper.in(ManManufactureOrderProcess::getManufactureOrderProcessSid, manManufactureOrderProcess.getManufactureOrderProcessSidList())
                .set(ManManufactureOrderProcess::getPlanStartDate, manManufactureOrderProcess.getPlanStartDate());
        row = manManufactureOrderProcessMapper.update(null, updateWrapper);
        return row;
    }

    /*
     * 设置计划完成日期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPlanEnd(ManManufactureOrderProcess manManufactureOrderProcess) {
        if (manManufactureOrderProcess.getManufactureOrderProcessSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderProcess> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (manManufactureOrderProcess.getPlanEndDate() == null) {
            manManufactureOrderProcess.setPlanEndDate(null);
        }
        //计划完成日期
        updateWrapper.in(ManManufactureOrderProcess::getManufactureOrderProcessSid, manManufactureOrderProcess.getManufactureOrderProcessSidList())
                .set(ManManufactureOrderProcess::getPlanEndDate, manManufactureOrderProcess.getPlanEndDate());
        row = manManufactureOrderProcessMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置即将到期提醒天数
     * @param manManufactureOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(ManManufactureOrderProcess orderProcess) {
        if (orderProcess.getManufactureOrderProcessSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderProcess> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(ManManufactureOrderProcess::getManufactureOrderProcessSid, orderProcess.getManufactureOrderProcessSidList());
        updateWrapper.set(ManManufactureOrderProcess::getToexpireDaysScddGx, orderProcess.getToexpireDaysScddGx());
        row = manManufactureOrderProcessMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 生产进度跟踪报表（工序）
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    @Override
    public List<ManManuOrderProcessTracking> selectManufactureOrderProcessTrackingList(ManManuOrderProcessTracking request) {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManuOrderProcessTracking> list = manManufactureOrderProcessMapper.selectManufactureOrderProcessTrackingList(request);
        list.forEach(item -> {
            // 图片视频预警灯
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddGx()));
        });
        return list;
    }

}
