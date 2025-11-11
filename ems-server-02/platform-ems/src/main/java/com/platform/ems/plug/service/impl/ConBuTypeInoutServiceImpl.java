package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBuTypeInout;
import com.platform.ems.plug.domain.ConInventoryDocumentCategory;
import com.platform.ems.plug.mapper.ConBuTypeInoutMapper;
import com.platform.ems.plug.mapper.ConInventoryDocumentCategoryMapper;
import com.platform.ems.plug.service.IConBuTypeInoutService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 业务类型-出入库Service业务层处理
 *
 * @author wangp
 * @date 2022-10-09
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeInoutServiceImpl extends ServiceImpl<ConBuTypeInoutMapper, ConBuTypeInout> implements IConBuTypeInoutService {
    @Autowired
    private ConBuTypeInoutMapper conBuTypeInoutMapper;

    @Autowired
    private ConInventoryDocumentCategoryMapper conInventoryDocumentCategoryMapper;

    private static final String TITLE = "业务类型-出入库";

    /**
     * 查询业务类型-出入库
     *
     * @param sid 业务类型-出入库ID
     * @return 业务类型-出入库
     */
    @Override
    public ConBuTypeInout selectConBuTypeInoutById(Long sid) {
        ConBuTypeInout conBuTypeInout = conBuTypeInoutMapper.selectConBuTypeInoutById(sid);
        conBuTypeInout.setDocumentCategoryList(conBuTypeInout.getDocumentCategory().split(";"));
        MongodbUtil.find(conBuTypeInout);
        return conBuTypeInout;
    }

    /**
     * 查询业务类型-出入库列表
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 业务类型-出入库
     */
    @Override
    public List<ConBuTypeInout> selectConBuTypeInoutList(ConBuTypeInout conBuTypeInout) {

        // Controller层的startPage方法会自动为查询的Sql语句添加 LIMIT关键字
        // 故此处使用 conInventoryDocumentCategoryMapper.selecList方法来查询所有 库存凭证类别 对象

        List<ConBuTypeInout> conBuTypeInouts = conBuTypeInoutMapper.selectConBuTypeInoutList(conBuTypeInout);

        //查询数据库中所有 库存凭证类别 对象
        List<ConInventoryDocumentCategory> list = conInventoryDocumentCategoryMapper.selectList(new QueryWrapper<>());


        //将查询出来的 库存凭证类别 列表 转化为以 code 为 key 对象为value的Map
        Map<String, ConInventoryDocumentCategory> map = list.stream().collect(Collectors.toMap(ConInventoryDocumentCategory::getCode,
                Function.identity()));


        for (int i = 0; i < conBuTypeInouts.size(); i++) {
            ConBuTypeInout conInOut = conBuTypeInouts.get(i);
            String[] docs = conInOut.getDocumentCategory().split(";");
            conInOut.setDocumentCategoryList(docs);
            StringBuilder sBuilder = new StringBuilder();
            for (int j = 0; j < docs.length; j++) {
                sBuilder.append(map.get(docs[j]).getName()).append("；");
            }
            sBuilder.deleteCharAt(sBuilder.lastIndexOf("；"));
            conInOut.setDocumentCategoryName(sBuilder.toString());
            conBuTypeInouts.set(i, conInOut);
        }
        return conBuTypeInouts;
    }

    /**
     * 查询业务类型-出入库列表  下拉框接口
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 业务类型-出入库集合
     */
    @Override
    public List<ConBuTypeInout> getConBuTypeInoutList(ConBuTypeInout conBuTypeInout) {
        // 模糊查询定位 库存凭证类别
        List<ConBuTypeInout> list = conBuTypeInoutMapper.getConBuTypeInoutList(conBuTypeInout);
        // 因为库存凭证类别是模糊查询的，所以 比如 "CK" 可能查询到 "SCKL","CKS"
        if (conBuTypeInout.getDocumentCategory() != null) {
            List<ConBuTypeInout> response = new ArrayList<>();
            // 每个所属业务类型的库存凭证类别字符串
            String documentCategory = null;
            boolean has = false;
            for (int i = 0; i < list.size(); i++) {
                documentCategory = list.get(i).getDocumentCategory();
                if (documentCategory != null) {
                    // 将库存类字符串按 ; 分割成数组
                    String[] documentCategorys = documentCategory.split(";");
                    if (Arrays.asList(documentCategorys).contains(conBuTypeInout.getDocumentCategory())) {
                        response.add(list.get(i));
                    }
                }
            }
            return response;
        }
        return list;
    }

    /**
     * 新增业务类型-出入库
     * 需要注意编码重复校验
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeInout(ConBuTypeInout conBuTypeInout) {

//        for (ConBuTypeInout inout : conBuTypeInoutMapper.selectConBuTypeInoutList(conBuTypeInout)) {
//            //点击确认时，校验该租户ID下所属业务类型编码不能重复
//            if (inout.getCode().equals(conBuTypeInout.getCode())) {
//                throw new CustomException("所属业务类型编码已存在，请核实！");
//            }
//
//            //点击确认时，校验该租户ID下所属业务类型名称不能重复
//            if (inout.getName().equals(conBuTypeInout.getName())) {
//                throw new CustomException("所属业务类型名称已存在，请核实！");
//            }
//        }


        List<ConBuTypeInout> checkCode = conBuTypeInoutMapper.selectList(new QueryWrapper<ConBuTypeInout>().lambda()
                .eq(ConBuTypeInout::getCode, conBuTypeInout.getCode()));
        if (CollectionUtil.isNotEmpty(checkCode)) {
            throw new CustomException("所属业务类型编码已存在，请核实！");
        }

        List<ConBuTypeInout> checkName = conBuTypeInoutMapper.selectList(new QueryWrapper<ConBuTypeInout>().lambda()
                .eq(ConBuTypeInout::getName, conBuTypeInout.getName()));
        if (CollectionUtil.isNotEmpty(checkName)) {
            throw new CustomException("所属业务类型名称已存在，请核实！");
        }

        if ("".equals(conBuTypeInout.getDocumentCategory())) {
            throw new CustomException("库存凭证类别不能为空");
        }

        // 新增多个库存类别
        // 以 ; 间隔
        StringBuilder sBuilder = new StringBuilder();
        for (String docCat : conBuTypeInout.getDocumentCategoryList()) {
            sBuilder.append(docCat).append(";");
        }
        //将最后加的一个 ; 去除
        sBuilder.deleteCharAt(sBuilder.lastIndexOf(";"));

        conBuTypeInout.setDocumentCategory(sBuilder.toString());

        int row = conBuTypeInoutMapper.insert(conBuTypeInout);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConBuTypeInout(), conBuTypeInout);
            MongodbDeal.insert(conBuTypeInout.getSid(), conBuTypeInout.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改业务类型-出入库
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeInout(ConBuTypeInout conBuTypeInout) {
        ConBuTypeInout original = conBuTypeInoutMapper.selectConBuTypeInoutById(conBuTypeInout.getSid());
        int row = conBuTypeInoutMapper.updateById(conBuTypeInout);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, conBuTypeInout);
            MongodbDeal.update(conBuTypeInout.getSid(), original.getHandleStatus(), conBuTypeInout.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更业务类型-出入库
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeInout(ConBuTypeInout conBuTypeInout) {

        List<ConBuTypeInout> checkCode = conBuTypeInoutMapper.selectList(new QueryWrapper<ConBuTypeInout>().lambda()
                .ne(ConBuTypeInout::getSid, conBuTypeInout.getSid()).eq(ConBuTypeInout::getCode, conBuTypeInout.getCode()));
        if (CollectionUtil.isNotEmpty(checkCode)) {
            throw new CustomException("所属业务类型编码已存在，请核实！");
        }

        List<ConBuTypeInout> checkName = conBuTypeInoutMapper.selectList(new QueryWrapper<ConBuTypeInout>().lambda()
                .ne(ConBuTypeInout::getSid, conBuTypeInout.getSid()).eq(ConBuTypeInout::getName, conBuTypeInout.getName()));
        if (CollectionUtil.isNotEmpty(checkName)) {
            throw new CustomException("所属业务类型名称已存在，请核实！");
        }

        if (conBuTypeInout.getDocumentCategory() == null) {
            throw new CustomException("库存凭证类别不能为空");
        }

        // 以 ; 间隔
        StringBuilder sBuilder = new StringBuilder();
        for (String docCat : conBuTypeInout.getDocumentCategoryList()) {
            sBuilder.append(docCat).append(";");
        }
        //将最后加的一个 ; 去除
        sBuilder.deleteCharAt(sBuilder.lastIndexOf(";"));

        conBuTypeInout.setDocumentCategory(sBuilder.toString());

        //修改更新人、更新时间， 和确认人、确认时间
        conBuTypeInout.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());

        ConBuTypeInout response = conBuTypeInoutMapper.selectConBuTypeInoutById(conBuTypeInout.getSid());

        int row = conBuTypeInoutMapper.updateAllById(conBuTypeInout);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInout.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeInout, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型-出入库
     *
     * @param sids 需要删除的业务类型-出入库ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeInoutByIds(List<Long> sids) {
        List<ConBuTypeInout> list = conBuTypeInoutMapper.selectList(new QueryWrapper<ConBuTypeInout>()
                .lambda().in(ConBuTypeInout::getSid, sids));
        int row = conBuTypeInoutMapper.deleteBatchIds(sids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConBuTypeInout());
                MongodbUtil.insertUserLog(o.getSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeInout
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConBuTypeInout conBuTypeInout) {
        int row = 0;
        Long[] sids = conBuTypeInout.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeInoutMapper.update(null, new UpdateWrapper<ConBuTypeInout>().lambda().set(ConBuTypeInout::getStatus, conBuTypeInout.getStatus())
                    .in(ConBuTypeInout::getSid, sids));
            if (row == 0) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                //插入日志
                String remark = conBuTypeInout.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbDeal.status(id, conBuTypeInout.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param conBuTypeInout
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConBuTypeInout conBuTypeInout) {
        int row = 0;
        Long[] sids = conBuTypeInout.getSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ConBuTypeInout> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ConBuTypeInout::getSid, sids);
            updateWrapper.set(ConBuTypeInout::getHandleStatus, conBuTypeInout.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(conBuTypeInout.getHandleStatus())) {
                updateWrapper.set(ConBuTypeInout::getConfirmDate, new Date());
                updateWrapper.set(ConBuTypeInout::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = conBuTypeInoutMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, conBuTypeInout.getHandleStatus(), null, TITLE, null);
                }
            } else {
                throw new CustomException("确认失败,请联系管理员");
            }
        }
        return row;
    }

}
