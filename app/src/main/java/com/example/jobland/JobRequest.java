package com.example.jobland;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class JobRequest {
    private boolean accepted;
    private boolean appliedJob;
    private String hostId;
    private String title;
    private @ServerTimestamp
    Date applyDate;

    public JobRequest(){}

    public JobRequest(boolean accepted, boolean appliedJob, String hostId, String title, Date applyDate) {
        this.accepted = accepted;
        this.appliedJob = appliedJob;
        this.hostId = hostId;
        this.title = title;
        this.applyDate = applyDate;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isAppliedJob() {
        return appliedJob;
    }

    public String getHostId() {
        return hostId;
    }

    public String getTitle() {
        return title;
    }

    public Date getApplyDate() {
        return applyDate;
    }
}
