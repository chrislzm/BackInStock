package com.chrisleung.notifications.objects;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Notification model - Maps to notifications collection objects in MongoDB.  
 * 
 * @author Chris Leung
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "notifications")
public class Notification {
    @Id
    String id;
    String email;
    Long variantId;
    @DateTimeFormat(iso = ISO.DATE_TIME)
	Date createdDate;
    Boolean sent;
    @DateTimeFormat(iso = ISO.DATE_TIME)
	Date sentDate;
    
    public Notification() {
    	this.createdDate = new Date();
    	this.sent = new Boolean(false);
    }
    
    public Notification(String email, Long variantId) {
    	this();
    	this.email = email;
    	this.variantId = variantId;
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
	
	public Long getVariantId() {
		return variantId;
	}

	public void setVariantId(Long variantId) {
		this.variantId = variantId;
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
                ", variantId='" + variantId + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", sent='" + sent + '\'' +
                ", sentDate='" + sentDate + '\'' +
                '}';
    }
}
