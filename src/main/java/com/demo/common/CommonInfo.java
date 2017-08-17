package com.demo.common;

import lombok.Data;

/**
 * @author c
 *         用于前后端交互的基础信息类
 */
@Data
public class CommonInfo
{
    private int code;                   //业务码
    private String message;             //描述信息
    private Object data;                //逻辑数据

    public CommonInfo(int code)
    {
        this.code = code;
    }
}
