package com.zyj.demo.file;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

/**
 * Apache Commons IO 文件名操作测试
 *
 * @author zyj
 * @version 1.0.0
 */
public class FileNameTest {

    @Test
    public void fileNameTest() {
        String inputPath = "D:\\testData\\pdf\\3岁以下婴幼儿健康养育照护指南.pdf";
        int len = 24;
        System.out.println(StringUtils.rightPad("name:", len) + FilenameUtils.getName(inputPath));
        System.out.println(StringUtils.rightPad("baseName:", len) + FilenameUtils.getBaseName(inputPath));
        System.out.println(StringUtils.rightPad("extension:", len) + FilenameUtils.getExtension(inputPath));
        System.out.println(StringUtils.rightPad("prefix:", len) + FilenameUtils.getPrefix(inputPath));
        System.out.println(StringUtils.rightPad("path:", len) + FilenameUtils.getPath(inputPath));
        System.out.println(StringUtils.rightPad("pathNoEndSeparator:", len) + FilenameUtils.getPathNoEndSeparator(inputPath));
        System.out.println(StringUtils.rightPad("fullPath:", len) + FilenameUtils.getFullPath(inputPath));
        System.out.println(StringUtils.rightPad("fullPathNoEndSeparator:", len) + FilenameUtils.getFullPathNoEndSeparator(inputPath));

//        name:                   3岁以下婴幼儿健康养育照护指南.pdf
//        baseName:               3岁以下婴幼儿健康养育照护指南
//        extension:              pdf
//        prefix:                 D:\
//        path:                   testData\pdf\
//        pathNoEndSeparator:     testData\pdf
//        fullPath:               D:\testData\pdf\
//        fullPathNoEndSeparator: D:\testData\pdf
    }

    @Test
    public void pathTest() {
        String inputPath = "D:\\testData\\pdf\\3岁以下婴幼儿健康养育照护指南.pdf";
        Path path = Path.of(inputPath);
        System.out.println(path);
        System.out.println(path.getFileName());
        System.out.println(path.getParent());
        System.out.println(path.getRoot());
        System.out.println(path.getName(0));
        System.out.println(path.getName(1));

//        D:\testData\pdf\3岁以下婴幼儿健康养育照护指南.pdf
//        3岁以下婴幼儿健康养育照护指南.pdf
//        D:\testData\pdf
//        D:\
//        testData
//        pdf
    }

}
