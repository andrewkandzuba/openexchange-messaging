package org.openexchange.jpa;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "sms")
public class SmsEntity implements Serializable {
    private static final long serialVersionUID = -4214775545721925949L;
    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true, name = "CODE")
    private Integer code;
    @Column(nullable = false, unique = true, name = "MESSAGEID")
    private String messageId;
    @Column(nullable = false, name = "MOBILEORIGINATE")
    private String mobileOriginate;
    @Column(nullable = false, name = "MOBILETERMINATE")
    private String mobileTerminate;
    @Column(nullable = false, name = "TEXT")
    private String text;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", name = "RECEIVETIME")
    private Date receiveTime;

    public SmsEntity() {
    }

    public SmsEntity(String messageId, String mobileOriginate, String mobileTerminate, String text) {
        this.messageId = messageId;
        this.mobileOriginate = mobileOriginate;
        this.mobileTerminate = mobileTerminate;
        this.text = text;
    }

    public SmsEntity(String messageId, String mobileOriginate, String mobileTerminate, String text, Date receiveTime) {
        this.messageId = messageId;
        this.mobileOriginate = mobileOriginate;
        this.mobileTerminate = mobileTerminate;
        this.text = text;
        this.receiveTime = receiveTime;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMobileOriginate() {
        return mobileOriginate;
    }

    public void setMobileOriginate(String mobileOriginate) {
        this.mobileOriginate = mobileOriginate;
    }

    public String getMobileTerminate() {
        return mobileTerminate;
    }

    public void setMobileTerminate(String mobileTerminate) {
        this.mobileTerminate = mobileTerminate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }
}
