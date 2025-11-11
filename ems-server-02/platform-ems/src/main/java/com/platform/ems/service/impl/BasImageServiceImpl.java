package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.BasImageAttachMapper;
import com.platform.ems.mapper.BasImageMapper;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IBasImageService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 图案档案Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-14
 */
@Service
@SuppressWarnings("all")
public class BasImageServiceImpl extends ServiceImpl<BasImageMapper, BasImage> implements IBasImageService {
    @Autowired
    private BasImageMapper basImageMapper;
    @Autowired
    private BasImageAttachMapper basImageAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "图案档案";

    /**
     * 查询图案档案
     *
     * @param imageSid 图案档案ID
     * @return 图案档案
     */
    @Override
    public BasImage selectBasImageById(Long imageSid) {
        BasImage basImage = basImageMapper.selectBasImageById(imageSid);
        basImage.setAttachmentList(new ArrayList<>());
        // 附件
        List<BasImageAttach> attachmentList = basImageAttachMapper.selectBasImageAttachList(
                new BasImageAttach().setImageSid(imageSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            basImage.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(basImage);
        return basImage;
    }

    /**
     * 查询图案档案列表
     *
     * @param basImage 图案档案
     * @return 图案档案
     */
    @Override
    public List<BasImage> selectBasImageList(BasImage basImage) {
        return basImageMapper.selectBasImageList(basImage);
    }

    /**
     * 新增图案档案
     * 需要注意编码重复校验
     *
     * @param basImage 图案档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasImage(BasImage basImage) {
        checkBasbasImageMapperImageUnique(basImage);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(basImage.getHandleStatus())) {
            basImage.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basImageMapper.insert(basImage);
        if (row > 0) {
            BasImage image = basImageMapper.selectById(basImage.getImageSid());
            // 写入附件
            if (CollectionUtil.isNotEmpty(basImage.getAttachmentList())) {
                basImage.getAttachmentList().forEach(item -> {
                    item.setImageSid(basImage.getImageSid());
                    item.setImageCode(image.getImageCode());
                });
                basImageAttachMapper.inserts(basImage.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(basImage.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                           .setTableName(ConstantsTable.TABLE_BAS_IMAGE)
                           .setDocumentSid(basImage.getImageSid());
                sysTodoTask.setTitle("图案档案" + image.getImageCode() + "当前是保存状态，请及时处理！")
                           .setDocumentCode(image.getImageCode().toString())
                           .setNoticeDate(new Date())
                           .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            // 插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasImage(), basImage);
            MongodbDeal.insert(basImage.getImageSid(), basImage.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param basImage 图案档案
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBasImageAttach(BasImage basImage) {
        // 先删后加
        basImageAttachMapper.delete(new QueryWrapper<BasImageAttach>().lambda()
                                                          .eq(BasImageAttach::getImageSid, basImage.getImageSid()));
        if (CollectionUtil.isNotEmpty(basImage.getAttachmentList())) {
            basImage.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getImageAttachSid() == null) {
                    att.setImageSid(basImage.getImageSid());
                    att.setImageCode(basImage.getImageCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            basImageAttachMapper.inserts(basImage.getAttachmentList());
        }
    }

    /**
     * 修改图案档案
     *
     * @param basImage 图案档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasImage(BasImage basImage) {
        checkBasbasImageMapperImageUnique(basImage);
        BasImage original = basImageMapper.selectBasImageById(basImage.getImageSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(basImage.getHandleStatus())) {
            basImage.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, basImage);
        if (CollectionUtil.isNotEmpty(msgList)) {
            basImage.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = basImageMapper.updateAllById(basImage);
        if (row > 0) {
            // 修改附件
            this.updateBasImageAttach(basImage);
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basImage.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                                                        .eq(SysTodoTask::getDocumentSid,
                                                                            basImage.getImageSid())
                                                                        .eq(SysTodoTask::getTaskCategory,
                                                                            ConstantsEms.TODO_TASK_DB)
                                                                        .eq(SysTodoTask::getTableName,
                                                                            ConstantsTable.TABLE_BAS_IMAGE));
            }
            // 插入日志
            MongodbDeal.update(basImage.getImageSid(),
                               original.getHandleStatus(),
                               basImage.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
        }
        return row;
    }

    /**
     * 变更图案档案
     *
     * @param basImage 图案档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasImage(BasImage basImage) {
        checkBasbasImageMapperImageUnique(basImage);
        BasImage response = basImageMapper.selectBasImageById(basImage.getImageSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(basImage, basImage);
        if (CollectionUtil.isNotEmpty(msgList)) {
            basImage.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = basImageMapper.updateAllById(basImage);
        if (row > 0) {
            // 修改附件
            this.updateBasImageAttach(basImage);
            // 插入日志
            MongodbUtil.insertUserLog(basImage.getImageSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除图案档案
     *
     * @param imageSids 需要删除的图案档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasImageByIds(List<Long> imageSids) {
        List<BasImage> list = basImageMapper.selectList(new QueryWrapper<BasImage>()
                                                                .lambda().in(BasImage::getImageSid, imageSids));
        // 删除校验
        list = list.stream().filter(o -> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(
                           o.getHandleStatus()))
                   .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = basImageMapper.deleteBatchIds(imageSids);
        if (row > 0) {
            // 删除附件
            basImageAttachMapper.delete(new QueryWrapper<BasImageAttach>().lambda()
                                                                          .in(BasImageAttach::getImageSid, imageSids));
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                                                    .in(SysTodoTask::getDocumentSid, imageSids)
                                                                    .eq(SysTodoTask::getTaskCategory,
                                                                        ConstantsEms.TODO_TASK_DB)
                                                                    .eq(SysTodoTask::getTableName,
                                                                        ConstantsTable.TABLE_BAS_IMAGE));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new BasImage());
                MongodbUtil.insertUserLog(o.getImageSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param basImage
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasImage basImage) {
        int row = 0;
        Long[] sids = basImage.getImageSidList();
        if (sids != null && sids.length > 0) {
            row = basImageMapper.update(null,
                                        new UpdateWrapper<BasImage>().lambda().set(BasImage::getStatus,
                                                                                   basImage.getStatus())
                                                                     .in(BasImage::getImageSid, sids));
            if (row == 0) {
                throw new BaseException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                // 插入日志
                MongodbDeal.status(id, basImage.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param basImage
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasImage basImage) {
        int row = 0;
        Long[] sids = basImage.getImageSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<BasImage> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(BasImage::getImageSid, sids);
            updateWrapper.set(BasImage::getHandleStatus, basImage.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(basImage.getHandleStatus())) {
                updateWrapper.set(BasImage::getConfirmDate, new Date());
                updateWrapper.set(BasImage::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = basImageMapper.update(null, updateWrapper);
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(basImage.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                                                            .in(SysTodoTask::getDocumentSid, sids)
                                                                            .eq(SysTodoTask::getTaskCategory,
                                                                                ConstantsEms.TODO_TASK_DB)
                                                                            .eq(SysTodoTask::getTableName,
                                                                                ConstantsTable.TABLE_BAS_IMAGE));
                }
                for (Long id : sids) {
                    // 插入日志
                    MongodbDeal.check(id, basImage.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    public void checkBasbasImageMapperImageUnique(BasImage givenCase) {
        List<BasImage> result = basImageMapper.selectList(
                new QueryWrapper<BasImage>().lambda()
                                            .eq(BasImage::getImageName,
                                                givenCase.getImageName())
        );

        if (result.isEmpty()) {
            // 不存在这个人，说明是新建的
            return;
        }

        if (result.size() != 1) {
            // 存在多个这个 叫这个名字的人，有问题
            throw new CheckedException("图案名称已存在");
        }

        BasImage onlyOne = result.get(0);

        if (onlyOne.getImageSid().equals(givenCase.getImageSid())) {
            // 这两个是同一个人，这没问题。
            return;
        }

        // 这两个不是同一个人，但叫了同样名字，这是有问题的。
        throw new CheckedException("图案名称已存在");
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
        int num = 0;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //数据字典Map
            List<DictData> imageTypeDict = sysDictDataService.selectDictData("s_image_type"); // 图案类型
            imageTypeDict = imageTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> imageTypeMaps = imageTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            // 基本
            BasImage image = null;
            List<BasImage> imageList = new ArrayList<>();
            Map<String, String> imageHasMap = new HashMap<>();
            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                /**
                 * 图案名称 必填
                 */
                String imageName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(imageName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("图案名称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (imageName.length() > 300){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("图案名称长度不能超过300个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        // 表格中
                        if (imageHasMap.containsKey(imageName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("表格中，图案名称已存在！");
                            errMsgList.add(errMsg);
                        } else {
                            // 存入map
                            imageHasMap.put(imageName, "1");
                            // 去空格
                            imageName = imageName.replaceAll(" ","");
                            // 系统中
                            List<BasImage> images = basImageMapper.selectList(new QueryWrapper<BasImage>().lambda()
                                    .eq(BasImage::getImageName, imageName));
                            if (CollectionUtil.isNotEmpty(images)) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("系统中，图案名称已存在！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 图案类型(数据字典) 选填
                 */
                String imageTypeName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                String imageType = null;
                if (StrUtil.isNotBlank(imageTypeName)) {
                    imageType = imageTypeMaps.get(imageTypeName); // 通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(imageType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("图案类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 图案说明 选填
                 */
                String description = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isNotBlank(description)) {
                    if (description.length() > 600){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("图案说明长度不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (StrUtil.isNotBlank(remark) && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能超过600个字符，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)){
                    image = new BasImage();
                    image.setImageName(imageName).setImageType(imageType).setImageDescription(description)
                            .setRemark(remark);
                    image.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
                    imageList.add(image);
                }
            }

            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }
            if (CollectionUtil.isNotEmpty(imageList)){
                imageList.forEach(item->{
                    this.insertBasImage(item);
                });
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num-2);
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }

}
