package cn.zwz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域配置类
 * 解决前端http://localhost:8080访问后端http://localhost:8082接口的跨域问题
 * 适配Spring Boot 2.x/3.x版本
 */
@Configuration // 标记为Spring配置类，启动时自动加载
public class CorsConfig {

    /**
     * 注入跨域过滤器Bean，全局生效
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 创建跨域核心配置对象
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ========== 核心跨域配置 ==========
        // 允许前端访问的域名（开发环境：Vue运行的8080端口）
        // 生产环境替换为实际前端域名，如：https://xxx.com
        corsConfig.addAllowedOrigin("http://localhost:8080");
        // 兼容Spring Boot 2.4+版本（可选，防止部分浏览器兼容问题）
        corsConfig.addAllowedOriginPattern("*");

        // 允许携带Cookie（验证码Redis存储、登录态等需要）
        corsConfig.setAllowCredentials(true);

        // 允许所有HTTP请求方法（GET/POST/PUT/DELETE/OPTIONS等）
        corsConfig.addAllowedMethod(CorsConfiguration.ALL);

        // 允许所有请求头（token、Content-Type、自定义头等）
        corsConfig.addAllowedHeader(CorsConfiguration.ALL);

        // 预检请求（OPTIONS）的缓存时间，单位秒（减少OPTIONS请求次数）
        corsConfig.setMaxAge(3600L);

        // ========== 配置跨域路径 ==========
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口生效（/** 表示匹配所有路径）
        source.registerCorsConfiguration("/**", corsConfig);

        // 2. 返回跨域过滤器
        return new CorsFilter(source);
    }
}//package cn.zwz.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
///**
// * 全局跨域配置类
// * 允许前端http://localhost:8080访问后端接口
// */
//@Configuration // 标记为配置类，SpringBoot会自动加载
//public class CorsConfig {
//
//    @Bean // 将CorsFilter注入Spring容器
//    public CorsFilter corsFilter() {
//        // 1. 创建跨域配置对象
//        CorsConfiguration corsConfig = new CorsConfiguration();
//
//        // 允许前端地址（你的Vue项目运行地址）
//        corsConfig.addAllowedOrigin("http://localhost:8080");
//        // 允许携带Cookie（登录态、验证码等需要）
//        corsConfig.setAllowCredentials(true);
//        // 允许所有请求方法（GET/POST/PUT/DELETE等）
//        corsConfig.addAllowedMethod(CorsConfiguration.ALL);
//        // 允许所有请求头（token、content-type等）
//        corsConfig.addAllowedHeader(CorsConfiguration.ALL);
//
//        // 2. 配置跨域路径（所有接口都允许跨域）
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig); // /** 表示所有接口
//
//        // 3. 返回跨域过滤器
//        return new CorsFilter(source);
//    }
//}