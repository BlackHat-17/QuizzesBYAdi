package com.example.quizesbyadi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


public class ScoresActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseReference databaseReference;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        databaseReference = FirebaseDatabase.getInstance().getReference("QuizScores");

        TextView scoreText = findViewById(R.id.scoreText);
        Button restartButton = findViewById(R.id.restartButton);

        // Get score from intent
        int finalScore = getIntent().getIntExtra("score", 0);
        scoreText.setText("🎉 Congratulations! 🎉\nYour Score: " + finalScore + "/10");

        // Restart Quiz
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScoresActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        });
    }
}
