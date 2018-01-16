// TODO: This is essentially identical to Notification class on the REST API server; should create a single shared class

package com.chrisleung.notifications.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
	   	String id;
	    String email;
	    String sku;
	    @DateTimeFormat(iso = ISO.DATE_TIME)
		Date createdDate;
	    Boolean sent;
	    @DateTimeFormat(iso = ISO.DATE_TIME)
		Date sentDate;
	    
	    public Notification() {
	    }
	    
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public Date getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}
		
		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public Boolean getIsSent() {
			return sent;
		}

		public void setIsSent(Boolean sent) {
			this.sent = sent;
		}

		public Date getSentDate() {
			return sentDate;
		}

		public void setSentDate(Date sentDate) {
			this.sentDate = sentDate;
		}
		
	    @Override
	    public String toString() {
	        return "Notification{" +
	                "id='" + id + '\'' +
	                ", email='" + email + '\'' +
	                ", sku='" + sku + '\'' +
	                ", createdDate='" + createdDate + '\'' +
	                ", sent='" + sent + '\'' +
	                ", sentDate='" + sentDate + '\'' +
	                '}';
	    }
}
