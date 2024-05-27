package cn.com.tzy.springbootfs.config.fs;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.fs.*;
import cn.com.tzy.springbootentity.dome.fs.VdnPhone;
import cn.com.tzy.springbootentity.dome.fs.VdnSchedule;
import cn.com.tzy.springbootfs.convert.fs.*;
import cn.com.tzy.springbootfs.service.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.AgentStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.assign.*;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.lineup.CustomLineupStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.lineup.DefaultLineupStrategy;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.lineup.VipLineupStrategy;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Order(20)
@Component
public class CompanyRunner implements CommandLineRunner {

    @Resource
    private CompanyService companyService;
    @Resource
    private RouteCallService routeCallService;
    @Resource
    private RouteGroupService routeGroupService;
    @Resource
    private RouteGatewayService routeGatewayService;
    @Resource
    private RouteGatewayGroupService routeGatewayGroupService;
    @Resource
    private GroupService groupService;
    @Resource
    private GroupOverflowService groupOverflowService;
    @Resource
    private OverflowFrontService overflowFrontService;
    @Resource
    private OverflowExpService overflowExpService;
    @Resource
    private GroupAgentStrategyService groupAgentStrategyService;
    @Resource
    private GroupStrategyExpService groupStrategyExpService;
    @Resource
    private GroupMemoryConfigService groupMemoryConfigService;
    @Resource
    private CompanyPhoneService companyPhoneService;
    @Resource
    private CompanyPhoneGroupService companyPhoneGroupService;
    @Resource
    private SkillGroupService skillGroupService;
    @Resource
    private VdnCodeService vdnCodeService;
    @Resource
    private VdnConfigService vdnConfigService;
    @Resource
    private VdnScheduleService vdnScheduleService;
    @Resource
    private VdnDtmfService vdnDtmfService;
    @Resource
    private VdnPhoneService vdnPhoneService;
    @Resource
    private PlaybackService playbackService;
    @Resource
    private CompanyConferenceService companyConferenceService;

    @Override
    public void run(String... args) throws Exception {
        //清除缓存
        RedisService.getAgentInfoManager().delAll();
        RedisService.getCompanyInfoManager().delAll();
        RedisService.getVdnPhoneManager().delAll();
        RedisService.getPlaybackInfoManager().delAll();
        RedisService.getCompanyConferenceInfoManager().delAll();
        initCompany();
    }

