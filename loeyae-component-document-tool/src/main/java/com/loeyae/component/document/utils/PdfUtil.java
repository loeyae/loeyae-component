package com.loeyae.component.document.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.engine.thymeleaf.ThymeleafEngine;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.loeyae.component.document.core.PdfWaterMarker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * PdfUtil
 *
 * @author ZhangYi
 */
@Slf4j
public class PdfUtil {

    private PdfUtil() {

    }

    /**
     * 根据html模板生成pdf文件
     *
     * @param fileName 文件名
     * @param templateFile 模板文件
     * @param data 数据
     */
    @SneakyThrows
    public static void write(String fileName, String templateFile, Map<String, Object> data) {
        Document document = new Document(PageSize.A4);
        File file = renderTemplate(templateFile, data);
        try (InputStream render = new FileInputStream(file)) {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(fileName)));
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, render, StandardCharsets.UTF_8, new PdfFont());
        } finally {
            document.close();
            FileUtil.del(file);
        }
    }

    /**
     * 根据html模板生成pdf下载流
     *
     * @param response instance of HttpServletResponse
     * @param templateFile 模板文件
     * @param data 数据
     */
    @SneakyThrows
    public static void write(HttpServletResponse response, String templateFile, Map<String, Object> data) {
        Document document = new Document(PageSize.A4);
        File file = renderTemplate(templateFile, data);
        try (InputStream render = new FileInputStream(file)) {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, render, StandardCharsets.UTF_8, new PdfFont());
        } finally {
            document.close();
            FileUtil.del(file);
        }
    }

    /**
     * 根据html模板生成带水印pdf文件
     *
     * @param fileName 文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param waterMarker 水印
     */
    @SneakyThrows
    public static void write(String fileName, String templateFile, Map<String, Object> data, PdfWaterMarker waterMarker) {
        Document document = new Document(PageSize.A4);
        File file = renderTemplate(templateFile, data);
        try (InputStream render = new FileInputStream(file)) {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(fileName)));
            pdfWriter.setPageEvent(new CustomEvent(waterMarker));
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, render, StandardCharsets.UTF_8, new PdfFont());
        } finally {
            document.close();
            FileUtil.del(file);
        }
    }

    /**
     * 根据html模板生成带水印pdf下载流
     *
     * @param response instance of HttpServletResponse
     * @param templateFile 模板文件
     * @param data 数据
     * @param waterMarker 水印
     */
    @SneakyThrows
    public static void write(HttpServletResponse response, String templateFile, Map<String, Object> data, PdfWaterMarker waterMarker) {
        Document document = new Document(PageSize.A4);
        File file = renderTemplate(templateFile, data);
        try (InputStream render = new FileInputStream(file)) {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
            pdfWriter.setPageEvent(new CustomEvent(waterMarker));
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, render, StandardCharsets.UTF_8, new PdfFont());
        } finally {
            document.close();
            FileUtil.del(file);
        }
    }

    /**
     * 渲染html
     *
     * @param templateFile 模板文件
     * @param data 数据
     * @return 临时html文件
     */
    private static File renderTemplate(String templateFile, Map<String, Object> data) {
        File file = FileUtil.createTempFile();
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setResourceMode(TemplateConfig.ResourceMode.FILE);
        ThymeleafEngine engine = new ThymeleafEngine(templateConfig);
        engine.getTemplate(templateFile).render(data, file);
        return file;
    }

    /**
     * 中文字体支持
     */
    static class PdfFont extends XMLWorkerFontProvider {

        @Override
        public Font getFont(String fontName, String encoding, boolean embedded,
                            float size, int style, BaseColor color) {
            try {
                BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);//中文字体
                return new Font(bfChinese, size, style);
            } catch (Exception ex) {
                return new Font(Font.FontFamily.UNDEFINED, size, style);
            }
        }

    }

    /**
     * 事件处理类, 用于监听pdf页码增加时, 每页增加水印
     */
    static class CustomEvent extends PdfPageEventHelper {

        private PdfWaterMarker waterMark;

        public CustomEvent(PdfWaterMarker waterMark) {
            this.waterMark = waterMark;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                // 加入水印
                PdfContentByte waterMar = writer.getDirectContentUnder();
                // 开始设置水印
                waterMar.beginText();
                // 设置水印透明度
                PdfGState gs = new PdfGState();
                // 设置填充字体不透明度为0.2f
                gs.setFillOpacity(waterMark.getOpacity());
                // 设置水印字体参数及大小
                BaseFont baseFont = waterMark.getFont();
                waterMar.setFontAndSize(baseFont, waterMark.getFondSize());
                // 设置透明度
                waterMar.setGState(gs);
                // 设置水印对齐方式 水印内容 X坐标 Y坐标 旋转角度
                waterMar.showTextAligned(Element.ALIGN_CENTER, waterMark.getText(), waterMark.getX(), waterMark.getY(), waterMark.getRotation());
                //结束设置
                waterMar.endText();
                waterMar.stroke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
