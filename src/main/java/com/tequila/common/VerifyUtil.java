package com.tequila.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tequila.domain.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
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
    // 验证码干扰线数
    private static final int lineCount = 200;
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

    public static void writeCode(OutputStream outputStream, String code, int width, int height) throws Exception{
        int x = 0, fontHeight = 0, codeY = 0;
        int red = 0, green = 0, blue = 0;

        x = width / (code.length() + 2);//每个字符的宽度(左右各空出一个字符)
        fontHeight = height - 2;//字体的高度
        codeY = height - 4;

        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体,可以修改为其它的
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
//        Font font = new Font("Times New Roman", Font.ROMAN_BASELINE, fontHeight);
        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            // 设置随机开始和结束坐标
            int xs = random.nextInt(width);//x坐标开始
            int ys = random.nextInt(height);//y坐标开始
            int xe = xs + random.nextInt(width / 8);//x坐标结束
            int ye = ys + random.nextInt(height / 8);//y坐标结束

            // 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }

        for (int i = 0; i < code.length(); i++) {
            String item = String.valueOf(code.charAt(i));
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawString(item, (i + 1) * x, codeY);
        }
        ImageIO.write(buffImg, "png", outputStream);
        outputStream.close();
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
