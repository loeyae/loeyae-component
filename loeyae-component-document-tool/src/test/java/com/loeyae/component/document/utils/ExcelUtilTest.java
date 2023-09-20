package com.loeyae.component.document.utils;

import cn.hutool.system.SystemUtil;
import com.loeyae.component.document.core.ExcelWaterMarker;
import com.loeyae.component.document.utils.ExcelUtil;
import com.loeyae.component.document.utils.TestExport;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * com.loeyae.component.document.utils.ExcelUtilTest
 *
 * @author ZhangYi
 */
public class ExcelUtilTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    @Test
    @SneakyThrows
    void testWrite() {
        List<TestExport> data = new ArrayList<>();
        TestExport data1 = PODAM_FACTORY.manufacturePojo(TestExport.class);
        data1.setAvatar(new ClassPathResource("test.jpg").getFile().getAbsolutePath());
        data.add(data1);
        TestExport data2 = PODAM_FACTORY.manufacturePojo(TestExport.class);
        data2.setAvatar(new ClassPathResource("test.jpg").getFile().getAbsolutePath());
        data.add(data2);
        ExcelWaterMarker waterMarker = new ExcelWaterMarker("我是水印");
        ExcelUtil.write(SystemUtil.getUserInfo().getCurrentDir() + "/target/test.xlsx", TestExport.class, data, waterMarker);
    }
}
