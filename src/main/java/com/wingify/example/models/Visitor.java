package com.wingify.example.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Visitor {
    @JsonProperty("customerId")
    private Integer customerId;

    @JsonProperty("visitorId")
    private String visitorId;

    @JsonProperty("action")
    private String action;

    public Visitor(Integer customerId, String action, String visitorId) {
        this.visitorId = visitorId;
        this.customerId = customerId;
        this.action = action;
    }

    public Visitor() {

    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "customerId=" + customerId +
                ", visitorId='" + visitorId + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}

