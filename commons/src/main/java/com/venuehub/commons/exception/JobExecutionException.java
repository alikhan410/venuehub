package com.venuehub.commons.exception;

public class JobExecutionException extends InternalServerException{
    private static final String field="Job Scheduler";
    private static final String error = "Job execution failed in the server";
    public JobExecutionException(String field, String error) {
        super(field, error);
    }
}
