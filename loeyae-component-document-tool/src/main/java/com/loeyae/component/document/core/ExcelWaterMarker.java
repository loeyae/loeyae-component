package com.loeyae.component.document.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * ExcelWaterMarker
 *
 * @author ZhangYi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelWaterMarker {

    public ExcelWaterMarker(String text) {
        this.text = text;
    }

    /**
     * 水印内容
     */
    private String text;

    /**
     * 画笔颜色. 格式为"#RRGGBB"，eg: "#C5CBCF"
     */
    private String color = "#d8d8d8";

    /**
     * 字体样式
     */
    private Font font = new Font("新宋", Font.BOLD, 20);

    /**
     * 水印宽度
     */
    private int width = 300;

    /**
     * 水印高度
     */
    private int height = 100;

    /**
     * 水平倾斜度
     */
    private double shearX = 0.1;

    /**
     * 垂直倾斜度
     */
    private double shearY = -0.26;

    /**
     * 字体的y轴位置
     */
    private int yAxis = 50;
}
