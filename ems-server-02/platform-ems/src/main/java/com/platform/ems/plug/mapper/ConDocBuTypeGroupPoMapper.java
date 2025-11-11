package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocBuTypeGroupPo;

/**
 * 采购订单单据类型与业务类型组合关系Mapper接口
 * 
 * @author chenkw
 * @date 2021-12-24
 */
public interface ConDocBuTypeGroupPoMapper  extends BaseMapper<ConDocBuTypeGroupPo> {


    ConDocBuTypeGroupPo selectConDocBuTypeGroupPoById(Long sid);

    List<ConDocBuTypeGroupPo> selectConDocBuTypeGroupPoList(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

    /**
     * 添加多个
     * @param list List ConDocBuTypeGroupPo
     * @return int
     */
    int inserts(@Param("list") List<ConDocBuTypeGroupPo> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocBuTypeGroupPo
    * @return int
    */
    int updateAllById(ConDocBuTypeGroupPo entity);

    /**
     * 更新多个
     * @param list List ConDocBuTypeGroupPo
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocBuTypeGroupPo> list);


}
