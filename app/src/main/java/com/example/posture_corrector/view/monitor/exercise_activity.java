package com.example.posture_corrector.view.monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.posture_corrector.R;

public class exercise_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_activity);
        findViewById(R.id.image3).setOnClickListener(v -> onBackPressed());
    }
}