package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeOutsourceMi;

/**
 * 单据类型_外发加工发料单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeOutsourceMiMapper  extends BaseMapper<ConDocTypeOutsourceMi> {


    ConDocTypeOutsourceMi selectConDocTypeOutsourceMiById(Long sid);

    List<ConDocTypeOutsourceMi> selectConDocTypeOutsourceMiList(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

    /**
     * 添加多个
     * @param list List ConDocTypeOutsourceMi
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeOutsourceMi> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeOutsourceMi
    * @return int
    */
    int updateAllById(ConDocTypeOutsourceMi entity);

    /**
     * 更新多个
     * @param list List ConDocTypeOutsourceMi
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeOutsourceMi> list);


}
