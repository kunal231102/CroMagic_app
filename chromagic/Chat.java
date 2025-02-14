package com.example.chromagic;

public class Chat {
    private final String message;
    private final boolean isUser;

    public Chat(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
