package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProcessStepCompleteRecord;
import com.platform.ems.domain.StaffCompleteSummary;
import com.platform.ems.domain.StaffCompleteSummaryTable;
import com.platform.ems.domain.StepFinishDetail;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 商品道序完成量台账-主Service接口
 *
 * @author chenkw
 * @date 2022-10-20
 */
public interface IManProcessStepCompleteRecordService extends IService<ManProcessStepCompleteRecord> {
    /**
     * 查询商品道序完成量台账-主
     *
     * @param stepCompleteRecordSid 商品道序完成量台账-主ID
     * @return 商品道序完成量台账-主
     */
    public ManProcessStepCompleteRecord selectManProcessStepCompleteRecordById(Long stepCompleteRecordSid);

    /**
     * 复制商品道序完成量台账-主
     *
     * @param stepCompleteRecordSid 商品道序完成量台账-主ID
     * @return 商品道序完成量台账-主
     */
    public ManProcessStepCompleteRecord copyManProcessStepCompleteRecordById(Long stepCompleteRecordSid);

    /**
     * 查询商品道序完成量台账-主列表
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 商品道序完成量台账-主集合
     */
    public List<ManProcessStepCompleteRecord> selectManProcessStepCompleteRecordList(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 新增商品道序完成量台账-主
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    public EmsResultEntity insertManProcessStepCompleteRecord(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 校验台账唯一性
     * 新建页面，“基本信息”弹窗中，点击“下一步”时，根据
     * “工厂(工序)、班组、操作部门、完成日期、商品工价类型、计薪完工类型、录入维度、商品编码”
     * 进行重复校验，相关提示信息需要修改为：
     * 工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+完成日期+商品编码(款号)+录入维度”组合已存在道序完成台账单，是否继续新建？
     * 若选择“确定”，则进入“道序完成台账新建”页面；若点击“取消”，则关闭提示弹窗
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    public EmsResultEntity verifyUnique(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 修改商品道序完成量台账-主
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    public int updateManProcessStepCompleteRecord(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 变更商品道序完成量台账-主
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    public int changeManProcessStepCompleteRecord(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 批量删除商品道序完成量台账-主
     *
     * @param stepCompleteRecordSids 需要删除的商品道序完成量台账-主ID
     * @return 结果
     */
    public int deleteManProcessStepCompleteRecordByIds(List<Long> stepCompleteRecordSids);

    /**
     * 更改确认状态
     *
     * @param manProcessStepCompleteRecord
     * @return
     */
    public int check(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     *
     * 道序完成台账明细报表数据
     * @param stepFinishDetail
     * @return
     */
    List<StepFinishDetail> itemProcessStepCompleteExportData(StepFinishDetail stepFinishDetail);

    /**
     * 员工完成量汇总数据
     * @param staffCompleteSummary
     * @return
     */
    List<StaffCompleteSummary> getStaffCompleteSummary(StaffCompleteSummary staffCompleteSummary);

    /**
     * 员工完成量汇总的查看详情
     */
    public StaffCompleteSummaryTable getStaffCompleteSummaryTable(StaffCompleteSummary request);
}
