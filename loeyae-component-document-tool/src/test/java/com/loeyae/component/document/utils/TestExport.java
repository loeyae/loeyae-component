package com.loeyae.component.document.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.loeyae.component.document.core.converter.LocalImageConverter;
import com.loeyae.component.document.core.converter.RemoteImageConverter;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TestExport
 *
 * @author ZhangYi
 */
@Data
@Builder
public class TestExport {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty(value = "头像", converter = LocalImageConverter.class)
    private String avatar;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
