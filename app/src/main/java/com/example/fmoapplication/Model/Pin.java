package com.example.fmoapplication.Model;

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