package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConBuTypeOutsourcePo;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeOutsourceDn;

/**
 * 业务类型_外发加工交货单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeOutsourceDnMapper  extends BaseMapper<ConBuTypeOutsourceDn> {


    ConBuTypeOutsourceDn selectConBuTypeOutsourceDnById(Long sid);

    List<ConBuTypeOutsourceDn> selectConBuTypeOutsourceDnList(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**
     * 添加多个
     * @param list List ConBuTypeOutsourceDn
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeOutsourceDn> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeOutsourceDn
    * @return int
    */
    int updateAllById(ConBuTypeOutsourceDn entity);

    /**
     * 更新多个
     * @param list List ConBuTypeOutsourceDn
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeOutsourceDn> list);

    /** 获取下拉列表 */
    List<ConBuTypeOutsourceDn> getConBuTypeOutsourceDnList();
}
