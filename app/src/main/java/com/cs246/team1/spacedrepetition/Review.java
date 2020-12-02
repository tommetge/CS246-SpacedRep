package com.cs246.team1.spacedrepetition;

import android.util.Log;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {
    private static final String LOGTAG = "Review";

    /**
     * Integrates with Reminder to provide a list of Reminder instances that should be included in
     * the next review session or notification. This could be genericized but there isn't any point.
     */
    public static class ReminderReview {
        public static List<Reminder> getRemindersForReview(List<Reminder> reminders) {
            LocalDate current = LocalDate.now();
            ArrayList<Reminder> remindersToReview = new ArrayList<>();
            for (Reminder reminder : reminders) {
                LocalDate expiresAt = localDateFromDate(
                        reminder.getCreatedAt()).plusDays(reminder.getDaysToLive());
                if (expiresAt.compareTo(current) <= 0) {
                    Log.d(LOGTAG,
                            String.format("Skipping expired reminder: %s", reminder.toString()));
                    continue;
                }
                LocalDate reviewDate = localDateFromDate(reminder.getNextReviewDate());
                if (reviewDate.compareTo(current) <= 0) {
                    Log.d(LOGTAG,
                            String.format("Adding reminder to review: %s", reminder.toString()));
                    remindersToReview.add(reminder);
                }
            }

            return remindersToReview;
        }
    }

    /**
     * Interface for interacting with any of the concrete review schedule implementations. There may
     * be several ReviewSchedules at the same time, though we should only ship one of them. A
     * Reminder will store its associated schedule type and notifications and reviews will be
     * driven by that schedule.
     */
    public interface ReviewSchedule {
        /**
         * The ReviewScheduleType implemented by the class.
         * @return Applicable ReviewScheduleType
         */
        ReviewScheduleType getType();

        /**
         * Returns the date of the next review based on the specific review schedule implementation.
         * Note: This can return a date in the past. It will never return a date before the
         * provided lastReviewDate parameter. If the date is past, the Reminder should be included
         * in the next review.
         * @param startDate date that the schedule was started for the specific Reminder.
         *                  Corresponds to Reminder.createdAt
         * @param lastReviewDate date of the last known review. Corresponds to
         *                       Reminder.lastNotifiedAt
         * @return date of the next review
         */
        Date getNextReviewDate(Date startDate, Date lastReviewDate);
    }

    /**
     * All valid review schedules. Each enumerated type must correspond to a valid ReviewSchedule
     * implementation.
     */
    public enum ReviewScheduleType {
        SIMPLE(1);

        private int value;

        ReviewScheduleType(int i) {
            this.value = i;
        }

        /**
         * Converts an int value into a ReviewScheduleType
         * @param type the int value corresponding to the ReviewScheduleType
         * @return the ReviewScheduleType or null, if invalid
         */
        public static ReviewScheduleType valueOf(int type) {
            for (ReviewScheduleType t : values()) {
                if (t.value == type) {
                    return t;
                }
            }

            // Default value
            return SIMPLE;
        }

        /**
         * The int value of a ReviewScheduleType. Reversible via ReviewScheduleType.valueOf().
         * @return int value of the ReviewScheduleType
         */
        public int getValue() {
            return value;
        }
    }

    /**
     * Offers the default, current ReviewSchedule that the application should use.
     * @return default ReviewSchedule instance.
     */
    public static ReviewSchedule defaultSchedule() {
        return new SimpleSchedule();
    }

    /**
     * Allows the caller to retrieve a ReviewSchedule implementation for the provided type.
     * @param type the type associated with the ReviewSchedule
     * @return the correct ReviewSchedule implementation if available
     */
    public static ReviewSchedule getSchedule(ReviewScheduleType type) {
        switch (type) {
            case SIMPLE:
                return new SimpleSchedule();
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    // Date helpers to keep things clean and readable
    protected static LocalDate localDateFromDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    protected static Date dateFromLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private static int daysBetween(LocalDate begin, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(begin, end);
    }

    /**
     * The most bare-bones schedule that is backed by research. Reviews happen:
     * 1. Every day for 7 days
     * 2. Every week for 4 weeks
     * 3. Every month in perpetuum
     */
    public static class SimpleSchedule implements ReviewSchedule {

        @Override
        public ReviewScheduleType getType() {
            return ReviewScheduleType.SIMPLE;
        }

        @Override
        public Date getNextReviewDate(Date startDate, Date lastReviewDate) {
            if (startDate == null && lastReviewDate == null) {
                Log.e(LOGTAG, "Invalid start / lastReview dates!");
                return null;
            }

            if (startDate == null) {
                startDate = lastReviewDate;
            }
            if (lastReviewDate == null) {
                lastReviewDate = startDate;
            }

            LocalDate start = localDateFromDate(startDate);
            LocalDate lastReview = localDateFromDate(lastReviewDate);

            Log.d(LOGTAG, String.format(
                    "Getting next review date: start=%s, lastReview=%s",
                    start.toString(), lastReview.toString()));

            LocalDate current = LocalDate.now();
            int daysElapsed = daysBetween(start, current);
            int daysSinceReview = daysBetween(lastReview, current);
            Log.d(LOGTAG,
                    String.format("Elapsed: %d, since review: %d", daysElapsed, daysSinceReview));
            if (daysElapsed < 8) { // Every day for a week
                if (daysSinceReview >= 0) {
                    Log.d(LOGTAG, String.format("Returning %s", lastReview.plusDays(1).toString()));
                    return dateFromLocalDate(lastReview.plusDays(1));
                }
            } else if (daysElapsed < 31) { // Every week for a month
                if (daysSinceReview >= 0) {
                    Log.d(LOGTAG, String.format("Returning %s", lastReview.plusDays(7).toString()));
                    return dateFromLocalDate(lastReview.plusDays(7));
                }
            } else { // every month
                if (daysSinceReview >= 0) {
                    Log.d(LOGTAG, String.format("Returning %s",
                                    lastReview.plusDays(30).toString()));
                    return dateFromLocalDate(lastReview.plusDays(30));
                }
            }

            Log.d(LOGTAG, String.format("Returning %s", lastReviewDate.toString()));
            return lastReviewDate;
        }
    }
}
