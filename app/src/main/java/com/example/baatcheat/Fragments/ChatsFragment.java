package com.example.baatcheat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.baatcheat.R;
import com.example.baatcheat.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private ImageView backtonormal, search, btn_contacts;
    private TextView text1;
    private EditText searchbar;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);

        searchbar = view.findViewById(R.id.searchbar);
        backtonormal = view.findViewById(R.id.backtonormal);
        text1 = view.findViewById(R.id.text1);
        search = view.findViewById(R.id.searchcontacts);
        btn_contacts = view.findViewById(R.id.btn_contacts);

        btn_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open contacts fragments
                //replace btn_contacts to btn_contacts_pressed
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_chatsFragment_to_contactsFragment);
                btn_contacts.setVisibility(View.GONE);
            }
        });

        searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performaction();
                    return true;
                }
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(),SearchActivity.class));

                //make things invisible
                text1.setVisibility(View.GONE);
                btn_contacts.setVisibility(View.GONE);
                search.setVisibility(View.GONE);

                //make things visible
                backtonormal.setVisibility(View.VISIBLE);
                searchbar.setVisibility(View.VISIBLE);
                searchbar.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        backtonormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make things invisible
                backtonormal.setVisibility(View.GONE);
                searchbar.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //make things visible
                text1.setVisibility(View.VISIBLE);
                btn_contacts.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                searchbar.clearFocus();
            }
        });

        return view;

    }
}
