package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinBookAccountPayable;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应付Service接口
 *
 * @author linhongwei
 * @date 2021-06-03
 */
public interface IFinBookAccountPayableService extends IService<FinBookAccountPayable>{

    /**
     * 报表查询
     * @param request
     * @return
     */
    List<FinBookAccountPayable> getReportForm(FinBookAccountPayable request);

    /**
     * 导入
     * @param file
     * @return
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 记账
     * @param list
     * @return
     */
    int addForm(List<FinBookAccountPayable> list);
}
