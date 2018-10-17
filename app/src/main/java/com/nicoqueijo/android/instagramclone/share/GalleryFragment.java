package com.nicoqueijo.android.instagramclone.share;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nicoqueijo.android.instagramclone.R;
import com.nicoqueijo.android.instagramclone.utils.FilePaths;
import com.nicoqueijo.android.instagramclone.utils.FileSearch;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private static final String TAG = GalleryFragment.class.getSimpleName();

    // widgets
    private GridView mGridView;
    private ImageView mGalleryImage;
    private ProgressBar mProgressBar;
    private Spinner mDirectorySpinner;

    // vars
    private ArrayList<String> directories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGridView = view.findViewById(R.id.gridView);
        mGalleryImage = view.findViewById(R.id.galleryImageView);
        mProgressBar = view.findViewById(R.id.progressBar);
        mDirectorySpinner = view.findViewById(R.id.spinnerDirectory);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();

        ImageView shareClose = view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        init();
        return view;
    }

    private void init() {
        FilePaths filePaths = new FilePaths();
        // check for other folders inside "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, directories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDirectorySpinner.setAdapter(adapter);
        mDirectorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
