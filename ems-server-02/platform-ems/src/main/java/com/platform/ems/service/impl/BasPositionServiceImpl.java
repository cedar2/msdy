package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasStaff;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.BasStaffMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.core.domain.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.BasCompany;
import com.platform.ems.domain.BasPosition;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.ems.mapper.BasPositionMapper;
import com.platform.ems.service.IBasPositionService;

import cn.hutool.core.collection.CollectionUtil;

/**
 * 岗位Service业务层处理
 *
 * @author qhq
 * @date 2021-03-18
 */
@Service
@SuppressWarnings("all")
public class BasPositionServiceImpl extends ServiceImpl<BasPositionMapper,BasPosition>  implements IBasPositionService {
    @Autowired
    private BasPositionMapper basPositionMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
	@Autowired
	private BasStaffMapper basStaffMapper;
	@Autowired
	private SysTodoTaskMapper sysTodoTaskMapper;

	private static final String TITLE = "岗位档案";

    /**
     * 查询岗位
     *
     * @param positionSid 岗位ID
     * @return 岗位
     */
    @Override
    public BasPosition selectBasPositionById(Long positionSid) {
    	BasPosition position = basPositionMapper.selectBasPositionById(positionSid);
		MongodbUtil.find(position);
        return position;
    }

    /**
     * 查询岗位列表
     *
     * @param basPosition 岗位
     * @return 岗位
     */
    @Override
    public List<BasPosition> selectBasPositionList(BasPosition basPosition) {
    	List<BasPosition> list = basPositionMapper.selectBasPositionList(basPosition);
        return list;
    }

