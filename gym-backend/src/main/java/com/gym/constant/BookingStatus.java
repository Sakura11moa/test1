package com.gym.constant;

public final class BookingStatus {
    private BookingStatus() {}

    public static final String PENDING = "0";
    public static final String APPROVED = "1";
    public static final String REJECTED = "2";
    public static final String MEMBER_CANCELLED = "3";
    public static final String ADMIN_CANCELLED = "4";
    public static final String TIMEOUT_REJECTED = "5";
    public static final String FINISHED = "6";
    public static final String NO_SHOW = "7";
}
