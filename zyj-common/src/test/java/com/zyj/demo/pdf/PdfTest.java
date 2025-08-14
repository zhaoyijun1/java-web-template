package com.zyj.demo.pdf;

import com.zyj.utils.PdfboxUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * PDF 文件操作
 *
 * @author zyj
 * @version 1.0.0
 */
@Slf4j
public class PdfTest {

    /**
     * 拆分PDF文件
     */
    @Test
    public void splitPDFTest1() throws Exception {
        String inputPath = "D:\\testData\\pdf\\3岁以下婴幼儿健康养育照护指南.pdf";
        String outputPath = FilenameUtils.getFullPath(inputPath) + "测试.pdf";
        PdfboxUtil.splitPDF(inputPath, outputPath, 4, 1);
    }

    /**
     * 拆分PDF文件
     */
    @Test
    public void splitPDFTest2() throws Exception {
        String inputPath = "D:\\testData\\pdf\\3岁以下婴幼儿健康养育照护指南.pdf";
        String outputPath = FilenameUtils.getFullPath(inputPath) + "测试.pdf";
        PdfboxUtil.splitPDF(inputPath, outputPath, List.of(new int[]{0, 2}, new int[]{2, 6}, new int[]{7, 8}));
    }

    /**
     * 合并PDF文件
     */
    @Test
    public void mergePDFTest1() throws Exception {
        String inputDir = "D:\\testData\\pdf\\测试";
        String mergePath = inputDir + File.separator + "合并.pdf";
        PdfboxUtil.mergePDFs(inputDir, mergePath);
    }

    /**
     * 合并PDF文件
     */
    @Test
    public void mergePDFTest2() throws Exception {
        String inputPath1 = "D:\\testData\\pdf\\测试\\测试_1.pdf";
        String inputPath2 = "D:\\testData\\pdf\\测试\\测试_2.pdf";
        String inputPath3 = "D:\\testData\\pdf\\测试\\测试_3.pdf";
        String mergePath = FilenameUtils.getFullPath(inputPath1) + "合并.pdf";
        PdfboxUtil.mergePDFs(List.of(new File(inputPath1), new File(inputPath2), new File(inputPath3)), mergePath);
    }

    /**
     * 更新PDF文件页码
     */
    @Test
    public void updatePageNumberTest() throws Exception {
        String inputPath = "D:\\testData\\pdf\\测试\\合并.pdf";
        String outputPath = inputPath.replace(".pdf", "_更新页码.pdf");

        PdfboxUtil.updatePageNumbers(inputPath, outputPath);
        Files.move(Path.of(outputPath), Path.of(inputPath), StandardCopyOption.REPLACE_EXISTING);
    }

}
