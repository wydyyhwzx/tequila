package com.tequila.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tequila.domain.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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
        verifyCode.put(Constants.rerifyUUIDKey, getUUid());
        verifyCode.put(Constants.rerifyCodeKey, uuidToVerifyCache.get(verifyCode.get(Constants.rerifyUUIDKey)));
        return verifyCode;
    }

    public static Result verifyCodeCheck(HttpServletRequest request, CookieEnum cookieEnum, String verifyCode) throws ExecutionException {
        Result result = ValidatorUtil.isVerifyCode(verifyCode);
        if (null != result) {
            return result;
        }
        String verifyCookie = CookieUtil.getValue(request, cookieEnum);
        if (StringUtils.isBlank(verifyCookie)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码已过期，请刷新验证码");
            return result;
        }

        String localCode = uuidToVerifyCache.getIfPresent(verifyCookie);
        if (null == localCode || !localCode.equalsIgnoreCase(verifyCode)){
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码不正确，请重新输入");
            return result;
        }

        return null;
    }

    public static String getRandomString(int size) {
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
