package com.vaibhav.letschat.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewMessageNotificationModel {

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("message_index")
    @Expose
    private String messageIndex;
    @SerializedName("message_sid")
    @Expose
    private String messageSid;
    @SerializedName("twi_message_type")
    @Expose
    private String twiMessageType;
    @SerializedName("conversation_sid")
    @Expose
    private String conversationSid;
    @SerializedName("twi_message_id")
    @Expose
    private String twiMessageId;
    @SerializedName("twi_body")
    @Expose
    private String twiBody;
    @SerializedName("conversation_title")
    @Expose
    private String conversationTitle;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(String messageIndex) {
        this.messageIndex = messageIndex;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public void setMessageSid(String messageSid) {
        this.messageSid = messageSid;
    }

    public String getTwiMessageType() {
        return twiMessageType;
    }

    public void setTwiMessageType(String twiMessageType) {
        this.twiMessageType = twiMessageType;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public String getTwiMessageId() {
        return twiMessageId;
    }

    public void setTwiMessageId(String twiMessageId) {
        this.twiMessageId = twiMessageId;
    }

    public String getTwiBody() {
        return twiBody;
    }

    public void setTwiBody(String twiBody) {
        this.twiBody = twiBody;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

}