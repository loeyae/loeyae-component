package com.loeyae.component.document.core.converter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * LocalImageConverter
 *
 * @author ZhangYi
 */
@Slf4j
public class LocalImageConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        try {
            if (CharSequenceUtil.isBlank(value)) {
                return new WriteCellData<>("");
            }
            if (!FileUtil.exist(value)) {
                return new WriteCellData<>("");
            }
            return new WriteCellData<>(FileUtil.readBytes(value));
        } catch (Exception e) {
            log.error("Excel Image Convert Error ", e);
            return new WriteCellData<>("");
        }
    }
}
