package com.zyj.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF 文件操作工具类
 *
 * @author zyj
 * @version 1.0.0
 */
public class PdfUtil {

    /**
     * 页码信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageNumberInfo {
        public Integer pageIndex; // 页码，从1开始
        public String text;
        public Float x;
        public Float y;
        public Float width;
        public Float height;
    }

    /**
     * 更新PDF文件页码
     *
     * <p>1.识别PDF文件中原有的页码
     * 2.使用矩形覆盖原有页码
     * 3.添加新的页码</p>
     *
     * @param inputPath  输入PDF文件路径
     * @param outputPath 输出PDF文件路径
     */
    public static void updatePageNumbers(String inputPath, String outputPath) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(inputPath))) {
            int totalPages = document.getNumberOfPages();

            // 创建新文档用于保存修改后的内容
            PDDocument newDocument = new PDDocument();

            // 从PDF文档中提取原有的页码信息
            List<PageNumberInfo> ps = extractPageNumbers(document);
            PageNumberInfo pos = new PageNumberInfo();
            if (!ps.isEmpty()) {
                pos = ps.get(0);
            }

            // 获取resources目录下的字体
            PDFont font = PDType0Font.load(document, PdfUtil.class.getResourceAsStream("/fonts/simhei.ttf"));

            for (int i = 0; i < totalPages; i++) {
                // 获取当前页
                PDPage page = document.getPage(i);
                newDocument.addPage(page);

                // 添加页码
                addPageNumber(newDocument, page, pos, font, i + 1, totalPages);
            }

            // 保存新文档
            newDocument.save(outputPath);
            newDocument.close();
        }
    }

    /**
     * 在页面底部添加页码
     *
     * @param document    PDF文档
     * @param page        当前页面
     * @param pos         页码位置
     * @param font        页码字体
     * @param currentPage 当前页码
     * @param totalPages  总页数
     */
    private static void addPageNumber(PDDocument document, PDPage page, PageNumberInfo pos, PDFont font, int currentPage, int totalPages)
            throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true)) {

            // 计算覆盖区域（基于原页码位置和估计的文本宽度）
            contentStream.setNonStrokingColor(Color.WHITE);
            contentStream.addRect(pos.getX() - 5, pos.getY() - 5, 90, pos.getHeight() + 10);
            contentStream.fill();

            // 获取页面尺寸
            PDRectangle pageSize = page.getMediaBox();
            float pageWidth = pageSize.getWidth();

            // 设置字体和字号
            float fontSize = 10;
            contentStream.setFont(font, fontSize);
            // 设置文本颜色
            contentStream.setNonStrokingColor(0.2f, 0.2f, 0.2f);
            // 页码文本
            String pageText = String.format("第 %d / %d 页", currentPage, totalPages);

            // 计算文本宽度以居中显示
            float textWidth = font.getStringWidth(pageText) / 1000 * fontSize;
            float startX = (pageWidth - textWidth) / 2;
            float startY = 30; // 距离底部30个单位
            if (pos.getX() != null && pos.getY() != null) {
                startY = pos.getY();
            }

            // 添加页码文本
            contentStream.beginText();
            contentStream.newLineAtOffset(startX, startY);
            contentStream.showText(pageText);
            contentStream.endText();
        }
    }

    /**
     * 从PDF文件中提取页码信息
     *
     * <p>仅支持提取页面底部的页码，支持的页码格式：</p>
     * <p>第1页 共21页；第1-2页；第1/2页；第1页；1/2；-1-</p>
     *
     * @param document PDF文档
     */
    public static List<PageNumberInfo> extractPageNumbers(PDDocument document) throws IOException {

        List<PageNumberInfo> results = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) {

                // 只处理位于页面底部区域内的文本
                float pageHeight = getCurrentPage().getMediaBox().getHeight();
                float footerStartY = pageHeight * 0.92f;

                // 更全面的页码正则表达式
                Pattern pageNumPattern = Pattern.compile(
                        "\\b第\\s?(\\d+)\\s?页\\s?共\\s?(\\d+)\\s?页\\b|" +        // 第1页 共21页
                                "\\b第\\s?(\\d+)\\s?[/-]\\s?(\\d+)\\s?页\\b|" +   // 第1-2页
                                "\\b第\\s?(\\d+)\\s?页\\b|" +                     // 第1页
                                "\\b(\\d+)\\s?[/-]\\s?(\\d+)\\b|" +              // 1/2, 1-2
                                "\\b-\\s?(\\d+)\\s?-\\b|" +                      // - 1 -
                                "\\b(\\d+)\\b"                                   // 简单数字
                );
                Matcher matcher = pageNumPattern.matcher(text);
                if (matcher.find()) {
                    // 获取匹配的页码文本
                    String matchedText = matcher.group();
                    // 检查文本位置是否在页脚区域
                    for (TextPosition pos : textPositions) {
                        if (pos.getY() > footerStartY) {
                            results.add(new PageNumberInfo(
                                    getCurrentPageNo() + 1,
                                    matchedText,
                                    pos.getTextMatrix().getTranslateX(),
                                    pos.getTextMatrix().getTranslateY(),
                                    pos.getWidth(),
                                    pos.getHeight()));
                            break;
                        }
                    }
                }
            }
        };

        stripper.setSortByPosition(true);  // 按位置排序有助于区域分析
        stripper.getText(document);
        return results;
    }

}
