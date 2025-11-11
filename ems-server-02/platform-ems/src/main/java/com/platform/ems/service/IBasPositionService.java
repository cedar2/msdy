package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasPosition;

import java.util.List;

/**
 * 岗位Service接口
 * 
 * @author qhq
 * @date 2021-03-18
 */
public interface IBasPositionService extends IService<BasPosition>{
    /**
     * 查询岗位
     * 
     * @param positionSid 岗位ID
     * @return 岗位
     */
    public BasPosition selectBasPositionById(Long positionSid);

    /**
     * 查询岗位列表
     * 
     * @param basPosition 岗位
     * @return 岗位集合
     */
    public List<BasPosition> selectBasPositionList(BasPosition basPosition);

    /**
     * 新增岗位
     * 
     * @param basPosition 岗位
     * @return 结果
     */
    public int insertBasPosition(BasPosition basPosition);

    /**
     * 修改岗位
     * 
     * @param basPosition 岗位
     * @return 结果
     */
    public int updateBasPosition(BasPosition basPosition);

    /**
     * 批量删除岗位
     * 
     * @param positionSids 需要删除的岗位ID
     * @return 结果
     */
    public int deleteBasPositionByIds(List<Long>  positionSids);

    public int status(BasPosition basPosition);
    
    public int handleStatus(BasPosition basPosition);
    
    /**
     * 获取公司所属的岗位
     */
    public List<BasPosition> getCompanyPosition(Long companySid);

    /**
     * 岗位，下拉值为状态为确认且启用、当前操作用户所属员工的所属公司下的岗位档案的数据
     */
    public List<BasPosition> getSelfPosition(BasPosition basPosition);
}
