package com.example.baatcheat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.example.baatcheat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UsernameActivity extends AppCompatActivity {

    private EditText username;
    private TextView text1,text2;
    ImageView goBack,done;

    FirebaseUser firebaseUser;

    TashieLoader tashieLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        username = findViewById(R.id.username);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        goBack = findViewById(R.id.backToMain);
        done = findViewById(R.id.done);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tashieLoader = findViewById(R.id.lazyLoader);

        updateEditTextAtStart();
        spannableBoldText();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str_username = username.getText().toString().replace(" ","").trim();

                Query usernameQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(str_username);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            username.setError("Username is taken");
                            username.requestFocus();
                            return;
                        }else if (username.length() < 5){
                            username.setError("Username must be between 5 - 25 characters");
                            username.requestFocus();
                            return;
                        }else{
                            updateProfile(username.getText().toString().replace(" ","").trim());
                            DottedLoader();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void spannableBoldText(){
        String str_text2 = ("Name must be between 5-25 characters.");
        SpannableString spannableString1 = new SpannableString(str_text2);
        StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
        spannableString1.setSpan(boldSpan1, 21, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text2.setText(spannableString1);

        String str_text1 = ("You can use a-z, A-Z, 0-9 and underscores.");
        SpannableString spannableString2 = new SpannableString(str_text1);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        spannableString2.setSpan(boldSpan2, 12, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text1.setText(spannableString2);
    }

    private void updateEditTextAtStart(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User users = dataSnapshot.getValue(User.class);
                username.setText(users.getUsername());
                tashieLoader.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile(String username) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", username);
        hashMap.put("search",username.toLowerCase());

        reference.updateChildren(hashMap);
    }

    void DottedLoader(){
        TashieLoader tashie = new TashieLoader(
                this, 5,
                30, 10,
                ContextCompat.getColor(this, R.color.colorPrimaryDark));

        tashie.setAnimDuration(500);
        tashie.setAnimDelay(100);
        tashie.setInterpolator(new LinearInterpolator());

        tashieLoader.addView(tashie);
        tashieLoader.setVisibility(View.VISIBLE);

    }
}
