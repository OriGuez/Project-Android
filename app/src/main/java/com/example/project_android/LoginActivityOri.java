

package com.example.project_android;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.view.View;


import com.google.android.material.textfield.TextInputLayout;

public class LoginActivityOri extends Activity {
    public void onCreateAccountClicked(View view) {
        // Start RegistrationActivity
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ori);

        final EditText passwordEditText = findViewById(R.id.editTextPassword);
        final TextInputLayout passwordLayout = findViewById(R.id.textInputLayoutPassword);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.length() < 8) {
                    passwordLayout.setError("Password must be at least 8 characters long");
                } else if (password.matches("\\d+")) {
                    passwordLayout.setError("Password must contain at least one non-numeric character");
                } else {
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}