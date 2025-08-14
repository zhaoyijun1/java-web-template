package com.zyj.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * PDF文件操作工具类（org.apache.pdfbox）
 *
 * @author zyj
 * @version 1.0.0
 */
public class PdfboxUtil {

    /**
     * 拆分PDF文件
     *
     * <p>
     * <div>outputPath: D:\pdf\测试.pdf</div>
     * <div>拆分后的文件路径：D:\pdf\测试\测试_{起始页}-{结束页}.pdf</div>
     * </p>
     *
     * @param inputPath     输入PDF文件路径
     * @param outputPath    输出PDF文件路径
     * @param pageLimit     每个PDF文件的页数
     * @param splitNameMode 拆分文件命名模式 0: 输出文件名_{起始页}-{结束页}.pdf； 1: 输出文件名_{序号}.pdf
     */
    public static void splitPDF(String inputPath, String outputPath, int pageLimit, int splitNameMode)
            throws IOException {
        String dir = outputPath.replace(".pdf", "");

        // 加载 PDF 文档
        PDDocument document = Loader.loadPDF(new File(inputPath));

        // 实例化 Splitter 类
        Splitter splitter = new Splitter();

        // 拆分 PDF 文档的页面
        List<PDDocument> pages = splitter.split(document);

        // 按页数，逐个拆分
        for (int i = 0; i < pages.size(); i += pageLimit) {

            // 合并页码范围内的文档
            PDDocument mergedDoc = new PDDocument();
            int maxPage = Math.min(i + pageLimit, pages.size());
            for (int j = i; j < maxPage; j++) {
                mergedDoc.addPage(pages.get(j).getPage(0));
            }

            // 创建文件夹
            if (i == 0) {
                File file = new File(dir);
                if (!file.exists()) {
                    boolean b = file.mkdirs();
                    if (!b) {
                        throw new IOException("创建文件夹失败");
                    }
                }
            }
            // 保存文档
            String baseName = FilenameUtils.getBaseName(outputPath);
            String extension = FilenameUtils.getExtension(outputPath);
            String fileName = dir + File.separator + baseName + "_" + (i + 1) + "-" + (Math.min(i + pageLimit, pages.size())) + "." + extension;
            if (splitNameMode == 1) {
                fileName = dir + File.separator + baseName + "_" + (i / pageLimit + 1) + "." + extension;
            }
            mergedDoc.save(fileName);
            mergedDoc.close();
        }

        document.close();
    }

    /**
     * 拆分PDF文件
     * <p>
     * <div>outputPath: D:\pdf\测试.pdf</div>
     * <div>拆分后的文件路径：D:\pdf\测试\测试_{起始页}-{结束页}.pdf</div>
     * <br/>
     * 示例：PdfboxUtil.splitPDF(inputPath, outputPath, List.of(new int[]{0, 1}, new int[]{2, 6}, new int[]{7, 10}));
     * </p>
     *
     * @param inputPath  输入PDF文件路径
     * @param outputPath 输出PDF文件路径
     * @param pageRange  页码范围列表，包含起始页和结束页 [{起始页：0开始, 结束页}, ...]
     */
    public static void splitPDF(String inputPath, String outputPath, List<int[]> pageRange)
            throws IOException, IllegalArgumentException {
        String dir = outputPath.replace(".pdf", "");

        // 加载 PDF 文档
        PDDocument document = Loader.loadPDF(new File(inputPath));

        // 实例化 Splitter 类
        Splitter splitter = new Splitter();

        // 拆分 PDF 文档的页面
        List<PDDocument> pages = splitter.split(document);

        // 按页码范围列表，逐个拆分
        for (int i = 0; i < pageRange.size(); i++) {
            int[] range = pageRange.get(i);
            // 起始页和结束页
            int startPage = range[0];
            int endPage = range[1];

            // 检查页码范围
            if (startPage < 0 || startPage > pages.size() - 1) {
                throw new IllegalArgumentException("起始页码超出范围");
            }
            if (startPage > endPage) {
                throw new IllegalArgumentException("起始页码不能大于结束页码");
            }
            if (endPage > pages.size() - 1) {
                endPage = pages.size() - 1;
            }

            // 合并页码范围内的文档
            PDDocument mergedDoc = new PDDocument();
            for (int j = startPage; j <= Math.min(pages.size() - 1, endPage); j++) {
                mergedDoc.addPage(pages.get(j).getPage(0));
            }

            // 创建文件夹
            if (i == 0) {
                File file = new File(dir);
                if (!file.exists()) {
                    boolean b = file.mkdirs();
                    if (!b) {
                        throw new IOException("创建文件夹失败");
                    }
                }
            }

            // 保存文档
            String baseName = FilenameUtils.getBaseName(outputPath);
            String extension = FilenameUtils.getExtension(outputPath);
            String fileName = dir + File.separator + baseName + "_" + (startPage + 1) + "-" + (Math.min(endPage + 1, pages.size())) + "." + extension;
            mergedDoc.save(fileName);
            mergedDoc.close();
        }
        document.close();
    }

