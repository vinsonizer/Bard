package com.shemasoft.android.bard.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jason Vinson
 *
 * Model class that represents a single file in an AudioBook
 */
public class AudioBookFile {

    private static final String JSON_FILEPATH="filePath";
    private static final String JSON_FILE_DURATION="fileDuration";

    private String filePath;
    private long fileDuration;

    public AudioBookFile(String filePath, long fileDuration) {
        this.filePath = filePath;
        this.fileDuration = fileDuration;
    }

    public AudioBookFile(JSONObject json) throws JSONException {
        this.fileDuration = json.getLong(JSON_FILE_DURATION);
        this.filePath = json.getString(JSON_FILEPATH);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(long fileDuration) {
        this.fileDuration = fileDuration;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILEPATH, getFilePath());
        json.put(JSON_FILE_DURATION, getFileDuration());
        return json;
    }
}
