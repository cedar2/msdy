package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPdm;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConSaleStation;
import com.platform.ems.plug.mapper.ConSaleStationMapper;
import com.platform.ems.service.IPrjProjectImportService;
import com.platform.ems.service.IPrjProjectService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all" )
public class PrjProjectImportServiceImpl extends ServiceImpl<PrjProjectMapper, PrjProject> implements IPrjProjectImportService {

    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private DevDevelopPlanMapper developPlanMapper;
    @Autowired
    private DevCategoryPlanItemMapper categoryPlanItemMapper;
    @Autowired
    private BasMaterialMapper materialMapper;
    @Autowired
    private BasMaterialBarcodeMapper materialBarcodeMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private ConSaleStationMapper saleStationMapper;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;
    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导入试销项目
     * @param file 文件
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importShix(MultipartFile file, PrjProject prjProject) {
        int num = 0;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();

            // 数据字典年份
            List<DictData> yearList = sysDictDataService.selectDictData("s_year");
            yearList = yearList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearMaps = yearList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 数据字典试销类型
            List<DictData> trialsaleTypeList = sysDictDataService.selectDictData("s_trialsale_type");
            trialsaleTypeList = trialsaleTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> trialsaleTypeMaps = trialsaleTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 数据字典市场区域
            List<DictData> marketRegionList = sysDictDataService.selectDictData("s_market_region");
            marketRegionList = marketRegionList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> marketRegionMaps = marketRegionList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();

            // 列表
            List<PrjProject> projectList = new ArrayList<>();

            // 存在重复的负责人行数
            int staff = 0;

            // 开发项目编号 + 销售站点/网店 组合
            Map<String, Integer> map = new HashMap<>();

            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                // 单行
                PrjProject project = new PrjProject();

                /*
                 * 开发计划号 必填
                 */
                String developCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long developSid = null;
                if (StrUtil.isBlank(developCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("开发计划号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        DevDevelopPlan developPlan = developPlanMapper.selectOne(new QueryWrapper<DevDevelopPlan>()
                                .lambda().eq(DevDevelopPlan::getDevelopPlanCode, developCode));
                        if (developPlan == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("开发计划号“" + developCode + "”不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!ConstantsEms.CHECK_STATUS.equals(developPlan.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("开发计划号“" + developCode + "”未确认，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            developSid = developPlan.getDevelopPlanSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("开发计划号“" + developCode + "”系统中存在重复，请先检查此开发计划，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 开发项目编号 必填
                 */
                String developProjectCode = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                PrjProject developProject = null;
                if (StrUtil.isBlank(developProjectCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("开发项目编号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else if (developSid != null) {
                    developProject = prjProjectMapper.selectOne(new QueryWrapper<PrjProject>().lambda()
                            .eq(PrjProject::getProjectCode, developProjectCode)
                            .eq(PrjProject::getProjectType, ConstantsPdm.PROJECT_TYPE_KAIF)
                            .eq(PrjProject::getDevelopPlanSid, developSid));
                    if (developProject == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("开发计划“" + developCode + "”下不存在开发项目编号“" + developProjectCode + "”，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else if (!ConstantsEms.CHECK_STATUS.equals(developProject.getHandleStatus())) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("开发项目编号“" + developProjectCode + "未确认，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 年度 必填
                 */
                String yearName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                String year = null;
                if (StrUtil.isBlank(yearName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年度不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    year = yearMaps.get(yearName);
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 计划开始日期 必填
                 */
                String planStartDateS = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Date planStartDate = null;
                String yearmonth = null;
                if (StrUtil.isBlank(planStartDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("计划开始日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(planStartDateS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划开始日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        planStartDate = new Date();
                        try {
                            planStartDate = DateUtil.parse(planStartDateS);
                            yearmonth = DateUtil.format(planStartDate, "yyyy_MM");
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计划开始日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /*
                 * 计划完成日期 必填
                 */
                String planEndDateS = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                Date planEndDate = null;
                if (StrUtil.isBlank(planEndDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("计划完成日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(planEndDateS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划完成日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        planEndDate = new Date();
                        try {
                            planEndDate = DateUtil.parse(planEndDateS);
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计划完成日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                if (planStartDate != null && planEndDate != null) {
                    if (planStartDate.getTime() > planEndDate.getTime()) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划完成日期应小于计划开始日期，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 项目负责人姓名 必填
                 */
                String projectLeaderName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                Long projectLeaderSid = null;
                String projectLeaderCode = null;
                if (StrUtil.isBlank(projectLeaderName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("项目负责人姓名不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 员工 可能姓名存在重复
                    List<BasStaff> staffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda().eq(BasStaff::getStaffName, projectLeaderName));
                    if (CollectionUtil.isNotEmpty(staffList)) {
                        staffList = staffList.stream().filter(o -> ConstantsEms.ENABLE_STATUS.equals(o.getStatus())
                                && ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())
                                && ConstantsEms.IS_ON_JOB_ZZ.equals(o.getIsOnJob())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(staffList)) {
                            BasStaff basStaff = staffList.get(0);
                            if (staffList.size() > 1) {
                                infoMsg = new CommonErrMsgResponse();
                                infoMsg.setItemNum(num);
                                infoMsg.setMsg("系统存在多个姓名“" + projectLeaderName + "”的负责人档案，本次导入的是编号 " + basStaff.getStaffCode() + " 的负责人");
                                infoMsgList.add(infoMsg);
                                staff += 1;
                            }
                            projectLeaderSid = basStaff.getStaffSid();
                            projectLeaderCode = basStaff.getStaffCode();
                        }
                        else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(projectLeaderName + "对应的负责人必须是确认且在职状态，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("项目负责人" + projectLeaderName +"不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 试销类型 必填
                 */
                String trialsaleTypeName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                String trialsaleType = null;
                if (StrUtil.isBlank(trialsaleTypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("试销类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    trialsaleType = trialsaleTypeMaps.get(trialsaleTypeName);
                    if (StrUtil.isBlank(trialsaleType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("试销类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 市场区域名称 选填
                 */
                String marketRegionName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                String marketRegion = null;
                if (StrUtil.isNotBlank(marketRegionName)) {
                    // 通过数据字典标签获取数据字典的值
                    marketRegion = marketRegionMaps.get(marketRegionName);
                    if (StrUtil.isBlank(marketRegion)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("市场区域名称填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 销售站点/网店名称 选填
                 */
                String saleStationName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                String saleStationCode = null;
                if (StrUtil.isNotBlank(saleStationName)) {
                    try {
                        ConSaleStation saleStation = saleStationMapper.selectOne(new QueryWrapper<ConSaleStation>().lambda()
                                .eq(ConSaleStation::getName, saleStationName)
                                .eq(ConSaleStation::getStatus, ConstantsEms.ENABLE_STATUS).eq(ConSaleStation::getHandleStatus, ConstantsEms.CHECK_STATUS));
                        if (saleStation == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("销售站点/网店名称“" + saleStationName + "”填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            saleStationCode = saleStation.getCode().toString();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售站点/网店名称“" + saleStationName + "”系统中存在重复，请先检查此销售站点/网店，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    // 判断 开发计划编号 + 销售站点/网点
                    if (StrUtil.isNotBlank(developProjectCode)) {
                        if (map.containsKey(developProjectCode + "-" + saleStationName)) {
                            warnMsg = new CommonErrMsgResponse();
                            warnMsg.setItemNum(num);
                            warnMsg.setMsg("开发项目编号“" + developProjectCode + "”已存在销售站点/网店名称为“" + saleStationName + "”的试销项目，是否继续导入！");
                            warnMsgList.add(warnMsg);
                        }
                        else {
                            map.put(developProjectCode + "-" + saleStationName, num);
                        }
                    }
                }

                /*
                 * 商品MSKU编号(ERP) 选填
                 */
                String erpMaterialMskuCode = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (StrUtil.isNotBlank(erpMaterialMskuCode)) {
                    erpMaterialMskuCode = erpMaterialMskuCode.trim();
                    if (erpMaterialMskuCode.length() > 120) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品MSKU编号(ERP)最大长度只能到120位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 项目名称 选填
                 */
                String projectName = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                if (StrUtil.isNotBlank(projectName)) {
                    if (projectName.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("项目名称最大长度只能到300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 项目说明 选填
                 */
                String projectDescription = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                if (StrUtil.isNotBlank(projectDescription)) {
                    if (projectDescription.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("项目说明最大长度只能到600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 备注 选填
                 */
                String remark = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注最大长度只能到600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)) {
                    project.setDevelopPlanSid(developSid).setDevelopPlanCode(developCode).setYear(year)
                            .setPlanStartDate(planStartDate).setPlanEndDate(planEndDate)
                            .setYearmonthProject(yearmonth).setTrialsaleType(trialsaleType)
                            .setProjectLeaderSid(projectLeaderSid).setProjectLeaderCode(projectLeaderCode)
                            .setErpMaterialMskuCode(erpMaterialMskuCode).setProjectName(projectName)
                            .setProjectDescription(projectDescription).setRemark(remark);
                    project.setProjectType(ConstantsPdm.PROJECT_TYPE_SHIX).setProjectStatus(ConstantsPdm.PROJECT_STATUS_WKS)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS).setImportType(BusinessType.IMPORT.getValue())
                            .setProjectPhase(prjProject.getProjectPhase());
                    if (StrUtil.isNotBlank(marketRegion)) {
                        project.setMarketRegionList(new String[]{marketRegion});
                    }
                    if (StrUtil.isNotBlank(saleStationCode)) {
                        project.setSaleStationCodeList(new String[]{saleStationCode});
                    }
                    if (developProject != null) {
                        project.setCategoryPlanSid(developProject.getCategoryPlanSid()).setCategoryPlanCode(developProject.getCategoryPlanCode())
                                .setProductSeasonSid(developProject.getProductSeasonSid()).setProductSeasonCode(developProject.getProductSeasonCode())
                                .setPreProjectSid(developProject.getProjectSid()).setPreProjectCode(Long.parseLong(developProject.getProjectCode()))
                                .setPlanType(developProject.getPlanType()).setErpMaterialSkuBarcode(developProject.getErpMaterialSkuBarcode())
                                .setProductSid(developProject.getProductSid()).setProductCode(developProject.getProductCode());
                    }
                    projectList.add(project);
                }
            }

            // 判断
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "导入成功" + projectList.size() + "数据，系统中负责人姓名存在重复" + staff + "条";
                }
                return EmsResultEntity.warning(projectList, warnMsgList, infoMsgList, message);
            }
            else if (CollectionUtil.isNotEmpty(projectList)) {
                for (PrjProject project : projectList) {
                    prjProjectService.insertPrjProject(project);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "导入成功" + projectList.size() + "数据，系统中负责人姓名存在重复" + staff + "条";
                }
                return EmsResultEntity.success(num-2, null, infoMsgList, message);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success();
    }

    /**
     * 导入开发项目
     * @param file 文件
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importKaif(MultipartFile file, PrjProject prjProject) {
        int num = 0;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();

            // 数据字典年份
            List<DictData> yearList = sysDictDataService.selectDictData("s_year");
            yearList = yearList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearMaps = yearList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 数据字典试销类型
            List<DictData> trialsaleTypeList = sysDictDataService.selectDictData("s_trialsale_type");
            trialsaleTypeList = trialsaleTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> trialsaleTypeMaps = trialsaleTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 数据字典市场区域
            List<DictData> marketRegionList = sysDictDataService.selectDictData("s_market_region");
            marketRegionList = marketRegionList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> marketRegionMaps = marketRegionList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();

            // 获取租户字段配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectSysDefaultSettingClientById(ApiThreadLocalUtil.get().getClientId());
            // 获取商品SKU编码(ERP)录入方式(项目)
            String erpMaterialSkuEnterModeProject = null;
            // 商品款号/SPU号录入方式(项目)
            String productCodeEnterModeProject = null;
            if (settingClient != null) {
                erpMaterialSkuEnterModeProject = settingClient.getErpMaterialSkuEnterModeProject();
                productCodeEnterModeProject = settingClient.getProductCodeEnterModeProject();
            }

            // 列表
            List<PrjProject> projectList = new ArrayList<>();

            // 存在重复的负责人行数
            int staff = 0;

            // 开发计划号重复判断
            Map<String, Integer> map = new HashMap<>();

            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                // 单行
                PrjProject project = new PrjProject();

                /*
                 * 开发计划号 必填
                 */
                String developCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                DevDevelopPlan developPlan = null;
                Long developSid = null;
                String planType = null;
                if (StrUtil.isBlank(developCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("开发计划号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        developPlan = developPlanMapper.selectOne(new QueryWrapper<DevDevelopPlan>()
                                .lambda().eq(DevDevelopPlan::getDevelopPlanCode, developCode));
                        if (developPlan == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("开发计划号“" + developCode + "”不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (map.containsKey(developCode)) {
                                warnMsg = new CommonErrMsgResponse();
                                warnMsg.setItemNum(num);
                                warnMsg.setMsg("开发计划号“" + developCode + "”表格内存在重复，是否继续导入！");
                                warnMsgList.add(warnMsg);
                            }
                            else {
                                map.put(developCode, num);
                            }
                            if (!ConstantsEms.CHECK_STATUS.equals(developPlan.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("开发计划号“" + developCode + "”未确认，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            developSid = developPlan.getDevelopPlanSid();
                            if (developPlan.getCategoryPlanItemSid() != null) {
                                DevCategoryPlanItem categoryPlanItem = categoryPlanItemMapper.selectById(developPlan.getCategoryPlanItemSid());
                                if (categoryPlanItem != null) {
                                    planType = categoryPlanItem.getPlanType();
                                }
                            }
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("开发计划号“" + developCode + "”系统中存在重复，请先检查此开发计划，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 年度 必填
                 */
                String yearName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                String year = null;
                if (StrUtil.isBlank(yearName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年度不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    year = yearMaps.get(yearName);
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 计划开始日期 必填
                 */
                String planStartDateS = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Date planStartDate = null;
                String yearmonth = null;
                if (StrUtil.isBlank(planStartDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("计划开始日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(planStartDateS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划开始日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        planStartDate = new Date();
                        try {
                            planStartDate = DateUtil.parse(planStartDateS);
                            yearmonth = DateUtil.format(planStartDate, "yyyy_MM");
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计划开始日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /*
                 * 计划完成日期 必填
                 */
                String planEndDateS = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                Date planEndDate = null;
                if (StrUtil.isBlank(planEndDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("计划完成日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(planEndDateS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划完成日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        planEndDate = new Date();
                        try {
                            planEndDate = DateUtil.parse(planEndDateS);
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计划完成日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                if (planStartDate != null && planEndDate != null) {
                    if (planStartDate.getTime() > planEndDate.getTime()) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划完成日期应小于计划开始日期，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 项目负责人姓名 必填
                 */
                String projectLeaderName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Long projectLeaderSid = null;
                String projectLeaderCode = null;
                BasStaff basStaff = null;
                if (StrUtil.isBlank(projectLeaderName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("项目负责人姓名不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 员工 可能姓名存在重复
                    List<BasStaff> staffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda().eq(BasStaff::getStaffName, projectLeaderName));
                    if (CollectionUtil.isNotEmpty(staffList)) {
                        staffList = staffList.stream().filter(o -> ConstantsEms.ENABLE_STATUS.equals(o.getStatus())
                                && ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())
                                && ConstantsEms.IS_ON_JOB_ZZ.equals(o.getIsOnJob())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(staffList)) {
                            basStaff = staffList.get(0);
                            if (staffList.size() > 1) {
                                infoMsg = new CommonErrMsgResponse();
                                infoMsg.setItemNum(num);
                                infoMsg.setMsg("系统存在多个姓名“" + projectLeaderName + "”的负责人档案，本次导入的是编号 " + basStaff.getStaffCode() + " 的负责人");
                                infoMsgList.add(infoMsg);
                                staff += 1;

                            }
                            projectLeaderSid = basStaff.getStaffSid();
                            projectLeaderCode = basStaff.getStaffCode();
                        }
                        else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(projectLeaderName + "对应的负责人必须是确认且在职状态，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("项目负责人" + projectLeaderName +"不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 市场区域名称 选填
                 */
                String marketRegionNames = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                List<String> marketRegionCodeList = new ArrayList<>();
                if (StrUtil.isNotBlank(marketRegionNames)) {
                    String[] marketRegionNameList = marketRegionNames.split(";|；");
                    //字符串拆分数组后利用set去重复
                    Set<String> namesSet = new HashSet<>(Arrays.asList(marketRegionNameList));
                    for (String marketRegionName : namesSet) {
                        // 通过数据字典标签获取数据字典的值
                        String marketRegion = marketRegionMaps.get(marketRegionName);
                        if (StrUtil.isBlank(marketRegion)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("“" + marketRegionName + "”市场区域名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            marketRegionCodeList.add(marketRegion);
                        }
                    }
                }

                /*
                 * 销售站点/网店名称 选填
                 */
                String saleStationNames = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                List<String> saleStationCodeList = new ArrayList<>();
                if (StrUtil.isNotBlank(saleStationNames)) {
                    String[] saleStationNameList = saleStationNames.split(";|；");
                    //字符串拆分数组后利用set去重复
                    Set<String> namesSet = new HashSet<>(Arrays.asList(saleStationNameList));
                    for (String saleStationName : namesSet) {
                        try {
                            ConSaleStation saleStation = saleStationMapper.selectOne(new QueryWrapper<ConSaleStation>().lambda()
                                    .eq(ConSaleStation::getName, saleStationName)
                                    .eq(ConSaleStation::getStatus, ConstantsEms.ENABLE_STATUS).eq(ConSaleStation::getHandleStatus, ConstantsEms.CHECK_STATUS));
                            if (saleStation == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("销售站点/网店名称“" + saleStationName + "”填写错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                saleStationCodeList.add(saleStation.getCode().toString());
                            }
                        } catch (TooManyResultsException e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("销售站点/网店名称“" + saleStationName + "”系统中存在重复，请先检查此销售站点/网店，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /*
                 * 商品SKU编码(ERP) 选填
                 */
                String erpMaterialSkuBarcode = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                if (StrUtil.isNotBlank(erpMaterialSkuBarcode)) {
                    if (ConstantsPdm.PROJECT_INFO_ENTER_MODE_SG.equals(erpMaterialSkuEnterModeProject)) {
                        erpMaterialSkuBarcode = erpMaterialSkuBarcode.trim();
                        if (erpMaterialSkuBarcode.length() > 120) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品SKU编码(ERP)最大长度只能到120位，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else if (ConstantsPdm.PROJECT_INFO_ENTER_MODE_XZ.equals(erpMaterialSkuEnterModeProject)) {
                        List<BasMaterialBarcode> barcodeList = materialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                                .eq(BasMaterialBarcode::getErpMaterialSkuBarcode, erpMaterialSkuBarcode));
                        if (CollectionUtil.isEmpty(barcodeList)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品SKU编码(ERP)”" + erpMaterialSkuBarcode + "“不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /*
                 * 商品款号/SPU号 选填
                 */
                String productCode = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                Long productSid = null;
                if (StrUtil.isNotBlank(productCode)) {
                    if (ConstantsPdm.PROJECT_INFO_ENTER_MODE_SG.equals(productCodeEnterModeProject)) {
                        productCode = productCode.trim();
                        if (productCode.length() > 120) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品款号/SPU号最大长度只能到120位，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else if (ConstantsPdm.PROJECT_INFO_ENTER_MODE_XZ.equals(productCodeEnterModeProject)) {
                        BasMaterial material = materialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, productCode));
                        if (material == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品款号/SPU号”" + productCode + "“不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            productSid = material.getMaterialSid();
                        }
                    }
                }

                /*
                 * 项目名称 选填
                 */
                String projectName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                if (StrUtil.isNotBlank(projectName)) {
                    if (projectName.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("项目名称最大长度只能到300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 项目说明 选填
                 */
                String projectDescription = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (StrUtil.isNotBlank(projectDescription)) {
                    if (projectDescription.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("项目说明最大长度只能到600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 备注 选填
                 */
                String remark = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注最大长度只能到600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)) {
                    project.setDevelopPlanSid(developSid).setDevelopPlanCode(developCode).setYear(year)
                            .setPlanStartDate(planStartDate).setPlanEndDate(planEndDate).setYearmonthProject(yearmonth)
                            .setPlanType(planType).setProjectLeaderSid(projectLeaderSid).setProjectLeaderCode(projectLeaderCode)
                            .setErpMaterialSkuBarcode(erpMaterialSkuBarcode).setProductCode(productCode).setProductSid(productSid)
                            .setProjectName(projectName).setProjectDescription(projectDescription).setRemark(remark);
                    if (developPlan != null) {
                        project.setCategoryPlanSid(developPlan.getCategoryPlanSid()).setCategoryPlanCode(developPlan.getCategoryPlanCode())
                                .setProductSeasonSid(developPlan.getProductSeasonSid()).setProductSeasonCode(developPlan.getProductSeasonCode());
                    }
                    if (CollectionUtil.isNotEmpty(marketRegionCodeList)) {
                        project.setMarketRegionList(marketRegionCodeList.toArray(new String[marketRegionCodeList.size()]));
                    }
                    if (CollectionUtil.isNotEmpty(saleStationCodeList)) {
                        project.setSaleStationCodeList(saleStationCodeList.toArray(new String[saleStationCodeList.size()]));
                    }
                    project.setProjectType(ConstantsPdm.PROJECT_TYPE_KAIF).setProjectStatus(ConstantsPdm.PROJECT_STATUS_WKS)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS).setImportType(BusinessType.IMPORT.getValue());
                    projectList.add(project);
                }
            }

            // 判断
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "导入成功" + projectList.size() + "数据，系统中负责人姓名存在重复" + staff + "条";
                }
                return EmsResultEntity.warning(projectList, warnMsgList, infoMsgList, message);
            }
            else if (CollectionUtil.isNotEmpty(projectList)) {
                for (PrjProject project : projectList) {
                    prjProjectService.insertPrjProject(project);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "导入成功" + projectList.size() + "数据，系统中负责人姓名存在重复" + staff + "条";
                }
                return EmsResultEntity.success(num-2, null, infoMsgList, message);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success();
    }

    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
}
