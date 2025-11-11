package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProductJijianSettleInfor;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品计件结算信息Service接口
 *
 * @author chenkw
 * @date 2022-07-14
 */
public interface IPayProductJijianSettleInforService extends IService<PayProductJijianSettleInfor> {
    /**
     * 查询商品计件结算信息
     *
     * @param jijianSettleInforSid 商品计件结算信息ID
     * @return 商品计件结算信息
     */
    public PayProductJijianSettleInfor selectPayProductJijianSettleInforById(Long jijianSettleInforSid);

    /**
     * 查询商品计件结算信息列表
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 商品计件结算信息集合
     */
    public List<PayProductJijianSettleInfor> selectPayProductJijianSettleInforList(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 查询商品计件结算信息列表 -- 精确查询
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 商品计件结算信息集合
     */
    public List<PayProductJijianSettleInfor> selectPayProductJijianSettleInforListPrecision(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 新增商品计件结算信息
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    public int insertPayProductJijianSettleInfor(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 修改商品计件结算信息
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    public int updatePayProductJijianSettleInfor(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 变更商品计件结算信息
     *
     * @param payProductJijianSettleInfor 商品计件结算信息
     * @return 结果
     */
    public int changePayProductJijianSettleInfor(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 批量删除商品计件结算信息
     *
     * @param jijianSettleInforSids 需要删除的商品计件结算信息ID
     * @return 结果
     */
    public int deletePayProductJijianSettleInforByIds(List<Long> jijianSettleInforSids);

    /**
     * 更改确认状态
     *
     * @param payProductJijianSettleInfor
     * @return
     */
    int check(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 导入
     *
     * @param file
     * @return
     */
    EmsResultEntity importData(MultipartFile file);

    /**
     * 根据计薪申报量单查询
     *
     * @param payProcessStepComplete
     * @return
     */
    List<PayProductJijianSettleInfor> listBy(PayProcessStepComplete payProcessStepComplete);

    /**
     点击此按钮，从“商品计件结算数”数据库表中获取满足查询条件的数据，并按以下逻辑对“结算数”进行小计：
     》结算数累计(已确认)
     将查询出来的数据，按“工厂、商品编码(款号)、排产批次号、操作部门、商品工价类型、计薪完工类型”且“处理状态是已确认”的结算数进行小计

     》结算数累计(含保存)
     将查询出来的数据，按“工厂、商品编码(款号)、排产批次号、操作部门、商品工价类型、计薪完工类型”的结算数进行小计，即“处理状态”是“保存”和“已确认”都进行小计。
     * @param payProductJijianSettleInfor
     * @return
     */
    List<PayProductJijianSettleInfor> collect(PayProductJijianSettleInfor payProductJijianSettleInfor);


    /**
     * 根据计薪申报量单查询汇总
     *
     * @param payProcessStepComplete
     * @return
     */
    List<PayProductJijianSettleInfor> collectBy(PayProcessStepComplete payProcessStepComplete);

}
