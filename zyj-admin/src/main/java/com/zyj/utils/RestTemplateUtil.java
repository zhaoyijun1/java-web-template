//package com.zyj.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.*;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//
///**
// * RestTemplate 请求工具类
// *
// * @author zyj
// * @version 1.0
// */
//@Slf4j
//@Component
//public class RestTemplateUtil {
//
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//
//        return builder.requestFactory(() -> {
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.16.10.188", 8888)));
//            return requestFactory;
//        }).messageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8)).build();
//    }
//
//    private static RestTemplate restTemplate;
//
//    @Autowired
//    public void setRestTemplate(RestTemplate restTemplate) {
//        System.setProperty("http.proxyHost", "172.16.10.188");
//        System.setProperty("https.proxyHost", "172.16.10.188");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.proxyPort", "8888");
//
//        RestTemplateUtil.restTemplate = restTemplate;
//    }
//
//    /**
//     * post请求
//     *
//     * @param url    请求路径
//     * @param params 请求参数
//     * @return 返回值
//     */
//    public static String post(String url, Map<String, Object> params) {
//
//        // 设置请求头
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, requestHeaders);
//        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
//        String body = responseEntity.getBody();
//        log.info("远程调用结果，body为：{}", body);
//        return body;
//    }
//
//    /**
//     * get请求
//     *
//     * @param url    请求路径
//     * @param params 请求参数
//     * @return 返回值
//     */
//    public static String get(String url, Map<String, Object> params, Map<String, String> header) {
//
//        // 组装 url
//        if (params != null && !params.isEmpty()) {
//            url = url + "?";
//            for (String key : params.keySet()) {
//                url = url + key + "=" + params.get(key) + "&";
//            }
//            url = url.substring(0, url.length() - 1);
//        }
//
//        // 设置请求头
//        HttpHeaders requestHeaders = new HttpHeaders();
//        if (header != null && header.size() > 0) {
//            for (String key : header.keySet()) {
//                requestHeaders.add(key, header.get(key));
//            }
//        }
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, requestHeaders);
//        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
//        String body = responseEntity.getBody();
//        log.info("远程调用结果，body为：{}", body);
//        return body;
//    }
//
//}
