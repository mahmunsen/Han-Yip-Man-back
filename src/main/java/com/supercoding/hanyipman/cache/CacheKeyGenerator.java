package com.supercoding.hanyipman.cache;

import com.supercoding.hanyipman.security.JwtToken;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static org.springframework.util.StringUtils.arrayToDelimitedString;

public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String methodName = method.getName();

        /** OrderService - viewOrderDetail 메소드 */

        if ("viewOrderDetail".equals(methodName)) {
            String key = String.format("%s-%s-%s-%s", target.getClass().getSimpleName(), method.getName(), JwtToken.user().getId(), params[1]);
            System.out.println("viewOrderDetail key = " + key);
            return key;
        }


        // 기본적으로 메소드 이름을 사용하여 키를 생성하는 로직
        String basicKey = String.format("%s-%s-%s-%s", target.getClass().getSimpleName(), methodName, JwtToken.user().getId(), arrayToDelimitedString(params, "-"));
        System.out.println("basicKey = " + basicKey);
        return basicKey;
    }
}
