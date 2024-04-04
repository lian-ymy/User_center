package com.example.usercenter.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id  唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 登录密码
     */
    private String userPassword;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户职位 0 普通用户 1 管理员
     */
    private Integer userRole;

    /**
     * 是否删除
     */
    //逻辑删除字段：首先在yml文件中设置有关逻辑删除的对应属性，之后在这里添加注解
    @TableLogic
    private Integer isDelete;

    /**
     * 星球编号:用于校验登录注册用户是否合法
     */
    private String planetCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}