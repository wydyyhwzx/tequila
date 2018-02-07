package com.tequila.controller;

import com.tequila.common.Constants;
import com.tequila.common.StatusCode;
import com.tequila.common.UserUtil;
import com.tequila.domain.Result;
import com.tequila.domain.WechartArticle;
import com.tequila.model.UserDO;
import com.tequila.service.SougouWechartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyudong on 2018/2/6.
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private SougouWechartService sougouWechartService;
    @Value("${manager.phone}")
    private String managerPhone;

    @RequestMapping(value = "/keyword/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,List<WechartArticle>>> keywordlist(@RequestParam String query, @RequestParam(required = false, defaultValue = "1") int page) throws Exception {
        List<WechartArticle> wechartArticles = sougouWechartService.getArticleList(query, page, null, 0);
        Map<String,List<WechartArticle>> result = new HashMap<>(1);
        result.put("list", wechartArticles);
        return Result.success(result);
    }

    @RequestMapping(value = "/keyword/info", method = RequestMethod.GET)
    @ResponseBody
    public Result keywordInfo(@RequestParam String url, HttpServletResponse response) throws Exception {
        String articleHtml = sougouWechartService.getArticle(url, 0);
        if (StringUtils.isBlank(articleHtml)) {
            Result result = Result.fail(StatusCode.SYSTEM_ERROR);
            result.setDescription("获取详情失败，请稍后在试");
            return result;
        }

        response.setContentType(Constants.CONTENT_TYPE_HTML);
        ServletOutputStream out = response.getOutputStream();
        out.write(articleHtml.getBytes(Constants.UTF8));
        out.flush();
        return null;
    }

    @RequestMapping(value = "/setProxy", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> setProxy(@RequestParam String ip, @RequestParam int port, @RequestParam int type) throws Exception {
        UserDO userDO = UserUtil.getUser();
        if (null == userDO || !managerPhone.contains(userDO.getPhone())) {
            return Result.fail(StatusCode.NO_ACCESS_ERROR);
        }

        return Result.success(sougouWechartService.setProxy(ip, port, type));
    }
}
