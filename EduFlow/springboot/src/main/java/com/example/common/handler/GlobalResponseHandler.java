package com.example.common.handler;

import com.example.common.Result;
import com.example.common.utils.GlobalTranslationUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Map;

/**
 * 全局响应处理器 - 自动翻译所有API响应中的中文
 */
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 对所有返回值进行处理
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 只处理JSON响应
        if (body instanceof Result) {
            Result result = (Result) body;

            // 翻译Result对象中的所有中文
            if (result.getData() != null) {
                result.setData(translateJsonObject(result.getData()));
            }

            return result;
        }

        // 对于其他类型的响应，也进行翻译
        return translateJsonObject(body);
    }

    /**
     * 递归翻译JSON对象中的所有中文字符串
     */
    private Object translateJsonObject(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            return GlobalTranslationUtil.translate((String) obj);
        }

        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                entry.setValue(translateJsonObject(entry.getValue()));
            }
            return map;
        }

        if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, translateJsonObject(list.get(i)));
            }
            return list;
        }

        // 对于其他对象类型，使用反射处理
        try {
            Class<?> clazz = obj.getClass();
            if (clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.")) {
                return obj; // 跳过Java内置类型
            }

            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    field.set(obj, translateJsonObject(value));
                }
            }
        } catch (Exception e) {
            // 静默处理异常
        }

        return obj;
    }
}