package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.PayTeamWorkattendDay;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 班组日出勤信息Service接口
 * 
 * @author linhongwei
 * @date 2022-07-27
 */
public interface IPayTeamWorkattendDayService extends IService<PayTeamWorkattendDay>{
    /**
     * 查询班组日出勤信息
     * 
     * @param teamWorkattendDaySid 班组日出勤信息ID
     * @return 班组日出勤信息
     */
    public PayTeamWorkattendDay selectPayTeamWorkattendDayById(Long teamWorkattendDaySid);
    /**
     * 获取当前账号的信息
     *
     */
    public PayTeamWorkattendDay getPayTeamWorkattend();
    /**
     * 查询班组日出勤信息列表
     * 
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 班组日出勤信息集合
     */
    public List<PayTeamWorkattendDay> selectPayTeamWorkattendDayList(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 查询班组日出勤信息
     *
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 班组日出勤信息集合
     */
    PayTeamWorkattendDay selectPayTeamWorkattendDayListBy(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 新增班组日出勤信息
     * 
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 结果
     */
    public int insertPayTeamWorkattendDay(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 修改班组日出勤信息
     * 
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 结果
     */
    public int updatePayTeamWorkattendDay(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 变更班组日出勤信息
     *
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 结果
     */
    public int changePayTeamWorkattendDay(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 批量删除班组日出勤信息
     * 
     * @param teamWorkattendDaySids 需要删除的班组日出勤信息ID
     * @return 结果
     */
    public int deletePayTeamWorkattendDayByIds(List<Long> teamWorkattendDaySids);

    /**
    * 启用/停用
    * @param payTeamWorkattendDay
    * @return
    */
    int changeStatus(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 更改确认状态
     * @param payTeamWorkattendDay
     * @return
     */
    int check(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 班组日出勤 导入
     */
    public AjaxResult importDataPur(MultipartFile file);

}
