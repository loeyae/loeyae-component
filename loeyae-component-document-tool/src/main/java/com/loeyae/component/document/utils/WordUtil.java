package com.loeyae.component.document.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.deepoove.poi.XWPFTemplate;
import com.loeyae.component.document.core.WordWaterMarker;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.vml.*;
import lombok.SneakyThrows;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * WordUtil
 *
 * @author ZhangYi
 */
public class WordUtil {
    private WordUtil() {}

    /**
     * 通过模板生成word下载流
     *
     * @param response instance of HttpServletResponse
     * @param fileName 下载文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(HttpServletResponse response, String fileName, String templateFile, T data) {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/octet-stream;charset=UTF-8");
        try (OutputStream out = response.getOutputStream();
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)
        ) {
            xwpfTemplate.write(out);
        }
    }

    /**
     * 通过模板生成带水印的word下载流
     *
     * @param response instance of HttpServletResponse
     * @param fileName 下载文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param waterMarker 水印
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(HttpServletResponse response, String fileName, String templateFile, T data, WordWaterMarker waterMarker) {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/octet-stream;charset=UTF-8");
        try (OutputStream out = response.getOutputStream();
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)
        ) {
            createWatermark(xwpfTemplate.getXWPFDocument(), waterMarker);
            xwpfTemplate.write(out);
        }
    }

    /**
     * 通过模板生成word文件
     *
     * @param output 生成文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(String output, String templateFile, T data) {
        try (FileOutputStream out = new FileOutputStream(FileUtil.file(output));
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)
        ) {
            xwpfTemplate.write(out);
        }
    }

    /**
     * 通过模板生成带水印word文件
     *
     * @param output 生成文件名
     * @param templateFile 模板文件
     * @param data 数据
     * @param waterMarker 水印
     * @param <T> 数据类型
     */
    @SneakyThrows
    public static <T> void write(String output, String templateFile, T data, WordWaterMarker waterMarker) {
        try (FileOutputStream out = new FileOutputStream(FileUtil.file(output));
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(data)
        ) {
            createWatermark(xwpfTemplate.getXWPFDocument(), waterMarker);
            xwpfTemplate.write(out);
        }
    }

    /**
     * 添加水印
     *
     * @param document 文档对象
     * @param waterMarker 水印对象
     */
    public static void createWatermark(XWPFDocument document, WordWaterMarker waterMarker) {
        List<XWPFHeader> headerList = new ArrayList<>(document.getHeaderList());
        if (headerList.isEmpty()) {
            headerList.add(document.createHeader(HeaderFooterType.DEFAULT));
        }
        for (XWPFHeader header :
                headerList) {
            int size = header.getParagraphs().size();
            if (size == 0) {
                header.createParagraph();
            }
            XWPFParagraph paragraph = header.getParagraphArray(0);
            CTP ctp = paragraph.getCTP();
            byte[] rsidR = document.getDocument().getBody().getPArray(0).getRsidR();
            byte[] rsidRDefault = document.getDocument().getBody().getPArray(0).getRsidRDefault();
            ctp.setRsidP(rsidR);
            ctp.setRsidRDefault(rsidRDefault);
            CTPPr ctpPr = ctp.addNewPPr();
            ctpPr.addNewPStyle().setVal("Header");
            //开始添加水印
            CTR ctr = ctp.addNewR();
            CTRPr ctrPr = ctr.addNewRPr();
            ctrPr.addNewNoProof();
            CTGroup group = CTGroup.Factory.newInstance();
            CTShapetype shapetype = group.addNewShapetype();
            CTTextPath textPath = shapetype.addNewTextpath();
            textPath.setOn(STTrueFalse.T);
            textPath.setFitshape(STTrueFalse.T);
            CTLock lock = shapetype.addNewLock();
            lock.setExt(STExt.VIEW);
            CTShape shape = group.addNewShape();
            shape.setId("PowerPlusWaterMarkObject");
            shape.setSpid("_x0000_s102");
            shape.setType("#_x0000_t136");
            shape.setStyle(formatStyle(waterMarker));
            shape.setFillcolor(waterMarker.getColor());
            shape.setStroked(STTrueFalse.FALSE);
            CTTextPath shapeTextPath = shape.addNewTextpath();
            shapeTextPath.setStyle(CharSequenceUtil.format("font-family:{};font-size:{}pt", waterMarker.getFontFamily(), waterMarker.getFontSize()));
            shapeTextPath.setString(waterMarker.getText());
            CTPicture picture = ctr.addNewPict();
            picture.set(group);
        }
    }

    private static String formatStyle(WordWaterMarker waterMarker) {
        String style = "position:absolute;margin-left:{};margin-top:{};width:{}pt;height:{}pt;z-index:-251654144;mso-wrap-edited:f;mso-position-horizontal:center;mso-position-horizontal-relative:margin;mso-position-vertical:center;mso-position-vertical-relative:margin;rotation:{}";
        return CharSequenceUtil.format(style, waterMarker.getMarginLeft(), waterMarker.getMarginTop(),
                waterMarker.getWidth(), waterMarker.getHeight(), waterMarker.getRotation());
    }
}
