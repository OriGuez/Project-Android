package com.example.project_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.example.project_android.model.UserData;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    public void onCreateAccountClicked(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.editTextUserName);
        passwordEditText = findViewById(R.id.editTextPassword);
        final TextInputLayout passwordLayout = findViewById(R.id.textInputLayoutPassword);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String enteredUsername = usernameEditText.getText().toString();
            String enteredPassword = passwordEditText.getText().toString();

            if (validateLogin(enteredUsername, enteredPassword)) {
                finish(); // close the LoginActivity
            } else {
                // If login fails, show a toast message
                Toast.makeText(this, R.string.invalidLogin, Toast.LENGTH_SHORT).show();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.length() < 8) {
                    passwordLayout.setError(getString(R.string.invalidPassword));
                } else if (password.matches("\\d+")) {
                    passwordLayout.setError(getString(R.string.invalidPassword2));
                } else {
                    passwordLayout.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private boolean validateLogin(String username, String password) {
        if (MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    MainActivity.currentUser = user;
                    return true;
                }
            }
        }
        return false;
    }
}