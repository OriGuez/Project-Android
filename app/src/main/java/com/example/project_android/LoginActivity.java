package com.example.project_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import com.example.project_android.model.TokenRequest;
import com.example.project_android.viewModel.UsersViewModel;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {
    private UsersViewModel viewModel;
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
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.editTextUserName);
        passwordEditText = findViewById(R.id.editTextPassword);
        final TextInputLayout passwordLayout = findViewById(R.id.textInputLayoutPassword);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            loginButton.setEnabled(false);
            String enteredUsername = usernameEditText.getText().toString();
            String enteredPassword = passwordEditText.getText().toString();
            TokenRequest request = new TokenRequest(enteredUsername, enteredPassword);
            viewModel.login(request).observe(this, tokenResponse -> {
                if (tokenResponse != null) {
                    //saving the token in memory
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", tokenResponse.getToken());
                    editor.putString("username", enteredUsername);
                    editor.apply();
                    Log.d("ADDED", "ADDED TOKEN TO MEMORY");
                    // Handle successful token creation
                    Toast.makeText(getApplicationContext(), getString(R.string.loggedInSuccessfully), Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                    finish(); // close the LoginActivity
                } else {
                    loginButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                    // Handle the error case
                }
            });
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {
            }
        });
    }
}