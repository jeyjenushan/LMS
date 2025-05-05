package org.ai.server.exception;

public class CourseOperationException extends RuntimeException {
    private final String userMessage;

    public CourseOperationException(String userMessage, String technicalMessage) {
        super(technicalMessage);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}