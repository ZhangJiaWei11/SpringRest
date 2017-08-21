package com.demo.repository;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author c
 */
@Log4j
@Repository
public class UserDao
{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbkcTemplate)
    {

        this.jdbcTemplate=jdbkcTemplate;
    }

    /**
     * 添加一个用户
     *
     * @param password    密码
     * @param nickName    昵称
     * @param phoneNumber 账号
     */
    public boolean addUser(String phoneNumber, String nickName, String password)
    {
        int rowschange = jdbcTemplate.update("INSERT INTO user (phonenumber,nickname,password) VALUES (?,?,?)", phoneNumber, nickName, password);
        return rowschange == 1;
    }

    /**
     * 修改昵称
     *
     * @param nickname 新昵称
     * @param phonenum 账号
     */
    public boolean changeNickname(String nickname, String phonenum)
    {
        int rowschange = jdbcTemplate.update("UPDATE user SET nickname=? WHERE phonenumber=?", nickname, phonenum);
        return rowschange == 1;
    }

    /**
     * 账号是否存在
     *
     * @param phoneNumber 账号
     */
    public boolean phoneExist(String phoneNumber)
    {
        Integer counts = jdbcTemplate.queryForObject("SELECT count(*) AS c FROM user WHERE phonenumber=?", new Object[]{phoneNumber}, Integer.class);
        return counts > 0;
    }

    /**
     * 账号密码是否存在
     *
     * @param phonenumber 账号
     * @param password    密码
     */
    public boolean existPhonePassword(String phonenumber, String password)
    {
        Integer phoneCount = jdbcTemplate.queryForObject("SELECT count(*) FROM user WHERE phonenumber=? AND  password=?", Integer.class, phonenumber, password);
        return phoneCount == 1;
    }

    /**
     * 登录成功更新信息
     *
     * @param phonenumber 登录的手机号
     */
    public void updateLoginSucess(String phonenumber)
    {
        jdbcTemplate.update("UPDATE user SET canlogin=1,failtime='' WHERE phonenumber=?", phonenumber);
    }

    /**
     * 是否可以登录
     *
     * @param phonenumber 账号
     */
    public boolean canLogin(String phonenumber)
    {
        Integer phonenumberCount = jdbcTemplate.queryForObject("SELECT COUNT(*)  AS c FROM user WHERE phonenumber=?", Integer.class, phonenumber);
        if(phonenumberCount==0) return false;

        Boolean ret = jdbcTemplate.queryForObject("SELECT canlogin FROM user WHERE phonenumber=?", Boolean.class, phonenumber);
        if (ret) return true;

        List<Long> failtimesList = findFailtimes(phonenumber);
        return failtimesList.get(failtimesList.size() - 1) < DateTime.now().minusMinutes(10).getMillis();
    }

    /**
     * 获取账号的登录失败时间
     *
     * @param phonenumber 账号
     */
    public List<Long> findFailtimes(String phonenumber)
    {
        String failtimes = jdbcTemplate.queryForObject("SELECT failtime FROM user WHERE phonenumber=?", String.class, phonenumber);

        return JSON.parseArray(failtimes, Long.class);
    }

    /**
     * 设置账号不能登录
     */
    public void setCanNotLogin(String phonenumber)
    {
        jdbcTemplate.update("UPDATE user SET canlogin=0 WHERE phonenumber=?", phonenumber);
    }

    /**
     * 设置账号的登陆失败时间
     *
     * @param failtimes   失败时间
     * @param phonenumber 账号
     */
    public void setFailTimes(List<Long> failtimes, String phonenumber)
    {
        jdbcTemplate.update("UPDATE user SET failtime=? WHERE phonenumber=?", JSON.toJSONString(failtimes), phonenumber);
    }

    /**
     * 增加登录失败的次数
     */
    public void addFailcount(String phonenumber)
    {
        jdbcTemplate.update("UPDATE user SET failcount =failcount+1 WHERE phonenumber=?", phonenumber);
    }

}
