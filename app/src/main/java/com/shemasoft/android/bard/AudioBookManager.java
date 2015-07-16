package com.shemasoft.android.bard;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.preference.PreferenceManager;

import com.shemasoft.android.bard.model.AudioBook;
import com.shemasoft.android.bard.model.AudioBookFile;
import com.shemasoft.android.bard.model.BardException;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

/**
 * Created by jv on 7/15/2015.
 */
public class AudioBookManager {

    private static final String TAG = "AudioBookManager";

    private static final String AUDIOBOOK_JSON_FILE="bard.json";
    private static final String PREFS_AUDIOBOOK_PATH = "prefs_audiobook_path";

    private static AudioBookManager audioBookManager;

    private Context context;

    private AudioBookManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AudioBookManager get(Context context) {
        if(audioBookManager == null) {
            audioBookManager = new AudioBookManager(context);
        }
        return audioBookManager;
    }


    public AudioBook load() throws BardException {
        String audioBookDirectoryPath = PreferenceManager.getDefaultSharedPreferences(this.context).getString(PREFS_AUDIOBOOK_PATH, "");
        AudioBook audioBook = null;
        try {
            // Try to load directory as audiobook folder
            // TODO: Try to look at directory of files once instead of multiple iterations
            File audioBookDirectory = new File(audioBookDirectoryPath);
            if(audioBookDirectory.exists()) {

                // Check for existing json descriptor
                File[] files = audioBookDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.equals(AUDIOBOOK_JSON_FILE);
                    }
                });


                if (null != files && files.length == 1) {
                    Scanner scanner = new Scanner(files[0]);
                    scanner.useDelimiter("//Z");
                    String jsonString = scanner.next();
                    audioBook = new AudioBook((JSONObject) new JSONTokener(jsonString).nextValue());
                } else {
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

                    long totalDuration = 0;
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    if (null != audioBookFileList) {
                        for (int i = 0; i < audioBookFileList.length; i++) {
                            mmr.setDataSource(audioBookFileList[i].getPath());
                            long duration = new Long(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            audioBook.getBookFiles().add(new AudioBookFile(audioBookFileList[i].getPath(), duration));
                            totalDuration += duration;
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

                    if(null != imageFiles && imageFiles.length > 0) {
                        audioBook.setCoverImagePath(imageFiles[0].getAbsolutePath());
                    }
                }
            }
            return audioBook;
        } catch (Exception e) {
            throw new BardException(e);
        }
    }
}
