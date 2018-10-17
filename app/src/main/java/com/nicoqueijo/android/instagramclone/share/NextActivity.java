package com.nicoqueijo.android.instagramclone.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nicoqueijo.android.instagramclone.R;

public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Toast.makeText(this, getIntent().getStringExtra("selected_image"), Toast.LENGTH_SHORT).show();
    }
}
