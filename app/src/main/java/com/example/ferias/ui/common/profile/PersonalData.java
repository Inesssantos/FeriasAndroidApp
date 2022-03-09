package com.example.ferias.ui.common.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.ZipCodeValidation;
import com.example.ferias.data.common.Address;
import com.example.ferias.data.common.User;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.traveler.Traveler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.Calendar;

public class PersonalData extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private User user;
    private String path;

    private EditText et_Name, et_Surname, et_Age,et_EmailAddress;
    private EditText et_City,et_Address,et_ZipCode;

    private CountryCodePicker ccp_PhoneCode;
    private EditText et_Phone;

    private CountryCodePicker ccp_Country;

    private Button bt_save_preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile_personal_data, container, false);

        readUserData();

        initializeElements(root);

        loadDatatoElements();

        clickListeners();

        return root;
    }

    private void initializeElements(View root){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child(path).child(firebaseUser.getUid());

        et_Name = root.findViewById(R.id.et_Name);
        et_Surname = root.findViewById(R.id.et_Surname);
        et_Age = root.findViewById(R.id.et_Age);
        et_EmailAddress = root.findViewById(R.id.et_Email);

        ccp_PhoneCode = root.findViewById(R.id.ccp_PhoneCode);
        et_Phone = root.findViewById(R.id.et_Phone);
        ccp_PhoneCode.registerCarrierNumberEditText(et_Phone);

        ccp_Country = root.findViewById(R.id.ccp_Country);

        et_City = root.findViewById(R.id.et_City);
        et_Address = root.findViewById(R.id.et_Address);
        et_ZipCode = root.findViewById(R.id.et_ZipCode);

        bt_save_preferences = root.findViewById(R.id.bt_save_preferences);
    }

    private void clickListeners() {
        et_Age.setOnClickListener(v -> {
            final DatePickerDialog[] picker = new DatePickerDialog[1];
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker[0] = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        Calendar currentdate = Calendar.getInstance();
                        Calendar birthdaydate = Calendar.getInstance();
                        birthdaydate.set(year1, monthOfYear, dayOfMonth);

                        int age = currentdate.get(Calendar.YEAR) - birthdaydate.get(Calendar.YEAR);
                        if (currentdate.get(Calendar.DAY_OF_YEAR) < birthdaydate.get(Calendar.DAY_OF_YEAR)) {
                            age--;
                        }

                        if (age < 18) {
                            Toast.makeText(getContext(), "Invalid birth date, must be over 18 years old. Please enter a valid date", Toast.LENGTH_LONG).show();
                            et_Age.setText("");
                        } else {
                            et_Age.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                        }
                    }, year, month, day);
            picker[0].show();
        });

        bt_save_preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalData();
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

    private void loadDatatoElements(){
        et_Name.setText(user.getName());
        et_Surname.setText(user.getSurname());
        et_Age.setText(user.getBirthday());
        ccp_PhoneCode.setFullNumber(user.getPhone());
        et_EmailAddress.setText(user.getEmail());

        if(user.getAddress() != null){
            ccp_Country.setDefaultCountryUsingNameCode(user.getAddress().getCountry());
            ccp_Country.resetToDefaultCountry();
            et_City.setText(user.getAddress().getCity());
            et_Address.setText(user.getAddress().getAddress());
            et_ZipCode.setText(user.getAddress().getZipcode());
        }

    }

    private void savePersonalData(){
        String name = et_Name.getText().toString().trim();
        String surname = et_Surname.getText().toString().trim();
        String age = et_Age.getText().toString().trim();
        String phone = ccp_PhoneCode.getFormattedFullNumber();
        String email = et_EmailAddress.getText().toString().trim();

        String country = ccp_Country.getSelectedCountryNameCode();
        String city = et_City.getText().toString().trim();
        String address = et_Address.getText().toString().trim();
        String zipcode = et_ZipCode.getText().toString().trim();

        boolean error = false;

        if(name.isEmpty()){
            et_Name.setError("Full name is required");
            et_Name.requestFocus();
            error = true;
        }

        if(surname.isEmpty()){
            et_Surname.setError("Full name is required");
            et_Surname.requestFocus();
            error = true;
        }

        if(age.isEmpty()){
            et_Age.setError("Age is required");
            et_Age.requestFocus();
            error = true;
        }

        if(!ccp_PhoneCode.isValidFullNumber()){
            et_Phone.setError("Phone is required and valid");
            et_Phone.requestFocus();
            error = true;
        }

        if(email.isEmpty()){
            et_EmailAddress.setError("Email Address is required");
            et_EmailAddress.requestFocus();
            error = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_EmailAddress.setError("Please provide valid Email Address");
            et_EmailAddress.requestFocus();
            error = true;
        }

        if(country.isEmpty()){
            error = true;
        }

        if(city.isEmpty()){
            et_City.setError("City is required");
            et_City.requestFocus();
            error = true;
        }

        if(address.isEmpty()){
            et_Address.setError("Address is required");
            et_Address.requestFocus();
            error = true;
        }

        ZipCodeValidation zipCodeValidation = new ZipCodeValidation();
        if(zipcode.isEmpty() || zipCodeValidation.validation_code(ccp_Country.getSelectedCountryNameCode(),zipcode)){
            //if(zipcode.isEmpty()){
            et_ZipCode.setError("Zip-Code is required and valid");
            et_ZipCode.requestFocus();
            error = true;
        }


        if(error){
            return;
        }

        user.setName(name);
        user.setSurname(surname);
        user.setBirthday(age);
        user.setPhone(phone);
        user.setEmail(email);

        Address newaddress = new Address();
        newaddress.setCountry(country);
        newaddress.setCity(city);
        newaddress.setAddress(address);
        newaddress.setZipcode(zipcode);

        user.setAddress(newaddress);

        databaseReference.setValue(user);
    }
}