    private void initCompany(){
        //开始查询
        List<Company> companyList = companyService.list(new LambdaQueryWrapper<Company>().ne(Company::getStatus, 0));
        if(companyList.isEmpty()){
            return;
        }
        List<CompanyInfo> companyInfoList = CompanyConvert.INSTANCE.convertCompanyInfoList(companyList);
        Map<Long, CompanyInfo> companyIdMap = companyInfoList.stream().collect(Collectors.toMap(o->Long.parseLong(o.getId()), Function.identity()));
        //查询所需要缓存数据
        //查询会议
        List<CompanyConference> companyConferenceList = companyConferenceService.list(Wrappers.<CompanyConference>lambdaQuery().in(CompanyConference::getCompanyId, companyIdMap.keySet()));
        //查询网关路由相关
        List<RouteGroup> routeGroupList = routeGroupService.list();
        List<RouteGroupInfo> routeGroupInfoList = RouteGroupConvert.INSTANCE.convertRouteGroupInfoList(routeGroupList);
        List<RouteCall> routeCallList = routeCallService.list(new LambdaQueryWrapper<RouteCall>().in(RouteCall::getCompanyId,companyIdMap.keySet()));
        Map<Long, List<RouteCall>> routeCallCompanyMap = routeCallList.stream().collect(Collectors.groupingBy(RouteCall::getCompanyId));
        //查询技能组相关
        List<Group> groupList = groupService.list(new LambdaQueryWrapper<Group>().in(Group::getCompanyId, companyIdMap.keySet()));
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
        //号码相关
        List<CompanyPhoneGroup> companyPhoneGroupList = companyPhoneGroupService.list();
        List<CompanyPhone> companyPhoneList = companyPhoneService.list();
        //技能组相关
        List<SkillGroup> skillGroupList = skillGroupService.list(new LambdaQueryWrapper<SkillGroup>().in(SkillGroup::getCompanyId, companyIdMap.keySet()));
        List<SkillGroupInfo> skillGroupInfoList = SkillGroupConvert.INSTANCE.convertSkillGroupInfoList(skillGroupList);
        //查询呼入路由相关
        List<VdnCode> vdnCodeList = vdnCodeService.list(new LambdaQueryWrapper<VdnCode>().in(VdnCode::getCompanyId, companyIdMap.keySet()).eq(VdnCode::getStatus, ConstEnum.Flag.YES.getValue()));
        Map<Long, List<VdnCode>> vdnCodeCompanyMap = vdnCodeList.stream().collect(Collectors.groupingBy(VdnCode::getCompanyId));
        List<VdnConfig> vdnConfigList = vdnConfigService.list(new LambdaQueryWrapper<VdnConfig>().in(VdnConfig::getCompanyId, companyIdMap.keySet()));
        Map<Long, List<VdnConfig>> vdnConfigCompanyMap = vdnConfigList.stream().collect(Collectors.groupingBy(VdnConfig::getCompanyId));
        List<VdnSchedule> vdnScheduleList= vdnScheduleService.list(new LambdaQueryWrapper<VdnSchedule>().in(VdnSchedule::getCompanyId, companyIdMap.keySet()));
        Map<Long, List<VdnSchedule>> vdnScheduleCompanyMap = vdnScheduleList.stream().collect(Collectors.groupingBy(VdnSchedule::getCompanyId));
        List<VdnDtmf> vdnDtmfList = vdnDtmfService.list(new LambdaQueryWrapper<VdnDtmf>().in(VdnDtmf::getCompanyId, companyIdMap.keySet()));
        Map<Long, List<VdnDtmf>> vdnDtmfCompanyMap = vdnDtmfList.stream().collect(Collectors.groupingBy(VdnDtmf::getCompanyId));
        //查询开的vdn企业
        List<VdnPhone> vdnPhoneList = vdnPhoneService.list(new LambdaQueryWrapper<VdnPhone>().in(VdnPhone::getCompanyId, companyIdMap.keySet()));
        List<VdnPhoneInfo> vdnScheduleInfoList = VdnPhoneConvert.INSTANCE.convertVdnScheduleInfoList(vdnPhoneList);
        //查询企业录音回放
        List<Playback> playbackList = playbackService.list(new LambdaQueryWrapper<Playback>().in(Playback::getCompanyId, companyIdMap.keySet()));
        List<PlaybackInfo> playbackInfoList = PlaybackConvert.INSTANCE.convertPlaybackInfoList(playbackList);
        //初始化会议
        initCompanyConferenceInfo(companyConferenceList);
        //初始化路由网关
        initRouteGroupInfo(routeGroupInfoList);
        //初始化-缓存-技能组
        initGroupStrategy(groupInfoList,groupOverFlowInfoList,overflowFrontInfoList,overflowExpInfoList,groupAgentStrategyInfoList,groupStrategyExpInfoList,groupMemoryConfigInfoList,companyPhoneGroupList,companyPhoneList,skillGroupInfoList);
        //开始缓存
        for (CompanyInfo companyInfo : companyIdMap.values()) {
            //初始化企业路由
            companyInfo.initRouteCalls(routeGroupInfoList,RouteCallConvert.INSTANCE.convertRouteCallInfoList(routeCallCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())));
            //初始化vdn
            companyInfo.initVdn(
                    VdnCodeConvert.INSTANCE.convertVdnCodeInfoList(vdnCodeCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())),
                    VdnConfigConvert.INSTANCE.convertVdnScheduleInfoList(vdnConfigCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())),
                    VdnScheduleConvert.INSTANCE.convertVdnScheduleInfoList(vdnScheduleCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>())),
                    VdnDtmfConvert.INSTANCE.convertVdnScheduleInfoList(vdnDtmfCompanyMap.computeIfAbsent(Long.parseLong(companyInfo.getId()), k -> new ArrayList<>()))
            );
            //缓存企业数据
            RedisService.getCompanyInfoManager().put(companyInfo);
        }
        //缓存被叫vdn企业
        vdnScheduleInfoList.forEach(o->{
            RedisService.getVdnPhoneManager().put(o);
        });
        //缓存放音
        playbackInfoList.forEach(o->{
            RedisService.getPlaybackInfoManager().put(o);
        });
    }

    //初始化会议
    private void initCompanyConferenceInfo(List<CompanyConference> companyConferenceList) {
        List<CompanyConferenceInfo> companyConferenceInfoList = CompanyConferenceConvert.INSTANCE.convertCompanyConferenceInfoList(companyConferenceList);
        if(companyConferenceInfoList.isEmpty()){
            return;
        }
        companyConferenceInfoList.forEach(o->{
            RedisService.getCompanyConferenceInfoManager().put(o);
        });
    }

    //初始化路由网关
    private void initRouteGroupInfo(List<RouteGroupInfo> routeGroupInfoList){
        List<RouteGatewayGroup> routeGatewayGroupList = routeGatewayGroupService.list();
        Map<Long, List<RouteGatewayGroup>> routeGatewayGroupMap = routeGatewayGroupList.stream().collect(Collectors.groupingBy(RouteGatewayGroup::getRouteGroupId));
        List<RouteGateway> routeGatewayList = routeGatewayService.list();
        List<RouteGateWayInfo> routeGateWayInfoList = RouteGatewayConvert.INSTANCE.convertRouteGateWayInfoList(routeGatewayList);
        Map<Long, RouteGateWayInfo> routeGatewayMap = routeGateWayInfoList.stream().collect(Collectors.toMap(RouteGateWayInfo::getId, o -> o));
        for (RouteGroupInfo routeGroupInfo : routeGroupInfoList) {
            List<RouteGatewayGroup> gatewayGroupList = routeGatewayGroupMap.computeIfAbsent(routeGroupInfo.getId(), k -> new ArrayList<>());
            if(gatewayGroupList.isEmpty()){
                continue;
            }
            List<Long> gatewayIdList = gatewayGroupList.stream().map(RouteGatewayGroup::getGatewayId).collect(Collectors.toList());
            List<RouteGateWayInfo> collect = gatewayIdList.stream().map(routeGatewayMap::get).collect(Collectors.toList());
            if(collect.isEmpty()){
                continue;
            }
            routeGroupInfo.setRouteGateWayInfoList(collect);
        }
    }

    //初始化技能组
    public void initGroupStrategy(
            List<GroupInfo> groupInfoList,
            List<GroupOverFlowInfo> groupOverFlowInfoList,
            List<OverflowFrontInfo> overflowFrontInfoList,
            List<OverflowExpInfo> overflowExpInfoList,
            List<GroupAgentStrategyInfo> groupAgentStrategyInfoList,
            List<GroupStrategyExpInfo> groupStrategyExpInfoList,
            List<GroupMemoryConfigInfo> groupMemoryConfigInfoList,
            List<CompanyPhoneGroup> companyPhoneGroupList ,
            List<CompanyPhone> companyPhoneList,
            List<SkillGroupInfo> skillGroupInfoList
    ) {
        RedisService.getGroupInfoManager().delAll();
        if(groupInfoList == null || groupInfoList.isEmpty()){
            return;
        }
        Map<Long, List<GroupOverFlowInfo>>  groupOverFlowInfoMap= groupOverFlowInfoList.stream().collect(Collectors.groupingBy(GroupOverFlowInfo::getGroupId));
        Map<Long, List<OverflowFrontInfo>> overflowFrontInfoMap = overflowFrontInfoList.stream().collect(Collectors.groupingBy(OverflowFrontInfo::getOverflowId));
        Map<Long, List<OverflowExpInfo>> overflowExpInfoMap = overflowExpInfoList.stream().collect(Collectors.groupingBy(OverflowExpInfo::getOverflowId));
        Map<Long, GroupAgentStrategyInfo> groupAgentStrategyInfoMap = groupAgentStrategyInfoList.stream().collect(Collectors.toMap(GroupAgentStrategyInfo::getGroupId,o->o,(o1,o2)->o2));
        Map<Long, List<GroupStrategyExpInfo>> groupStrategyExpInfoMap = groupStrategyExpInfoList.stream().collect(Collectors.groupingBy(GroupStrategyExpInfo::getGroupId));
        Map<Long, GroupMemoryConfigInfo> groupMemoryConfigInfoMap = groupMemoryConfigInfoList.stream().collect(Collectors.toMap(GroupMemoryConfigInfo::getGroupId,o->o,(o1,o2)->o2));
        Map<Long, List<CompanyPhoneGroup>> companyPhoneGroupMap = companyPhoneGroupList.stream().collect(Collectors.groupingBy(CompanyPhoneGroup::getDisplayId));
        Map<Long, List<SkillGroupInfo>> skillGroupInfoMap = skillGroupInfoList.stream().collect(Collectors.groupingBy(SkillGroupInfo::getGroupId));

        for (GroupInfo groupInfo : groupInfoList) {
            //添加 前置条件 与 自定义策略
            List<GroupOverFlowInfo> overFlowInfoList = groupOverFlowInfoMap.get(Long.valueOf(groupInfo.getId()));
            if(overFlowInfoList.isEmpty()){
                groupInfo.setGroupOverflows(new ArrayList<>());
            }else {
                for (GroupOverFlowInfo groupOverFlowInfo : overFlowInfoList) {
                    groupOverFlowInfo.setOverflowFronts(overflowFrontInfoMap.computeIfAbsent(groupOverFlowInfo.getOverflowId(), k -> new ArrayList<>()));
                    groupOverFlowInfo.setOverflowExps(overflowExpInfoMap.computeIfAbsent(groupOverFlowInfo.getOverflowId(), k -> new ArrayList<>()));
                }
                groupInfo.setGroupOverflows(overFlowInfoList);
            }
            GroupAgentStrategyInfo groupAgentStrategyInfo = groupAgentStrategyInfoMap.get(Long.valueOf(groupInfo.getId()));
            if(groupAgentStrategyInfo!=null){
                groupAgentStrategyInfo.setStrategyExpList(groupStrategyExpInfoMap.computeIfAbsent(Long.valueOf(groupInfo.getId()), k -> new ArrayList<>()));
                groupInfo.setGroupAgentStrategyPo(groupAgentStrategyInfo);
            }
            groupInfo.setGroupMemoryConfig(groupMemoryConfigInfoMap.get(Long.valueOf(groupInfo.getId())));
            //号码相关
            List<Long> calledPhoneIdList = companyPhoneGroupMap.computeIfAbsent(groupInfo.getCalledDisplayId(), o -> new ArrayList<>()).stream().map(CompanyPhoneGroup::getPhoneId).collect(Collectors.toList());
            List<Long> callerPhoneIdList = companyPhoneGroupMap.computeIfAbsent(groupInfo.getCallerDisplayId(), o -> new ArrayList<>()).stream().map(CompanyPhoneGroup::getPhoneId).collect(Collectors.toList());
            groupInfo.setCalledDisplays(companyPhoneList.stream().filter(o->calledPhoneIdList.contains(o.getId())).map(CompanyPhone::getPhone).collect(Collectors.toList()));
            groupInfo.setCallerDisplays(companyPhoneList.stream().filter(o->callerPhoneIdList.contains(o.getId())).map(CompanyPhone::getPhone).collect(Collectors.toList()));
            groupInfo.setSkills(skillGroupInfoMap.computeIfAbsent(Long.valueOf(groupInfo.getId()), o -> new ArrayList<>()));
        }
        /**
         * 电话多媒体排队策略
         */
        for (GroupInfo groupInfo : groupInfoList) {
            groupInfo.getGroupOverflows().forEach(overflowConfig -> {
                //1:先进先出,2:vip,3:自定义
                switch (overflowConfig.getBusyType()) {
                    case 1:
                        overflowConfig.setLineupStrategy(new DefaultLineupStrategy());
                        break;
                    case 2:
                        overflowConfig.setLineupStrategy(new VipLineupStrategy());
                        break;
                    case 3://自定义暂未实现，等待完善
                        overflowConfig.setLineupStrategy(new CustomLineupStrategy(""));
                        break;
                    default:
                }
            });

            /**
             * 坐席在技能组中空闲策略
             */
            if (groupInfo.getGroupAgentStrategyPo() == null) {
                return;
            }
            Integer agentStrategyType = groupInfo.getGroupAgentStrategyPo().getStrategyType();
            Integer agentStrategyValue = groupInfo.getGroupAgentStrategyPo().getStrategyValue();
            AgentStrategy agentStrategy = null;
            if (agentStrategyType == 1) {
                // (1当前最长空闲时间、2空闲次数最多、3最少应答次数、4累计最少通话时长、5累计话后时长、6轮选、7随机)
                switch (agentStrategyValue) {
                    case 1:
                        agentStrategy = new LongReadyAssign();
                        break;
                    case 2:
                        agentStrategy = new TotalReadyTimesAssign();
                        break;
                    case 3:
                        agentStrategy = new LeastAnswerAssign();
                        break;
                    case 4:
                        agentStrategy = new LeastTalkAssign();
                        break;
                    case 5:
                        agentStrategy = new TotalAfterTimeAssign();
                        break;
                    case 6:
                        agentStrategy = new PollAssign();
                        break;
                    case 7:
                        agentStrategy = new RandomAssign();
                        break;
                    default:
                        break;
                }
            } else if (agentStrategyType == 2) {
                agentStrategy = new AgentCustomAssign(groupInfo.getGroupAgentStrategyPo().getCustomExpression());
            }
            if (agentStrategy == null) {
                log.warn("companyId:{} groupName:{} init agentStrategy error", groupInfo.getCompanyId(), groupInfo.getName());
                agentStrategy = new LongReadyAssign();
            }
            groupInfo.getGroupAgentStrategyPo().setAgentStrategy(agentStrategy);
            //缓存技能组
            RedisService.getGroupInfoManager().put(groupInfo);
        }
    }

}
