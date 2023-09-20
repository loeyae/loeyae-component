package com.loeyae.component.document.core.converter;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * UrlImageConverter
 *
 * @author ZhangYi
 */
@Slf4j
public class RemoteImageConverter implements Converter<String> {

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
            if (!Validator.isUrl(value)) {
                return new WriteCellData<>("");
            }
            HttpRequest request = HttpRequest.get(value);
            HttpResponse response = request.execute();
            if (response.isOk()) {
                return new WriteCellData<>(response.bodyBytes());
            }
            return new WriteCellData<>("");
        } catch (Exception e) {
            log.error("Excel Image Convert Error ", e);
            return new WriteCellData<>("");
        }
    }
}
