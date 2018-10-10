package com.nicoqueijo.android.instagramclone.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nicoqueijo.android.instagramclone.R;
import com.nicoqueijo.android.instagramclone.utils.UniversalImageLoader;

public class EditProfileFragment extends Fragment {

    private ImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        setProfileImage();
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    private void setProfileImage() {
        String imgURL = "o.aolcdn.com/images/dims?quality=100&image_uri=http%3A%2F%2Fo.aolcdn.com%2Fhss%2Fstorage%2Fmidas%2F965fc84051391e2ed53ce19881178931%2F202486815%2F10%2Bam%2BOfficial%2BAnnounce_G_FB%2Bcopy.jpg&client=amp-blogside-v2&signature=6a6cab645d24897df83fe4a2fc399f58891167ca";
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
    }

}
