package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsProcess;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.system.domain.SysRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IPayProductJijianSettleInforService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品计件结算信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-07-14
 */
@Service
@SuppressWarnings("all" )
public class PayProductJijianSettleInforServiceImpl extends ServiceImpl<PayProductJijianSettleInforMapper, PayProductJijianSettleInfor> implements IPayProductJijianSettleInforService {
    @Autowired
    private PayProductJijianSettleInforMapper payProductJijianSettleInforMapper;
    @Autowired
    private PayProcessStepCompleteItemMapper payProcessStepCompleteItemMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteSystemService remoteSystemService;

    private static final String TITLE = "商品计件结算信息" ;

    /**
     * 查询商品计件结算信息
     *
     * @param jijianSettleInforSid 商品计件结算信息ID
     * @return 商品计件结算信息
     */
    @Override
    public PayProductJijianSettleInfor selectPayProductJijianSettleInforById(Long jijianSettleInforSid) {
        PayProductJijianSettleInfor payProductJijianSettleInfor = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforById(jijianSettleInforSid);
        MongodbUtil.find(payProductJijianSettleInfor);
        return payProductJijianSettleInfor;
    }

    /**
     * 查询商品计件结算信息列表
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 商品计件结算信息
     */
    @Override
    public List<PayProductJijianSettleInfor> selectPayProductJijianSettleInforList(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforList(payProductJijianSettleInfor);
        this.setStatus(list);
        return list;
    }

    /**
     * 查询商品计件结算信息列表 --- 精确查询
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 商品计件结算信息
     */
    @Override
    public List<PayProductJijianSettleInfor> selectPayProductJijianSettleInforListPrecision(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforListPrecision(payProductJijianSettleInfor);
        this.setStatus(list);
        return list;
    }

