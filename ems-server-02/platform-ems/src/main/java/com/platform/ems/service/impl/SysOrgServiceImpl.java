package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysOrg;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasCompany;
import com.platform.ems.domain.BasDepartment;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.ems.mapper.BasDepartmentMapper;
import com.platform.ems.mapper.BasStaffMapper;
import com.platform.ems.util.BuildTreeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.SysOrgMapper;
import com.platform.ems.service.ISysOrgService;

/**
 * 组织架构信息Service业务层处理
 *
 * @author qhq
 * @date 2021-03-18
 */
@Service
@SuppressWarnings("all")
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg>  implements ISysOrgService {
    @Autowired
    private SysOrgMapper sysOrgMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "组织架构信息";
    /**
     * 查询组织架构信息
     *
     * @param organizationNodeSid 组织架构信息ID
     * @return 组织架构信息
     */
    @Override
    public SysOrg selectSysOrgById(Long organizationNodeSid) {
        SysOrg sysOrg = sysOrgMapper.selectSysOrgById(organizationNodeSid);
        MongodbUtil.find(sysOrg);
        return sysOrg;
    }

    /**
     * 查询组织架构信息列表
     *
     * @param sysOrg 组织架构信息
     * @return 组织架构信息
     */
    @Override
    public List<SysOrg> selectSysOrgList(SysOrg sysOrg) {
        return sysOrgMapper.selectSysOrgList(sysOrg);
    }

    /**
     * 新增组织架构信息
     * 需要注意编码重复校验
     * @param sysOrg 组织架构信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysOrg(SysOrg sysOrg) {
        check(sysOrg);
        int row= sysOrgMapper.insert(sysOrg);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(sysOrg.getNodeSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改组织架构信息
     *
     * @param sysOrg 组织架构信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysOrg(SysOrg sysOrg) {
        check(sysOrg);
        SysOrg response = sysOrgMapper.selectSysOrgById(sysOrg.getNodeSid());
        int row= sysOrgMapper.updateById(sysOrg);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysOrg.getNodeSid(), BusinessType.UPDATE.ordinal(), response, sysOrg,TITLE);
        }
        return row;
    }

    /**
     * 变更组织架构信息
     *
     * @param sysOrg 组织架构信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysOrg(SysOrg sysOrg) {
        check(sysOrg);
        SysOrg response = sysOrgMapper.selectSysOrgById(sysOrg.getNodeSid());
        int row= sysOrgMapper.updateAllById(sysOrg);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysOrg.getNodeSid(), BusinessType.CHANGE.ordinal(), response, sysOrg,TITLE);
        }
        return row;
    }

    /**
     * 批量删除组织架构信息
     *
     * @param organizationNodeSids 需要删除的组织架构信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysOrgByIds(List<Long> organizationNodeSids) {
        int row = 0;
        List<Long> nodeSids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(organizationNodeSids)) {
            for (Long id : organizationNodeSids) {
                nodeSids.add(id);
                treeChild(id, nodeSids, BusinessType.DELETE.getValue());
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.DELETE.getValue(), msgList, TITLE);
            }
            row = sysOrgMapper.delete(new QueryWrapper<SysOrg>().lambda().in(SysOrg::getNodeSid, nodeSids));
        }
        return row;
    }

    //遍历所有子节点
    private void treeChild(Long id, List<Long> nodeSids, String type) {
        List<SysOrg> childSysOrgList = sysOrgMapper.selectList(new QueryWrapper<SysOrg>().lambda()
                .eq(SysOrg::getParentNodeSid, id));
        for (SysOrg sysOrg : childSysOrgList) {
            nodeSids.add(sysOrg.getNodeSid());
            if (BusinessType.DELETE.getValue().equals(type)){
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sysOrg.getNodeSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            }
            id = sysOrg.getNodeSid();
            treeChild(id, nodeSids, type);
        }
    }

    //遍历所有父节点
    private void treeParent(Long id, SysOrg sysOrg) {
        SysOrg parentSysOrg = sysOrgMapper.selectOne(new QueryWrapper<SysOrg>().lambda()
                .eq(SysOrg::getNodeSid, id));
        if (parentSysOrg != null){
            if (ConstantsEms.NODE_TYPE_GS.equals(sysOrg.getNodeType()) && ConstantsEms.NODE_TYPE_GS.equals(parentSysOrg.getNodeType())
                    && parentSysOrg.getCompanySid().equals(sysOrg.getCompanySid())){
                throw new BaseException("自己不能作为自己的下级");
            }
            if (ConstantsEms.NODE_TYPE_BM.equals(sysOrg.getNodeType()) && ConstantsEms.NODE_TYPE_BM.equals(parentSysOrg.getNodeType())
                    && parentSysOrg.getDepartmentSid().equals(sysOrg.getDepartmentSid())){
                throw new BaseException("自己不能作为自己的下级");
            }
            if (ConstantsEms.NODE_TYPE_YG.equals(sysOrg.getNodeType()) && ConstantsEms.NODE_TYPE_YG.equals(parentSysOrg.getNodeType())
                    && parentSysOrg.getStaffSid().equals(sysOrg.getStaffSid())){
                throw new BaseException("自己不能作为自己的下级");
            }
            id = parentSysOrg.getParentNodeSid();
            treeParent(id, sysOrg);
        }
    }

/*    @Override
    public List<TreeSelect> buildTreeSelect(List<SysOrg> organizationInforList) {
        BuildTreeService buildTreeService=new BuildTreeService<>(SysOrg.class);
        List<SysOrg> organizationInforTrees=buildTreeService.buildTreeSelect(organizationInforList);
        return organizationInforTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }*/

    @Override
    public List<SysOrg> buildTreeSelect(List<SysOrg> organizationInforList) {
        BuildTreeService<SysOrg> buildTreeService=new BuildTreeService<>("nodeSid","parentNodeSid","children");
        List<SysOrg> organizationInforTrees=buildTreeService.buildTreeSelect(organizationInforList);
        return organizationInforTrees;
    }

    /**
     * 提示员工在其他地方已存在是否继续创建
     */
    @Override
    public boolean checkStaff(SysOrg sysOrg) {
        if (ConstantsEms.NODE_TYPE_YG.equals(sysOrg.getNodeType())){
            List<SysOrg> orgLists = sysOrgMapper.selectList(new QueryWrapper<SysOrg>().lambda()
                    .ne(SysOrg::getNodeSid, sysOrg.getNodeSid() == null ? "" : sysOrg.getNodeSid())
                    .eq(SysOrg::getStaffSid, sysOrg.getStaffSid())
                    .eq(SysOrg::getParentNodeSid, sysOrg.getParentNodeSid()));
            if (orgLists.size() > 0){
                throw new BaseException("同级下不能存在相同员工");
            }
            List<SysOrg> staffList = sysOrgMapper.selectList(new QueryWrapper<SysOrg>().lambda()
                    .ne(SysOrg::getNodeSid, sysOrg.getNodeSid() == null ? "" : sysOrg.getNodeSid())
                    .eq(SysOrg::getStaffSid, sysOrg.getStaffSid())
                    .eq(SysOrg::getNodeType, ConstantsEms.NODE_TYPE_YG));
            if (staffList.size() > 0){
                return false;
            }
        }
        return true;
    }

    public void check(SysOrg sysOrg){
        //检查同级中不能重复
        if (ConstantsEms.NODE_TYPE_GS.equals(sysOrg.getNodeType())){
            List<SysOrg> orgLists = sysOrgMapper.selectList(new QueryWrapper<SysOrg>().lambda()
                    .ne(SysOrg::getNodeSid, sysOrg.getNodeSid() == null ? "" : sysOrg.getNodeSid())
                    .eq(SysOrg::getCompanySid, sysOrg.getCompanySid())
                    .eq(SysOrg::getNodeType, ConstantsEms.NODE_TYPE_GS));
            if (orgLists.size() > 0){
                throw new BaseException("公司已存在，请核实!");
            }
            BasCompany basCompany = basCompanyMapper.selectById(sysOrg.getCompanySid());
            sysOrg.setNodeName(basCompany.getCompanyName());
        }
        if (ConstantsEms.NODE_TYPE_BM.equals(sysOrg.getNodeType())){
            List<SysOrg> orgLists = sysOrgMapper.selectList(new QueryWrapper<SysOrg>().lambda()
                    .ne(SysOrg::getNodeSid, sysOrg.getNodeSid() == null ? "" : sysOrg.getNodeSid())
                    .eq(SysOrg::getDepartmentSid, sysOrg.getDepartmentSid())
                    .eq(SysOrg::getNodeType, ConstantsEms.NODE_TYPE_BM));
            if (orgLists.size() > 0){
                throw new BaseException("部门已存在，请核实!");
            }
            BasDepartment basDepartment = basDepartmentMapper.selectById(sysOrg.getDepartmentSid());
            sysOrg.setNodeName(basDepartment.getDepartmentName());
        }
        if (ConstantsEms.NODE_TYPE_YG.equals(sysOrg.getNodeType())){
            List<SysOrg> orgLists = sysOrgMapper.selectList(new QueryWrapper<SysOrg>().lambda()
                    .ne(SysOrg::getNodeSid, sysOrg.getNodeSid() == null ? "" : sysOrg.getNodeSid())
                    .eq(SysOrg::getStaffSid, sysOrg.getStaffSid())
                    .eq(SysOrg::getParentNodeSid, sysOrg.getParentNodeSid()));
            if (orgLists.size() > 0){
                throw new BaseException("同级下不能存在相同员工");
            }
            BasStaff basStaff = basStaffMapper.selectById(sysOrg.getStaffSid());
            sysOrg.setNodeName(basStaff.getStaffName());
        }
        //检查自己不能作为自己的下级
        treeParent(sysOrg.getParentNodeSid(), sysOrg);
    }

}
