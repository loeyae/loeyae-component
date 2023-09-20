package com.loeyae.component.document.core.handler;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.loeyae.component.document.core.ExcelWaterMarker;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * WaterMarkHandler
 *
 * @author ZhangYi
 */
public class MultiExcelWaterMarkHandler implements SheetWriteHandler {

    private final ExcelWaterMarker waterMarker;

    public MultiExcelWaterMarkHandler(ExcelWaterMarker waterMarker) {
        this.waterMarker = waterMarker;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        BufferedImage bufferedImage = createWaterMarkerImage();
        setWaterMarker((SXSSFWorkbook) writeWorkbookHolder.getWorkbook(), bufferedImage);
    }

    /**
     * 设置水印
     *
     * @param workbook workbook
     * @param bufferedImage 图片
     */
    private void setWaterMarker(SXSSFWorkbook workbook, BufferedImage bufferedImage) {
        //将图片添加到工作簿
        int pictureIdx = workbook.addPicture(ImgUtil.toBytes(bufferedImage, ImgUtil.IMAGE_TYPE_PNG), Workbook.PICTURE_TYPE_PNG);
        //建立 sheet 和 图片 的关联关系
        XSSFPictureData xssfPictureData = (XSSFPictureData) workbook.getAllPictures().get(pictureIdx);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet xssfSheet = (XSSFSheet)ReflectUtil.getFieldValue(workbook.getSheetAt(i), "_sh");
            PackageRelationship packageRelationship = xssfSheet
                    .addRelation(null, XSSFRelation.IMAGES, xssfPictureData).getRelationship();
            //添加水印到工作表
            xssfSheet.getCTWorksheet().addNewPicture().setId(packageRelationship.getId());
        }
    }

    /**
     * 生成水印图片
     *
     * @return 图片
     */
    private BufferedImage createWaterMarkerImage() {
        final Font font = waterMarker.getFont();
        final int width = waterMarker.getWidth();
        final int height = waterMarker.getHeight();

        String[] textArray = waterMarker.getText().split(",");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 背景透明 开始
        Graphics2D g = image.createGraphics();
        image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g.dispose();
        // 背景透明 结束
        g = image.createGraphics();
        // 设定画笔颜色
        g.setColor(new Color(Integer.parseInt(waterMarker.getColor().substring(1), 16)));
        // 设置画笔字体
        g.setFont(font);
        // 设定倾斜度
        g.shear(waterMarker.getShearX(), waterMarker.getShearY());

        // 设置字体平滑
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = waterMarker.getYAxis();
        for (String s : textArray) {
            // 从画框的y轴开始画字符串.假设电脑屏幕中心为0，y轴为正数则在下方
            g.drawString(s, 0, y);
            y = y + font.getSize();
        }

        // 释放画笔
        g.dispose();
        return image;
    }


}
