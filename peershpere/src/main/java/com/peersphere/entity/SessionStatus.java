package com.peersphere.entity;

public enum SessionStatus {
    SCHEDULED,   // default when created — session is upcoming
    CANCELLED,   // organizer cancelled it
    COMPLETED    // organizer marked it as done
}