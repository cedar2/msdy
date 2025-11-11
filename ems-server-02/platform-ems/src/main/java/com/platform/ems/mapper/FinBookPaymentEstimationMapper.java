package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookPaymentEstimation;

/**
 * 财务流水账-应付暂估Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-31
 */
public interface FinBookPaymentEstimationMapper  extends BaseMapper<FinBookPaymentEstimation> {

    FinBookPaymentEstimation selectFinBookPaymentEstimationById(Long bookPaymentEstimationSid);

    List<FinBookPaymentEstimation> selectFinBookPaymentEstimationList(FinBookPaymentEstimation finBookPaymentEstimation);

    /**
     * 添加多个
     * @param list List FinBookPaymentEstimation
     * @return int
     */
    int inserts(@Param("list") List<FinBookPaymentEstimation> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookPaymentEstimation
    * @return int
    */
    int updateAllById(FinBookPaymentEstimation entity);

    List<FinBookPaymentEstimation> getReportForm(FinBookPaymentEstimation entity);
}
