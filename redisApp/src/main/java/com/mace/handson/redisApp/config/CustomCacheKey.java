package com.mace.handson.redisApp.config;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * description:
 * <br />
 * Created by mace on 14:20 2018/7/30.
 */
@Slf4j
public class CustomCacheKey implements Serializable {

    private static final long serialVersionUID = 1838981017389490745L;

    private final Object[] params;
    private final int hashCode;
    private final String className;
    private final String methodName;

    public CustomCacheKey(Object target, Method method, Object[] params){
        this.className=target.getClass().getName();
        this.methodName=getMethodName(method);
        this.params = new Object[params.length];
        System.arraycopy(params, 0, this.params, 0, params.length);
        this.hashCode=generatorHashCode();
    }

    private String getMethodName(Method method){
        StringBuilder builder = new StringBuilder(method.getName());
        Class<?>[] types = method.getParameterTypes();
        if(types.length!=0){
            builder.append("(");
            for (int i = 0; i < types.length; i++) {
                String name = types[i].getName();
                builder.append(name);
                if(i == types.length - 1)
                    continue;
                else
                    builder.append(",");
            }
//            for(Class<?> type:types){
//                String name = type.getName();
//                builder.append(name+",");
//            }
            builder.append(")");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj){
        if(this==obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomCacheKey o=(CustomCacheKey) obj;
        if(this.hashCode!=o.hashCode())
            return false;
        if(!Optional.ofNullable(o.className).orElse("").equals(this.className))
            return false;
        if(!Optional.ofNullable(o.methodName).orElse("").equals(this.methodName))
            return false;
        if (!Arrays.equals(params, o.params))
            return false;
        return true;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }

    private int generatorHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode;
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + Arrays.deepHashCode(params);
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public String toString() {
        log.debug(Arrays.deepToString(params));
        StringBuilder cacheKey = new StringBuilder();
        cacheKey
                .append(className)
                .append(":")
                .append(methodName)
                .append(":")
                .append(Arrays.deepToString(params));
//        String cacheKey = className + ":" + methodName + ":" + Arrays.deepToString(params);
        log.info("cacheKey: {}",cacheKey.toString());
        return cacheKey.toString();
//        return "BaseCacheKey [params=" + Arrays.deepToString(params)
//                + ", className=" + className
//                + ", methodName=" + methodName + "]";
    }


}
