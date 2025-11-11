package com.platform.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.StringUtils;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.service.ISysDefaultSettingClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统默认设置_租户级Service业务层处理
 *
 * @author chenkw
 * @date 2022-04-22
 */
@Service
@SuppressWarnings("all")
public class SysDefaultSettingClientServiceImpl extends ServiceImpl<SysDefaultSettingClientMapper, SysDefaultSettingClient> implements ISysDefaultSettingClientService {
    @Autowired
    private SysDefaultSettingClientMapper sysDefaultSettingClientMapper;

    private static final String TITLE = "系统默认设置_租户级";
    /**
     * 查询系统默认设置_租户级
     *
     * @param clientId 系统默认设置_租户级ID
     * @return 系统默认设置_租户级
     */
    @Override
    public SysDefaultSettingClient selectSysDefaultSettingClientById(String clientId) {
        SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper.selectSysDefaultSettingClientById(clientId);
        getProductSeason(sysDefaultSettingClient);
        return sysDefaultSettingClient;
    }

    /**
     * 查询系统默认设置_租户级列表
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 系统默认设置_租户级
     */
    @Override
    public List<SysDefaultSettingClient> selectSysDefaultSettingClientList(SysDefaultSettingClient sysDefaultSettingClient) {
        return sysDefaultSettingClientMapper.selectSysDefaultSettingClientList(sysDefaultSettingClient);
    }

    /**
     * 新增系统默认设置_租户级
     * 需要注意编码重复校验
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysDefaultSettingClient(SysDefaultSettingClient sysDefaultSettingClient) {
        int row = 0;
        SysDefaultSettingClient response = null;
        try {
            response = sysDefaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, sysDefaultSettingClient.getClientId()));
        } catch (Exception e) {
            throw new BaseException("配置存在重复，请联系管理员核查！");
        }
        if (response != null) {
            throw new BaseException("配置已存在，请刷新！");
        } else {
            setProductSeason(sysDefaultSettingClient);
            row = sysDefaultSettingClientMapper.insert(sysDefaultSettingClient);
        }
        return row;
    }

    /**
     * 修改系统默认设置_租户级
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysDefaultSettingClient(SysDefaultSettingClient sysDefaultSettingClient) {
        SysDefaultSettingClient response = sysDefaultSettingClientMapper.selectSysDefaultSettingClientById(sysDefaultSettingClient.getClientId());
        setProductSeason(sysDefaultSettingClient);
        int row = sysDefaultSettingClientMapper.updateAllById(sysDefaultSettingClient);
        return row;
    }

    /**
     * 变更系统默认设置_租户级
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysDefaultSettingClient(SysDefaultSettingClient sysDefaultSettingClient) {
        SysDefaultSettingClient response = sysDefaultSettingClientMapper.selectSysDefaultSettingClientById(sysDefaultSettingClient.getClientId());
        setProductSeason(sysDefaultSettingClient);
        int row = sysDefaultSettingClientMapper.updateAllById(sysDefaultSettingClient);
        return row;
    }

    /**
     * 批量删除系统默认设置_租户级
     *
     * @param clientIds 需要删除的系统默认设置_租户级ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDefaultSettingClientByIds(List<String> clientIds) {
        return sysDefaultSettingClientMapper.deleteBatchIds(clientIds);
    }

    /**
     * 更改确认状态
     *
     * @param sysDefaultSettingClient
     * @return
     */
    @Override
    public int check(SysDefaultSettingClient sysDefaultSettingClient) {
        int row = 0;
        String[] sids = sysDefaultSettingClient.getClientIdList();
        if (sids != null && sids.length > 0) {
            row = sysDefaultSettingClientMapper.update(null, new UpdateWrapper<SysDefaultSettingClient>()
                    .lambda().set(SysDefaultSettingClient::getHandleStatus, sysDefaultSettingClient.getHandleStatus())
                    .in(SysDefaultSettingClient::getClientId, sids));
        }
        return row;
    }


