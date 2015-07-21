package com.shemasoft.android.bard;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.shemasoft.android.bard.model.AudioBook;
import com.shemasoft.android.bard.model.AudioBookFile;
import com.shemasoft.android.bard.model.AudioBookLibrary;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author Jason Vinson
 *         <p/>
 *         Class to manage AudioBookLibrary objects, primarily focused on Load/Save
 */
public class AudioBookLibraryManager {

    private static final String TAG = "AudioBookLibraryManager";

    private static final String LIBRARY_JSON_FILE = "bard.json";

    private static AudioBookLibraryManager audioBookManager;

    private AudioBookLibrary library;
    private Context context;

    private AudioBookLibraryManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AudioBookLibraryManager get(Context context) {
        if (audioBookManager == null) {
            audioBookManager = new AudioBookLibraryManager(context);
        }
        return audioBookManager;
    }


    public boolean save() {
        try (
                OutputStream os = context.openFileOutput(LIBRARY_JSON_FILE, Context.MODE_PRIVATE);
                Writer writer = new OutputStreamWriter(os)
        ) {
            JSONObject json = library.toJSON();
            writer.write(json.toString());
            writer.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving library", e);
            return false;
        }
    }

    public AudioBookLibrary load() throws IOException, JSONException {
        if (library == null) {
            library = new AudioBookLibrary();
            try (
                    InputStream is = context.openFileInput(LIBRARY_JSON_FILE);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is))
            ) {
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String jsonString = sb.toString();
                if (!jsonString.isEmpty()) {
                    JSONObject json = (JSONObject) new JSONTokener(sb.toString()).nextValue();
                    library = new AudioBookLibrary(json);
                }
            } catch (FileNotFoundException e) {
                // Ignore this, since it happens on first load
            }
        }
        return library;
    }

    public AudioBook loadBookFromPath(String audioBookDirectoryPath) {
        AudioBook audioBook = null;
        if (null != audioBookDirectoryPath && !audioBookDirectoryPath.isEmpty()) {
            // Try to load directory as audiobook folder
            // TODO: Try to look at directory of files once instead of multiple iterations
            File audioBookDirectory = new File(audioBookDirectoryPath);
            if (audioBookDirectory.exists()) {

                audioBook = new AudioBook();
                audioBook.setBookPath(audioBookDirectoryPath);
                audioBook.setTitle(audioBookDirectory.getName());
                File[] audioBookFileList = audioBookDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        // TODO: Support more than Mp3?
                        return filename.endsWith(".mp3");
                    }
                });

                int totalDuration = 0;
                int startingOffset = 0;
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                if (null != audioBookFileList) {
                    for (File audioBookFile : audioBookFileList) {
                        mmr.setDataSource(audioBookFile.getPath());
                        int duration = new Integer(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                        audioBook.getBookFiles().add(new AudioBookFile(audioBookFile.getPath(), startingOffset, duration));
                        totalDuration += duration;
                        startingOffset += duration;
                    }
                }
                audioBook.setTotalDuration(totalDuration);

                File[] imageFiles = audioBookDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".jpg") ||
                                filename.endsWith(".jpeg") ||
                                filename.endsWith(".gif") ||
                                filename.endsWith(".png");
                    }
                });

                if (null != imageFiles && imageFiles.length > 0) {
                    audioBook.setCoverImagePath(imageFiles[0].getAbsolutePath());
                }
            }
        }
        return audioBook;
    }
}
