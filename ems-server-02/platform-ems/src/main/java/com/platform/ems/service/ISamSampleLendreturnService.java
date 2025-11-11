package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SamSampleLendreturn;
import com.platform.ems.domain.SamSampleLendreturnItem;
import com.platform.ems.domain.dto.request.SamSampleLendreturnItemRequest;
import com.platform.ems.domain.dto.response.SamSampleLendreturnReportResponse;
import org.springframework.transaction.annotation.Transactional;

/**
 * 样品借还单-主Service接口
 * 
 * @author linhongwei
 * @date 2021-12-20
 */
public interface ISamSampleLendreturnService extends IService<SamSampleLendreturn>{
    /**
     * 查询样品借还单-主
     * 
     * @param lendreturnSid 样品借还单-主ID
     * @return 样品借还单-主
     */
    public SamSampleLendreturn selectSamSampleLendreturnById(Long lendreturnSid);
    public int processCheck(List<Long> sidList);
    //获取添加明细信息
    public List<SamSampleLendreturnItem> getSamSampleLendreturnItem(SamSampleLendreturnItem samSampleLendreturn);
    //获取添加明细价格
    public List<SamSampleLendreturnItem> getPrice(SamSampleLendreturnItemRequest data);

    //借还单明细报表
    public List<SamSampleLendreturnReportResponse> getReport(SamSampleLendreturn samSampleLendreturn);
    /**
     * 查询样品借还单-主列表
     * 
     * @param samSampleLendreturn 样品借还单-主
     * @return 样品借还单-主集合
     */
    public List<SamSampleLendreturn> selectSamSampleLendreturnList(SamSampleLendreturn samSampleLendreturn);

    /**
     * 新增样品借还单-主
     * 
     * @param samSampleLendreturn 样品借还单-主
     * @return 结果
     */
    public int insertSamSampleLendreturn(SamSampleLendreturn samSampleLendreturn);

    /**
     * 修改样品借还单-主
     * 
     * @param samSampleLendreturn 样品借还单-主
     * @return 结果
     */
    public int updateSamSampleLendreturn(SamSampleLendreturn samSampleLendreturn);

    /**
     * 变更样品借还单-主
     *
     * @param samSampleLendreturn 样品借还单-主
     * @return 结果
     */
    public int changeSamSampleLendreturn(SamSampleLendreturn samSampleLendreturn);

    /**
     * 批量删除样品借还单-主
     * 
     * @param lendreturnSids 需要删除的样品借还单-主ID
     * @return 结果
     */
    public int deleteSamSampleLendreturnByIds(List<Long> lendreturnSids);



    public int deleteDOTo(List<Long> lendreturnSids);

    /**
    * 启用/停用
    * @param samSampleLendreturn
    * @return
    */
    int changeStatus(SamSampleLendreturn samSampleLendreturn);

    /**
     * 更改确认状态
     * @param samSampleLendreturn
     * @return
     */
    int check(SamSampleLendreturn samSampleLendreturn);

}
