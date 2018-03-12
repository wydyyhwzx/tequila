package com.tequila.service;

import com.tequila.common.MonitorType;
import com.tequila.common.StatusCode;
import com.tequila.domain.Result;
import com.tequila.mapper.HistoryMapper;
import com.tequila.model.HistoryDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyudong on 2018/3/12.
 */
@Component
public class HistoryService {
    private static Logger logger = LoggerFactory.getLogger(HistoryService.class);

    @Autowired
    private HistoryMapper historyMapper;

    public void insert(Integer userId, String keyword) {
        try {
            HistoryDO historyDO = historyMapper.findByUserIdAndKeyword(userId, keyword);
            if (null != historyDO) {
                HistoryDO newHistoryDO = new HistoryDO();
                newHistoryDO.setId(historyDO.getId());
                historyMapper.update(newHistoryDO);
                return;
            }

            historyDO = new HistoryDO();
            historyDO.setUserId(userId);
            historyDO.setKeyword(keyword);
            historyDO.setMonitor(MonitorType.NO.getCode());
            historyDO.setExtend("{}");
            historyMapper.insert(historyDO);
        } catch (Exception e) {
            logger.error("[HistoryService] insert err. userId:" + userId + ",keyword:" + keyword, e);
        }
    }

    public List<HistoryDO> listByUserId(Integer userId, int pageNum, int pageSize) {
        Integer startRow = (pageNum - 1) * pageSize;
        List<HistoryDO> result = null;

        try {
            result = historyMapper.listByUserId(userId, startRow, pageSize);
        } catch (Exception e) {
            logger.error("[HistoryService] listByUserId err. userId:" + userId + ",startRow:" + startRow + ",pageSize:" + pageSize, e);
        }

        if (null == result || result.size() < 1) {
            return new ArrayList<>(0);
        }

        return result;
    }

    public Result monitor(Integer userId, String keyword, String url) {
        try {
            HistoryDO historyDO = historyMapper.findByUserIdAndKeyword(userId, keyword);
            if (null != historyDO) {
                HistoryDO newHistoryDO = new HistoryDO();
                newHistoryDO.setId(historyDO.getId());
                newHistoryDO.setUrl(url);
                newHistoryDO.setMonitor(MonitorType.YES.getCode());
                historyMapper.update(newHistoryDO);
            } else {
                historyDO = new HistoryDO();
                historyDO.setUserId(userId);
                historyDO.setKeyword(keyword);
                historyDO.setUrl(url);
                historyDO.setMonitor(MonitorType.YES.getCode());
                historyDO.setExtend("{}");
                historyMapper.insert(historyDO);
            }

            return Result.success();
        } catch (Exception e) {
            logger.error("[HistoryService] monitor err. userId:" + userId + ",keyword:" + keyword + ",url:" + url, e);
        }

        return Result.fail(StatusCode.SYSTEM_ERROR);
    }
}
