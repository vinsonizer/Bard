package com.shemasoft.android.bard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shemasoft.android.bard.model.AudioBook;
import com.shemasoft.android.bard.model.AudioBookLibrary;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import java.io.File;
import java.util.List;

/**
 * Created by jv on 7/20/2015.
 */
public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";
    private static final int REQUEST_DIRECTORY = 0;

    private AudioBookLibrary library;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);

        try {
            library = AudioBookLibraryManager.get(getActivity()).load();
        } catch (Exception e) {
            Log.e(TAG, "Failed to load library", e);
            library = new AudioBookLibrary();
        }

        mRecyclerView = (RecyclerView) v.findViewById(R.id.library_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new LibraryAdapter(library.getAudioBooks(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        /**

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

         */
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
                mAdapter.notifyDataSetChanged();
            } else {
                // Nothing selected
            }
        }
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> implements View.OnClickListener {


        private List<AudioBook> dataSet;
        private Context context;


        // Provide a suitable constructor (depends on the kind of dataset)
        public LibraryAdapter(List<AudioBook> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int position = LibraryFragment.this.mRecyclerView.indexOfChild(v);
            Intent i = new Intent(getActivity(), PlayerActivity.class);
            i.putExtra(PlayerFragment.EXTRA_BOOKINDEX, position);
            startActivity(i);

        }

        // Create new views (invoked by the layout manager)
        @Override
        public LibraryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_library, parent, false);

            v.setOnClickListener(this);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AudioBook audioBook = dataSet.get(position);
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.cardTitle.setText(audioBook.getTitle());
            holder.cardPosition.setText(String.format(context.getResources().getString(R.string.library_card_position), audioBook.getCurrentPositionString(), audioBook.getTotalDurationString()));
            if (audioBook.getCoverImagePath() != null) {
                File imgFile = new File(audioBook.getCoverImagePath());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.coverImage.setImageBitmap(myBitmap);
                }
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView cardTitle;
            private TextView cardPosition;
            private ImageView coverImage;

            public ViewHolder(View cardView) {
                super(cardView);
                cardTitle = (TextView) cardView.findViewById(R.id.card_view_title);
                cardPosition = (TextView) cardView.findViewById(R.id.card_view_position);
                coverImage = (ImageView) cardView.findViewById(R.id.card_view_cover_image);
            }
        }
    }
}
