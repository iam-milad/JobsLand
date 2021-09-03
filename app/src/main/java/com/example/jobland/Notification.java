package com.example.jobland;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notification {
    private String title;
    private String message;
    private boolean viewed;
    private  @ServerTimestamp Date time;
    private String state;
    private String jobId;
    private String applicantId;
    private boolean accepted;

    public Notification(){

    }

    public Notification(String title, String message, boolean viewed, Date time, String state, String jobId, String applicantId, boolean accepted) {
        this.title = title;
        this.message = message;
        this.viewed = viewed;
        this.time = time;
        this.state = state;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.accepted = accepted;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isViewed() {
        return viewed;
    }

    public Date getTime() {
        return time;
    }

    public String getState() {
        return state;
    }

    public String getJobId() {
        return jobId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
