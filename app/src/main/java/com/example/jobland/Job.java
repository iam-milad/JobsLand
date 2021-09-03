package com.example.jobland;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Job {
    private String title;
    private String date;
    private String fromTime;
    private String toTime;
    private String payRate;
    private String address;
    private String city;
    private String postCode;
    private String phoneNumber;
    private String description;
    private GeoPoint mLocation;
    private Date createdDate;
    private String hostId;
    private Double distance;
    private String jobId;

    public Job(String title, String date, String fromTime, String toTime,
               String payRate, String address, String city, String
                           postCode, String phoneNumber, String description, GeoPoint mLocation, Date createdDate, String hostId, Double distance, String jobId) {
        this.title = title;
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.address = address;
        this.city = city;
        this.postCode = postCode;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.createdDate = createdDate;
        this.mLocation = mLocation;
        this.hostId = hostId;
        this.payRate = payRate;
        this.distance = distance;
        this.jobId = jobId;
    }

    public Job(){}

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPayRate() {
        return payRate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPayRate(String payRate) {
        this.payRate = payRate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getHostId() {
        return hostId;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public GeoPoint getmLocation() {
        return mLocation;
    }

    public void setmLocation(GeoPoint mLocation) {
        this.mLocation = mLocation;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
