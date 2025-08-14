package com.zyj.demo.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 读取配置文件
 *
 * @author zyj
 */
public class ReadConfigTest {

    @Test
    public void test() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getResourceAsStream("/aa.yml");
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Object key : properties.keySet()) {
            System.out.println(key + " = " + properties.get(key));
        }
    }

}
