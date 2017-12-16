package com.spring.restapi.models;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * Notification model - Maps to notifications collections in MongoDB 
 * @author Chris Leung
 *
 */
@Document(collection = "notifications")
public class Notification {
    @Id
    String id;
    String email;
    String sku;
    @DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDate;
    Boolean sent;
    @DateTimeFormat(iso = ISO.DATE_TIME)
	private Date sentDate;
    
    public Notification(String email, String sku) {
    	this.email = email;
    	this.sku = sku;
    	this.createdDate = new Date();
    	this.sent = new Boolean(false);
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
}
