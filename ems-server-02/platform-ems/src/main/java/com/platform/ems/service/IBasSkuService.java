package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasSku;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKU档案Service接口
 *
 * @author linhongwei
 * @date 2021-03-22
 */
public interface IBasSkuService extends IService<BasSku> {
    /**
     * 查询SKU档案
     *
     * @param skuSid SKU档案ID
     * @return SKU档案
     */
    public BasSku selectBasSkuById(Long skuSid);

    /**
     * 查询SKU档案列表
     *
     * @param basSku SKU档案
     * @return SKU档案集合
     */
    public List<BasSku> selectBasSkuList(BasSku basSku);

    /**
     * 新增SKU档案
     *
     * @param basSku SKU档案
     * @return 结果
     */
    public int insertBasSku(BasSku basSku);

    /**
     * 修改SKU档案
     *
     * @param basSku SKU档案
     * @return 结果
     */
    public int updateBasSku(BasSku basSku);

    /**
     * 批量删除SKU档案
     *
     * @param skuSids 需要删除的SKU档案ID
     * @return 结果
     */
    public int deleteBasSkuByIds(List<String> skuSids);

    List<BasSku> getList(String skuType);

    int changeStatus(BasSku basSku);

    int check(BasSku basSku);

    Object importData(MultipartFile file);

}
