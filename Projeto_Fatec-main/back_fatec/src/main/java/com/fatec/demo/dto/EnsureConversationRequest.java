package com.fatec.demo.dto;

public class EnsureConversationRequest {
    private Long orderId;
    private String serviceTitle;
    private Long clientUserId;
    private Long providerUserId;

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

    public Long getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(Long providerUserId) {
        this.providerUserId = providerUserId;
    }
}