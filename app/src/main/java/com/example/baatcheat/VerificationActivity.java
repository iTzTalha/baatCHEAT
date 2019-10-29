package com.example.baatcheat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;

    EditText usernameText;
    private Button next_phone;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        relativeLayout = findViewById(R.id.profileActivityLayout);

        usernameText = findViewById(R.id.username);
        next_phone = findViewById(R.id.next_phone);

        next_phone.setEnabled(false);
        next_phone.setBackgroundResource(R.drawable.phone_next_disabled);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    next_phone.setEnabled(false);
                    next_phone.setBackgroundResource(R.drawable.phone_next_disabled);

                    next_phone.setVisibility(View.VISIBLE);

                } else {

                    next_phone.setVisibility(View.VISIBLE);

                    next_phone.setEnabled(true);
                    next_phone.setBackgroundResource(R.drawable.btn_next_phone);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String UserName = usernameText.getText().toString().trim().toLowerCase().replace(" ","");

                if (usernameText.length() < 5 || usernameText.length() > 25) {
                    usernameText.startAnimation(VibrateError());
                    shakeItBaby();
                } else {
                    Query usernameQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(UserName);
                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){
                                usernameText.setError("Username is taken");
                                usernameText.requestFocus();
                                return;
                            }else {

                                updateProfile(usernameText.getText().toString().trim().toLowerCase().replace(" ",""));

                                Intent mainIntent = new Intent(VerificationActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                hideKeyboard(VerificationActivity.this);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(VerificationActivity.this);
            }
        });
    }

    private void updateProfile(String username) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", username);

        reference.updateChildren(hashMap);
    }

    //Vibrate Phone
    public void shakeItBaby() {
        int DURATION = 500; // you can change this according to your need
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(DURATION);
        }

    }
    //Vibrate Text
    public TranslateAnimation VibrateError() { // Edit text vibrate Animation
        TranslateAnimation vibrate = new TranslateAnimation(0, 10, 0, 0);
        vibrate.setDuration(600);
        vibrate.setInterpolator(new CycleInterpolator(8));
        return vibrate;
    }

    //Closing keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mAuth.signOut();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mAuth.signOut();
        super.onStop();
    }
}
