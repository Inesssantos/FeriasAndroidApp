/*
This is the version created by me (Martin), that is going to replace Registration.java
*/

package com.example.ferias.ui.common.registration;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ferias.R;
import com.example.ferias.data.PasswordStrength;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.traveler.Traveler;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;


public class Registration extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Button bt_RegisterUser;
    private ImageButton bt_Backhome_Registration;
    private ProgressBar progressBar_Register;

    private MaterialCardView cw_chooseHotelmanager, cw_chooseTraveler;

    private EditText et_Name, et_Surname;

    private CountryCodePicker ccp_PhoneCode;
    private EditText et_Phone;

    private TextInputLayout tilEmail_Registration;
    private EditText et_EmailAddress;

    private LinearLayout ll_Password;
    private EditText et_Password;
    private TextView tv_passwordstrength_registration;
    private ProgressBar progressBar_passwordstrength_registration;

    private boolean googleRegistration;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        initializeElements(root);

        clickListeners(root);

        return root;
    }

    private void initializeElements(View root) {
        bt_Backhome_Registration =  root.findViewById(R.id.bt_Backhome_Registration);

        cw_chooseHotelmanager = root.findViewById(R.id.choose_hotelmanager);
        cw_chooseTraveler = root.findViewById(R.id.choose_traveler);
        cw_chooseTraveler.setChecked(true);

        et_Name = root.findViewById(R.id.et_Name_Registration);
        et_Surname = root.findViewById(R.id.et_Surname_Registration);

        ccp_PhoneCode = root.findViewById(R.id.ccp_PhoneCode_Registration);
        et_Phone =  root.findViewById(R.id.et_Phone_Registration);
        ccp_PhoneCode.registerCarrierNumberEditText(et_Phone);

        tilEmail_Registration = root.findViewById(R.id.tilEmail_Registration);
        et_EmailAddress = root.findViewById(R.id.et_Email_Registration);

        ll_Password  = root.findViewById(R.id.ll_Password);
        et_Password = root.findViewById(R.id.et_Password_Registration);

        progressBar_passwordstrength_registration = root.findViewById(R.id.progressBar_PasswordStrength_Registration);
        tv_passwordstrength_registration = root.findViewById(R.id.tv_PasswordStrength_Registration);

        progressBar_Register =  root.findViewById(R.id.progressBar_RegistrationUser);
        progressBar_Register.setVisibility(View.GONE);

        bt_RegisterUser =  root.findViewById(R.id.bt_RegistrationUser);

        verifyIsGoogle();
    }

    private void verifyIsGoogle() {
        if(!getArguments().isEmpty()){
            googleRegistration = getArguments().getBoolean("IsGoogle",false);
        }
        else{
            googleRegistration = false;
        }

        if(googleRegistration){
            tilEmail_Registration.setVisibility(View.GONE);
            ll_Password.setVisibility(View.GONE);
        }
    }

    private void clickListeners(final View root) {

        bt_Backhome_Registration.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_registration_to_login);
        });

        bt_RegisterUser.setOnClickListener(v -> verifyData());

        et_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cw_chooseHotelmanager.setOnClickListener(v -> {
            cw_chooseHotelmanager.setChecked(true);
            cw_chooseHotelmanager.setCardBackgroundColor(Color.parseColor("#FF3F51B5"));

            cw_chooseTraveler.setChecked(false);
            cw_chooseTraveler.setCardBackgroundColor(Color.parseColor("#15194A"));
        });

        cw_chooseTraveler.setOnClickListener(v -> {
            cw_chooseTraveler.setChecked(true);
            cw_chooseTraveler.setCardBackgroundColor(Color.parseColor("#FF3F51B5"));

            cw_chooseHotelmanager.setChecked(false);
            cw_chooseHotelmanager.setCardBackgroundColor(Color.parseColor("#15194A"));
        });
    }

    private void verifyData(){

        String name = et_Name.getText().toString().trim();
        String surname = et_Surname.getText().toString().trim();
        String phone = ccp_PhoneCode.getFormattedFullNumber();
        String email = et_EmailAddress.getText().toString().trim();
        String password = et_Password.getText().toString().trim();
        boolean error = false;

        if(name.isEmpty()){
            et_Name.setError("Name is required");
            et_Name.requestFocus();
            error = true;
        }

        if(surname.isEmpty()){
            et_Surname.setError("Surname is required");
            et_Surname.requestFocus();
            error = true;
        }

        if(!ccp_PhoneCode.isValidFullNumber()){
            et_Phone.setError("Phone is required or is not valid");
            et_Phone.requestFocus();
            error = true;
        }

        if(!googleRegistration){
            if(email.isEmpty()){
                et_EmailAddress.setError("Email address is required");
                et_EmailAddress.requestFocus();
                error = true;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                et_EmailAddress.setError("Please provide a valid email address");
                et_EmailAddress.requestFocus();
                error = true;
            }

            PasswordStrength passwordStrength = PasswordStrength.calculate(password);
            if(password.isEmpty() || passwordStrength.getStrength() <= 1){
                et_Password.setError("Password strength error");
                et_Password.requestFocus();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Password strength error");
                String mensage = "Your password needs to:" +
                        "\n\tInclude both lower and upper case characters" +
                        "\n\tInclude at least one number and symbol" +
                        "\n\tBe at least 8 characters long";

                dialog.setMessage(mensage);
                dialog.setNegativeButton("Confirm", (dialogInterface, which) -> dialogInterface.dismiss());
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                error = true;
            }

        }
        else{
            email = firebaseAuth.getCurrentUser().getEmail();
        }

        if(error){
            return;
        }

        registerUser(name,surname,phone,email,password);
    }

    private void registerUser(String name, String surname, String phone, String email, String password) {
        progressBar_Register.setVisibility(View.VISIBLE);

        if(!googleRegistration){
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            Toast.makeText(getContext(),"An email has been sent to activate your account!", Toast.LENGTH_LONG).show();
                            firebaseUser.sendEmailVerification();
                            registerInFirebase(name,surname,phone,email,password,firebaseUser.getUid());
                        }
                        else {
                            Toast.makeText(getContext(),"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            progressBar_Register.setVisibility(View.GONE);
                            return;
                        }
                    });
        }
        else {
            firebaseUser = firebaseAuth.getCurrentUser();
            registerInFirebase(name,surname,phone,email,password,firebaseUser.getUid());
        }

    }

    private void registerInFirebase(String name, String surname, String phone, String email, String password, String userID) {
        Object newuser = null;
        String path = "";

        if(cw_chooseTraveler.isChecked()){
            newuser = googleRegistration ?  new Traveler(name, surname, phone, email,true) : new Traveler(name, surname, email, phone, password);
            path = "Traveler";
        }

        if(cw_chooseHotelmanager.isChecked()){
            newuser = googleRegistration ?   new HotelManager(name, surname, phone, email,true) :  new HotelManager(name, surname, email, phone, password);
            path = "Hotel Manager";
        }

        FirebaseDatabase.getInstance().getReference(path)
        .child(userID)
        .setValue(newuser).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"User has ben registered successfully!", Toast.LENGTH_LONG).show();
                progressBar_Register.setVisibility(View.GONE);

                FirebaseAuth.getInstance().signOut();

                Navigation.findNavController(getView()).navigate(R.id.action_registration_to_login);
            }
            else {
                Toast.makeText(getContext(),"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                progressBar_Register.setVisibility(View.GONE);
            }
        });
    }

    private void calculatePasswordStrength(String str) {
        PasswordStrength passwordStrength = PasswordStrength.calculate(str);

        progressBar_passwordstrength_registration.setProgressTintList(ColorStateList.valueOf(passwordStrength.getColor()));
        progressBar_passwordstrength_registration.setProgress(passwordStrength.getStrength());
        tv_passwordstrength_registration.setText(passwordStrength.getMsg());

    }
}