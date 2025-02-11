package com.ashcollege.entities;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class EventClient {
    private SseEmitter sseEmitter;
    private String secret;
    private boolean connected;

    public EventClient(SseEmitter sseEmitter, String secret) {
        this.sseEmitter = sseEmitter;
        this.secret = secret;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public SseEmitter getSseEmitter() {
        return sseEmitter;
    }

    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
