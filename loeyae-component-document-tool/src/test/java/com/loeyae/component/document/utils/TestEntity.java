package com.loeyae.component.document.utils;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * TestEntity
 *
 * @author ZhangYi
 */
@Data
@Builder
public class TestEntity {

    private String title;

    private String content;

    private List<User> sections;

    @Data
    @Builder
    public static class User {

        private String name;

        private String phone;

        private String birthday;

        private String gender;

        private String idCard;

        private String avatar;
    }
}
