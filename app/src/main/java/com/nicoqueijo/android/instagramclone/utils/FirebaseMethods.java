package com.nicoqueijo.android.instagramclone.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.nicoqueijo.android.instagramclone.R;
import com.nicoqueijo.android.instagramclone.models.User;

public class FirebaseMethods {

    private static final String TAG = FirebaseMethods.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
                return true;
            }
        }
        return true;
    }

    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }
                    }
                });
    }
}
