package com.spring.restapi.models;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(collection = "notifications")
public class Notification {
    @Id
    String id;
    String email;
    String sku;
    @DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDate;
    boolean sent;
    @DateTimeFormat(iso = ISO.DATE_TIME)
	private Date sentDate;
    
    public Notification(String email, String sku) {
    	this.email = email;
    	this.sku = sku;
    	this.createdDate = new Date();
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

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
}
