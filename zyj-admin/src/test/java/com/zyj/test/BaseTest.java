package com.zyj.test;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class BaseTest {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
        log.info("test");
    }

    @Test
    public void testJdbcTemplate() {
        log.info("testJdbcTemplate");
        jdbcTemplate.queryForObject("select 1", Integer.class);
    }

}
