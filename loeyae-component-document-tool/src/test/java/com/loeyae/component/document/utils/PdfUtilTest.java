package com.loeyae.component.document.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.system.SystemUtil;
import com.loeyae.component.document.core.PdfWaterMarker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * PdfUtilTest
 *
 * @author ZhangYi
 */
public class PdfUtilTest {
    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    @Test
    @SneakyThrows
    void testWrite() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "张三");
        params.put("gender", "男");
        params.put("intro", RandomUtil.randomString(500));
        PdfUtil.write(SystemUtil.getUserInfo().getCurrentDir() + "/target/test.pdf", new ClassPathResource("template.html").getFile().getAbsolutePath(),
                params);
    }
}
