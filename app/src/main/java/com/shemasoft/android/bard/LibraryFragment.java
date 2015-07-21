package com.shemasoft.android.bard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.shemasoft.android.bard.model.AudioBook;
import com.shemasoft.android.bard.model.AudioBookLibrary;

/**
 * Created by jv on 7/20/2015.
 */
public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";

    private AudioBookLibrary library;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);

        try {
            library = AudioBookLibraryManager.get(getActivity()).load();
        } catch (Exception e) {
            Log.e(TAG, "Failed to load library", e);
            library = new AudioBookLibrary();
        }

        ListView libraryList = (ListView) v.findViewById(R.id.library_listView);
        ArrayAdapter<AudioBook> adapter = new ArrayAdapter<AudioBook>(getActivity(), android.R.layout.simple_list_item_1, library.getAudioBooks());
        libraryList.setAdapter(adapter);
        libraryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), PlayerActivity.class);
                i.putExtra(PlayerFragment.EXTRA_BOOKINDEX, position);
                startActivity(i);
            }
        });

        ImageButton addBookButton = (ImageButton) v.findViewById(R.id.library_addBook);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked Add");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                intent.setDataAndType(uri, "*/*");
                startActivityForResult(Intent.createChooser(intent, "Open folder"), 0);
            }
        });

        return v;
    }

}
