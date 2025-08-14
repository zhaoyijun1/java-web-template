package com.zyj.demo.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程
 *
 * @author zyj
 */
@Slf4j
public class ThreadTest {

    @Test
    public void f() {
        AtomicInteger ai = new AtomicInteger();
        Thread thread1 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                ai.getAndAdd(i);
            }
            log.info("{}：{}", Thread.currentThread().getName(), ai);
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                ai.getAndAdd(i);
            }
            log.info("{}：{}", Thread.currentThread().getName(), ai);
        });
        thread1.start();
        thread2.start();
        log.info("{}：{}", Thread.currentThread().getName(), ai);
    }

    volatile int vi = 0;

    @Test
    public void f2() {
        Thread thread1 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                vi += i;
            }
            log.info("{}：{}", Thread.currentThread().getName(), vi);
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                vi += i;
            }
            log.info("{}：{}", Thread.currentThread().getName(), vi);
        });
        thread1.start();
        thread2.start();
        log.info("{}：{}", Thread.currentThread().getName(), vi);
    }


}
