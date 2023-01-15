package com.example.fmoapplication;

public class Pin {
    String AuthCode;

    public Pin(String authCode) {
        AuthCode = authCode;
    }

    public Pin() {
    }

    public String getAuthCode() {
        return AuthCode;
    }
}