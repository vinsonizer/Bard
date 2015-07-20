package com.shemasoft.android.bard.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Vinson
 *         <p/>
 *         Represents the "library" of AudioBooks
 */
public class AudioBookLibrary {

    private static final String JSON_AUDIO_BOOKS = "audioBooks";
    private List<AudioBook> audioBooks;

    public AudioBookLibrary() {
        audioBooks = new ArrayList<>();
    }

    public AudioBookLibrary(JSONObject json) throws JSONException {
        audioBooks = new ArrayList<>();
        JSONArray array = json.getJSONArray(JSON_AUDIO_BOOKS);
        for (int i = 0; i < array.length(); i++) {
            audioBooks.add(new AudioBook(array.getJSONObject(i)));
        }
    }

    public List<AudioBook> getAudioBooks() {
        return audioBooks;
    }

    public void setAudioBooks(List<AudioBook> audioBooks) {
        this.audioBooks = audioBooks;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (AudioBook audioBook : audioBooks) {
            array.put(audioBook.toJSON());
        }
        json.put(JSON_AUDIO_BOOKS, array);
        return json;
    }
}
