package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.spingbootstartermybatis.core.tenant.context.TenantContextHolder;
import cn.com.tzy.springbootbean.config.init.AppConfig;
import cn.com.tzy.springbootbean.mapper.sql.*;
import cn.com.tzy.springbootbean.service.api.TenantService;
import cn.com.tzy.springbootbean.service.api.UserService;
import cn.com.tzy.springbootbean.utils.PassWordUtils;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootentity.common.info.UserPayload;
import cn.com.tzy.springbootentity.dome.bean.MiniUser;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.dome.bean.UserSet;
import cn.com.tzy.springbootentity.dome.sys.Tenant;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.springbootentity.vo.bean.UserInfoVo;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PrivilegeMapper privilegeMapper;
    @Autowired
    private UserSetMapper userSetMapper;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private UserConnectDepartmentMapper userConnectDepartmentMapper;
    @Autowired
    private UserConnectPositionMapper userConnectPositionMapper;
    @Autowired
    private UserConnectRoleMapper userConnectRoleMapper;
    @Autowired
    private MiniUserMapper miniUserMapper;

    @Override
    public PageResult choiceUserPage(UserParam userPageModel) {
        int total =  baseMapper.findChoiceUserPageCount(userPageModel);
        List<User> pageResult = baseMapper.findChoiceUserPageResult(userPageModel);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putString("loginAccount", obj.getLoginAccount());
            map.putString("userName", obj.getUserName());
            map.putString("nickName", obj.getNickName());
            map.putString("idCard", obj.getIdCard());
            map.putString("phone", obj.getPhone());
            map.putString("address", obj.getAddress());
            map.putDateTime("loginLastTime", obj.getLoginLastTime());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> userSelect(List<Long> userIdList, String userName, Integer limit) {
        List<User> userList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(userIdList)){
            userList.addAll(baseMapper.selectBatchIds(userIdList));
        }
        userList.addAll(baseMapper.selectNameLimit(userIdList,userName, limit));
        List<NotNullMap> data = new ArrayList<>();
        userList.forEach(obj ->{
            NotNullMap map = new NotNullMap();
            map.putLong("id",obj.getId());
            map.putString("name", obj.getUserName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(), null, data);
    }

    @Override
    public PageResult findPage(UserParam userPageModel) {
        int total = baseMapper.findPageCount(userPageModel);
        List<User> pageResult = baseMapper.findPageResult(userPageModel);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putString("loginAccount", obj.getLoginAccount());
            map.putString("userName", obj.getUserName());
            map.putString("nickName", obj.getNickName());
            map.putString("idCard", obj.getIdCard());
            map.putString("phone", obj.getPhone());
            map.putString("address", obj.getAddress());
            map.putDateTime("loginLastTime", obj.getLoginLastTime());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> phone(String phone) {
        if(StringUtils.isEmpty(phone)){
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到手机号");
        }
        return findLoginTypeByUserInfo(TypeEnum.WEB_MOBILE,phone);
    }

    @Override
    public RestResult<?> openId(String openId) {
        if(StringUtils.isEmpty(openId)){
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取微信token");
        }
        return findLoginTypeByUserInfo(TypeEnum.WEB_WX_MINI,openId);
    }
    /**
     * oauth2获取登录用户信息
     *
     * @param loginAccount
     * @return
     */
    @Override
    public RestResult<?> findLoginAccount(String loginAccount) {
        return findLoginTypeByUserInfo(TypeEnum.WEB_ACCOUNT,String.valueOf(loginAccount));
    }
    /**
     * oauth2获取登录用户信息
     *
     * @param id
     * @return
     */
    @Override
    public RestResult<?> findLoginUserId(Long id) {
        return findLoginTypeByUserInfo(TypeEnum.WEB_ID,String.valueOf(id));
    }
    @Override
    public RestResult<?> findLoginInfo() {
        Map map = JwtUtils.getJwtPayload();
        if(map == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到用户信息");
        }
        UserPayload userJwtPayload = AppUtils.convertValue2(map, UserPayload.class);
        if(userJwtPayload == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"解析用户信息失败");
        }
        TypeEnum clientType = TypeEnum.getClientType(userJwtPayload.getLoginType());
        return findLoginTypeByUserInfo(clientType==TypeEnum.APP_WX_MINI?clientType:TypeEnum.WEB_ID,String.valueOf(userJwtPayload.getUserId()));
    }
    private RestResult<?> findLoginTypeByUserInfo(TypeEnum clientType,String userNo){
        Long userId = null;
        SecurityBaseUser user = null;
        switch (clientType){
            case WEB_ACCOUNT:
                user = baseMapper.findLoginAccount(userNo);
                if (user == null) {
                    return RestResult.result(RespCode.CODE_2.getValue(), "未获取到用户信息");
                }
                userId = user.getId();
                user = baseMapper.findLoginUserId(userId);
                break;
            case WEB_MOBILE:
                user = baseMapper.selectPhone(userNo);
                if (user == null) {
                    return RestResult.result(RespCode.CODE_2.getValue(), "未获取到用户信息");
                }
                userId = user.getId();
                user = baseMapper.findLoginUserId(userId);
                break;
            case WEB_WX_MINI:
                user = baseMapper.selectOpenId(userNo);
                if (user == null) {
                    return RestResult.result(RespCode.CODE_315.getValue(), "请先登录web端登录绑定微信");
                }
                userId = user.getId();
                user = baseMapper.findLoginUserId(userId);
                break;
            case APP_WX_MINI:
                MiniUser miniUser = miniUserMapper.selectOne(new LambdaQueryWrapper<MiniUser>().eq(MiniUser::getMiniId, Long.valueOf(userNo)));
                if(miniUser == null){
                    return RestResult.result(RespCode.CODE_2.getValue(),"未获取用户信息");
                }
                userId = miniUser.getUserId();
                user = baseMapper.findLoginUserId(userId);
                break;
            case WEB_ID:
                user = baseMapper.findLoginUserId(Long.valueOf(userNo));
                if (user == null) {
                    return RestResult.result(RespCode.CODE_2.getValue(), "未获取到用户信息");
                }
                userId = user.getId();
                break;
            default:
                return RestResult.result(RespCode.CODE_2.getValue(), "登陆类型错误");
        }
        RestResult<?> result = findUserInfo(userId);
        if (result.getCode() != RespCode.CODE_0.getValue()) {
            return result;
        }
        UserInfoVo userInfoVo = BeanUtil.toBean(result.getData(),UserInfoVo.class) ;
        SecurityBaseUser build = SecurityBaseUser.builder()
                .id(userInfoVo.getId())
                .userName(userInfoVo.getUserName())
                .nickName(userInfoVo.getNickName())
                .imageUrl(userInfoVo.getImageUrl())
                .password(user.getPassword())
                .credentialssalt(user.getCredentialssalt())
                .isAdmin(userInfoVo.getIsAdmin())
                .isEnabled(userInfoVo.getIsEnabled())
                .tenantId(userInfoVo.getTenantId())
                .tenantStatus(userInfoVo.getTenantStatus())
                .roleIdList(userInfoVo.getRoleIdList())
                .positionIdList(userInfoVo.getPositionIdList())
                .departmentIdList(userInfoVo.getDepartmentIdList())
                .privilegeList(userInfoVo.getPrivilegeList())
                .build();
        return RestResult.result(RespCode.CODE_0.getValue(), null, build);
    }

    /**
     * 获取用户基本信息
     *
     * @param userId 用户编号
     * @return
     */
    @Override
    @Cacheable(value = RedisCommon.USER_INFO,key = "#userId")
    public RestResult<?> findUserInfo(Long userId) {
        if (userId == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到用户信息");
        }
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到用户信息");
        }
        Integer miniCount = miniUserMapper.selectCount(new LambdaQueryWrapper<MiniUser>().eq(MiniUser::getUserId, user.getId()));
        UserSet userSet = userSetMapper.selectById(user.getId());
        Set<Map> roleIdList = userConnectRoleMapper.findAllByUserId(user.getId());
        Set<Map> positionIdList = userConnectPositionMapper.findAllByUserId(user.getId());
        Set<Map> departmentIdList = userConnectDepartmentMapper.findAllByUserId(user.getId());
        Set<String> userPrivilegeSet = new HashSet<>();
        Integer isAdmin = ConstEnum.Flag.NO.getValue();
        Integer isEnabled = ConstEnum.Flag.NO.getValue();
        Long tenantId =  null;
        Integer tenantStatus =  ConstEnum.Flag.YES.getValue();
        if(user.getTenantId() !=null){
            Tenant tenant = tenantService.getById(user.getTenantId());
            Assert.notNull(tenant,"未获取租户信息");
            tenantId = tenant.getId();
            tenantStatus = tenant.getStatus();
        }
        if (userSet != null) {
            isAdmin =userSet.getIsAdmin();
            isEnabled =userSet.getIsEnabled();
            if(userSet.getIsAdmin() == ConstEnum.Flag.YES.getValue() && Objects.equals(user.getTenantId(), Constant.TENANT_ID)){
                //系统管理员所有权限都有
                userPrivilegeSet = privilegeMapper.findUserAdmin();
            } else if(userSet.getIsAdmin() == ConstEnum.Flag.YES.getValue()){
                //系统管理员所有权限都有
                userPrivilegeSet = privilegeMapper.findTenantPrivilegeList(user.getTenantId());
            }else {
                Set<String> rolePrivilegeList = privilegeMapper.findUserRolePrivilegeList(user.getId());
                if (!rolePrivilegeList.isEmpty()) {
                    userPrivilegeSet.addAll(rolePrivilegeList);
                }
                Set<String> departmentPrivilegeList = privilegeMapper.findUserDepartmentPrivilegeList(user.getId());
                if (!rolePrivilegeList.isEmpty()) {
                    userPrivilegeSet.addAll(departmentPrivilegeList);
                }
                Set<String> positionPrivilegeList = privilegeMapper.findUserPositionPrivilegeList(user.getId());
                if (!rolePrivilegeList.isEmpty()) {
                    userPrivilegeSet.addAll(positionPrivilegeList);
                }
            }
        }
        String name = ConstEnum.Sex.getName(user.getGender());
        if(StringUtils.isEmpty(name)){
            return RestResult.result(RespCode.CODE_2.getValue(),"性别类型错误");
        }
        UserInfoVo build = UserInfoVo.builder()
                .id(userId)
                .userName(user.getLoginAccount())//改为登录账户
                .nickName(user.getNickName())
                .imageUrl(user.getImageUrl())
                .loginAccount(user.getLoginAccount())
                .phone(user.getPhone())
                .gender(user.getGender())
                .provinceId(user.getProvinceId())
                .cityId(user.getCityId())
                .areaId(user.getAreaId())
                .idCard(user.getIdCard())
                .address(user.getAddress())
                .memo(user.getMemo())
                .wxMiniStatus(miniCount > 0?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue())
                .loginLastTime(user.getLoginLastTime())
                .isAdmin(isAdmin)
                .isEnabled(isEnabled)
                .tenantId(tenantId)
                .tenantStatus(tenantStatus)
                .roleIdList(roleIdList.isEmpty()?new ArrayList<>():roleIdList.stream().map(o->Long.parseLong(o.get("roleId").toString())).collect(Collectors.toList()))
                .positionIdList(positionIdList.isEmpty()?new ArrayList<>():positionIdList.stream().map(o->Long.parseLong(o.get("positionId").toString())).collect(Collectors.toList()))
                .departmentIdList(departmentIdList.isEmpty()?new ArrayList<>():departmentIdList.stream().map(o->Long.parseLong(o.get("departmentId").toString())).collect(Collectors.toList()))
                .privilegeList(new ArrayList<>(userPrivilegeSet))
                .build();
        return RestResult.result(RespCode.CODE_0.getValue(), null, build);
    }

    /**
     * 新增用户
     *
     * @param param
     * @return
     */
    @Override
    public RestResult<?> insert(User param,Integer isAdmin,Integer isEnabled) {
        SecurityBaseUser securityBaseUser = baseMapper.findLoginAccount(param.getLoginAccount());
        if (securityBaseUser != null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "当前账号已注册请重新输入");
        }
        Tenant tenant = tenantService.getById(TenantContextHolder.getTenantId());
        Integer count = baseMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getTenantId, TenantContextHolder.getTenantId()));
        if(tenant.getAccountCount() > 0 && count >= tenant.getAccountCount() ){
            return RestResult.result(RespCode.CODE_2.getValue(), "当前租户已超出用户创建数量限制");
        }
        param.setCredentialssalt(PassWordUtils.getSalt());
        param.setPassword(PassWordUtils.getEncryptionPassWord(param.getPassword(), param.getCredentialssalt()));
        param.setAddress(appConfig.findAddress(param.getProvinceId(), param.getCityId(), param.getAreaId()));
        param.setLoginLastTime(new Date());
        baseMapper.insert(param);
        UserSet userSet = new UserSet();
        userSet.setId(param.getId());
        userSet.setIsAdmin(isAdmin);
        userSet.setIsEnabled(isEnabled);
        userSetMapper.insertSet(userSet);
        return RestResult.result(RespCode.CODE_0);
    }

    /**
     * 修改用户
     *
     * @param param
     * @return
     */
    @Override
    @CacheEvict(value = RedisCommon.USER_INFO,key = "#param.id",allEntries = true)
    public RestResult<?> update(User param,Integer isAdmin,Integer isEnabled) {
        if (param.getId() == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户编号");
        }
        User user = baseMapper.selectById(param.getId());
        if (user == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户信息");
        }
        if(isAdmin != null || isEnabled != null){
            UserSet userSet = userSetMapper.selectById(user.getId());
            if (userSet == null) {
                userSet = new UserSet();
                userSet.setId(user.getId());
                userSet.setIsAdmin(isAdmin);
                userSet.setIsEnabled(isEnabled);
                userSetMapper.insertSet(userSet);
            } else {
                userSet.setIsAdmin(isAdmin);
                userSet.setIsEnabled(isEnabled);
                userSetMapper.updateById(userSet);
            }
        }
        param.setAddress(appConfig.findAddress(param.getProvinceId(), param.getCityId(), param.getAreaId()));
        baseMapper.updateById(param);
        return RestResult.result(RespCode.CODE_0);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @Override
    @CacheEvict(value = RedisCommon.USER_INFO,key = "#id",allEntries = true)
    public RestResult<?> remove(Long id) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户信息");
        }
        baseMapper.deleteById(user.getId());
        userSetMapper.deleteById(user.getId());
        return RestResult.result(RespCode.CODE_0);
    }


    /**
     * 根据用户Id获取用户集合
     */
    @Override
    public RestResult<?> findUserIdList(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户编号");
        }
        List<User> userList = baseMapper.selectList(new QueryWrapper<User>().in("id", idList));
        if (userList == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,userList);
    }
    /**
     * 根据角色Id集合获取用户集合
     */
    @Override
    public RestResult<?> findRoleIdList(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取角色编号");
        }
        List<User> roleIdList = baseMapper.findRoleIdList(idList);
        if (roleIdList == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,roleIdList);
    }

    @Override
    public RestResult<?> findDepartmentIdList(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取部门编号");
        }
        List<User> roleIdList = baseMapper.findDepartmentIdList(idList);
        if (roleIdList == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,roleIdList);
    }

    @Override
    public RestResult<?> findPositionIdList(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取职位编号");
        }
        List<User> roleIdList = baseMapper.findPositionIdList(idList);
        if (roleIdList == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取用户信息");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,roleIdList);
    }

}



