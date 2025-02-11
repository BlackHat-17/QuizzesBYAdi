package com.example.quizesbyadi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getStartedButton = findViewById(R.id.getStartedButton);

        // Start QuizActivity when clicking Get Started
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, QuizActivity.class);
            startActivity(intent);
        });
    }
}
