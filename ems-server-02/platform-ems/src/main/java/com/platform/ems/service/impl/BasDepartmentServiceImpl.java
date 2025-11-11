package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.BasDepartment;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.ems.mapper.BasDepartmentMapper;
import com.platform.ems.service.IBasDepartmentService;

import cn.hutool.core.collection.CollectionUtil;

/**
 * 部门档案Service业务层处理
 *
 * @author qhq
 * @date 2021-04-09
 */
@Service
@SuppressWarnings("all")
public class BasDepartmentServiceImpl extends ServiceImpl<BasDepartmentMapper,BasDepartment>  implements IBasDepartmentService {
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
	@Autowired
	private SysTodoTaskMapper sysTodoTaskMapper;

	private static final String TITLE = "部门档案";

    /**
     * 查询部门档案
     *
     * @param clientId 部门档案ID
     * @return 部门档案
     */
    @Override
    public BasDepartment selectBasDepartmentById(Long positionSid) {
    	BasDepartment dept = basDepartmentMapper.selectBasDepartmentById(positionSid);
		MongodbUtil.find(dept);
    	return dept;
    }

    /**
     * 查询部门档案列表
     *
     * @param basDepartment 部门档案
     * @return 部门档案
     */
    @Override
    public List<BasDepartment> selectBasDepartmentList(BasDepartment basDepartment) {
    	List<BasDepartment> list = basDepartmentMapper.selectBasDepartmentList(basDepartment);
        return list;
    }

