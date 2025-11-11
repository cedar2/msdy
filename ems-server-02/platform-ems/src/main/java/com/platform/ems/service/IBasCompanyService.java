package com.platform.ems.service;

import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.BasButtonRequest;

import java.util.List;

/**
 * 公司档案Service接口
 * 
 * @author hjj
 * @date 2021-01-22
 */
public interface IBasCompanyService {
    /**
     * 查询公司档案
     * 
     * @param companySid 公司档案ID
     * @return 公司档案
     */
    public BasCompany selectBasCompanyById(Long companySid);

    /**
     * 查询公司档案列表
     * 
     * @param request 公司档案
     * @return 公司档案集合
     */
    public List<BasCompany> selectBasCompanyList(BasCompany request);

    /**
     * 新增公司档案
     * 
     * @param request 公司档案
     * @return 结果
     */
    public int insertBasCompany(BasCompany request);

    /**
     * 修改公司档案
     * 
     * @param request 公司档案
     * @return 结果
     */
    public int editBasCompany(BasCompany request);
    public int changeBasCompany(BasCompany request);

    int check(BasCompany basCompany);
    /**
     * 批量删除公司档案
     * 
     * @param companySids 需要删除的公司档案ID
     * @return 结果
     */
    public int deleteBasCompanyByIds(List<Long> companySids);


    /**
     * 批量启用/停用
     */
    int editStatus(BasButtonRequest request);


    /**
     * 批量导出
     */
    List<BasCompany> export(BasCompany request);

    /**
     * 公司档案下拉框列表
     */
    List<BasCompany> getCompanyList(BasCompany company);

    List<BasCompanyBrand> getCompanyBrandList(String companySid);

    List<BasCompanyBrand> getBrandList(BasCompanyBrand companyBrand);

    List<BasCompanyBrandMark> getCompanyBrandMarkList(Long brandSid);

    int changeStatus(BasCompany basCompany);


}