    /**
     * 合并多个 PDF 文件
     *
     * <p>合并 inputDir 目录下的所有 PDF 文件，按文件名排序，保存到 mergePath</p>
     *
     * @param inputDir  输入PDF文件所在目录
     * @param mergePath 合并后的PDF文件路径
     */
    public static void mergePDFs(String inputDir, String mergePath) throws IOException {
        try (Stream<Path> inputFiles = Files.list(Path.of(inputDir))) {
            List<File> files = inputFiles.filter(path -> path.toString().endsWith(".pdf"))
                    .sorted(Comparator.comparing(Path::getFileName))
                    .map(Path::toFile).toList();
            mergePDFs(files, mergePath);
        }
    }

    /**
     * 合并多个 PDF 文件
     *
     * @param inputFiles 输入PDF文件
     * @param mergePath  合并后的PDF文件路径
     */
    public static void mergePDFs(List<File> inputFiles, String mergePath) throws IOException {
        // 实例化 PDFMergerUtility 类
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        // 添加源文件
        for (File pdf : inputFiles) {
            pdfMerger.addSource(pdf);
        }

        // 设置目标文件
        pdfMerger.setDestinationFileName(mergePath);

        // 合并两个文档 -- 混合模式（300MB内存限制）
        pdfMerger.mergeDocuments(MemoryUsageSetting.setupMixed(300 * 1024 * 1024).streamCache);
    }

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
     * <div>1. 识别PDF文件中原有的页码</div>
     * <div>2. 使用矩形覆盖原有页码</div>
     * <div>3. 添加新的页码</div>
     *
     * @param inputPath  输入PDF文件路径
     * @param outputPath 输出PDF文件路径
     */
    public static void updatePageNumbers(String inputPath, String outputPath) throws IOException {
        updatePageNumbers(new File(inputPath), new File(outputPath));
    }

    /**
     * 更新PDF文件页码
     *
     * <div>1. 识别PDF文件中原有的页码</div>
     * <div>2. 使用矩形覆盖原有页码</div>
     * <div>3. 添加新的页码</div>
     *
     * @param inputFile  输入PDF文件
     * @param outputFile 输出PDF文件
     */
    public static void updatePageNumbers(File inputFile, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputFile)) {
            int totalPages = document.getNumberOfPages();

            // 创建新文档用于保存修改后的内容
            PDDocument newDocument = new PDDocument();

            // 从PDF文档中提取原有的页码信息
            List<PageNumberInfo> ps = extractPageNumbers(document);

            // 获取resources目录下的字体
            PDFont font = PDType0Font.load(document, PdfboxUtil.class.getResourceAsStream("/fonts/simhei.ttf"));

            for (int i = 0; i < totalPages; i++) {
                // 获取当前页
                PDPage page = document.getPage(i);
                newDocument.addPage(page);

                int finalI = i;
                PageNumberInfo pos = ps.stream().filter(p -> p.getPageIndex() == finalI + 1).findFirst().orElse(new PageNumberInfo());
                // 添加页码
                addPageNumber(newDocument, page, pos, ps.get(0), font, i + 1, totalPages);
            }

            // 保存新文档
            newDocument.save(outputFile);
            newDocument.close();
        }
    }

    /**
     * 在页面底部添加页码
     *
     * @param document    PDF文档
     * @param page        当前页面
     * @param posOriginal 原页码位置
     * @param posNew      新页码位置
     * @param font        页码字体
     * @param currentPage 当前页码
     * @param totalPages  总页数
     */
    private static void addPageNumber(PDDocument document, PDPage page,
                                      PageNumberInfo posOriginal, PageNumberInfo posNew,
                                      PDFont font, int currentPage, int totalPages)
            throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true)) {

            // 设置画笔颜色
            contentStream.setNonStrokingColor(Color.WHITE);

            // 计算覆盖区域（基于原页码位置和估计的文本宽度）
            if (posOriginal.getX() != null && posOriginal.getY() != null) {
                contentStream.addRect(posOriginal.getX() - 5, posOriginal.getY() - 5, 90, posOriginal.getHeight() + 10);
                contentStream.fill();
            }

            // 覆盖新页码位置--防止页码重叠
            if (posNew.getX() != null && posNew.getY() != null) {
                contentStream.addRect(posNew.getX() - 5, posNew.getY() - 5, 90, posNew.getHeight() + 10);
                contentStream.fill();
            }

            // 获取页面尺寸
            PDRectangle pageSize = page.getMediaBox();
            float pageWidth = pageSize.getWidth();

            // 设置字体和字号
            float fontSize = 10;
            contentStream.setFont(font, fontSize);
            // 设置文本颜色
            contentStream.setNonStrokingColor(Color.DARK_GRAY);
            // 页码文本
            String pageText = String.format("第 %d / %d 页", currentPage, totalPages);

            // 计算文本宽度以居中显示
            float textWidth = font.getStringWidth(pageText) / 1000 * fontSize;
            float startX = (pageWidth - textWidth) / 2;
            float startY = 30; // 距离底部30个单位
            if (posNew.getX() != null && posNew.getY() != null) {
                startY = posNew.getY();
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
