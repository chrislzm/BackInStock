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
    
    private RestApi restapi;
    private ShopifyApi shopifyapi;
    private Log log;
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
    @Override
    public String toString() {
        return "ApplicationProperties [restapi=" + restapi + ", shopifyapi=" + shopifyapi + ", log=" + log + "]";
    }
    
}
