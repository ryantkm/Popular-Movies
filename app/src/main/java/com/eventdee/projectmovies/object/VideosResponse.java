package com.eventdee.projectmovies.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VideosResponse {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public ArrayList<Video> results = new ArrayList<Video>();

    public VideosResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<Video> getResults() {
        return results;
    }

    public void setResults(ArrayList<Video> results) {
        this.results = results;
    }
}
