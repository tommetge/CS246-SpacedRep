package com.cs246.team1.spacedrepetition;

import com.google.gson.Gson;

import java.util.Date;

public class Reminder {

    private static final String LOGTAG = "Reminder";

    private String identifier;
    private String summary;
    private String content;
    private Integer daysToLive = 30;
    private Date lastNotifiedAt;
    private Integer currentPeriod = 0;

    // Accessors
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDaysToLive() {
        return daysToLive;
    }

    public void setDaysToLive(Integer daysToLive) {
        this.daysToLive = daysToLive;
    }

    public Date getLastNotifiedAt() {
        return lastNotifiedAt;
    }

    public void setLastNotifiedAt(Date date) {
        this.lastNotifiedAt = date;
    }

    public Integer getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(Integer period) {
        this.currentPeriod = period;
    }

    // Generates a unique identifier for notifications derived from this reminder.

    /**
     * Generates a unique identifier for notifications derived from this reminder.
     * @return An identifier suitable for use with NotificationManagerCompat.notify()
     */
    public Integer notificationId() {
        // Note: We'll want to make this dependent on last notification date
        // and notification period. For now, we'll just make it the current
        // date.
        return Math.toIntExact(System.currentTimeMillis() / 1000);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;

        Reminder other = (Reminder)obj;
        if (this.summary == null && other.getSummary() != null)
            return false;
        if (this.summary != null && !this.summary.equals(other.getSummary()))
            return false;
        if (this.content == null && other.getContent() != null)
            return false;
        if (this.content != null && !this.content.equals(other.getContent()))
            return false;
        if (this.daysToLive == null && other.getDaysToLive() != null)
            return false;
        if (this.daysToLive != null && !this.daysToLive.equals(other.getDaysToLive()))
            return false;
        if (this.lastNotifiedAt == null && other.getLastNotifiedAt() != null)
            return false;
        if (this.lastNotifiedAt != null && !this.lastNotifiedAt.equals(other.getLastNotifiedAt()))
            return false;
        if (this.currentPeriod == null && other.getCurrentPeriod() != null)
            return false;
        return this.currentPeriod != null && this.currentPeriod.equals(other.getCurrentPeriod());
    }

    /**
     * Offers support for deserializing a Reminder instance that is JSON-ified.
     * @param json Source JSON describing the Reminder.
     * @return Deserialized Reminder instance.
     */
    public static Reminder fromJSON(String json) {
        return new Gson().fromJson(json, Reminder.class);
    }

    /**
     * Offers support for serializing a Reminder instance to JSON.
     * @return JSON-ified (serialized) Reminder instance.
     */
    public String toJSON() {
        return new Gson().toJson(this);
    }

    public String toString() {
        return super.toString() + "{" + identifier + ": summary=\"" + summary + "\", content: \""
                + "\", daysToLive=" + daysToLive + ", lastNotified=" + lastNotifiedAt
                + ", currentPeriod=" + currentPeriod;
    }
}
