package com.tequila.controller;

import com.tequila.domain.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangyudong on 2018/1/9.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/test")
    @ResponseBody
    public Result<String> test(@RequestParam(required = false) String name) {
        if (StringUtils.isBlank(name))
            return Result.fail(1, "name null", "名称不能为空");

        logger.info("user name is {}", name);

        return Result.success(name);
    }
}
