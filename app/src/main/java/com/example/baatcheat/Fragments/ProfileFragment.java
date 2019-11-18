package com.example.baatcheat.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.BundleCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.emoji.widget.EmojiTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.example.baatcheat.BioActivity;
import com.example.baatcheat.LoginActivity;
import com.example.baatcheat.Model.User;
import com.example.baatcheat.R;
import com.example.baatcheat.ShowNumberActivity;
import com.example.baatcheat.StartActivity;
import com.example.baatcheat.UsernameActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView tv_username, myPhoneNumner;
    private EmojiTextView myBio;
    private LinearLayout Username;
    private LinearLayout phone;
    private LinearLayout logout;
    private LinearLayout bio;

    private TextView fixedText, Editusername;

    private FirebaseAuth mAuth;

    TashieLoader tashieLoader;

    FirebaseUser firebaseUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getActivity());
        EmojiCompat.init(config);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_username = view.findViewById(R.id.tv_username);
        myPhoneNumner = view.findViewById(R.id.myphonenumner);
        myBio = (EmojiTextView) view.findViewById(R.id.myBio);
        Username = view.findViewById(R.id.username);
        phone = view.findViewById(R.id.phone);
        bio = view.findViewById(R.id.bio);
        logout = view.findViewById(R.id.logout);

        fixedText = view.findViewById(R.id.fixedText);
        fixedText.setText("@");
        Editusername = view.findViewById(R.id.Editusername);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tashieLoader = view.findViewById(R.id.lazyLoader);

        DottedLoader();
        updateProfileInfo();

        Username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UsernameActivity.class);
                startActivity(intent);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowNumberActivity.class);
                startActivity(intent);
            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BioActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void updateProfileInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User users = dataSnapshot.getValue(User.class);
                Editusername.setText(users.getUsername());
                tv_username.setText(users.getUsername());
                myPhoneNumner.setText(users.getPhone());
                myBio.setText(users.getBio());
                tashieLoader.setVisibility(View.GONE);
//                Glide.with(getApplicationContext()).load(users.getImageurl()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void DottedLoader() {
        TashieLoader tashie = new TashieLoader(
                getContext(), 5,
                30, 10,
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        tashie.setAnimDuration(500);
        tashie.setAnimDelay(100);
        tashie.setInterpolator(new LinearInterpolator());

        tashieLoader.addView(tashie);
        tashieLoader.setVisibility(View.VISIBLE);

    }
}
