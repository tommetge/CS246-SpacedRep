package com.cs246.team1.spacedrepetition;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class Reminder {
    private String _userId;
    private String _summary;
    private String _content;
    private Integer _daysToLive = 30;

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

    public Integer daysToLive() { return _daysToLive; }

    public void setDaysToLive(Integer daysToLive) { _daysToLive = daysToLive; }

    public String userId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public void setUser(FirebaseUser user) {
        setUserId(user.getUid());
    }

    public static Reminder fromJSON(String json) {
        return new Gson().fromJson(json, Reminder.class);
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
