package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketMessage {

    private String action;
    private Object data;
    private String resource;

    @JsonCreator
    public WebSocketMessage(
            @JsonProperty("action") String action, 
            @JsonProperty("data") Object data,
            @JsonProperty("resource") String resource) {
        this.action = action;
        this.data = data;
        this.resource = resource;
    }

    public WebSocketMessage(String action, Object data) {
        this.action = action;
        this.data = data;
        this.resource = null;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}