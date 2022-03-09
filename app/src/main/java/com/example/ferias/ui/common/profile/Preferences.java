package com.example.ferias.ui.common.profile;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.ferias.MainActivity;
import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.common.User;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.traveler.Traveler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Locale;

public class Preferences extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private User user;
    private String path;

    private RadioGroup rg_language;
    private RadioButton rb_English, rb_Portuguese, rb_Italian;

    private Button bt_save_preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile_preferences, container, false);

        readUserData();

        initializeElements(root);

        clickListeners(root);

        loadDatatoElements();

        return root;
    }

    private void initializeElements(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child(path).child(firebaseUser.getUid());

        rg_language = root.findViewById(R.id.rg_language);

        rb_English = root.findViewById(R.id.rb_English);
        rb_Portuguese = root.findViewById(R.id.rb_Portuguese);
        rb_Italian = root.findViewById(R.id.rb_Italian);

        bt_save_preferences = root.findViewById(R.id.bt_save_preferences);

    }

    private void clickListeners(View root) {
        bt_save_preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }

    private void readUserData() {
        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
            if(user instanceof Traveler){
                path = "Traveler";
            }

            if(user instanceof HotelManager){
                path = "Hotel Manager";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void loadDatatoElements() {
        if (user != null) {
            String language = user.getLanguage();

            if (!language.isEmpty()) {
                switch (language) {
                    case "en":
                        rb_English.toggle();
                    break;

                    case "pt":
                        rb_Portuguese.toggle();
                    break;

                    case "it":
                        rb_Italian.toggle();
                    break;
                }
            }
        }
    }

    private void savePreferences() {

        String language_chosen;

        int select_language = rg_language.getCheckedRadioButtonId();

        switch (select_language) {
            case R.id.rb_English:
                language_chosen = "en";
            break;

            case R.id.rb_Portuguese:
                language_chosen = "pt";
            break;

            case R.id.rb_Italian:
                language_chosen = "it";
            break;

            default:
                language_chosen = "en";
        }

        if (!user.getLanguage().equals(language_chosen)) {
            setLocale(language_chosen);
            user.setLanguage(language_chosen);
        }

        databaseReference.setValue(user);
    }

    private void setLocale(String language) {

        Configuration conf = new Configuration(getContext().getResources().getConfiguration());
        conf.locale = new Locale(language);
        getContext().getResources().updateConfiguration(conf, getContext().getResources().getDisplayMetrics());

        Intent refresh = new Intent(getContext(), MainActivity.class);
        startActivity(refresh);
    }
}