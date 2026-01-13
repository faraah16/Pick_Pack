package com.example.pick_pack;

public class ReviewManager {

    private static int lastRating = 0;
    private static String lastComment = "";

    public static void saveReview(int rating, String comment) {
        lastRating = rating;
        lastComment = comment;
    }

    public static int getLastRating() {
        return lastRating;
    }

    public static String getLastComment() {
        return lastComment;
    }
}
