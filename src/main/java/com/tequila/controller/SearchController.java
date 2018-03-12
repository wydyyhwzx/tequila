package com.tequila.controller;

import com.tequila.common.Constants;
import com.tequila.common.StatusCode;
import com.tequila.common.UserUtil;
import com.tequila.domain.Result;
import com.tequila.domain.WechartArticle;
import com.tequila.model.HistoryDO;
import com.tequila.model.UserDO;
import com.tequila.service.HistoryService;
import com.tequila.service.HttpService;
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
@CrossOrigin
@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private SougouWechartService sougouWechartService;
    @Resource
    private HttpService httpService;
    @Resource
    private HistoryService historyService;
    @Value("${manager.phone}")
    private String managerPhone;

    @RequestMapping(value = "/keyword/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,List<WechartArticle>>> keywordList(@RequestParam String query, @RequestParam(required = false, defaultValue = "1") int page) throws Exception {
        if (StringUtils.isBlank(query)) {
            return Result.fail(StatusCode.PARAM_ERROR.getCode(), "关键字不能为空", "关键字不能为空");
        }

        List<WechartArticle> wechartArticles = sougouWechartService.getArticleList(query, page, null, 0);
        Map<String,List<WechartArticle>> result = new HashMap<>(1);
        result.put("list", wechartArticles);
        historyService.insert(UserUtil.getUser().getId(), query);
        return Result.success(result);
    }

    @RequestMapping(value = "/keyword/info", method = RequestMethod.GET)
    @ResponseBody
    public Result keywordInfo(@RequestParam String url, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(url)) {
            return Result.fail(StatusCode.PARAM_ERROR.getCode(), "链接不能为空", "链接不能为空");
        }

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
    public Result<Boolean> setProxy(@RequestParam String ipPort, @RequestParam int type) throws Exception {
        UserDO userDO = UserUtil.getUser();
        if (null == userDO || !managerPhone.contains(userDO.getPhone())) {
            return Result.fail(StatusCode.NO_ACCESS_ERROR);
        }

        return Result.success(httpService.setProxy(ipPort, type));
    }

    @RequestMapping(value = "/reloadProxy", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reloadProxy() throws Exception {
        UserDO userDO = UserUtil.getUser();
        if (null == userDO || !managerPhone.contains(userDO.getPhone())) {
            return Result.fail(StatusCode.NO_ACCESS_ERROR);
        }

        return Result.success(httpService.reloadProxy());
    }

    @RequestMapping(value = "/setSougouHeader", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> setSougouHeader(@RequestParam String name, @RequestParam(required = false, defaultValue = "") String value) {
        UserDO userDO = UserUtil.getUser();
        if (null == userDO || !managerPhone.contains(userDO.getPhone())) {
            return Result.fail(StatusCode.NO_ACCESS_ERROR);
        }

        return Result.success(httpService.setSougouHeader(name, value));
    }

    @RequestMapping(value = "/keyword/history", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,List<HistoryDO>>> keywordHistory(@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int pageSize) throws Exception {
        if (pageSize > 100)
            pageSize = 100;

        List<HistoryDO> historyDOS = historyService.listByUserId(UserUtil.getUser().getId(), page, pageSize);
        Map<String,List<HistoryDO>> result = new HashMap<>(1);
        result.put("list", historyDOS);
        return Result.success(result);
    }

    @RequestMapping(value = "/keyword/monitor", method = RequestMethod.POST)
    @ResponseBody
    public Result keywordMonitor(@RequestParam String keyword, @RequestParam String url) throws Exception {
        if (StringUtils.isBlank(keyword)) {
            return Result.fail(StatusCode.PARAM_ERROR.getCode(), "关键字不能为空", "关键字不能为空");
        }
        if (StringUtils.isBlank(url)) {
            return Result.fail(StatusCode.PARAM_ERROR.getCode(), "文章链接不能为空", "文章链接不能为空");
        }

        return historyService.monitor(UserUtil.getUser().getId(), keyword, url);
    }
}
