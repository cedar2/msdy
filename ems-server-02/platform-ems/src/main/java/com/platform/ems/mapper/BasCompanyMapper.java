package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.model.DictData;
import com.platform.ems.domain.BasCompany;
import com.platform.ems.domain.dto.request.BasButtonRequest;

/**
 * 公司档案Mapper接口
 * 
 * @author hjj
 * @date 2021-01-22
 */
public interface BasCompanyMapper extends BaseMapper<BasCompany>{

    List<DictData>  getDictDataList();

    List<DictData>  selectDictData(String dictType);

    List<DictData>  getDictDataPrtvateList(String clientId);
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
     * @param basCompany 公司档案
     * @return 结果
     */
    public int insertBasCompany(BasCompany basCompany);

    /**
     * 修改公司档案
     * 
     * @param companyRequest 公司档案
     * @return 结果
     */
    public int updateBasCompany(BasCompany companyRequest);

    /**
     * 批量删除公司档案
     *
     * @param companySids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBasCompanyByIds(List<Long> companySids);

    /**
     * 删除公司档案
     *
     * @param companySid 公司档案ID
     * @return 结果
     */
    public int deleteBasCompanyById(Long companySid);

    /**
     * 判断公司代码是否存在
     *
     * @param companyCode 公司代码
     * @return 结果
     */
    int checkCodeUnique(String companyCode);

    /**
     * 判断公司名称是否存在
     *
     * @param companyName 公司名称
     * @return 结果
     */
    int checkNameUnique(String companyName);

    String getHandleStatus(Long companySid);

    /**
     * 批量启用/停用
     */
    int editStatus(BasButtonRequest request);


    /**
     * 批量导出
     */
    List<BasCompany> selectBasCompanyListByIds(@Param("companySids") String[] companySids);


    int confirmCompanyById(BasCompany basCompany);

    /**
     * 公司档案下拉框列表
     */
    List<BasCompany> getCompanyList(BasCompany basCompany);
}
