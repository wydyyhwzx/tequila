package com.tequila.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tequila.model.UserDO;

/**
 * Created by wangyudong on 2018/1/18.
 */
public class UserUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static ThreadLocal<UserDO> threadLocal = new ThreadLocal();

    public static void setUser(UserDO user) {
        threadLocal.set(user);
    }

    public static UserDO getUser() {
        return (UserDO)threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }

    public static ObjectNode getExtendJson(UserDO user) throws Exception{
        if (user.extendJsonVaule() != null) {
            return user.extendJsonVaule();
        }

        ObjectNode extendJson = (ObjectNode) mapper.readTree(user.getExtend());
        return extendJson;
    }
}
