package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasProductSeason;

/**
 * 产品季档案Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-22
 */
public interface BasProductSeasonMapper  extends BaseMapper<BasProductSeason> {


    BasProductSeason selectBasProductSeasonById(Long clientId);

    List<BasProductSeason> selectBasProductSeasonList(BasProductSeason basProductSeason);

    /**
     * 下拉框
     * @param basProductSeason 过滤条件
     * @return 下拉列表
     */
    List<BasProductSeason> getList(BasProductSeason basProductSeason);

    /**
     * 添加多个
     * @param list List BasProductSeason
     * @return int
     */
    int inserts(@Param("list") List<BasProductSeason> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasProductSeason
    * @return int
    */
    int updateAllById(BasProductSeason entity);

    /**
     * 更新多个
     * @param list List BasProductSeason
     * @return int
     */
    int updatesAllById(@Param("list") List<BasProductSeason> list);


}
