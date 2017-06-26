package com.olive.aoub_android.sdk.model;

/**
 * Created by jeyOlive 2017-06-06.
 */
public class AppAction {

    private String start_time;
    private String duration;
    private String end_time;

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
