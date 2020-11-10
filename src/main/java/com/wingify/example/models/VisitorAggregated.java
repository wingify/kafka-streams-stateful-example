package com.wingify.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;

public class VisitorAggregated {
    @JsonProperty("customerId")
    Integer customerId;

    @JsonProperty("events")
    ArrayList<Visitor> visitors;

    @JsonProperty("hits")
    Integer hits;

    public VisitorAggregated(){

    }

    public VisitorAggregated(Integer customerId) {
        this.customerId = customerId;
        this.hits = 0;
        this.visitors = new ArrayList<>();
    }

    public VisitorAggregated(Visitor visitor) {
        this.hits = 1;
        this.customerId = visitor.getCustomerId();
        this.visitors = new ArrayList<>(Collections.singletonList(visitor));
    }

    public ArrayList<Visitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(ArrayList<Visitor> visitors) {
        this.visitors = visitors;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public VisitorAggregated merge(VisitorAggregated other) {
        this.getVisitors().addAll(other.getVisitors());
        this.setHits(this.getHits() + other.getHits());
        return this;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "VisitorAggregated{" +
                "customerId=" + customerId +
                ", visitors=" + visitors +
                ", hits=" + hits +
                '}';
    }
}
