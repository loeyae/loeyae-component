package com.loeyae.component.document.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WaterMarker
 *
 * @author ZhangYi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordWaterMarker {

    public WordWaterMarker(String text) {
        this.text = text;
    }

    /**
     * 水印文字
     */
    private String text;

    /**
     * 字体
     */
    private String fontFamily = "Arial";

    /**
     * 字体大小
     */
    private Integer fontSize = 1;

    /**
     * 左移
     */
    private Float marginLeft = 0f;

    /**
     * 上移
     */
    private Float marginTop = 0f;

    /**
     * 宽度
     */
    private Float width = 415f;

    /**
     * 高度
     */
    private Float height = 207.5f;

    /**
     * 旋转角度
     */
    private Integer rotation = 315;

    /**
     * 字体颜色
     */
    private String color = "#d8d8d8";
}
