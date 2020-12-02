package com.cs246.team1.spacedrepetition;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.*;

public class ReviewTest {
    @Test
    public void testSimpleSchedule() {
        Review.ReviewSchedule schedule = new Review.SimpleSchedule();
        assertEquals(schedule.getType(), Review.ReviewScheduleType.SIMPLE);
        LocalDate current = LocalDate.now();
        LocalDate created = LocalDate.now().minusDays(1);
        LocalDate lastNotified = LocalDate.now();

        Date nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        LocalDate nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, lastNotified.plusDays(1));

        lastNotified = null;
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current);

        created = current.minusDays(6);
        lastNotified = current.minusDays(5);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current.minusDays(4));

        created = current.minusDays(6);
        lastNotified = current.minusDays(2);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current.minusDays(1));

        created = current.minusDays(8);
        lastNotified = current.minusDays(1);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current.plusDays(6));

        created = current.minusDays(28);
        lastNotified = null;
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, created.plusDays(7));

        created = current.minusDays(30);
        lastNotified = current.minusDays(1);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current.plusDays(6));

        created = current.minusDays(31);
        lastNotified = current.minusDays(1);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current.plusDays(29));

        created = current.minusDays(91);
        lastNotified = current.minusDays(1);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, created.plusDays(120));

        created = null;
        lastNotified = current.minusDays(1);
        nextReviewDate = schedule.getNextReviewDate(
                Review.dateFromLocalDate(created), Review.dateFromLocalDate(lastNotified));
        nextReview = Review.localDateFromDate(nextReviewDate);
        assertEquals(nextReview, current);
    }
}
