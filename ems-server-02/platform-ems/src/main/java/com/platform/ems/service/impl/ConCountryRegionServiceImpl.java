package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.entity.ConCountryRegion;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.mapper.ConCountryRegionMapper;
import com.platform.ems.service.IConCountryRegionService;

/**
 * 国家区域Service业务层处理
 *
 * @author qhq
 * @date 2021-03-26
 */
@Service
@SuppressWarnings("all")
public class ConCountryRegionServiceImpl extends ServiceImpl<ConCountryRegionMapper, ConCountryRegion>  implements IConCountryRegionService {
	@Autowired
	private ConCountryRegionMapper conCountryRegionMapper;
	@Autowired
	private MongoTemplate mongoTemplate;


	private static final String TITLE = "国家区域";
	/**
	 * 查询国家区域
	 *
	 * @param countryRegionSid 国家区域ID
	 * @return 国家区域
	 */
	@Override
	public ConCountryRegion selectConCountryRegionById(Long countryRegionSid) {
		ConCountryRegion conCountryRegion = conCountryRegionMapper.selectConCountryRegionById(countryRegionSid);
		MongodbUtil.find(conCountryRegion);
		return  conCountryRegion;
	}

	/**
	 * 查询国家区域列表
	 *
	 * @param conCountryRegion 国家区域
	 * @return 国家区域
	 */
	@Override
	public List<ConCountryRegion> selectConCountryRegionList(ConCountryRegion conCountryRegion) {
		return conCountryRegionMapper.selectConCountryRegionList(conCountryRegion);
	}

	/**
	 * 新增国家区域
	 * 需要注意编码重复校验
	 * @param conCountryRegion 国家区域
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertConCountryRegion(ConCountryRegion conCountryRegion) {
		int row= conCountryRegionMapper.insert(conCountryRegion);
		if(row>0){
			//插入日志
			List<OperMsg> msgList=new ArrayList<>();
			MongodbUtil.insertUserLog(conCountryRegion.getCountryRegionSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
		}
		return row;
	}

	/**
	 * 修改国家区域
	 *
	 * @param conCountryRegion 国家区域
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateConCountryRegion(ConCountryRegion conCountryRegion) {
		ConCountryRegion response = conCountryRegionMapper.selectConCountryRegionById(conCountryRegion.getCountryRegionSid());
		int row=conCountryRegionMapper.updateById(conCountryRegion);
		if(row>0){
			//插入日志
			MongodbUtil.insertUserLog(conCountryRegion.getCountryRegionSid(), BusinessType.UPDATE.ordinal(), response,conCountryRegion,TITLE);
		}
		return row;
	}

	/**
	 * 变更国家区域
	 *
	 * @param conCountryRegion 国家区域
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int changeConCountryRegion(ConCountryRegion conCountryRegion) {
		ConCountryRegion response = conCountryRegionMapper.selectConCountryRegionById(conCountryRegion.getCountryRegionSid());
		int row=conCountryRegionMapper.updateAllById(conCountryRegion);
		if(row>0){
			//插入日志
			MongodbUtil.insertUserLog(conCountryRegion.getCountryRegionSid(), BusinessType.CHANGE.ordinal(), response,conCountryRegion,TITLE);
		}
		return row;
	}

	/**
	 * 批量删除国家区域
	 *
	 * @param countryRegionSids 需要删除的国家区域ID
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteConCountryRegionByIds(List<Long> countryRegionSids) {
		return conCountryRegionMapper.deleteBatchIds(countryRegionSids);
	}

	/**
	 * 启用/停用
	 * @param conCountryRegion
	 * @return
	 */
	@Override
	public int changeStatus(ConCountryRegion conCountryRegion){
		int row=0;
		Long[] sids=conCountryRegion.getCountryRegionSidList();
		if(sids!=null&&sids.length>0){
			row=conCountryRegionMapper.update(null, new UpdateWrapper<ConCountryRegion>().lambda().set(ConCountryRegion::getStatus ,conCountryRegion.getStatus() )
					.in(ConCountryRegion::getCountryRegionSid,sids));
			for(Long id:sids){
				//插入日志
				List<OperMsg> msgList=new ArrayList<>();
				String remark=conCountryRegion.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
				MongodbUtil.insertUserLog(conCountryRegion.getCountryRegionSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
			}
		}
		return row;
	}


	/**
	 *更改确认状态
	 * @param conCountryRegion
	 * @return
	 */
	@Override
	public int check(ConCountryRegion conCountryRegion){
		int row=0;
		Long[] sids=conCountryRegion.getCountryRegionSidList();
		if(sids!=null&&sids.length>0){
			row=conCountryRegionMapper.update(null,new UpdateWrapper<ConCountryRegion>().lambda().set(ConCountryRegion::getHandleStatus ,ConstantsEms.CHECK_STATUS)
					.in(ConCountryRegion::getCountryRegionSid,sids));
			for(Long id:sids){
				//插入日志
				List<OperMsg> msgList=new ArrayList<>();
				MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
			}
		}
		return row;
	}

}
