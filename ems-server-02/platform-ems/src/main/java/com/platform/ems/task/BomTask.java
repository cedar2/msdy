package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.R;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.TecProductZipper;
import com.platform.ems.domain.dto.response.TecProductSizeZipperLengthResponse;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecBomAttachmentMapper;
import com.platform.ems.mapper.TecBomHeadMapper;
import com.platform.ems.mapper.TecBomItemMapper;
import com.platform.ems.service.ITecProductZipperService;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bom尺码拉链待办提醒
 * @author yangqz
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class BomTask {

    @Autowired
    ITecProductZipperService  tecProductZipperService;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    private static final String TABLE = "s_tec_bom_head_task";
   @Scheduled(cron = "00 00 01 * * *")
    public void start() {
        List<TecBomHead> allBom = tecBomHeadMapper.getAllBom();
        allBom.forEach(bom->{
            List<TecBomItem> tecBomItems = tecBomItemMapper.selectBomItemByBomSid(bom.getBomSid());
            if(CollectionUtils.isNotEmpty(tecBomItems)){
                tecBomItems.forEach(item->{
                    String zipperFlag = item.getZipperFlag();
                    if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)||ConstantsEms.ZIPPER_ZT.equals(zipperFlag)){
                        List<Long>  SidList = new ArrayList<>();
                        SidList.add(item.getBomMaterialSid());
                        TecProductZipper tecProductZipper = tecProductZipperService.selectTecProductZipperById(bom.getMaterialCode(), SidList);
                        Boolean isCreate=false;
                        if(tecProductZipper!=null){
                            List<TecProductZipper> listMaterial = tecProductZipper.getListMaterial();
                            List<TecProductSizeZipperLengthResponse> zipperList = listMaterial.get(0).getSizeZipperList();
                            List<TecProductSizeZipperLengthResponse> exitColor = zipperList.stream().filter(li -> li.getMaterialSkuSid() == null).collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(exitColor)){
                                isCreate=true;
                            }
                        }else{
                            isCreate=true;
                        }
                        if(isCreate){
                            R<LoginUser> userInfo = remoteUserService.getUserInfo(bom.getCreatorAccount());
                            Long userid = userInfo.getData().getSysUser().getUserId();
                            SysTodoTask sysTodoTask = new SysTodoTask();
                            int delete = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                    .eq(SysTodoTask::getTableName, TABLE)
                                    .eq(SysTodoTask::getDocumentItemSid,item.getBomMaterialSid())
                                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                    .eq(SysTodoTask::getDocumentSid, item.getBomSid())
                            );
                            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(TABLE)
                                    .setDocumentSid(item.getBomSid())
                                    .setDocumentItemSid(item.getBomMaterialSid());
                            sysTodoTask.setTitle("BOM款号" + bom.getMaterialCode() + "，"+item.getMaterialCode()+item.getMaterialName()+"，存在尺码拉链长度未维护，请及时处理！")
                                    .setDocumentCode(item.getMaterialCode())
                                    .setNoticeDate(new Date())
                                    .setUserId(userid);
                            sysTodoTaskMapper.insert(sysTodoTask);
                        }
                    }
                });

            }
        });
    }


}