    /**
     * 关联出申报状态
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 商品计件结算信息
     */
    private void setStatus(List<PayProductJijianSettleInfor> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                PayProcessStepCompleteItem completeItem = new PayProcessStepCompleteItem();
                completeItem.setYearmonth(item.getYearmonth()).setPlantSid(item.getPlantSid())
                        .setWorkCenterSid(item.getWorkCenterSid()).setDepartment(item.getDepartment())
                        .setProductPriceType(item.getProductPriceType()).setJixinWangongType(item.getJixinWangongType())
                        .setPaichanBatch(item.getPaichanBatch()).setProductSid(item.getProductSid()).setIsPaichanPre(ConstantsEms.YES);
                List<PayProcessStepCompleteItem> completeItemList = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(completeItem);
                if (CollectionUtil.isNotEmpty(completeItemList)) {
                    if (ConstantsEms.CHECK_STATUS.equals(completeItemList.get(0).getHandleStatus())) {
                        item.setJixinStatus(ConstantsProcess.JIXIN_REPORT_STATUS_YSB);
                    }
                    else {
                        item.setJixinStatus(ConstantsProcess.JIXIN_REPORT_STATUS_SBZ);
                    }
                }
                else {
                    item.setJixinStatus(ConstantsProcess.JIXIN_REPORT_STATUS_WSB);
                }
            });
        }
    }

    /**
     * 新增商品计件结算信息
     * 需要注意编码重复校验
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayProductJijianSettleInfor(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        checkUnique(payProductJijianSettleInfor);
        setPlantAndCenterCode(payProductJijianSettleInfor);
        int row = payProductJijianSettleInforMapper.insert(payProductJijianSettleInfor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PayProductJijianSettleInfor(), payProductJijianSettleInfor);
            MongodbDeal.insert(payProductJijianSettleInfor.getJijianSettleInforSid(), payProductJijianSettleInfor.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 设置工厂和班组的编码
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    private void setPlantAndCenterCode(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        // 工厂code
        if (payProductJijianSettleInfor.getPlantSid() != null) {
            BasPlant plant = basPlantMapper.selectById(payProductJijianSettleInfor.getPlantSid());
            if (plant != null) {
                payProductJijianSettleInfor.setPlantCode(plant.getPlantCode());
            }
        }
        // 班组code
        if (payProductJijianSettleInfor.getWorkCenterSid() != null) {
            ManWorkCenter center = manWorkCenterMapper.selectById(payProductJijianSettleInfor.getWorkCenterSid());
            if (center != null) {
                payProductJijianSettleInfor.setWorkCenterCode(center.getWorkCenterCode());
            }
        }
    }

    /**
     * 修改商品计件结算信息
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProductJijianSettleInfor(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        checkUnique(payProductJijianSettleInfor);
        PayProductJijianSettleInfor original = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforById(payProductJijianSettleInfor.getJijianSettleInforSid());
        payProductJijianSettleInfor.setUpdateDate(new Date());
        payProductJijianSettleInfor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        setPlantAndCenterCode(payProductJijianSettleInfor);
        int row = payProductJijianSettleInforMapper.updateAllById(payProductJijianSettleInfor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, payProductJijianSettleInfor);
            MongodbDeal.update(payProductJijianSettleInfor.getJijianSettleInforSid(), original.getHandleStatus(), payProductJijianSettleInfor.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更商品计件结算信息
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProductJijianSettleInfor(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        checkUnique(payProductJijianSettleInfor);
        PayProductJijianSettleInfor response = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforById(payProductJijianSettleInfor.getJijianSettleInforSid());
        payProductJijianSettleInfor.setUpdateDate(new Date());
        payProductJijianSettleInfor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        setPlantAndCenterCode(payProductJijianSettleInfor);
        int row = payProductJijianSettleInforMapper.updateAllById(payProductJijianSettleInfor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProductJijianSettleInfor.getJijianSettleInforSid(), BusinessType.CHANGE.getValue(), response, payProductJijianSettleInfor, TITLE);
        }
        return row;
    }

    /**
     * 重复性校验
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    private void checkUnique(PayProductJijianSettleInfor payProductJijianSettleInfor){
        QueryWrapper<PayProductJijianSettleInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayProductJijianSettleInfor::getYearmonth, payProductJijianSettleInfor.getYearmonth())
                .eq(PayProductJijianSettleInfor::getPlantSid, payProductJijianSettleInfor.getPlantSid())
                .eq(PayProductJijianSettleInfor::getWorkCenterSid, payProductJijianSettleInfor.getWorkCenterSid())
                .eq(PayProductJijianSettleInfor::getDepartment, payProductJijianSettleInfor.getDepartment())
                .eq(PayProductJijianSettleInfor::getProductPriceType, payProductJijianSettleInfor.getProductPriceType())
                .eq(PayProductJijianSettleInfor::getJixinWangongType, payProductJijianSettleInfor.getJixinWangongType())
                .eq(PayProductJijianSettleInfor::getProductSid, payProductJijianSettleInfor.getProductSid())
                .eq(PayProductJijianSettleInfor::getPaichanBatch, payProductJijianSettleInfor.getPaichanBatch());
        if (payProductJijianSettleInfor.getJijianSettleInforSid() != null) {
            queryWrapper.lambda().ne(PayProductJijianSettleInfor::getJijianSettleInforSid, payProductJijianSettleInfor.getJijianSettleInforSid());
        }
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)){
            throw new BaseException("所属年月+工厂+班组+操作部门+商品编码(款号)+排产批次号+商品工价类型+计薪完工类型“相关值的组合已存在结算数，请检查！");
        }
    }

    /**
     * 批量删除商品计件结算信息
     *
     * @param jijianSettleInforSids 需要删除的商品计件结算信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProductJijianSettleInforByIds(List<Long> jijianSettleInforSids) {
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforMapper.selectList(new QueryWrapper<PayProductJijianSettleInfor>()
                .lambda().in(PayProductJijianSettleInfor::getJijianSettleInforSid, jijianSettleInforSids));
        int row = payProductJijianSettleInforMapper.deleteBatchIds(jijianSettleInforSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PayProductJijianSettleInfor());
                MongodbUtil.insertUserLog(o.getJijianSettleInforSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param payProductJijianSettleInfor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayProductJijianSettleInfor payProductJijianSettleInfor) {
        int row = 0;
        Long[] sids = payProductJijianSettleInfor.getJijianSettleInforSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<PayProductJijianSettleInfor> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PayProductJijianSettleInfor::getJijianSettleInforSid, sids);
            updateWrapper.set(PayProductJijianSettleInfor::getHandleStatus, payProductJijianSettleInfor.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(payProductJijianSettleInfor.getHandleStatus())) {
                updateWrapper.set(PayProductJijianSettleInfor::getConfirmDate, new Date());
                updateWrapper.set(PayProductJijianSettleInfor::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = payProductJijianSettleInforMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, payProductJijianSettleInfor.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 根据计薪申报量单查询
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    public List<PayProductJijianSettleInfor> listBy(PayProcessStepComplete payProcessStepComplete){
        if (CollectionUtil.isNotEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())){
            Long[] productSidList = payProcessStepComplete.getPayProcessStepCompleteItemList().stream().map(PayProcessStepCompleteItem::getProductSid).toArray(Long[]::new);
            List<PayProductJijianSettleInfor> inforList = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforList(new PayProductJijianSettleInfor()
                    .setYearmonth(payProcessStepComplete.getYearmonth()).setPlantSid(payProcessStepComplete.getPlantSid())
                    .setWorkCenterSid(payProcessStepComplete.getWorkCenterSid()).setProductPriceType(payProcessStepComplete.getProductPriceType())
                    .setJixinWangongType(payProcessStepComplete.getJixinWangongType()).setDepartment(payProcessStepComplete.getDepartment())
                    .setProductSidList(productSidList));
            if (CollectionUtil.isNotEmpty(inforList)){
                inforList = inforList.stream().filter(item ->
                        payProcessStepComplete.getPayProcessStepCompleteItemList().stream().map(up -> up.getProductSid()+String.valueOf(up.getPaichanBatch())).collect(Collectors.toList())
                                .contains(item.getProductSid()+String.valueOf(item.getPaichanBatch()))).collect(Collectors.toList());
            }
            return inforList;
        }
        return new ArrayList<>();
    }

    /**
     * 根据计薪申报量单查询汇总
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    public List<PayProductJijianSettleInfor> collectBy(PayProcessStepComplete payProcessStepComplete) {
        List<PayProductJijianSettleInfor> response = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())) {
            Set<PayProcessStepCompleteItem> playerSet = new TreeSet<>(Comparator.comparing(o -> (o.getProductSid() + "" + String.valueOf(o.getPaichanBatch()))));
            playerSet.addAll(payProcessStepComplete.getPayProcessStepCompleteItemList());
            BigDecimal add = BigDecimal.ZERO;
            for (PayProcessStepCompleteItem item : playerSet){
                List<PayProductJijianSettleInfor> responseItem = payProductJijianSettleInforMapper.collectPayProductJijianSettleInforList(new PayProductJijianSettleInfor()
                        .setProductSid(item.getProductSid()).setPlantSid(payProcessStepComplete.getPlantSid()).setIsPaichanPre(ConstantsEms.YES)
                        .setPaichanBatchToString(String.valueOf(item.getPaichanBatch())).setDepartment(payProcessStepComplete.getDepartment())
                        .setProductPriceType(payProcessStepComplete.getProductPriceType()).setJixinWangongType(payProcessStepComplete.getJixinWangongType()));
                if (CollectionUtil.isNotEmpty(responseItem)) {
                    add = BigDecimal.ZERO;
                    if (!payProcessStepComplete.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
                        QueryWrapper<PayProductJijianSettleInfor> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda()
                                .eq(PayProductJijianSettleInfor::getYearmonth, payProcessStepComplete.getYearmonth())
                                .eq(PayProductJijianSettleInfor::getPlantSid, payProcessStepComplete.getPlantSid())
                                .eq(PayProductJijianSettleInfor::getWorkCenterSid, payProcessStepComplete.getWorkCenterSid())
                                .eq(PayProductJijianSettleInfor::getDepartment, payProcessStepComplete.getDepartment())
                                .eq(PayProductJijianSettleInfor::getProductSid, item.getProductSid());
                        if (item.getPaichanBatch() == null) {
                            queryWrapper.lambda().isNull(PayProductJijianSettleInfor::getPaichanBatch);
                        }
                        else {
                            queryWrapper.lambda().eq(PayProductJijianSettleInfor::getPaichanBatch, item.getPaichanBatch());
                        }
                        try {
                            PayProductJijianSettleInfor info = payProductJijianSettleInforMapper.selectOne(queryWrapper);
                            if (info != null) {
                                add = info.getSettleQuantity();
                            }
                        }catch (Exception e) {

                        }
                    }
                    for (PayProductJijianSettleInfor infor : responseItem) {
                        infor.setSettleQuantityMonth(infor.getSettleQuantityCheck().add(add));
                    }
                    response.addAll(responseItem);
                }
            }
        }
        return response;
    }

    /**
     * 计件结算数汇总
     * @param payProcessStepComplete
     * @return
     */
    @Override
    public List<PayProductJijianSettleInfor> collect(PayProductJijianSettleInfor payProductJijianSettleInfor){
        return payProductJijianSettleInforMapper.collectPayProductJijianSettleInforList(payProductJijianSettleInfor);
    }

    /**
     * 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file){
        PayProductJijianSettleInfor infor = new PayProductJijianSettleInfor();
        List<PayProductJijianSettleInfor> list = new ArrayList<>();
        // 报错信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        // 唯一性校验
        HashMap<String, String> mainMap = new HashMap<>();
        // 工厂权限
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败" );
            }
            ExcelReader reader = ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                int num = i + 1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                //唯一主表
                infor = new PayProductJijianSettleInfor();
                /*
                 * 所属年月 2022/06  必填
                 */
                String yearmonth = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(yearmonth)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("所属年月不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isYearMonth(yearmonth)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("所属年月格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yearmonth = yearmonth.replace("/","-");
                    }
                }
                /*
                 * SCM工厂简称  必填
                 */
                String plantShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long plantSid = null;
                String plantCode = null;
                if(StrUtil.isBlank(plantShortName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工厂简称，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasPlant basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>()
                                .lambda().eq(BasPlant::getShortName, plantShortName));
                        if(basPlant==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工厂" + plantShortName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basPlant.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basPlant.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("工厂，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                if (!isAll){
                                    Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
                                    if (staffSid != null) {
                                        BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                                        if (!basPlant.getPlantSid().equals(String.valueOf(staff.getDefaultPlantSid()))) {
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("无权限导入" + plantShortName + "（工厂）的商品计件结算数，导入失败！");
                                            errMsgList.add(errMsg);
                                        }else {
                                            plantSid = Long.parseLong(basPlant.getPlantSid());
                                            plantCode = basPlant.getPlantCode();
                                        }
                                    }
                                }
                                else {
                                    plantSid = Long.parseLong(basPlant.getPlantSid());
                                    plantCode = basPlant.getPlantCode();
                                }
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(plantShortName +"工厂档案存在重复，请先检查该工厂，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * SCM班组名称  必填
                 */
                String workCenterName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Long workCenterSid = null;
                String workCenterCode = null;
                if (StrUtil.isBlank(workCenterName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("班组名称，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (plantSid != null){
                        ManWorkCenter workCenter = new ManWorkCenter();
                        try {
                            workCenter = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                                    .eq(ManWorkCenter::getPlantSid, plantSid).eq(ManWorkCenter::getWorkCenterName, workCenterName));
                            if (workCenter != null){
                                if (!ConstantsEms.ENABLE_STATUS.equals(workCenter.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(workCenter.getHandleStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("班组，必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                workCenterSid = workCenter.getWorkCenterSid();
                                workCenterCode = workCenter.getWorkCenterCode();
                            }
                            else {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("找不到工厂" + plantShortName + "的班组" + workCenterName + "，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工厂" + plantShortName +"的班组" + workCenterName + "存在重复，请先检查该工厂与班组的关系，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * SCM操作部门名称  必填
                 */
                String departmentName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                String department = null;
                if (StrUtil.isBlank(departmentName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("操作部门名称，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>()
                                .lambda().eq(ConManufactureDepartment::getName, departmentName));
                        if (conManufactureDepartment == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("操作部门" + departmentName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.ENABLE_STATUS.equals(conManufactureDepartment.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conManufactureDepartment.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("操作部门，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                department = conManufactureDepartment.getCode();
                            }
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(departmentName + "操作部门配置存在重复，请先检查该操作部门，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * SCM商品编码(款号)  必填
                 */
                String productCode = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Long productSid = null;
                if (StrUtil.isBlank(productCode)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码(款号)，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>()
                                .lambda().eq(BasMaterial::getMaterialCode, productCode));
                        if(basMaterial==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品" + productCode + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("商品，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            productSid = basMaterial.getMaterialSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(productCode +"商品档案存在重复，请先检查该商品，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 结算数  必填
                 */
                String settleQuantity_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal settleQuantity = null;
                if (StrUtil.isBlank(settleQuantity_s)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("结算数，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(settleQuantity_s,8,3)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("结算数格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        settleQuantity = new BigDecimal(settleQuantity_s);
                        if (settleQuantity.compareTo(BigDecimal.ZERO) <= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("结算数只能是大于0的整数，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 排产批次号 非必填
                 */
                String paichanBatch_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                Long paichanBatch = null;
                if (StrUtil.isNotBlank(paichanBatch_s)){
                    try {
                        paichanBatch = Long.parseLong(paichanBatch_s);
                        if (paichanBatch_s.length() > 5){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("排产批次号格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("排产批次号格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (paichanBatch != null && paichanBatch.compareTo(new Long(0)) <= 0){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("排产批次号只能是大于0的整数，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                if (yearmonth != null && plantSid != null && workCenterSid != null && department != null && productSid != null){
                    if (mainMap.get(yearmonth.toString()+plantSid.toString()+workCenterSid.toString()+department.toString()+productSid.toString()+String.valueOf(paichanBatch)) == null){
                        QueryWrapper<PayProductJijianSettleInfor> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(PayProductJijianSettleInfor::getYearmonth, yearmonth)
                                .eq(PayProductJijianSettleInfor::getPlantSid, plantSid)
                                .eq(PayProductJijianSettleInfor::getWorkCenterSid, workCenterSid)
                                .eq(PayProductJijianSettleInfor::getDepartment, department)
                                .eq(PayProductJijianSettleInfor::getProductPriceType, ConstantsEms.PRODUCT_PRICE_TYPE_DH)
                                .eq(PayProductJijianSettleInfor::getJixinWangongType, ConstantsEms.JXCG)
                                .eq(PayProductJijianSettleInfor::getProductSid, productSid);
                        if (paichanBatch == null) {
                            queryWrapper.lambda().isNull(PayProductJijianSettleInfor::getPaichanBatch);
                        }
                        else {
                            queryWrapper.lambda().eq(PayProductJijianSettleInfor::getPaichanBatch, paichanBatch);
                        }
                        List<PayProductJijianSettleInfor> settleInforList = payProductJijianSettleInforMapper.selectList(queryWrapper);
                        if (CollectionUtil.isNotEmpty(settleInforList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中已存在该组合，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            mainMap.put(yearmonth.toString()+plantSid.toString()+workCenterSid.toString()+department.toString()+productSid.toString()+String.valueOf(paichanBatch), "1");
                        }
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中已存在该组合，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 备注 非必填
                 */
                String remark = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                if (CollectionUtil.isEmpty(errMsgList)){
                    infor.setYearmonth(yearmonth).setPlantSid(plantSid).setPlantCode(plantCode).setWorkCenterSid(workCenterSid).setWorkCenterCode(workCenterCode)
                            .setDepartment(department).setProductSid(productSid).setProductCode(productCode).setSettleQuantity(settleQuantity)
                            .setPaichanBatch(paichanBatch).setRemark(remark);
                    infor.setHandleStatus(ConstantsEms.SAVA_STATUS).setProductPriceType(ConstantsEms.PRODUCT_PRICE_TYPE_DH)
                            .setJixinWangongType(ConstantsEms.JXCG);
                    list.add(infor);
                }
            }
        }catch (Exception e){
            throw new BaseException("系统未知错误");
        }
        if (CollectionUtil.isNotEmpty(errMsgList)){
            return EmsResultEntity.error(errMsgList);
        }
        else {
            if (CollectionUtil.isNotEmpty(list)) {
                int row = payProductJijianSettleInforMapper.inserts(list);
                return EmsResultEntity.success(row);
            }
        }
        return EmsResultEntity.success();
    }

    //填充
    private void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        for (int i = lineSize; i < size; i++) {
            Object o = null;
            objects.add(o);
        }
    }

}
