package com.chrisleung.notifications.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my.notifications.email")
public class EmailServiceProperties {
    public static class Rate {
        private Boolean enableLimit;
        private Integer perHour;
        public Boolean getEnableLimit() {
            return enableLimit;
        }
        public void setEnableLimit(Boolean enableLimit) {
            this.enableLimit = enableLimit;
        }
        public Integer getPerHour() {
            return perHour;
        }
        public void setPerHour(Integer perHour) {
            this.perHour = perHour;
        }
        @Override
        public String toString() {
            return "Rate [limit=" + enableLimit + ", perHour=" + perHour + "]";
        }
    }
    public static class Queue {
        private Boolean enableLimit;
        private Integer size;
        public Boolean getEnableLimit() {
            return enableLimit;
        }
        public void setEnableLimit(Boolean enableLimit) {
            this.enableLimit = enableLimit;
        }
        public Integer getSize() {
            return size;
        }
        public void setSize(Integer size) {
            this.size = size;
        }
        @Override
        public String toString() {
            return "Queue [limit=" + enableLimit + ", size=" + size + "]";
        }
    }
    public static class Template {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return "Template [path=" + path + "]";
        }
    }
    public static class Sender {
        private String name;
        private String address;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        @Override
        public String toString() {
            return "Sender [name=" + name + ", address=" + address + "]";
        }
    }
    public static class Subject {
        private String template;

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        @Override
        public String toString() {
            return "Subject [template=" + template + "]";
        }
    }
    public static class Smtp {
        private String address;
        private Integer port;
        private String username;
        private String password;
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public Integer getPort() {
            return port;
        }
        public void setPort(Integer port) {
            this.port = port;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        @Override
        public String toString() {
            return "Smtp [address=" + address + ", port=" + port + ", username=" + username + ", password="
                    + password + "]";
        }            
    }
    public static class Shop {
        private String name;
        private String domain;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDomain() {
            return domain;
        }
        public void setDomain(String domain) {
            this.domain = domain;
        }
        @Override
        public String toString() {
            return "Shop [name=" + name + ", domain=" + domain + "]";
        }
    }
    private Rate rate;
    private Queue queue;
    private Template template;
    private Sender sender;
    private Subject subject;
    private Smtp smtp;
    private Shop shop;

    public Rate getRate() {
        return rate;
    }
    public void setRate(Rate rate) {
        this.rate = rate;
    }
    public Queue getQueue() {
        return queue;
    }
    public void setQueue(Queue queue) {
        this.queue = queue;
    }
    public Template getTemplate() {
        return template;
    }
    public void setTemplate(Template template) {
        this.template = template;
    }
    public Sender getSender() {
        return sender;
    }
    public void setSender(Sender sender) {
        this.sender = sender;
    }
    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
    public Smtp getSmtp() {
        return smtp;
    }
    public void setSmtp(Smtp smtp) {
        this.smtp = smtp;
    }
    public Shop getShop() {
        return shop;
    }
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    @Override
    public String toString() {
        return "EmailServiceProperties [rate=" + rate + ", queue=" + queue + ", template=" + template + ", sender="
                + sender + ", subject=" + subject + ", smtp=" + smtp + ", shop=" + shop + "]";
    }
}
