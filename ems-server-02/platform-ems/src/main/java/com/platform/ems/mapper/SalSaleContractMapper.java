package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.form.SalSaleContractFormResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSaleContract;

/**
 * 销售合同信息Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-18
 */
public interface SalSaleContractMapper  extends BaseMapper<SalSaleContract> {

    SalSaleContract selectSalSaleContractById(Long saleContractSid);

    List<SalSaleContract> selectSalSaleContractList(SalSaleContract salSaleContract);

    /**
     * 添加多个
     * @param list List SalSaleContract
     * @return int
     */
    int inserts(@Param("list") List<SalSaleContract> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalSaleContract
    * @return int
    */
    int updateAllById(SalSaleContract entity);

    /**
     * 更新多个
     * @param list List SalSaleContract
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSaleContract> list);

    int countByDomain(SalSaleContract params);

    SalSaleContract getName(SalSaleContract salSaleContract);

    /**
     * 合同下拉框接口
     */
    List<SalSaleContract> getSalSaleContractList();

    /**
     * 合同下拉框接口(带参数)
     */
    List<SalSaleContract> getSaleContractList(SalSaleContract salSaleContract);

    /**
     * 原合同号下拉框接口
     */
    List<SalSaleContract> getOriginalContractList(SalSaleContract salSaleContractr);

    /**
     * 查询即将到期的合同
     * offset : 设置到期前的天数才算即将到期
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSaleContract> getToexpireBusiness(@Param("offset") int offset);

    /**
     * 查询已逾期的合同
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSaleContract> getOverdueBusiness();

    /**
     * 查询出所有处理状态为“已确认”且签收状态(纸质协议)为“未签收”的销售框架协议，
     * @param salSaleContract
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSaleContract> selectSaleContractKuangjia(SalSaleContract salSaleContract);

    /**
     * 查询销售合同统计报表
     * @param salSaleContract
     * @return
     */
    List<SalSaleContractFormResponse> getCountForm(SalSaleContract salSaleContract);

    /**
     * 查询销售合同统计报表
     * @param salSaleContract
     * @return
     */
    List<SalSaleContractFormResponse> getCountFormItem(SalSaleContract salSaleContract);
}
