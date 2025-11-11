package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPlantJixinliangEnterMode;

/**
 * 工厂计薪量录入方式Service接口
 * 
 * @author zhuangyz
 * @date 2022-07-14
 */
public interface IConPlantJixinliangEnterModeService extends IService<ConPlantJixinliangEnterMode>{
    /**
     * 查询工厂计薪量录入方式
     * 
     * @param sid 工厂计薪量录入方式ID
     * @return 工厂计薪量录入方式
     */
    public ConPlantJixinliangEnterMode selectConPlantJixinliangEnterModeById(Long sid);

    /**
     * 查询工厂计薪量录入方式列表
     * 
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 工厂计薪量录入方式集合
     */
    public List<ConPlantJixinliangEnterMode> selectConPlantJixinliangEnterModeList(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

    /**
     * 新增工厂计薪量录入方式
     * 
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 结果
     */
    public int insertConPlantJixinliangEnterMode(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

    /**
     * 修改工厂计薪量录入方式
     * 
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 结果
     */
    public int updateConPlantJixinliangEnterMode(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

    /**
     * 变更工厂计薪量录入方式
     *
     * @param conPlantJixinliangEnterMode 工厂计薪量录入方式
     * @return 结果
     */
    public int changeConPlantJixinliangEnterMode(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

    /**
     * 批量删除工厂计薪量录入方式
     * 
     * @param sids 需要删除的工厂计薪量录入方式ID
     * @return 结果
     */
    public int deleteConPlantJixinliangEnterModeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conPlantJixinliangEnterMode
    * @return
    */
    int changeStatus(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

    /**
     * 更改确认状态
     * @param conPlantJixinliangEnterMode
     * @return
     */
    int check(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

}
