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
            public String getSent() {
                return sent;
            }
            public void setSent(String sent) {
                this.sent = sent;
            }
            public String getCreatedDate() {
                return createdDate;
            }
            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }
            @Override
            public String toString() {
                return "Param [sent=" + sent + ", createdDate=" + createdDate + "]";
            }
        }
        private Param param;
        private String username;
        private String password;
        private String url;
        private Integer refresh;
        public Param getParam() {
            return param;
        }
        public void setParam(Param param) {
            this.param = param;
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
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public Integer getRefresh() {
            return refresh;
        }
        public void setRefresh(Integer refresh) {
            this.refresh = refresh;
        }
        @Override
        public String toString() {
            return "RestApi [param=" + param + ", username=" + username + ", password=" + password + ", url=" + url
                    + ", refresh=" + refresh + "]";
        }
    }
    public static class ShopifyApi {
        public static class Product {
            public static class Variant {
                private String url;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                @Override
                public String toString() {
                    return "Variant [url=" + url + "]";
                }
            }
            private Variant variant;
            private String url;
            
            public Variant getVariant() {
                return variant;
            }
            public void setVariant(Variant variant) {
                this.variant = variant;
            }
            public String getUrl() {
                return url;
            }
            public void setUrl(String url) {
                this.url = url;
            }
            @Override
            public String toString() {
                return "Product [variant=" + variant + ", url=" + url + "]";
            }
        }
        private Product product;
        private String apiKey;
        private String password;
        private String urlPostFix;
        
        public Product getProduct() {
            return product;
        }
        public void setProduct(Product product) {
            this.product = product;
        }
        public String getApiKey() {
            return apiKey;
        }
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getUrlPostFix() {
            return urlPostFix;
        }
        public void setUrlPostFix(String urlPostFix) {
            this.urlPostFix = urlPostFix;
        }
        @Override
        public String toString() {
            return "ShopifyApi [product=" + product + ", apiKey=" + apiKey + ", password=" + password + ", urlPostFix="
                    + urlPostFix + "]";
        }
    }
    public static class Log {
        private String tag;
        private Boolean verbose;
        public String getTag() {
            return tag;
        }
        public void setTag(String tag) {
            this.tag = tag;
        }
        public Boolean getVerbose() {
            return verbose;
        }
        public void setVerbose(Boolean verbose) {
            this.verbose = verbose;
        }
        @Override
        public String toString() {
            return "Log [tag=" + tag + ", verbose=" + verbose + "]";
        }
    }
    public static class Email {
        public static class Limits {
            private Integer emailsPerHour;
            private Integer queueSize;
            public Integer getEmailsPerHour() {
                return emailsPerHour;
            }
            public void setEmailsPerHour(Integer emailsPerHour) {
                this.emailsPerHour = emailsPerHour;
            }
            public Integer getQueueSize() {
                return queueSize;
            }
            public void setQueueSize(Integer queueSize) {
                this.queueSize = queueSize;
            }
            @Override
            public String toString() {
                return "Limits [emailsPerHour=" + emailsPerHour + ", queueSize=" + queueSize + "]";
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
        private Limits limits;
        private Template template;
        private Sender sender;
        private Subject subject;
        private Smtp smtp;
        private Shop shop;

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
            return "Email [limits=" + limits + ", template=" + template + ", sender=" + sender + ", subject=" + subject
                    + ", smtp=" + smtp + ", shop=" + shop + "]";
        }
        
    }
    
    private RestApi restapi;
    private ShopifyApi shopifyapi;
    private Log log;
    private Email email;
    public RestApi getRestapi() {
        return restapi;
    }
    public void setRestapi(RestApi restapi) {
        this.restapi = restapi;
    }
    public ShopifyApi getShopifyapi() {
        return shopifyapi;
    }
    public void setShopifyapi(ShopifyApi shopifyapi) {
        this.shopifyapi = shopifyapi;
    }
    public Log getLog() {
        return log;
    }
    public void setLog(Log log) {
        this.log = log;
    }
    public Email getEmail() {
        return email;
    }
    public void setEmail(Email email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return "ApplicationProperties [restapi=" + restapi + ", shopifyapi=" + shopifyapi + ", log=" + log + ", email="
                + email + "]";
    }
    
}
