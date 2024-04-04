package com.example.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.common.ResultUtils;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.model.User;
import com.example.usercenter.service.UserService;
import com.example.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.usercenter.constant.UserConstant.SUCCESS_CODE;
import static com.example.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author lian
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-03-24 10:31:08
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    //对数据库的操作可以通过接口实现类进行实现，也可以通过mapper中的方法进行实现
    @Resource
    UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "ymy";

    /**
     * 用户注册
     *
     * @param userAccount   账户
     * @param userPassword  密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求参数为空");
        }
        if (userAccount.length() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名长度小于三位！");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于八位！");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不符合要求！");
        }
        //判断账户是否包含特殊字符,如果包含特殊字符就返回-1
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(userAccount);
        if (m.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户包含特殊字符！");
        }
        //密码与校验密码必须相同
        //字符串的比较坚决不能使用==进行比较，必须使用equals进行比较
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入密码与校验密码不同！");
        }
        //账户不能重复
        //由于这一步是对数据库进行查询，比较耗时间，所以如果后面有步骤不满足条件可以直接返回，这一步可以放到后面
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.REPEATED_USER,"用户账号重复！");
        }
        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.REPEATED_USER,"星球编号重复！");
        }
        //加密，将密码经过MD5单向处理之后保存到数据库中
        //定义盐值，对真实密码起到混淆的作用
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //向数据库中插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库插入数据失败！");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"输入参数为空！");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入参数有误！");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入参数有误！");
        }
        //判断账户是否包含特殊字符,如果包含特殊字符就返回-1
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(userAccount);
        if (m.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户名包含特殊字符！");
        }

        //2、查询用户信息
        //对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询数据库中是否有对应登录用户的信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        //这里一定要注意，比较的密码应该是加密之后的密码，比较普通的密码是不行的
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            //如果这里发生了错误，使用日志打印错误信息，同时日志最好使用英文
            //方便其他应用对报错信息进行有效检索
            log.info("User login failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询不到对应用户信息！");
        }

        //3、用户脱敏，返回给前端的数据一定是要经过脱敏后的，不能直接将密码返回回去
        User safetyUser = getSafetyUser(user);

        //4、记录用户登录态
        //这里获取对应会话，之后在对应的session对象中设置属性值
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        //返回脱敏后的用户信息，用于在网页中展示出来
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser 一开始的原始用户数据
     * @return 返回脱敏后的用户数据
     */
    @Override
    public User getSafetyUser(User originUser) {
        //每次接受一个参数都要判断它对应的是否合法，如果不合法需要直接返回
        if (originUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"输入参数为空！");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(0);
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        return safetyUser;
    }

    //我们设置用户登录时是通过给用户登录态设置一个特定的键值对完成的，这里我们只需要移除这个用户登录态即可完成注销
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return SUCCESS_CODE;
    }
}