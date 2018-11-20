package com.example.mayank.task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mayank.task.R.layout.activity_full_image);

        String flag = getIntent().getStringExtra("flag");
        if (flag != null) {
            ImageView imageView = findViewById(com.example.mayank.task.R.id.flag);
            Picasso.get().load(flag).placeholder(com.example.mayank.task.R.drawable.flag).into(imageView);
        }


    }
}
