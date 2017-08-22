package com.demo.ctrl;

import com.demo.common.CommonInfo;
import com.demo.repository.SessionIdDao;
import com.demo.repository.UserDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author c
 *         注册服务的 Service
 */
@Service
public class CommonServ
{
    private final UserDao userDao;
    private final SessionIdDao sessionIdDao;

    private final Pattern phonePattern;
    private final Pattern passwordPattern;
    private final Pattern specialCharPattern;

    @Autowired
    public CommonServ(UserDao userDao, SessionIdDao sessionIdDao)
    {
        this.userDao = userDao;
        this.sessionIdDao = sessionIdDao;

        phonePattern = Pattern.compile("^1[3|4|5|8][0-9]\\d{8}$");
        passwordPattern = Pattern.compile("^[A-Za-z1-9]+$");
        specialCharPattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]");
    }

    /**
     * 插入一个用户
     */
    CommonInfo registe(String phoneNumber, String nickName, String password)
    {
        boolean addUser = userDao.addUser(phoneNumber, nickName, password);
        if (!addUser) return new CommonInfo(10016);
        return new CommonInfo(10000);
    }


    /**
     * 修改昵称
     */
    CommonInfo changeNickname(String nickname, String phonenumber)
    {
        boolean change = userDao.changeNickname(nickname, phonenumber);
        if (change) return new CommonInfo(10000);

        return new CommonInfo(11030);
    }

    /**
     * 检测注册信息的合法性
     *
     * @return 全部合法：null，否则返回相应的错误信息
     */

    CommonInfo checkRegisteInfo(String phoneNumber, String password, String passRepeat, String nickName)
    {
        //验证：手机号的合法性
        if (phoneNumber == null) return new CommonInfo(10010);
        boolean phoneIsOk = phonePattern.matcher(phoneNumber).matches();
        if (!phoneIsOk) return new CommonInfo(10010);

        //验证：手机号的唯一性
        boolean phoneexist = userDao.phoneExist(phoneNumber);
        if (phoneexist) return new CommonInfo(10011);


        //验证：密码（6-20位字母数字组合）
        if (!passwordPattern.matcher(password).matches()) return new CommonInfo(10012);
        if (password.length() < 6 || password.length() > 20) return new CommonInfo(10012);


        //验证：密码一致
        if (!password.equals(passRepeat)) return new CommonInfo(10013);

        //验证：（昵称：2-6个字符,禁止特殊字符）
        boolean specialChar = false;
        for (int i = 0; i < nickName.length(); i++)
            if (specialCharPattern.matcher(nickName.charAt(i) + "").matches())
            {
                specialChar = true;
                break;
            }
        if (specialChar) return new CommonInfo(10015);
        if (nickName.length() < 2 || nickName.length() > 6) return new CommonInfo(10015);

        //全部合法返回
        return null;
    }

    /**
     * 检测登录信息的合法性
     *
     * @return 全部合法：null，否则返回相应的错误信息
     */
    CommonInfo checkLoginInfo(String phonenumber, String password)
    {
        //验证：账号合法
        if (phonenumber == null) return new CommonInfo(11020);
        if (!phonePattern.matcher(phonenumber).matches()) return new CommonInfo(11020);

        //验证：密码合法
        if (!passwordPattern.matcher(password).matches()) return new CommonInfo(11021);

        //全部合法返回
        return null;
    }

    /**
     * 尝试登录
     *
     * @param phonenumber 账号
     * @param password    密码
     */
    private boolean tryLogin(String phonenumber, String password)
    {
        boolean existPhonePassword = userDao.existPhonePassword(phonenumber, password);
        if (existPhonePassword)
        {
            userDao.updateLoginSucess(phonenumber);
            return true;
        }

        updateLoginFail(phonenumber);

        return false;
    }

    /**
     * 更新登录失败信息
     *
     * @param phonenumber 账号
     */
    private void updateLoginFail(String phonenumber)
    {
        List<Long> failtimes = userDao.findFailtimes(phonenumber);

        if (failtimes == null) failtimes = new ArrayList<>();

        if (failtimes.size() == 5)
            failtimes.remove(0);

        failtimes.add(DateTime.now().getMillis());

        if (failtimes.size() == 5 && failtimes.get(0) > DateTime.now().minusMinutes(5).getMillis())
            userDao.setCanNotLogin(phonenumber);

        userDao.setFailTimes(failtimes, phonenumber);
    }

    /**
     * 用户登录 业务逻辑
     */
    CommonInfo login(String phonenumber, String password)
    {
        //验证：是否可以登录
        Boolean canlogin = userDao.canLogin(phonenumber);
        if (!canlogin) return new CommonInfo(11022);

        //尝试登录
        boolean login = tryLogin(phonenumber, password);
        if (login)
        {
            CommonInfo ret = new CommonInfo(10000);
            String sessionId = getSessionID(phonenumber);
            ret.setData(sessionId);
            sessionIdDao.addSession(sessionId, phonenumber);
            return ret;
        }
        //登录失败增加失败次数
        userDao.addFailcount(phonenumber);

        return new CommonInfo(11023);
    }


    /**
     * 获取sessionID
     *
     * @param phonenumber 账号
     */
    private String getSessionID(String phonenumber)
    {
        String ret;
        while (true)
        {
            ret = phonenumber + "-" + DateTime.now().getMillis() + "-" + Math.random();
            String ls = sessionIdDao.findPhoneNumberBySessionid(ret);
            if (ls == null) break;
        }
        return ret;
    }

    /**
     * 根据sessionid 获取 phonenumber
     */
    String findPhoneNumberBySessionid(String sessionid)
    {
        return sessionIdDao.findPhoneNumberBySessionid(sessionid);
    }
}
