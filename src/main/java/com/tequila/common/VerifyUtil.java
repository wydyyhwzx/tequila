package com.tequila.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangyudong on 2018/1/20.
 */
public class VerifyUtil {
    private static Logger logger = LoggerFactory.getLogger(VerifyUtil.class);

    private static Random random=new Random();
    private static final char[] chars={'2','3','4','5','6','7','8','9','Q',
            'W','E','R','T','Y','U','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'};

    private static LoadingCache<String, String> uuidToVerifyCache =
            CacheBuilder
                    .newBuilder()
                    .maximumSize(100)
                    .expireAfterWrite(CookieEnum.REGISTER_VERIFY.getExpire(), TimeUnit.SECONDS)
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) throws Exception {
                            String value = getRandomString(Constants.verifySize);
                            logger.info("[VerifyUtil] guava loaded,key:{},value:{}", key, value);
                            return value;
                        }
                    });

    public static Map<String,String> createVerifyCode()  throws ExecutionException{
        Map<String,String> verifyCode = new HashMap<>();
        verifyCode.put("uuid", getUUid());
        verifyCode.put("code", uuidToVerifyCache.get(verifyCode.get("uuid")));
        return verifyCode;
    }

    public static boolean verifyCodeCheck(String verifyCookie, String verifyCode) throws ExecutionException {
        String localCode = uuidToVerifyCache.getIfPresent(verifyCookie);
        if (null == localCode)
            return false;
        if (localCode.equalsIgnoreCase(verifyCode))
            return true;

        return false;
    }

    private static String getRandomString(int size) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<size; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    private static String getUUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
