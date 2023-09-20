package com.loeyae.component.document.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * PdfWaterMarker
 *
 * @author ZhangYi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfWaterMarker {

    @SneakyThrows
    public PdfWaterMarker(String text) {
        this.text = text;
        this.font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                BaseFont.EMBEDDED);
    }

    public PdfWaterMarker(String text, BaseFont font) {
        this.text = text;
        this.font = font;
    }

    /**
     * 水印文字
     */
    private String text;


    private BaseFont font;

    private Integer fondSize = 60;

    /**
     * 水印透明度
     */
    private float opacity = 0.1f;

    /**
     * x轴
     */
    private float x = 300f;

    /**
     * y轴
     */
    private float y = 500f;

    private float rotation = 45f;

}
