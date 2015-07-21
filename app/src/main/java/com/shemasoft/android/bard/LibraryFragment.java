package com.shemasoft.android.bard;

import android.content.Intent;
import android.os.Bundle;
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

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

/**
 * Created by jv on 7/20/2015.
 */
public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";
    private static final int REQUEST_DIRECTORY = 0;

    private AudioBookLibrary library;
    private ArrayAdapter<AudioBook> adapter;

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
        adapter = new ArrayAdapter<AudioBook>(getActivity(), android.R.layout.simple_list_item_1, library.getAudioBooks());
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
                final Intent chooserIntent = new Intent(getActivity(), DirectoryChooserActivity.class);

                // Optional: Allow users to create a new directory with a fixed name.
                chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME,
                        "DirChooserSample");

                // REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
                startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String path = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                Log.d(TAG, "Chose Directory " + path);
                AudioBookLibraryManager manager = AudioBookLibraryManager.get(getActivity());
                AudioBook book = manager.loadBookFromPath(path);
                library.getAudioBooks().add(book);
                manager.save();
                adapter.notifyDataSetChanged();
            } else {
                // Nothing selected
            }
        }
    }
}
