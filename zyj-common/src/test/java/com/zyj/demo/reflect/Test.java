package com.zyj.demo.reflect;

import java.lang.reflect.Field;

/**
 * 反射
 *
 * @author zyj
 */
public class Test {

    public static void main(String[] args) {
        Class<?> clazz = Test.class;
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String comment = getFieldComment(field);
            if (comment != null) {
                System.out.println("Field: " + field.getName());
                System.out.println("Comment: " + comment);
            }
        }
    }

    private static String getFieldComment(Field field) {
        return "comment";
    }

}
