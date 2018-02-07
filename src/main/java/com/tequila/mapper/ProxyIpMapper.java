package com.tequila.mapper;

import com.tequila.model.ProxyIpDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangyudong on 2018/2/7.
 */
@Component
@Mapper
public interface ProxyIpMapper {
    void insert(ProxyIpDO proxyIp);

    void delete(ProxyIpDO proxyIp);

    List<ProxyIpDO> all();
}
