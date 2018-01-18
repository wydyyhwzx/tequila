package com.tequila.common;

import com.tequila.model.UserDO;

/**
 * Created by wangyudong on 2018/1/18.
 */
public class UserUtil {
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
}
