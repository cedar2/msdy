package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocBuTypeGroupSo;

/**
 * 销售订单单据类型与业务类型组合关系Mapper接口
 * 
 * @author chenkw
 * @date 2021-12-24
 */
public interface ConDocBuTypeGroupSoMapper  extends BaseMapper<ConDocBuTypeGroupSo> {


    ConDocBuTypeGroupSo selectConDocBuTypeGroupSoById(Long sid);

    List<ConDocBuTypeGroupSo> selectConDocBuTypeGroupSoList(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

    /**
     * 添加多个
     * @param list List ConDocBuTypeGroupSo
     * @return int
     */
    int inserts(@Param("list") List<ConDocBuTypeGroupSo> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocBuTypeGroupSo
    * @return int
    */
    int updateAllById(ConDocBuTypeGroupSo entity);

    /**
     * 更新多个
     * @param list List ConDocBuTypeGroupSo
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocBuTypeGroupSo> list);


}
