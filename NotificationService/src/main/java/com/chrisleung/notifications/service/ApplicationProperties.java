package com.chrisleung.notifications.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my.notifications")
public class ApplicationProperties {
    public static class RestApi {
        public static class Param {
            private String sent;
            private String createdDate;
        }
        private String username;
        private String password;
        private String url;
        private Integer refresh;
    }
    public static class ShopifyApi {
        public static class Product {
            public static class Variant {
                private String url;
            }
            private String url;
        }
        private String apiKey;
        private String password;
        private String urlPostFix;
    }
    public static class Log {
        private String tag;
        private Boolean verbose;
    }
    public static class Email {
        public static class Template {
            private String path;
        }
        public static class Sender {
            private String name;
            private String address;
        }
        public static class Subject {
            private String template;
        }
        public static class Smtp {
            private String address;
            private Integer port;
            private String username;
            private String password;            
        }
        public static class Shop {
            private String name;
            private String domain;
        }
    }
}
