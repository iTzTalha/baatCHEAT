package com.example.baatcheat.Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.example.baatcheat.Adapter.UserAdapter;
import com.example.baatcheat.CountryToPhonePrefix;
import com.example.baatcheat.Model.User;
import com.example.baatcheat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private TextView permissionText,text5;
    private ImageView backtonormal,btn_search;
    private EditText searchbar;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList, contactList;

    FirebaseAuth mAuth;

    TashieLoader tashieLoader;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        text5 = view.findViewById(R.id.text5);
        backtonormal = view.findViewById(R.id.backtonormal);
        btn_search = view.findViewById(R.id.btn_search);
        searchbar = view.findViewById(R.id.searchbar);

        permissionText = view.findViewById(R.id.permissionText);
        tashieLoader = view.findViewById(R.id.lazyLoader);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, false);
        recyclerView.setAdapter(userAdapter);

        mAuth = FirebaseAuth.getInstance();

        getPermissions();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            contactList.clear();
            permissionText.setVisibility(View.VISIBLE);

        } else {
            permissionText.setVisibility(View.GONE);
            getContacts();
        }

        //Search
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text5.setVisibility(View.GONE);
                backtonormal.setVisibility(View.VISIBLE);
                searchbar.setVisibility(View.VISIBLE);
                btn_search.setVisibility(View.GONE);
                searchbar.requestFocus();
            }
        });

        backtonormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text5.setVisibility(View.VISIBLE);
                backtonormal.setVisibility(View.GONE);
                searchbar.setVisibility(View.GONE);
                btn_search.setVisibility(View.VISIBLE);
                searchbar.clearFocus();
                filter("");
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void getContacts() {
        String ISOPrefix = getCountryISO();

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (!String.valueOf(phone.charAt(0)).equals("+")) {
                phone = ISOPrefix + phone;
            }
            User mContacts = new User("", name, phone, "","","","");
            contactList.add(mContacts);
            getUserDetails(mContacts);
        }
    }

    private void getUserDetails(User mContacts) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("phone").equalTo(mContacts.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone = "",
                            name = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if (childSnapshot.child("username").getValue() != null)
                            name = childSnapshot.child("username").getValue().toString();


                        User mUser = new User(childSnapshot.getKey(), name, phone, childSnapshot.child("bio").getValue().toString(),childSnapshot.child("imageUrl").getValue().toString(),childSnapshot.child("status").getValue().toString(),"");
                        if (name.equals(phone))
                            for (User mContactIterator : contactList) {
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setUsername(mContactIterator.getUsername());
                                }
                            }

                        userList.add(mUser);
                        userAdapter.notifyDataSetChanged();
                        tashieLoader.setVisibility(View.GONE);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryISO() {
        String ISO = null;

        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null) {
            if (!telephonyManager.getNetworkCountryIso().equals("")) {
                ISO = telephonyManager.getNetworkCountryIso();
            }
        }
        return CountryToPhonePrefix.getPhone(ISO);
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    void filter(String text){
        List<User> temp = new ArrayList();
        for(User d: userList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getUsername().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        userAdapter.updateList(temp);
    }
}
