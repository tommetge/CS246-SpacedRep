package com.cs246.team1.spacedrepetition;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Date;

public class Reminder {
    private String _userId;
    private String _summary;
    private String _content;
    private Integer _daysToLive = 30;
    private Date _lastNotifiedAt;
    private Integer _currentPeriod = 0;

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

    public Date lastNotifiedAt() { return _lastNotifiedAt; }

    public void setLastNotifiedAt(Date date) { _lastNotifiedAt = date; }

    public Integer notificationId() {
        // Note: We'll want to make this dependent on last notification date
        // and notification period. For now, we'll just make it the current
        // date.
        return Math.toIntExact(System.currentTimeMillis() / 1000);
    }

    public Integer currentPeriod() { return _currentPeriod; }

    public void setCurrentPeriod(Integer period) { _currentPeriod = period; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;

        Reminder other = (Reminder)obj;
        if (!_summary.equals(other.summary()))
            return false;
        if (!_content.equals(other.content()))
            return false;
        if (_daysToLive != other.daysToLive())
            return false;
        if (!_lastNotifiedAt.equals(other.lastNotifiedAt()))
            return false;
        if (_currentPeriod != other.currentPeriod())
            return false;

        return true;
    }

    public static Reminder fromJSON(String json) {
        return new Gson().fromJson(json, Reminder.class);
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
