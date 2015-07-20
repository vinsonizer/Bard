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
    private static final String JSON_STARTING_OFFSET = "startingOffset";

    private String filePath;
    private int fileDuration;
    private int startingOffset;

    public AudioBookFile(String filePath, int startingOffset, int fileDuration) {
        this.filePath = filePath;
        this.startingOffset = startingOffset;
        this.fileDuration = fileDuration;
    }

    public AudioBookFile(JSONObject json) throws JSONException {
        this.fileDuration = json.getInt(JSON_FILE_DURATION);
        this.startingOffset = json.getInt(JSON_STARTING_OFFSET);
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

    public void setFileDuration(int fileDuration) {
        this.fileDuration = fileDuration;
    }

    public int getStartingOffset() {
        return startingOffset;
    }

    public void setStartingOffset(int startingOffset) {
        this.startingOffset = startingOffset;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILEPATH, getFilePath());
        json.put(JSON_FILE_DURATION, getFileDuration());
        json.put(JSON_STARTING_OFFSET, getStartingOffset());
        return json;
    }
}
