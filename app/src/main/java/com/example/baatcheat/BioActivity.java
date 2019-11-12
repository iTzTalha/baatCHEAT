package com.example.baatcheat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.example.baatcheat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class BioActivity extends AppCompatActivity {

    private ImageView goBack,Done;
    private EditText Bio;

    FirebaseUser firebaseUser;

    TashieLoader tashieLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);
        
        goBack = findViewById(R.id.backToMain);
        Done = findViewById(R.id.done);
        Bio = findViewById(R.id.bio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tashieLoader = findViewById(R.id.lazyLoader);

        fillBioText();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBio();
                DottedLoader();
            }
        });

    }

    private void fillBioText() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User users = dataSnapshot.getValue(User.class);
                Bio.setText(users.getBio());
                tashieLoader.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateBio() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        String str_Bio = Bio.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("bio",str_Bio);

        reference.updateChildren(hashMap);

        finish();
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