    /**
     * 新增部门档案
     * 需要注意编码重复校验
     * @param basDepartment 部门档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasDepartment(BasDepartment basDepartment) {
    	QueryWrapper<BasDepartment> departmentWrapper = new QueryWrapper<BasDepartment>();
    	departmentWrapper.eq("company_sid", basDepartment.getCompanySid());
    	departmentWrapper.eq("department_name", basDepartment.getDepartmentName());
    	List<BasDepartment> deptList = basDepartmentMapper.selectList(departmentWrapper);
    	if(CollectionUtil.isNotEmpty(deptList)) {
    		throw new CheckedException("部门名称已存在！");
    	}
		QueryWrapper<BasDepartment> departmentWrapper2 = new QueryWrapper<BasDepartment>();
		departmentWrapper2.eq("company_sid", basDepartment.getCompanySid());
		departmentWrapper2.eq("department_code", basDepartment.getDepartmentCode());
		List<BasDepartment> deptList2 = basDepartmentMapper.selectList(departmentWrapper2);
		if(CollectionUtil.isNotEmpty(deptList2)) {
			throw new CheckedException("部门编码已存在！");
		}
		int row = basDepartmentMapper.insert(basDepartment);
		//待办通知
		if (ConstantsEms.SAVA_STATUS.equals(basDepartment.getHandleStatus())) {
			SysTodoTask sysTodoTask = new SysTodoTask();
			sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
					.setTableName("s_bas_department")
					.setDocumentSid(basDepartment.getDepartmentSid());
			sysTodoTask.setTitle("部门档案: " + basDepartment.getDepartmentCode() + " 当前是保存状态，请及时处理！")
					.setDocumentCode(String.valueOf(basDepartment.getDepartmentCode()))
					.setNoticeDate(new Date())
					.setUserId(ApiThreadLocalUtil.get().getUserid());
			sysTodoTaskMapper.insert(sysTodoTask);
		}
		//插入日志
		List<OperMsg> msgList=new ArrayList<>();
		MongodbDeal.insert(Long.valueOf(basDepartment.getDepartmentSid()), basDepartment.getHandleStatus(), null, TITLE,null);
		return row;
    }

    /**
     * 修改部门档案
     *
     * @param basDepartment 部门档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasDepartment(BasDepartment basDepartment) {
    	BasDepartment old = basDepartmentMapper.selectById(basDepartment.getDepartmentSid());
    	if(!old.getDepartmentName().equals(basDepartment.getDepartmentName())
			|| !old.getCompanySid().equals(basDepartment.getCompanySid())) {
    		QueryWrapper<BasDepartment> departmentWrapper = new QueryWrapper<BasDepartment>();
        	departmentWrapper.eq("company_sid", basDepartment.getCompanySid());
        	departmentWrapper.eq("department_name", basDepartment.getDepartmentName());
        	List<BasDepartment> deptList = basDepartmentMapper.selectList(departmentWrapper);
        	if(CollectionUtil.isNotEmpty(deptList)) {
        		throw new CheckedException("部门名称已存在！");
        	}
    	}
    	if (!old.getDepartmentCode().equals(basDepartment.getDepartmentCode())
				|| !old.getCompanySid().equals(basDepartment.getCompanySid())){
			BasDepartment item = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
					.eq(BasDepartment::getCompanySid, basDepartment.getCompanySid())
					.eq(BasDepartment::getDepartmentCode, basDepartment.getDepartmentCode()));
			if (item != null){
				throw new BaseException("部门编码已存在！");
			}
		}
    	if (StrUtil.equals(ConstantsEms.CHECK_STATUS , basDepartment.getHandleStatus())) {
    		basDepartment.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
		}
    	basDepartment.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
		//确认状态后删除待办
		if (!ConstantsEms.SAVA_STATUS.equals(basDepartment.getHandleStatus())){
			sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
					.eq(SysTodoTask::getDocumentSid, basDepartment.getDepartmentSid()));
		}
		//插入日志
		List<OperMsg> msgList = new ArrayList<>();
		msgList = BeanUtils.eq(old, basDepartment);
		MongodbDeal.update(Long.valueOf(basDepartment.getDepartmentSid()), old.getHandleStatus(), basDepartment.getHandleStatus(), msgList, TITLE, null);
        return basDepartmentMapper.updateAllById(basDepartment);
    }

    /**
     * 批量删除部门档案
     *
     * @param clientIds 需要删除的部门档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasDepartmentByIds(List<Long> departmentSid) {
		departmentSid.forEach(sid->{
			//插入日志
			MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
		});
		sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
				.in(SysTodoTask::getDocumentSid, departmentSid));
        return basDepartmentMapper.deleteBatchIds(departmentSid);
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int status(BasDepartment basDepartment) {
		BasDepartment update = new BasDepartment();
		List<Long> sids = basDepartment.getDepartmentSids();
		for(Long sid : sids) {
			update = new BasDepartment();
			update.setDepartmentSid(sid);
			update.setStatus(basDepartment.getStatus());
			update.setConfirmDate(new Date());
			update.setConfirmerAccount(SecurityUtils.getUsername());
			basDepartmentMapper.updateById(update);
			//插入日志
			String remark = StrUtil.isEmpty(basDepartment.getDisableRemark()) ? null : basDepartment.getDisableRemark();
			MongodbDeal.status(Long.valueOf(sid), basDepartment.getStatus(), null, TITLE, remark);
		}
		return 1;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int handleStatus(BasDepartment basDepartment) {
		BasDepartment update = new BasDepartment();
		List<Long> sids = basDepartment.getDepartmentSids();
		for(Long sid : sids) {
			update = new BasDepartment();
			update.setDepartmentSid(sid);
			update.setHandleStatus(basDepartment.getHandleStatus());
			update.setConfirmDate(new Date());
			update.setConfirmerAccount(SecurityUtils.getUsername());
			basDepartmentMapper.updateById(update);
			//插入日志
			MongodbDeal.check(Long.valueOf(sid), basDepartment.getHandleStatus(), null, TITLE, null);
		}
		//确认状态后删除待办
		if (!ConstantsEms.SAVA_STATUS.equals(basDepartment.getHandleStatus())){
			sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
					.in(SysTodoTask::getDocumentSid, sids));
		}
		return 1;
	}

	@Override
	public List<BasDepartment> getCompanyDept(Long companySid) {
		QueryWrapper<BasDepartment> departmentWrapper = new QueryWrapper<BasDepartment>();
		departmentWrapper.eq("company_sid", companySid)
				.eq("status",ConstantsEms.ENABLE_STATUS)
				.eq("handle_status",ConstantsEms.CHECK_STATUS);
		List<BasDepartment> deptList = basDepartmentMapper.selectList(departmentWrapper);
		return deptList;
	}

	@Override
	public List<BasDepartment> getDeptList(BasDepartment basDepartment) {
		List<BasDepartment> deptList = basDepartmentMapper.getDeptList(basDepartment);
		return deptList;
	}


}
