package com.cs246.team1.spacedrepetition;

import com.google.firebase.auth.FirebaseUser;

public class Reminder {
    private String _userId;
    private String _summary;
    private String _content;

    public String summary() {
        return _summary;
    }

    public void setSummary(String summary) {
        _summary = summary;
    }

    public String content() {
        return _content;
    }

    public void setContent(String content) {
        _content = content;
    }

    public String userId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public void setUser(FirebaseUser user) {
        setUserId(user.getUid());
    }
}
