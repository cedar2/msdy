package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrLaborContractAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**劳动合同-附件Mapper接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface HrLaborContractAttachMapper extends BaseMapper<HrLaborContractAttach> {

    HrLaborContractAttach selectHrLaborContractAttachById(Long laborContractAttachSid);

    List<HrLaborContractAttach> selectHrLaborContractAttachList(HrLaborContractAttach hrLaborContractAttach);

    /**
     * 添加多个
     *
     * @param list List HrLaborContractAttach
     * @return int
     */
    int inserts(@Param("list") List<HrLaborContractAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrLaborContractAttach
     * @return int
     */
    int updateAllById(HrLaborContractAttach entity);

    /**
     * 更新多个
     *
     * @param list List HrLaborContractAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<HrLaborContractAttach> list);

}
