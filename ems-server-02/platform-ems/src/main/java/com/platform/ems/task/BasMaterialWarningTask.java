package com.platform.ems.task;

import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasMaterial;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.ems.domain.excel.BasMaterialGydWarningExcel;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.plug.domain.ConBcstUserConfig;
import com.platform.ems.plug.mapper.ConBcstUserConfigMapper;
import com.platform.ems.util.CreateExcelUtil;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysClientMapper;
import com.platform.system.mapper.SystemUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 物料/商品/服务定时作业
 * @author chenkw
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
@Service
public class BasMaterialWarningTask {

    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ConBcstUserConfigMapper conBcstUserConfigMapper;
    @Autowired
    private SystemUserMapper userMapper;
    @Autowired
    private SysClientMapper clientMapper;

    /**
     * 查询确认状态中未上传生产制造单的商品
     *
     * @author chenkw
     */
    @Scheduled(cron = "00 00 14 ? * 1,4")
    @Transactional(rollbackFor = Exception.class)
    public void gydWarning() {
        log.info("=====>开始查询确认状态中未上传工艺单的商品");
        List<SysClient> clientList = clientMapper.selectList(new QueryWrapper<>());
        for (SysClient client : clientList) {
            Date now = new Date();
            BasMaterial basMaterial = new BasMaterial();
            basMaterial.setClientId(client.getClientId());
            basMaterial.setHandleStatus(ConstantsEms.CHECK_STATUS)
                    .setStatus(ConstantsEms.ENABLE_STATUS)
                    .setFileType(ConstantsEms.FILE_TYPE_SPEC)
                    .setIsUploadZhizaodan(ConstantsEms.YES)
                    .setIsHasUploadedZhizaodan(ConstantsEms.NO)
                    .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP);
            List<BasMaterial> basMaterialList = basMaterialMapper.selectNotGydList(basMaterial);
            if (CollectionUtils.isNotEmpty(basMaterialList)) {
                int num = basMaterialList.size();
                String title = "【工艺单未上传提醒】 当前有 " + String.valueOf(num) + " 款商品未上传工艺单附件，请知悉";
                String mailtext =
                        "<font color=\"warning\">具体商品明细请查阅附件，谢谢！</font><br />";
                //通知附件
                List<BasMaterialGydWarningExcel> rows = new ArrayList<>();
                //动态栏
                List<SysBusinessBcst> businessBcstList = new ArrayList<>();
                basMaterialList.forEach(item -> {
                    //通知附件内容行
                    BasMaterialGydWarningExcel excel = new BasMaterialGydWarningExcel();
                    excel.setMaterialCode(item.getMaterialCode());
                    excel.setMaterialName(item.getMaterialName());
                    excel.setCustomerName(item.getCustomerName());
                    excel.setSampleCodeSelf(item.getSampleCodeSelf());
                    excel.setDesignerAccountName(item.getDesignerAccountName());
                    excel.setModelCode(item.getModelCode());
                    excel.setCreatorAccountName(item.getCreatorAccountName());
                    rows.add(excel);
                });
                //获取通知配置表中工艺单需要通知的用户
                List<ConBcstUserConfig> conBcstUserConfigList = conBcstUserConfigMapper.selectConBcstUserConfigList(
                        new ConBcstUserConfig()
                                .setClientId(client.getClientId())
                                .setBcstType(ConstantsEms.BCST_TYPE_SPECWSC)
                                .setDataobjectCategoryCode(ConstantsEms.BCST_OBJECT_MATERIAL));
                if (CollectionUtils.isNotEmpty(conBcstUserConfigList)) {
                    String mailList = "";
                    List<Long> userIdList = new ArrayList<>();
                    //拼接邮箱+用户id
                    for (ConBcstUserConfig email : conBcstUserConfigList) {
                        mailList = mailList + email.getEmail() + ";";
                        userIdList.add(email.getUserId());
                    }
                    if (CollectionUtils.isNotEmpty(userIdList)) {
                        userIdList.forEach(id -> {
                            SysBusinessBcst businessBcst = new SysBusinessBcst();
                            businessBcst.setUserId(id).setDocumentSid(0L).setDocumentCode("").setClientId(client.getClientId())
                                    .setTitle("当前有 " + num + " 款商品未上传工艺单附件").setNoticeDate(new Date());
                            businessBcstList.add(businessBcst);
                        });
                        sysBusinessBcstMapper.inserts(businessBcstList);
                    }
                    if (mailList != "") {
                        CreateExcelUtil<BasMaterialGydWarningExcel> createExcelUtil = new CreateExcelUtil<>();
                        File excelfile = createExcelUtil.createExcel(rows, "未上传工艺单附件的商品清单");
                        try {
                            MailUtil.send(mailList, null,
                                    mailList, title, mailtext, true, excelfile);
                            excelfile.deleteOnExit();
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info("邮件发送失败");
                        }
                    }
                }
            }
        }
    }
}
