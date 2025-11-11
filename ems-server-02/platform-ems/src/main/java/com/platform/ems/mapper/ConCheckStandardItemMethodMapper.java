package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ConCheckStandardItemMethod;

/**
 * 检测标准/项目/方法关联Mapper接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface ConCheckStandardItemMethodMapper  extends BaseMapper<ConCheckStandardItemMethod> {


    ConCheckStandardItemMethod selectConCheckStandardItemMethodById (Long checkStandardItemMethodSid);

    List<ConCheckStandardItemMethod> selectConCheckStandardItemMethodList (ConCheckStandardItemMethod conCheckStandardItemMethod);

    /**
     * 添加多个
     * @param list List ConCheckStandardItemMethod
     * @return int
     */
    int inserts (@Param("list") List<ConCheckStandardItemMethod> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConCheckStandardItemMethod
    * @return int
    */
    int updateAllById (ConCheckStandardItemMethod entity);

    /**
     * 更新多个
     * @param list List ConCheckStandardItemMethod
     * @return int
     */
    int updatesAllById (@Param("list") List<ConCheckStandardItemMethod> list);


}
