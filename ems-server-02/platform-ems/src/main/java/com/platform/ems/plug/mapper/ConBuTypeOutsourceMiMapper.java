package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeOutsourceMi;

/**
 * 业务类型_外发加工发料单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeOutsourceMiMapper  extends BaseMapper<ConBuTypeOutsourceMi> {


    ConBuTypeOutsourceMi selectConBuTypeOutsourceMiById(Long sid);

    List<ConBuTypeOutsourceMi> selectConBuTypeOutsourceMiList(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

    /**
     * 添加多个
     * @param list List ConBuTypeOutsourceMi
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeOutsourceMi> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeOutsourceMi
    * @return int
    */
    int updateAllById(ConBuTypeOutsourceMi entity);

    /**
     * 更新多个
     * @param list List ConBuTypeOutsourceMi
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeOutsourceMi> list);


}
