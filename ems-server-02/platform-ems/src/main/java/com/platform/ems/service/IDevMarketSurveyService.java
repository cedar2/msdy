package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevMarketSurvey;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 市场调研Service接口
 *
 * @author chenkw
 * @date 2022-12-08
 */
public interface IDevMarketSurveyService extends IService<DevMarketSurvey> {
    /**
     * 查询市场调研
     * 
     * @param marketSurveySid 市场调研ID
     * @return 市场调研
     */
    public DevMarketSurvey selectDevMarketSurveyById(Long marketSurveySid);

    /**
     * 复制市场调研
     *
     * @param marketSurveySid 市场调研ID
     * @return 市场调研
     */
    public DevMarketSurvey copyDevMarketSurveyById(Long marketSurveySid);

    /**
     * 查询市场调研列表
     *
     * @param devMarketSurvey 市场调研
     * @return 市场调研集合
     */
    public List<DevMarketSurvey> selectDevMarketSurveyListOrderByDesc(DevMarketSurvey devMarketSurvey);

    /**
     * 新增市场调研
     * 
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    public int insertDevMarketSurvey(DevMarketSurvey devMarketSurvey);

    /**
     * 修改市场调研
     * 
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    public int updateDevMarketSurvey(DevMarketSurvey devMarketSurvey);

    /**
     * 变更市场调研
     *
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    public int changeDevMarketSurvey(DevMarketSurvey devMarketSurvey);

    /**
     * 批量删除市场调研
     * 
     * @param marketSurveySids 需要删除的市场调研ID
     * @return 结果
     */
    public int deleteDevMarketSurveyByIds(List<Long>  marketSurveySids);

    /**
    * 启用/停用
    * @param devMarketSurvey
    * @return
    */
    int changeStatus(DevMarketSurvey devMarketSurvey);

    /**
     * 更改确认状态
     * @param devMarketSurvey
     * @return
     */
    int check(DevMarketSurvey devMarketSurvey);

    /**
     * 导入
     * @param file
     * @return
     */
    EmsResultEntity importData(MultipartFile file);
}
