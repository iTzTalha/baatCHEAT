package com.example.baatcheat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button btn_getStarted;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_getStarted = findViewById(R.id.btn_getStarted);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        checksplashscreen();

        if (currentUser != null) {

            SendUserToMainActivity();

        } else {
            mAuth.signOut();
        }

    }

    private void SendUserToMainActivity() {
        Intent mainActivity = new Intent(StartActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    private void checksplashscreen() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            //show start activity
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
    }

}
