package com.demo.ctrl;

import com.demo.common.CommonInfo;
import com.demo.common.CommonUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author c
 *         注册服务的 Controller
 */
@Log4j
@RestController
public class CommonCtrl
{
    private final CommonServ commonServ;

    @Autowired
    public CommonCtrl(CommonServ commonServ)
    {
        this.commonServ = commonServ;
    }

    /**
     * 用户注册
     *
     * @param phonenumber 手机号
     * @param password    密码
     * @param passrepeat  密码重复
     * @param nickname    昵称
     */
    @RequestMapping("/registe")
    public CommonInfo registe(String phonenumber, String password, String passrepeat,
                              String nickname)
            {
        CommonInfo ret = commonServ.checkRegisteInfo(phonenumber, password, passrepeat, nickname);
        if (ret != null) return ret;

        return commonServ.registe(phonenumber, nickname, password);
    }

    /**
     * 用户登录
     *
     * @param password    密码
     * @param phonenumber 手机号
     */
    @RequestMapping("/login")
    public CommonInfo login(String phonenumber, String password)
    {
        CommonInfo ret = commonServ.checkLoginInfo(phonenumber, password);
        if (ret != null) return ret;

        return commonServ.login(phonenumber, password);
    }

    /**
     * 用户修改昵称
     *
     * @param sessionID   会话标识
     * @param newNickname 新的昵称
     */
    @RequestMapping("/changenickname")
    public CommonInfo changenickname(String newNickname, String sessionID)
    {
        if (sessionID == null) return new CommonInfo(10001);

        String phonenumber = commonServ.findPhoneNumberBySessionid(sessionID);
        if (phonenumber == null) return new CommonInfo(10001);

        return commonServ.changeNickname(newNickname, phonenumber);
    }
}