    //新建或者修改
    public void setProductSeason(SysDefaultSettingClient client) {
        if (client != null) {
            // 研发季
            if (client.getProductSeasonYanfaList() != null && client.getProductSeasonYanfaList().length > 0) {
                String productSeasonYanfa = StringUtils.join(client.getProductSeasonYanfaList(), ";");
                client.setProductSeasonYanfa(productSeasonYanfa);
            } else {
                client.setProductSeasonYanfa(null);
            }
            // 采购季
            if (client.getProductSeasonCaigouList() != null && client.getProductSeasonCaigouList().length > 0) {
                String productSeasonCaigou = StringUtils.join(client.getProductSeasonCaigouList(), ";");
                client.setProductSeasonCaigou(productSeasonCaigou);
            } else {
                client.setProductSeasonCaigou(null);
            }
            // 销售季
            if (client.getProductSeasonXiaoshouList() != null && client.getProductSeasonXiaoshouList().length > 0) {
                String productSeasonXiaoshou = StringUtils.join(client.getProductSeasonXiaoshouList(), ";");
                client.setProductSeasonXiaoshou(productSeasonXiaoshou);
            } else {
                client.setProductSeasonXiaoshou(null);
            }
            // 生产季
            if (client.getProductSeasonShengchanList() != null && client.getProductSeasonShengchanList().length > 0) {
                String productSeasonShengchan = StringUtils.join(client.getProductSeasonShengchanList(), ";");
                client.setProductSeasonShengchan(productSeasonShengchan);
            } else {
                client.setProductSeasonShengchan(null);
            }
            //销售财务对接人员（用户账号）
            if (client.getSaleFinanceAccountList() != null && client.getSaleFinanceAccountList().length > 0) {
                //将获取到的列表用';'分割然后组装成一个String
                String saleFinanceAccountList = StringUtils.join(client.getSaleFinanceAccountList(), ";");
                client.setSaleFinanceAccount(saleFinanceAccountList);
            } else {
                client.setSaleFinanceAccount(null);
            }

            //采购财务对接人员（用户账号）
            if (client.getPurchaseFinanceAccountList() != null && client.getPurchaseFinanceAccountList().length > 0) {
                //将获取到的列表用';'分割然后组装成一个String
                String purchaseFinanceAccountList = StringUtils.join(client.getPurchaseFinanceAccountList(), ";");
                client.setPurchaseFinanceAccount(purchaseFinanceAccountList);
            } else {
                client.setPurchaseFinanceAccount(null);
            }

            //电签超量提醒人员（用户账号）
            if (client.getDianqianExceedNoticeAccountList() != null && client.getDianqianExceedNoticeAccountList().length > 0) {
                //将获取到的列表用';'分割然后组装成一个String
                String dianqianExceedNoticeAccountList = StringUtils.join(client.getDianqianExceedNoticeAccountList(), ";");
                client.setDianqianExceedNoticeAccount(dianqianExceedNoticeAccountList);
            } else {
                client.setDianqianExceedNoticeAccount(null);
            }
            //销售待排产通知人员(用户账号)
            if (client.getSaleDpcNoticeAccountList() != null && client.getSaleDpcNoticeAccountList().length > 0) {
                //将获取到的列表用';'分割然后组装成一个String
                String saleDpcNoticeAccountList = StringUtils.join(client.getSaleDpcNoticeAccountList(), ";");
                client.setSaleDpcNoticeAccount(saleDpcNoticeAccountList);
            } else {
                client.setSaleDpcNoticeAccount(null);
            }
        }
    }

    //取详情时将分割出来存入数组
    public void getProductSeason(SysDefaultSettingClient client) {
        if (client != null) {
            // 研发季
            if (StrUtil.isNotBlank(client.getProductSeasonYanfa())) {
                String[] productSeasonYanfaList = client.getProductSeasonYanfa().split(";");
                client.setProductSeasonYanfaList(productSeasonYanfaList);
            }
            // 采购季
            if (StrUtil.isNotBlank(client.getProductSeasonCaigou())) {
                String[] productSeasonCaigouList = client.getProductSeasonCaigou().split(";");
                client.setProductSeasonCaigouList(productSeasonCaigouList);
            }
            // 销售季
            if (StrUtil.isNotBlank(client.getProductSeasonXiaoshou())) {
                String[] productSeasonXiaoshouList = client.getProductSeasonXiaoshou().split(";");
                client.setProductSeasonXiaoshouList(productSeasonXiaoshouList);
            }
            // 生产季
            if (StrUtil.isNotBlank(client.getProductSeasonShengchan())) {
                String[] productSeasonShengchanList = client.getProductSeasonShengchan().split(";");
                client.setProductSeasonShengchanList(productSeasonShengchanList);
            }
            //采购财务对接人员
            if (StrUtil.isNotBlank(client.getPurchaseFinanceAccount())) {
                String[] purchaseFinanceAccountList = client.getPurchaseFinanceAccount().split(";");
                client.setPurchaseFinanceAccountList(purchaseFinanceAccountList);
            }
            //电签超量提醒人员
            if (StrUtil.isNotBlank(client.getDianqianExceedNoticeAccount())) {
                String[] dianqianExceedNoticeAccountList = client.getDianqianExceedNoticeAccount().split(";");
                client.setDianqianExceedNoticeAccountList(dianqianExceedNoticeAccountList);
            }
            //销售待排产通知人员
            if (StrUtil.isNotBlank(client.getSaleDpcNoticeAccount())) {
                String[] saleDpcNoticeAccountList = client.getSaleDpcNoticeAccount().split(";");
                client.setSaleDpcNoticeAccountList(saleDpcNoticeAccountList);
            }
            //销售财务对接人员
            if (StrUtil.isNotBlank(client.getSaleFinanceAccount())) {
                String[] saleFinanceAccountList = client.getSaleFinanceAccount().split(";");
                client.setSaleFinanceAccountList(saleFinanceAccountList);
            }
        }
    }

}
