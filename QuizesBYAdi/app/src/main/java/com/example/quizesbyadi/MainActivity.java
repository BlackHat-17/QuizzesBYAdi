package com.example.quizesbyadi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private FirebaseAuth mAuth; // Initialize Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI Elements
        EditText etname = findViewById(R.id.name);
        EditText etmob = findViewById(R.id.number);
        EditText etemail = findViewById(R.id.email);
        EditText etaddress = findViewById(R.id.Address);
        EditText etpassword = findViewById(R.id.password);
        EditText etcpassword = findViewById(R.id.cpassword);
        Button registerbtn = findViewById(R.id.Registerbutton);
        RadioGroup gender = findViewById(R.id.radiogrp);

        // Button Click Listener
        registerbtn.setOnClickListener(v -> saveUser(etname, etaddress, etemail, etmob, etpassword, etcpassword, gender));
        TextView getStartedTextView = findViewById(R.id.log);

        // Click listener to start MainActivity
        getStartedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void saveUser(EditText etname, EditText etaddress, EditText etemail, EditText etmob, EditText etpassword, EditText etcpassword, RadioGroup gender) {
        // Get input values
        String name = etname.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String address = etaddress.getText().toString().trim();
        String password = etpassword.getText().toString().trim();
        String cpassword = etcpassword.getText().toString().trim();
        String mob = etmob.getText().toString().trim();

        // Validate Inputs
        if (mob.isEmpty() || name.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty() || cpassword.isEmpty()) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(cpassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate gender selection
        int selectedId = gender.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected gender
        RadioButton selectedRadioButton = findViewById(selectedId);
        String genderval = selectedRadioButton.getText().toString();

        // Firebase Authentication: Register User
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save to Firebase Realtime Database
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("UserCollection");
                            String userId = database.push().getKey();
                            if (userId != null) {
                                database.child(userId).setValue(new User(name, email, address, mob, genderval, password, cpassword))
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
