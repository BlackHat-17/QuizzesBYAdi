package com.example.quizesbyadi;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {
    String apiKey = getString(R.string.key);
    private static final String API_URL = "https://quizapi.io/api/v1/questions?apiKey=$apikey&limit=10";
    private TextView questionTextView, timerTextView, scoreTextView;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private List<JSONObject> questionsList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        timerTextView = findViewById(R.id.timer);
        scoreTextView = findViewById(R.id.score);

        databaseReference = FirebaseDatabase.getInstance().getReference("QuizScores");

        fetchQuestions();

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            loadNextQuestion();
        });
    }

    private void fetchQuestions() {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    questionsList.add(jsonArray.getJSONObject(i));
                }
                runOnUiThread(this::loadNextQuestion);
            } catch (Exception e) {
                runOnUiThread(() -> questionTextView.setText("Failed to load questions"));
            }
        }).start();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            JSONObject questionObject = questionsList.get(currentQuestionIndex);
            try {
                questionTextView.setText(questionObject.getString("question"));
                JSONArray options = questionObject.getJSONObject("answers").names();
                optionsGroup.clearCheck();
                optionsGroup.removeAllViews();

                for (int i = 0; i< options.length(); i++) {
                    RadioButton radioButton = new RadioButton(this);
                    String radioval=questionObject.getJSONObject("answers").getString(options.getString(i));
                    if((!radioval.equals("null")) | radioval.isEmpty()) {
                        radioButton.setText(radioval);
                        optionsGroup.addView(radioButton);
                    }
                }
                startTimer();
            } catch (JSONException e) {
                questionTextView.setText("Error loading question");
            }
        } else {
            questionTextView.setText("Quiz Completed!");
            nextButton.setText("Finish");
            nextButton.setOnClickListener(v -> {
                Intent intent=new Intent(QuizActivity.this,ScoresActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            });
        }
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            try {
                String correctAnswer = questionsList.get(currentQuestionIndex).getJSONObject("correct_answers").getString("answer_a_correct");
                if (correctAnswer.equalsIgnoreCase("true") && selectedRadioButton.getText().toString().equals(questionsList.get(currentQuestionIndex).getJSONObject("answers").getString("answer_a"))) {
                    score++;
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error checking answer", Toast.LENGTH_SHORT).show();
            }
        }
        updateScore();
        currentQuestionIndex++;
    }

    private void updateScore() {
        scoreTextView.setText("Score: " + score);
        databaseReference.child("userScore").setValue(score);
    }

    private void startTimer() {
        // Cancel any existing timer before starting a new one
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                Toast.makeText(QuizActivity.this, "⏳ TIMES UP!!!", Toast.LENGTH_SHORT).show();

                // Ensure timer is stopped before switching activities
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                // Move to ScoresActivity when time runs out
                Intent intent = new Intent(QuizActivity.this, ScoresActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }
        }.start(); // Start the countdown timer
    }
}