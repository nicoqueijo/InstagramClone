package com.nicoqueijo.android.instagramclone.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nicoqueijo.android.instagramclone.R;
import com.nicoqueijo.android.instagramclone.home.HomeActivity;
import com.nicoqueijo.android.instagramclone.models.Photo;
import com.nicoqueijo.android.instagramclone.models.User;
import com.nicoqueijo.android.instagramclone.models.UserAccountSettings;
import com.nicoqueijo.android.instagramclone.models.UserSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseMethods {

    private static final String TAG = FirebaseMethods.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, int count, String imgUrl) {

        FilePaths filePaths = new FilePaths();
        // case 1: new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));
            // convert imgUrl to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getUploadSessionUri();

                    // add the new photo to 'photos' node and 'user_photos' node
                    addPhotoToDatabase(caption, firebaseUrl.toString());

                    // navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                }
            });
        }
        // case 2: new profile photo
        else if (photoType.equals(mContext.getString(R.string.profile_photo))) {

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");
            // convert imgUrl to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getUploadSessionUri();

                    // insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                }
            });

        }
    }

    private void setProfilePhoto(String url) {
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url) {
        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        // insert to database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
    }

    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot.
                child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;
        }
        return count;
    }

    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber) {
        if (displayName != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_display_name)).setValue(displayName);
        }
        if (website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_website)).setValue(website);
        }
        if (description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_description)).setValue(description);
        }
        if (phoneNumber != 0L) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_phone_number)).setValue(phoneNumber);
        }
    }

    public void updateUsername(String username) {
        myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_username)).setValue(username);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_username)).setValue(username);
    }

    public void updateEmail(String email) {
        myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_email)).setValue(email);
    }

//    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//        User user = new User();
//        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }

    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            // send verification email
                            sendVerificationEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }
                    }
                });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void addNewUser(String email, String username, String description, String website, String profile_photo) {
        User user = new User(userID, 1, email, StringManipulation.condenseUsername(username));
        myRef.child(mContext.getString(R.string.dbname_users)).child(userID).setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).setValue(settings);
    }

    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            // user_account_setting node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                try {
                    settings.setDisplay_name(ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name());
                    settings.setUsername(ds.child(userID).getValue(UserAccountSettings.class).getUsername());
                    settings.setWebsite(ds.child(userID).getValue(UserAccountSettings.class).getWebsite());
                    settings.setDescription(ds.child(userID).getValue(UserAccountSettings.class).getDescription());
                    settings.setProfile_photo(ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo());
                    settings.setPosts(ds.child(userID).getValue(UserAccountSettings.class).getPosts());
                    settings.setFollowing(ds.child(userID).getValue(UserAccountSettings.class).getFollowing());
                    settings.setFollowers(ds.child(userID).getValue(UserAccountSettings.class).getFollowers());
                } catch (NullPointerException e) {
                    Log.d(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
            }
            // users node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                user.setPhone_number(ds.child(userID).getValue(User.class).getPhone_number());
                user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());
            }
        }
        return new UserSettings(user, settings);
    }
}
