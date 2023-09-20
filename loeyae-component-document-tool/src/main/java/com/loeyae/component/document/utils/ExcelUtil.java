package com.loeyae.component.document.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.loeyae.component.document.core.ExcelWaterMarker;
import com.loeyae.component.document.core.handler.CommonCellStyleStrategy;
import com.loeyae.component.document.core.handler.ExcelWaterMarkHandler;
import com.loeyae.component.document.core.handler.MultiExcelWaterMarkHandler;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * ExcelUtils.
 *
 * @author ZhangYi
 */
public class ExcelUtil {

    private ExcelUtil() {

    }

    /**
     * 生成excel文件
     *
     * @param fileName 文件名
     * @param head 头信息
     * @param data 数据
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(String fileName, Class<T> head, List<T> data) {
        try (OutputStream out = new FileOutputStream(fileName)) {
            EasyExcelFactory.write(out, head)
                    .registerWriteHandler(CommonCellStyleStrategy.getHorizontalCellStyleStrategy())
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(false)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet().doWrite(data);
        }
    }

    /**
     * 生成excel文件
     *
     * @param fileName 文件名
     * @param head 头信息
     * @param data 数据
     * @param waterMarker 水印
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(String fileName, Class<T> head, List<T> data, ExcelWaterMarker waterMarker) {
        try (OutputStream out = new FileOutputStream(fileName)) {
            EasyExcelFactory.write(out, head)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(false)
                    .registerWriteHandler(CommonCellStyleStrategy.getHorizontalCellStyleStrategy())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(new MultiExcelWaterMarkHandler(waterMarker))
                    .sheet().doWrite(data);
        }
    }

    /**
     * 导出
     *
     * @param response HttpServletResponse
     * @param fileName file name
     * @param head 实体
     * @param data 实体数据集
     * @param <T> 实体定义
     */
    @SneakyThrows
    public static <T> void write(HttpServletResponse response, String fileName, Class<T> head, List<T> data) {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (OutputStream out = response.getOutputStream()) {
            EasyExcelFactory.write(out, head)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(false)
                    .registerWriteHandler(CommonCellStyleStrategy.getHorizontalCellStyleStrategy())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet().doWrite(data);
        }
    }

    /**
     * 导出
     *
     * @param response HttpServletResponse
     * @param fileName file name
     * @param head 实体
     * @param data 实体数据集
     * @param waterMarker 水印
     * @param <T> 实体定义
     */
    @SneakyThrows
    public static <T> void write(HttpServletResponse response, String fileName, Class<T> head, List<T> data, ExcelWaterMarker waterMarker) {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (OutputStream out = response.getOutputStream()) {
            EasyExcelFactory.write(out, head)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(false)
                    .registerWriteHandler(CommonCellStyleStrategy.getHorizontalCellStyleStrategy())
                    .registerWriteHandler(new ExcelWaterMarkHandler(waterMarker))
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet().doWrite(data);
        }
    }

    /**
     * 导入
     *
     * @param file 上传的文件
     * @param head 实体
     * @param <T> 实体定义
     * @return 实体集合
     */
    @SneakyThrows
    public static <T> List<T> read(MultipartFile file, Class<T> head) {
        try (InputStream inputStream = file.getInputStream()) {
            return EasyExcelFactory.read(inputStream, head, null)
                    .autoCloseStream(false)
                    .doReadAllSync();
        }
    }
}