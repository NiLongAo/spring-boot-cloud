package cn.com.tzy.springbootfs.config.fs.runtime.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.fs.*;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigService;
import cn.com.tzy.springbootfs.convert.fs.*;
import cn.com.tzy.springbootfs.service.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.AgentStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.assign.*;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.lineup.CustomLineupStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.lineup.DefaultLineupStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.lineup.VipLineupStrategy;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class FsRuntimeConfigServiceImpl implements FsRuntimeConfigService {

    @Resource private CompanyService companyService;
    @Resource private RouteCallService routeCallService;
    @Resource private RouteGroupService routeGroupService;
    @Resource private RouteGatewayService routeGatewayService;
    @Resource private RouteGatewayGroupService routeGatewayGroupService;
    @Resource private GroupService groupService;
    @Resource private GroupOverflowService groupOverflowService;
    @Resource private OverflowFrontService overflowFrontService;
    @Resource private OverflowExpService overflowExpService;
    @Resource private GroupAgentStrategyService groupAgentStrategyService;
    @Resource private GroupStrategyExpService groupStrategyExpService;
    @Resource private GroupMemoryConfigService groupMemoryConfigService;
    @Resource private CompanyPhoneService companyPhoneService;
    @Resource private CompanyPhoneGroupService companyPhoneGroupService;
    @Resource private SkillGroupService skillGroupService;
    @Resource private VdnCodeService vdnCodeService;
    @Resource private VdnConfigService vdnConfigService;
    @Resource private VdnScheduleService vdnScheduleService;
    @Resource private VdnDtmfService vdnDtmfService;
    @Resource private VdnPhoneService vdnPhoneService;
    @Resource private PlaybackService playbackService;
    @Resource private CompanyConferenceService companyConferenceService;

    @Override
    public void refreshAll() {
        RedisService.getAgentInfoManager().delAll();
        RedisService.getCompanyInfoManager().delAll();
        RedisService.getVdnPhoneManager().delAll();
        RedisService.getPlaybackInfoManager().delAll();
        RedisService.getCompanyConferenceInfoManager().delAll();
        initAllCompanies();
    }

    @Override
    public void refreshCompany(Long companyId) {
        Company company = companyService.getById(companyId);
        if (company == null || company.getStatus() == null || company.getStatus() == 0) {
            RedisService.getCompanyInfoManager().del(String.valueOf(companyId));
            return;
        }
        refreshCompanies(Collections.singletonList(companyId));
    }

    // ---- private helpers ----

    private void initAllCompanies() {
        List<Company> companyList = companyService.list(
                new LambdaQueryWrapper<Company>().ne(Company::getStatus, 0));
        if (companyList.isEmpty()) {
            return;
        }
        List<Long> companyIds = companyList.stream().map(Company::getId).collect(Collectors.toList());
        buildAndCacheCompanies(companyList, companyIds);
    }

    private void refreshCompanies(List<Long> companyIds) {
        List<Company> companyList = companyService.listByIds(companyIds);
        if (companyList.isEmpty()) {
            return;
        }
        buildAndCacheCompanies(companyList, companyIds);
    }

    private void buildAndCacheCompanies(List<Company> companyList, List<Long> companyIds) {
        List<CompanyInfo> companyInfoList = CompanyConvert.INSTANCE.convertCompanyInfoList(companyList);
        Map<Long, CompanyInfo> companyIdMap = companyInfoList.stream()
                .collect(Collectors.toMap(o -> Long.parseLong(o.getId()), Function.identity()));

        // 会议
        List<CompanyConference> companyConferenceList = companyConferenceService.list(
                Wrappers.<CompanyConference>lambdaQuery().in(CompanyConference::getCompanyId, companyIds));
        // 路由
        List<RouteGroup> routeGroupList = routeGroupService.list();
        List<RouteGroupInfo> routeGroupInfoList = RouteGroupConvert.INSTANCE.convertRouteGroupInfoList(routeGroupList);
        List<RouteCall> routeCallList = routeCallService.list(
                new LambdaQueryWrapper<RouteCall>().in(RouteCall::getCompanyId, companyIds));
        Map<Long, List<RouteCall>> routeCallCompanyMap = routeCallList.stream()
                .collect(Collectors.groupingBy(RouteCall::getCompanyId));
        // 技能组
        List<Group> groupList = groupService.list(
                new LambdaQueryWrapper<Group>().in(Group::getCompanyId, companyIds));
        List<GroupInfo> groupInfoList = GroupConvert.INSTANCE.convertGroupInfoList(groupList);
        List<GroupOverFlowInfo> groupOverFlowInfoList = groupOverflowService.findGroupOverFlowInfo();
        List<OverflowFront> overflowFrontList = overflowFrontService.list();
        List<OverflowFrontInfo> overflowFrontInfoList = OverflowFrontConvert.INSTANCE.convertOverflowExpInfoList(overflowFrontList);
        List<OverflowExp> overflowExpList = overflowExpService.list();
        List<OverflowExpInfo> overflowExpInfoList = OverflowExpConvert.INSTANCE.convertOverflowExpInfoList(overflowExpList);
        List<GroupAgentStrategy> groupAgentStrategyList = groupAgentStrategyService.list();
        List<GroupAgentStrategyInfo> groupAgentStrategyInfoList = GroupAgentStrategyConvert.INSTANCE.convertCompanyInfoList(groupAgentStrategyList);
        List<GroupStrategyExp> groupStrategyExpList = groupStrategyExpService.list();
        List<GroupStrategyExpInfo> groupStrategyExpInfoList = GroupStrategyExpConvert.INSTANCE.convertGroupStrategyExpInfoList(groupStrategyExpList);
        List<GroupMemoryConfig> groupMemoryConfigList = groupMemoryConfigService.list();
        List<GroupMemoryConfigInfo> groupMemoryConfigInfoList = GroupMemoryConfigConvert.INSTANCE.convertGroupMemoryConfigInfoList(groupMemoryConfigList);
        // 号码
        List<CompanyPhoneGroup> companyPhoneGroupList = companyPhoneGroupService.list();
        List<CompanyPhone> companyPhoneList = companyPhoneService.list();
        // 技能组关系
        List<SkillGroup> skillGroupList = skillGroupService.list(
                new LambdaQueryWrapper<SkillGroup>().in(SkillGroup::getCompanyId, companyIds));
        List<SkillGroupInfo> skillGroupInfoList = SkillGroupConvert.INSTANCE.convertSkillGroupInfoList(skillGroupList);
        // VDN
        List<VdnCode> vdnCodeList = vdnCodeService.list(
                new LambdaQueryWrapper<VdnCode>().in(VdnCode::getCompanyId, companyIds)
                        .eq(VdnCode::getStatus, ConstEnum.Flag.YES.getValue()));
        Map<Long, java.util.List<VdnCode>> vdnCodeCompanyMap = vdnCodeList.stream().collect(Collectors.groupingBy(VdnCode::getCompanyId));
        List<VdnConfig> vdnConfigList = vdnConfigService.list(
                new LambdaQueryWrapper<VdnConfig>().in(VdnConfig::getCompanyId, companyIds));
        Map<Long, List<VdnConfig>> vdnConfigCompanyMap = vdnConfigList.stream().collect(Collectors.groupingBy(VdnConfig::getCompanyId));
        List<VdnSchedule> vdnScheduleList = vdnScheduleService.list(
                new LambdaQueryWrapper<VdnSchedule>().in(VdnSchedule::getCompanyId, companyIds));
        Map<Long, List<VdnSchedule>> vdnScheduleCompanyMap = vdnScheduleList.stream().collect(Collectors.groupingBy(VdnSchedule::getCompanyId));
        List<VdnDtmf> vdnDtmfList = vdnDtmfService.list(
                new LambdaQueryWrapper<VdnDtmf>().in(VdnDtmf::getCompanyId, companyIds));
        Map<Long, List<VdnDtmf>> vdnDtmfCompanyMap = vdnDtmfList.stream().collect(Collectors.groupingBy(VdnDtmf::getCompanyId));
        // VDN 号码
        List<VdnPhone> vdnPhoneList = vdnPhoneService.list(
                new LambdaQueryWrapper<VdnPhone>().in(VdnPhone::getCompanyId, companyIds));
        List<VdnPhoneInfo> vdnPhoneInfoList = VdnPhoneConvert.INSTANCE.convertVdnScheduleInfoList(vdnPhoneList);
        // 放音
        List<Playback> playbackList = playbackService.list(
                new LambdaQueryWrapper<Playback>().in(Playback::getCompanyId, companyIds));
        List<PlaybackInfo> playbackInfoList = PlaybackConvert.INSTANCE.convertPlaybackInfoList(playbackList);

        // 初始化路由网关
        initRouteGroupInfo(routeGroupInfoList);
        // 初始化技能组
        initGroupStrategy(groupInfoList, groupOverFlowInfoList, overflowFrontInfoList, overflowExpInfoList,
                groupAgentStrategyInfoList, groupStrategyExpInfoList, groupMemoryConfigInfoList,
                companyPhoneGroupList, companyPhoneList, skillGroupInfoList);
        // 会议
        companyConferenceList.forEach(c -> {
            java.util.List<CompanyConferenceInfo> list = CompanyConferenceConvert.INSTANCE.convertCompanyConferenceInfoList(Collections.singletonList(c));
            list.forEach(o -> RedisService.getCompanyConferenceInfoManager().put(o));
        });

        for (CompanyInfo companyInfo : companyIdMap.values()) {
            companyInfo.initRouteCalls(routeGroupInfoList,
                    RouteCallConvert.INSTANCE.convertRouteCallInfoList(
                            routeCallCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())));
            companyInfo.initVdn(
                    VdnCodeConvert.INSTANCE.convertVdnCodeInfoList(vdnCodeCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())),
                    VdnConfigConvert.INSTANCE.convertVdnScheduleInfoList(vdnConfigCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())),
                    VdnScheduleConvert.INSTANCE.convertVdnScheduleInfoList(vdnScheduleCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())),
                    VdnDtmfConvert.INSTANCE.convertVdnScheduleInfoList(vdnDtmfCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>()))
            );
            RedisService.getCompanyInfoManager().put(companyInfo);
        }
        vdnPhoneInfoList.forEach(o -> RedisService.getVdnPhoneManager().put(o));
        playbackInfoList.forEach(o -> RedisService.getPlaybackInfoManager().put(o));
    }

    private void initRouteGroupInfo(List<RouteGroupInfo> routeGroupInfoList) {
        List<RouteGatewayGroup> routeGatewayGroupList = routeGatewayGroupService.list();
        Map<Long, List<RouteGatewayGroup>> routeGatewayGroupMap = routeGatewayGroupList.stream()
                .collect(Collectors.groupingBy(RouteGatewayGroup::getRouteGroupId));
        List<RouteGateway> routeGatewayList = routeGatewayService.list();
        List<RouteGateWayInfo> routeGateWayInfoList = RouteGatewayConvert.INSTANCE.convertRouteGateWayInfoList(routeGatewayList);
        Map<Long, RouteGateWayInfo> routeGatewayMap = routeGateWayInfoList.stream()
                .collect(Collectors.toMap(RouteGateWayInfo::getId, o -> o));
        for (RouteGroupInfo routeGroupInfo : routeGroupInfoList) {
            List<RouteGatewayGroup> gatewayGroupList = routeGatewayGroupMap.computeIfAbsent(
                    routeGroupInfo.getId(), k -> new ArrayList<>());
            if (gatewayGroupList.isEmpty()) continue;
            List<Long> gatewayIdList = gatewayGroupList.stream().map(RouteGatewayGroup::getGatewayId).collect(Collectors.toList());
            List<RouteGateWayInfo> collect = gatewayIdList.stream().map(routeGatewayMap::get).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                routeGroupInfo.setRouteGateWayInfoList(collect);
            }
        }
    }

    private void initGroupStrategy(
            List<GroupInfo> groupInfoList,
            List<GroupOverFlowInfo> groupOverFlowInfoList,
            List<OverflowFrontInfo> overflowFrontInfoList,
            List<OverflowExpInfo> overflowExpInfoList,
            List<GroupAgentStrategyInfo> groupAgentStrategyInfoList,
            List<GroupStrategyExpInfo> groupStrategyExpInfoList,
            List<GroupMemoryConfigInfo> groupMemoryConfigInfoList,
            List<CompanyPhoneGroup> companyPhoneGroupList,
            List<CompanyPhone> companyPhoneList,
            List<SkillGroupInfo> skillGroupInfoList) {
        RedisService.getGroupInfoManager().delAll();
        if (groupInfoList == null || groupInfoList.isEmpty()) return;

        Map<Long, List<GroupOverFlowInfo>> groupOverFlowInfoMap = groupOverFlowInfoList.stream()
                .collect(Collectors.groupingBy(GroupOverFlowInfo::getGroupId));
        Map<Long, List<OverflowFrontInfo>> overflowFrontInfoMap = overflowFrontInfoList.stream()
                .collect(Collectors.groupingBy(OverflowFrontInfo::getOverflowId));
        Map<Long, List<OverflowExpInfo>> overflowExpInfoMap = overflowExpInfoList.stream()
                .collect(Collectors.groupingBy(OverflowExpInfo::getOverflowId));
        Map<Long, GroupAgentStrategyInfo> groupAgentStrategyInfoMap = groupAgentStrategyInfoList.stream()
                .collect(Collectors.toMap(GroupAgentStrategyInfo::getGroupId, o -> o, (o1, o2) -> o2));
        Map<Long, List<GroupStrategyExpInfo>> groupStrategyExpInfoMap = groupStrategyExpInfoList.stream()
                .collect(Collectors.groupingBy(GroupStrategyExpInfo::getGroupId));
        Map<Long, GroupMemoryConfigInfo> groupMemoryConfigInfoMap = groupMemoryConfigInfoList.stream()
                .collect(Collectors.toMap(GroupMemoryConfigInfo::getGroupId, o -> o, (o1, o2) -> o2));
        Map<Long, List<CompanyPhoneGroup>> companyPhoneGroupMap = companyPhoneGroupList.stream()
                .collect(Collectors.groupingBy(CompanyPhoneGroup::getDisplayId));
        Map<Long, List<SkillGroupInfo>> skillGroupInfoMap = skillGroupInfoList.stream()
                .collect(Collectors.groupingBy(SkillGroupInfo::getGroupId));

        for (GroupInfo groupInfo : groupInfoList) {
            List<GroupOverFlowInfo> overFlowInfoList = groupOverFlowInfoMap.get(Long.valueOf(groupInfo.getId()));
            if (overFlowInfoList == null || overFlowInfoList.isEmpty()) {
                groupInfo.setGroupOverflows(new ArrayList<>());
            } else {
                for (GroupOverFlowInfo info : overFlowInfoList) {
                    info.setOverflowFronts(overflowFrontInfoMap.computeIfAbsent(info.getOverflowId(), k -> new ArrayList<>()));
                    info.setOverflowExps(overflowExpInfoMap.computeIfAbsent(info.getOverflowId(), k -> new ArrayList<>()));
                }
                groupInfo.setGroupOverflows(overFlowInfoList);
            }
            GroupAgentStrategyInfo strategyInfo = groupAgentStrategyInfoMap.get(Long.valueOf(groupInfo.getId()));
            if (strategyInfo != null) {
                strategyInfo.setStrategyExpList(groupStrategyExpInfoMap.computeIfAbsent(Long.valueOf(groupInfo.getId()), k -> new ArrayList<>()));
                groupInfo.setGroupAgentStrategyPo(strategyInfo);
            }
            groupInfo.setGroupMemoryConfig(groupMemoryConfigInfoMap.get(Long.valueOf(groupInfo.getId())));
            List<Long> calledPhoneIdList = companyPhoneGroupMap.computeIfAbsent(groupInfo.getCalledDisplayId(), o -> new ArrayList<>())
                    .stream().map(CompanyPhoneGroup::getPhoneId).collect(Collectors.toList());
            List<Long> callerPhoneIdList = companyPhoneGroupMap.computeIfAbsent(groupInfo.getCallerDisplayId(), o -> new ArrayList<>())
                    .stream().map(CompanyPhoneGroup::getPhoneId).collect(Collectors.toList());
            groupInfo.setCalledDisplays(companyPhoneList.stream().filter(o -> calledPhoneIdList.contains(o.getId())).map(CompanyPhone::getPhone).collect(Collectors.toList()));
            groupInfo.setCallerDisplays(companyPhoneList.stream().filter(o -> callerPhoneIdList.contains(o.getId())).map(CompanyPhone::getPhone).collect(Collectors.toList()));
            groupInfo.setSkills(skillGroupInfoMap.computeIfAbsent(Long.valueOf(groupInfo.getId()), o -> new ArrayList<>()));
        }
        for (GroupInfo groupInfo : groupInfoList) {
            if (groupInfo.getGroupOverflows() == null) continue;
            groupInfo.getGroupOverflows().forEach(overflowConfig -> {
                switch (overflowConfig.getBusyType()) {
                    case 1: overflowConfig.setLineupStrategy(new DefaultLineupStrategy()); break;
                    case 2: overflowConfig.setLineupStrategy(new VipLineupStrategy()); break;
                    case 3: overflowConfig.setLineupStrategy(new CustomLineupStrategy("")); break;
                    default: break;
                }
            });
            if (groupInfo.getGroupAgentStrategyPo() == null) continue;
            Integer agentStrategyType = groupInfo.getGroupAgentStrategyPo().getStrategyType();
            Integer agentStrategyValue = groupInfo.getGroupAgentStrategyPo().getStrategyValue();
            AgentStrategy agentStrategy = null;
            if (agentStrategyType == 1) {
                switch (agentStrategyValue) {
                    case 1: agentStrategy = new LongReadyAssign(); break;
                    case 2: agentStrategy = new TotalReadyTimesAssign(); break;
                    case 3: agentStrategy = new LeastAnswerAssign(); break;
                    case 4: agentStrategy = new LeastTalkAssign(); break;
                    default: break;
                }
            }
            groupInfo.getGroupAgentStrategyPo().setAgentStrategy(agentStrategy);
            RedisService.getGroupInfoManager().put(groupInfo);
        }
    }
}
