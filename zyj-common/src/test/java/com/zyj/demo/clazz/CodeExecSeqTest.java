package com.zyj.demo.clazz;

/**
 * 类代码执行顺序（从类加载到对象创建）
 *
 * @author zyj
 */
public class CodeExecSeqTest {

    static class Parent {
        // 静态变量
        static String parentStaticVar = initStaticVar("父类静态变量");

        // 静态代码块
        static {
            System.out.println("父类静态代码块");
        }

        // 实例变量
        String parentInstanceVar = initInstanceVar("父类实例变量");

        // 实例代码块
        {
            System.out.println("父类实例代码块");
        }

        // 构造函数
        Parent() {
            System.out.println("父类构造函数");
        }

        // 辅助方法
        static String initStaticVar(String msg) {
            System.out.println(msg);
            return msg;
        }

        String initInstanceVar(String msg) {
            System.out.println(msg);
            return msg;
        }
    }

    static class Child extends Parent {
        // 静态变量
        static String childStaticVar = initStaticVar("子类静态变量");

        // 静态代码块
        static {
            System.out.println("子类静态代码块");
        }

        // 实例变量
        String childInstanceVar = initInstanceVar("子类实例变量");

        // 实例代码块
        {
            System.out.println("子类实例代码块");
        }

        // 构造函数
        Child() {
            System.out.println("子类构造函数");
        }
    }

    public static class ExecutionOrder {

        public static void main(String[] args) {
            System.out.println("--- 第一次创建对象 ---");
            new Child();

            System.out.println("\n--- 第二次创建对象 ---");
            new Child();
        }
    }

}
