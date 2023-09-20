package com.loeyae.component.document.utils;

import cn.hutool.system.SystemUtil;
import com.loeyae.component.document.core.WordWaterMarker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * WortUtilTest
 *
 * @author ZhangYi
 */
public class WortUtilTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    @Test
    @SneakyThrows
    void testWrite() {
        List<TestEntity.User> users = new ArrayList<>();
        TestEntity.User user1 = PODAM_FACTORY.manufacturePojo(TestEntity.User.class);
        user1.setAvatar(new ClassPathResource("test.jpg").getFile().getAbsolutePath());
        users.add(user1);
        TestEntity.User user2 = PODAM_FACTORY.manufacturePojo(TestEntity.User.class);
        user2.setAvatar(new ClassPathResource("test.jpg").getFile().getAbsolutePath());
        users.add(user2);
        TestEntity data = PODAM_FACTORY.manufacturePojo(TestEntity.class);
        data.setSections(users);
        WordWaterMarker waterMarker = new WordWaterMarker("我是水印");
        WordUtil.write(SystemUtil.getUserInfo().getCurrentDir() + "/target/test.docx", new ClassPathResource("template.docx").getFile().getAbsolutePath(), data, waterMarker);
    }
}
