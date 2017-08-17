package com.demo.Test;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @author c
 */
public class Test01
{
    @Test
    public void t01()
    {
        System.err.println(Pattern.compile("[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]").matcher("!").matches());
    }
}
