package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PaySalaryBillAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工资单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface PaySalaryBillAttachMapper extends BaseMapper<PaySalaryBillAttach> {


    PaySalaryBillAttach selectPaySalaryBillAttachById(Long attachmentSid);

    List<PaySalaryBillAttach> selectPaySalaryBillAttachList(PaySalaryBillAttach paySalaryBillAttach);

    /**
     * 添加多个
     *
     * @param list List PaySalaryBillAttach
     * @return int
     */
    int inserts(@Param("list") List<PaySalaryBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PaySalaryBillAttach
     * @return int
     */
    int updateAllById(PaySalaryBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List PaySalaryBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PaySalaryBillAttach> list);


}
