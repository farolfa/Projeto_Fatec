package com.fatec.demo.dto;

public class ChatConversationDto {
    private Long id;
    private Long orderId;
    private String serviceTitle;
    private Long clientUserId;
    private String clientName;
    private String clientPhoto;
    private Long providerUserId;
    private String providerName;
    private String providerPhoto;
    private String lastMessage;
    private String lastMessageTime;
    private Long unreadCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public Long getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(Long clientUserId) {
        this.clientUserId = clientUserId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhoto() {
        return clientPhoto;
    }

    public void setClientPhoto(String clientPhoto) {
        this.clientPhoto = clientPhoto;
    }

    public Long getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(Long providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPhoto() {
        return providerPhoto;
    }

    public void setProviderPhoto(String providerPhoto) {
        this.providerPhoto = providerPhoto;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}