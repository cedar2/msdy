package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ConCheckMethod;

/**
 * 检测方法Mapper接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface ConCheckMethodMapper  extends BaseMapper<ConCheckMethod> {


    ConCheckMethod selectConCheckMethodById (Long sid);

    List<ConCheckMethod> selectConCheckMethodList (ConCheckMethod conCheckMethod);

    /**
     * 添加多个
     * @param list List ConCheckMethod
     * @return int
     */
    int inserts (@Param("list") List<ConCheckMethod> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConCheckMethod
    * @return int
    */
    int updateAllById (ConCheckMethod entity);

    /**
     * 更新多个
     * @param list List ConCheckMethod
     * @return int
     */
    int updatesAllById (@Param("list") List<ConCheckMethod> list);


}
