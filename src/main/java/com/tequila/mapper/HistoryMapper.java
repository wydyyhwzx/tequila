package com.tequila.mapper;

import com.tequila.model.HistoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangyudong on 2018/3/12.
 */
@Component
@Mapper
public interface HistoryMapper {
    void insert(HistoryDO history);

    void update(HistoryDO history);

    HistoryDO findByUserIdAndKeyword(@Param("userId")Integer userId, @Param("keyword")String keyword);

    List<HistoryDO> listByUserId(@Param("userId")Integer userId, @Param("startRow")Integer startRow, @Param("pageSize")Integer pageSize);
}
