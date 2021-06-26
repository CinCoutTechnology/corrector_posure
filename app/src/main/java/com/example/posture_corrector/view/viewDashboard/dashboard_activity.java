package com.example.posture_corrector.view.viewDashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.posture_corrector.R;
import com.example.posture_corrector.view.viewConnect.connect_activity;
import com.example.posture_corrector.view.viewConnect.connect_activity2;
import com.example.posture_corrector.view.viewConnect.link_device_activity;
import com.example.posture_corrector.view.viewStudy.study_activity;

public class dashboard_activity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private AlertDialog dialogBu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_activity);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Corrector de Postura");
        builder.setMessage("Muy pronto tendremos información para ti");
        builder.setPositiveButton("Confirmar", null);

        findViewById(R.id.card_view).setOnClickListener(v -> startActivity(new Intent(dashboard_activity.this, link_device_activity.class)));
        findViewById(R.id.card_view_3).setOnClickListener(v -> {
            dialogBu = builder.create();
            dialogBu.show();
            Button positiveButton = dialogBu.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(Color.parseColor(getString(R.string.color)));
        });
    }
}