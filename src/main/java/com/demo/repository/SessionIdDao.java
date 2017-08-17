package com.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author c
 *         <p>
 *         会话ID
 */
@Repository
public class SessionIdDao
{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionIdDao(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     *添加一条session
     */
    public void addSession(String sessionid, String phonenumber)
    {
        jdbcTemplate.update("INSERT INTO sessionid (sessionid,phonenumber) VALUES (?,?)", sessionid, phonenumber);
    }

    /**
     * 根據sessionid獲取電話號碼
     */
    public String findPhoneNumberBySessionid(String sessionid)
    {
        return jdbcTemplate.queryForObject("SELECT phonenumber FROM sessionid WHERE sessionid=?", String.class, sessionid);
    }
}
