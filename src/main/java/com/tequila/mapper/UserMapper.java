package com.tequila.mapper;

import com.tequila.model.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by wangyudong on 2018/1/10.
 */
@Component
@Mapper
public interface UserMapper {
    void insert(UserDO user);

    UserDO findById(Integer id);
}
