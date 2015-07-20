package com.shemasoft.android.bard.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Vinson
 *
 * Model class that repesents a folder of audiobook files for a single AudioBook
 */
public class AudioBook {



    private static final String JSON_TITLE = "title";
    private static final String JSON_BOOKPATH = "bookPath";
    private static final String JSON_COVER_IMAGE_PATH = "coverImagePath";
    private static final String JSON_CURR_FILE_INDEX = "currentFileIndex";
    private static final String JSON_CURR_POS = "currentPosition";
    private static final String JSON_TOTAL_DURATION = "totalDuration";
    private static final String JSON_BOOK_FILES = "bookFiles";

    private String title;
    private String bookPath;
    private String coverImagePath;
    private int currentFileIndex = 0;
    private int currentPosition = 0;
    private long totalDuration = 0;
    private List<AudioBookFile> bookFiles;

    public AudioBook() {
        bookFiles = new ArrayList<>();
    }


    public AudioBook(JSONObject json) throws JSONException {
        setTitle(json.getString(JSON_TITLE));
        setBookPath(json.getString(JSON_BOOKPATH));
        setCoverImagePath(json.getString(JSON_COVER_IMAGE_PATH));
        setCurrentFileIndex(json.getInt(JSON_CURR_FILE_INDEX));
        setCurrentPosition(json.getInt(JSON_CURR_POS));
        setTotalDuration(json.getLong(JSON_TOTAL_DURATION));
        setBookFiles(getBookFilesFromJSON(json));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public int getCurrentFileIndex() {
        return currentFileIndex;
    }

    public void setCurrentFileIndex(int currentFileIndex) {
        this.currentFileIndex = currentFileIndex;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<AudioBookFile> getBookFiles() {
        return bookFiles;
    }

    public void setBookFiles(List<AudioBookFile> bookFiles) {
        this.bookFiles = bookFiles;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_TITLE, getTitle());
        json.put(JSON_BOOKPATH, getBookPath());
        json.put(JSON_COVER_IMAGE_PATH, getCoverImagePath());
        json.put(JSON_CURR_FILE_INDEX, getCurrentFileIndex());
        json.put(JSON_CURR_POS, getCurrentPosition());
        json.put(JSON_TOTAL_DURATION, getTotalDuration());
        json.put(JSON_BOOK_FILES, getBookFilesAsJSON());
        return json;
    }

    private JSONArray getBookFilesAsJSON() throws JSONException {
        JSONArray array = new JSONArray();
        for(AudioBookFile bookFile: getBookFiles()) {
            array.put(bookFile.toJSON());
        }
        return array;
    }

    private List<AudioBookFile> getBookFilesFromJSON(JSONObject json) throws JSONException {
        JSONArray array = json.getJSONArray(JSON_BOOK_FILES);
        List<AudioBookFile> bookFileList = new ArrayList<>();
        for(int i = 0; i<array.length(); i++) {
            bookFileList.add(new AudioBookFile(array.getJSONObject(i)));
        }
        return bookFileList;
    }
}