    /**
     * 新增岗位
     * 需要注意编码重复校验
     * @param basPosition 岗位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPosition(BasPosition basPosition) {
    	QueryWrapper<BasPosition> positionWrapper = new QueryWrapper<BasPosition>();
    	positionWrapper.eq("company_sid",basPosition.getCompanySid());
    	positionWrapper.eq("position_name", basPosition.getPositionName());
    	List<BasPosition> positionList =  basPositionMapper.selectList(positionWrapper);
    	if(CollectionUtil.isNotEmpty(positionList)) {
    		throw new CheckedException("岗位名称已存在，请核实！");
    	}
		BasPosition position = basPositionMapper.selectOne(new QueryWrapper<BasPosition>().lambda()
												 .eq(BasPosition::getCompanySid, basPosition.getCompanySid())
												 .eq(BasPosition::getPositionCode, basPosition.getPositionCode()));
    	if (position != null){
    		throw new BaseException("岗位编码已存在，请核实！");
		}
    	int row =  basPositionMapper.insert(basPosition);
		//待办通知
		SysTodoTask sysTodoTask = new SysTodoTask();
		if (ConstantsEms.SAVA_STATUS.equals(basPosition.getHandleStatus())) {
			sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
					.setTableName("s_bas_position")
					.setDocumentSid(basPosition.getPositionSid());
			sysTodoTask.setTitle("岗位档案: " + basPosition.getPositionCode() + " 当前是保存状态，请及时处理！")
					.setDocumentCode(String.valueOf(basPosition.getPositionCode()))
					.setNoticeDate(new Date())
					.setUserId(ApiThreadLocalUtil.get().getUserid());
			sysTodoTaskMapper.insert(sysTodoTask);
		}
		//插入日志
		List<OperMsg> msgList=new ArrayList<>();
		MongodbDeal.insert(Long.valueOf(basPosition.getPositionSid()), basPosition.getHandleStatus(), null, TITLE,null);
		return row;
    }

    /**
     * 修改岗位
     *
     * @param basPosition 岗位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPosition(BasPosition basPosition) {
    	QueryWrapper<BasPosition> positionWrapper = new QueryWrapper<BasPosition>();
    	BasPosition old =  basPositionMapper.selectById(basPosition.getPositionSid());
    	if(!old.getPositionName().equals(basPosition.getPositionName())) {
    		positionWrapper = new QueryWrapper<BasPosition>();
    		positionWrapper.eq("company_sid",basPosition.getCompanySid());
        	positionWrapper.eq("position_name", basPosition.getPositionName());
        	List<BasPosition> nameList = basPositionMapper.selectList(positionWrapper);
    		if(CollectionUtil.isNotEmpty(nameList)) {
    			throw new CheckedException("岗位名称已存在，请核实！");
    		}
    	}
		BasPosition item = basPositionMapper.selectOne(new QueryWrapper<BasPosition>().lambda()
				.eq(BasPosition::getCompanySid, basPosition.getCompanySid())
				.eq(BasPosition::getPositionCode, basPosition.getPositionCode()));
		if (item != null && !basPosition.getPositionSid().equals(item.getPositionSid())){
			throw new BaseException("岗位编码已存在，请核实！");
		}

		LoginUser loginUser = ApiThreadLocalUtil.get();
		basPosition.setUpdateDate(new Date()).setUpdaterAccount(loginUser.getUsername());
		basPosition.setCreateDate(old.getCreateDate());
		basPosition.setCreatorAccount(old.getCreatorAccount());
		//确认状态后删除待办
		if (!ConstantsEms.SAVA_STATUS.equals(basPosition.getHandleStatus())){
			basPosition.setConfirmDate(new Date()).setConfirmerAccount(loginUser.getUsername());
			sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
					.eq(SysTodoTask::getDocumentSid, basPosition.getPositionSid()));
		}
		//插入日志
		List<OperMsg> msgList = new ArrayList<>();
		msgList = BeanUtils.eq(old, basPosition);
		MongodbDeal.update(Long.valueOf(basPosition.getPositionSid()), old.getHandleStatus(), basPosition.getHandleStatus(), msgList, TITLE, null);
        return basPositionMapper.updateAllById(basPosition);
    }

    /**
     * 批量删除岗位
     *
     * @param positionSids 需要删除的岗位ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPositionByIds(List<Long> positionSids) {
		positionSids.forEach(sid->{
			//插入日志
			MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
		});
		sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
				.in(SysTodoTask::getDocumentSid, positionSids));
        return basPositionMapper.deleteBatchIds(positionSids);
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int status(BasPosition basPosition) {
		BasPosition update = new BasPosition();
		List<Long> sids = basPosition.getPositionSids();
		for(Long sid : sids) {
			update.setPositionSid(sid);
			update.setStatus(basPosition.getStatus());
			update.setConfirmDate(new Date());
			update.setConfirmerAccount(SecurityUtils.getUsername());
			basPositionMapper.updateById(update);
			//插入日志
			String remark = StrUtil.isEmpty(basPosition.getDisableRemark()) ? null : basPosition.getDisableRemark();
			MongodbDeal.status(Long.valueOf(sid), basPosition.getStatus(), null, TITLE, remark);
		}
		return 1;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int handleStatus(BasPosition basPosition) {
		BasPosition update = new BasPosition();
		List<Long> sids = basPosition.getPositionSids();
		for(Long sid : sids) {
			update.setPositionSid(sid);
			update.setHandleStatus(basPosition.getHandleStatus());
			update.setConfirmDate(new Date());
			update.setConfirmerAccount(SecurityUtils.getUsername());
			basPositionMapper.updateById(update);
			//插入日志
			MongodbDeal.check(Long.valueOf(sid), basPosition.getHandleStatus(), null, TITLE, null);
		}
		//确认状态后删除待办
		if (!ConstantsEms.SAVA_STATUS.equals(basPosition.getHandleStatus())){
			sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
					.in(SysTodoTask::getDocumentSid, sids));
		}
		return 1;
	}

	@Override
	public List<BasPosition> getCompanyPosition(Long companySid) {
		QueryWrapper<BasPosition> positionWrapper = new QueryWrapper<BasPosition>();
		positionWrapper.eq("company_sid",companySid).eq("status",ConstantsEms.ENABLE_STATUS).eq("handle_status",ConstantsEms.CHECK_STATUS);
		List<BasPosition> list =  basPositionMapper.selectList(positionWrapper);
		return list;
	}

	/**
	 * 岗位，下拉值为状态为确认且启用、当前操作用户所属员工的所属公司下的岗位档案的数据
	 */
	@Override
	public List<BasPosition> getSelfPosition(BasPosition basPosition) {
		Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
		if (staffSid != null) {
			BasStaff staff = basStaffMapper.selectById(staffSid);
			if (staff != null && staff.getDefaultCompanySid() != null) {
				QueryWrapper<BasPosition> queryWrapper = new QueryWrapper<>();
				queryWrapper.lambda().eq(BasPosition::getCompanySid, staff.getDefaultCompanySid());
				if (basPosition.getHandleStatus() != null) {
					queryWrapper.lambda().eq(BasPosition::getHandleStatus, basPosition.getHandleStatus());
				}
				if (basPosition.getStatus() != null) {
					queryWrapper.lambda().eq(BasPosition::getStatus, basPosition.getStatus());
				}
				return basPositionMapper.selectList(queryWrapper);
			}
		}
		return new ArrayList<>();
	}

}
