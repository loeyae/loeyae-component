package com.loeyae.component.document.utils;

import cn.hutool.core.io.FileUtil;
import com.deepoove.poi.XWPFTemplate;
import com.loeyae.component.document.core.WordWaterMarker;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * PdfUtl
 *
 * @author ZhangYi
 */
public class PdfConvertUtil {

    private PdfConvertUtil() {

    }

    /**
     * 根据word模板生成带水印pdf下载流
     *
     * @param response instance of HttpServletResponse
     * @param templateFile 模板文件
     * @param data 数据
     * @param waterMarker 水印
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(HttpServletResponse response, String templateFile, T data, WordWaterMarker waterMarker) {
        File temp = FileUtil.createTempFile();
        try(OutputStream out = response.getOutputStream();
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)) {
            PdfOptions options = PdfOptions.create();
            try(FileOutputStream tempOut = new FileOutputStream(temp)) {
                WordUtil.createWatermark(xwpfTemplate.getXWPFDocument(), waterMarker);
                xwpfTemplate.write(tempOut);
            }
            try (XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(temp))) {
                PdfConverter.getInstance().convert(xwpfDocument.getXWPFDocument(), out, options);
            }
        }
    }

    /**
     * 根据word模板生成pdf下载流
     *
     * @param response instance of HttpServletResponse
     * @param templateFile 模板文件
     * @param data 数据
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(HttpServletResponse response, String templateFile, T data) {
        File temp = FileUtil.createTempFile();
        try(OutputStream out = response.getOutputStream();
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)) {
            PdfOptions options = PdfOptions.create();
            try(FileOutputStream tempOut = new FileOutputStream(temp)) {
                xwpfTemplate.write(tempOut);
            }
            try (XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(temp))) {
                PdfConverter.getInstance().convert(xwpfDocument.getXWPFDocument(), out, options);
            }
        }
    }

    /**
     * 根据word模板生成pdf文件
     *
     * @param output 文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param waterMarker 水印
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(String output, String templateFile, T data, WordWaterMarker waterMarker) {
        File temp = FileUtil.createTempFile();
        try(OutputStream out = Files.newOutputStream(Paths.get(output));
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)) {
            PdfOptions options = PdfOptions.create();
            try(FileOutputStream tempOut = new FileOutputStream(temp)) {
                WordUtil.createWatermark(xwpfTemplate.getXWPFDocument(), waterMarker);
                xwpfTemplate.write(tempOut);
            }
            try (XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(temp))) {
                PdfConverter.getInstance().convert(xwpfDocument.getXWPFDocument(), out, options);
            }
        }
    }

    /**
     * 根据word模板生成pdf文件
     *
     * @param output 文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(String output, String templateFile, T data) {
        File temp = FileUtil.createTempFile();
        try(OutputStream out = Files.newOutputStream(Paths.get(output));
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)) {
            PdfOptions options = PdfOptions.create();
            try(FileOutputStream tempOut = new FileOutputStream(temp)) {
                xwpfTemplate.write(tempOut);
            }
            try (XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(temp))) {
                PdfConverter.getInstance().convert(xwpfDocument.getXWPFDocument(), out, options);
            }
        }
    }

    /**
     * 将已有word文件转化为pdf
     *
     * @param output pdf文件
     * @param source word文件
     */
    @SneakyThrows
    public static void convert(String output, String source) {
        try (OutputStream out = Files.newOutputStream(Paths.get(output));
        XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(source))) {
            PdfConverter.getInstance().convert(xwpfDocument.getXWPFDocument(), out, PdfOptions.create());
        }
    }
}
