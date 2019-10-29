package com.example.baatcheat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static maes.tech.intentanim.CustomIntent.customType;

public class StartActivity extends AppCompatActivity {

    int SPLASH_TIME = 1000; //This is 0 seconds
//    ProgressBar splashProgress;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        //This is additional feature, used to run a progress bar
//        splashProgress = findViewById(R.id.splashProgress);
//        playProgress();

        //Code to start timer and take action after the timer ends
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do any action here. Now we are moving to next page
                if (currentUser != null) {
                    String currentUserId = mAuth.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("username").exists()){
                                Intent mainIntent = new Intent(StartActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                                customType(StartActivity.this,"fadein-to-fadeout");
                            }else {
                                Intent putUsernameIntent = new Intent(StartActivity.this,VerificationActivity.class);
                                putUsernameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(putUsernameIntent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else {
                    Intent mySuperIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(mySuperIntent);

                    //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                    finish();
                }
            }
        }, SPLASH_TIME);
    }
//    //Method to run progress bar for 5 seconds
//    private void playProgress() {
//        ObjectAnimator.ofInt(splashProgress, "progress", 100)
//                .setDuration(3000)
//                .start();
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (currentUser != null) {
//            Intent putUsernameIntent = new Intent(StartActivity.this, VerificationActivity.class);
//            putUsernameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(putUsernameIntent);
//            String currentUserId = mAuth.getUid();
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//            databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.child("username").exists()){
//                        Intent mainIntent = new Intent(StartActivity.this,MainActivity.class);
//                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(mainIntent);
//                    }else {
//                        Intent putUsernameIntent = new Intent(StartActivity.this,VerificationActivity.class);
//                        putUsernameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(putUsernameIntent);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }
}
