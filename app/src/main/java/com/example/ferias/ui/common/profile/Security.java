package com.example.ferias.ui.common.profile;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.PasswordStrength;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.common.User;
import com.example.ferias.data.traveler.Traveler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class Security extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private User user;
    private String path;

    private LinearLayout ll_ChangePassword;
    private EditText et_Old_Password;

    private EditText et_New_Password;

    private TextView tv_passwordstrength_change;
    private ProgressBar progressBar_passwordstrength_change;

    private Button bt_reset_password;

    private Button bt_send_password;

    private Button bt_delete_account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile_security, container, false);

        readUserData();

        initializeElements(root);

        clickListeners(root);

        return root;
    }

    private void readUserData() {
        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
            if(user instanceof Traveler){
                path = "Traveler";
            }

            if(user instanceof  HotelManager){
                path = "Hotel Manager";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeElements(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child(path).child(firebaseUser.getUid());

        ll_ChangePassword = root.findViewById(R.id.ll_ChangePassword);
        if(user.isGoogle()){
            ll_ChangePassword.setVisibility(View.GONE);
        }

        et_Old_Password = root.findViewById(R.id.et_Old_Password);
        et_New_Password = root.findViewById(R.id.et_New_Password);

        progressBar_passwordstrength_change = root.findViewById(R.id.progressBar_passwordstrength_change);
        tv_passwordstrength_change = root.findViewById(R.id.tv_passwordstrength_change);

        bt_reset_password = root.findViewById(R.id.bt_reset_password);

        bt_send_password = root.findViewById(R.id.bt_send_password);

        bt_delete_account = root.findViewById(R.id.bt_delete_account);

    }

    private void clickListeners(View root) {
        et_New_Password.addTextChangedListener(new TextWatcher() {
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

        bt_reset_password.setOnClickListener(v -> {
            String password = et_New_Password.getText().toString().trim();
            PasswordStrength passwordStrength = PasswordStrength.calculate(password);
            if(password.isEmpty() || passwordStrength.getStrength() <= 1){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Password Strength Error");
                String mensage = "Your password needs to:" +
                                    "\n\tInclude both lower and upper case characters" +
                                    "\n\tInclude at least one number and symbol" +
                                    "\n\tBe at least 8 characters long";

                dialog.setMessage(mensage);
                dialog.setNegativeButton("Confirm", (dialogInterface, which) -> dialogInterface.dismiss());
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
            else{
                if(! user.getPassword().equals(et_Old_Password.getText().toString())){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Password Error");
                    String mensage = "Old password is incorrect, please enter your current password";

                    dialog.setMessage(mensage);
                    dialog.setNegativeButton("Confirm", (dialogInterface, which) -> dialogInterface.dismiss());
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                    et_Old_Password.setError("Password is incorrect");
                }
                else {
                    user.setPassword(password);
                    databaseReference.setValue(user);

                    et_Old_Password.setText("");
                    et_New_Password.setText("");
                    calculatePasswordStrength("");
                }
            }

        });

        bt_send_password.setOnClickListener(v -> {
            String email = user.getEmail();

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Check your email to reset your password!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(),"Try again! Something wrong happened", Toast.LENGTH_LONG).show();
                }
            });
        });

        bt_delete_account.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Are you sure?");
            dialog.setMessage("Deleting this account will result in completely removing your account and you won't be able to access the app");
            dialog.setPositiveButton("Delete", (dialog1, which) -> {
                FirebaseDatabase.getInstance().getReference().child(path).child(firebaseUser.getUid()).removeValue();

                firebaseUser.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(),"Account Deleted", Toast.LENGTH_LONG).show();

                        Navigation.findNavController(root).navigate(R.id.action_security_to_login);
                    }
                });
            });
            dialog.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });
    }

    private void calculatePasswordStrength(String str) {
        // Now, we need to define a PasswordStrength enum
        // with a calculate static method returning the password strength
        PasswordStrength passwordStrength = PasswordStrength.calculate(str);

        progressBar_passwordstrength_change.setProgressTintList(ColorStateList.valueOf(passwordStrength.getColor()));
        progressBar_passwordstrength_change.setProgress(passwordStrength.getStrength());
        tv_passwordstrength_change.setText(passwordStrength.getMsg());

    }
}