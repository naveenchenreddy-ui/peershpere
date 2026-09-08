package com.peersphere.entity;

public enum NotificationType {
    NEW_ANSWER,           // someone answered your question
    ANSWER_ACCEPTED,      // your answer was accepted
    NEW_SESSION,          // new session in your group
    SESSION_CANCELLED,    // session in your group was cancelled
    NEW_MEMBER,           // someone joined your group
    NEW_NOTE,             // new note in your group
    QUESTION_UPVOTE,      // someone upvoted your question
    ANSWER_UPVOTE         // someone upvoted your answer
}