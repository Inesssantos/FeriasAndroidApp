package com.example.ferias.ui.common.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ferias.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends Fragment{

    private FirebaseAuth firebaseAuth;

    private EditText et_EmailAddress;

    private Button bt_Reset;
    private ImageButton bt_Backhome_reset;

    private ProgressBar progressBar_Reset;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        initializeElements(root);

        clickListener(root);

        return root;
    }

    private void initializeElements(View root) {
        bt_Backhome_reset =  root.findViewById(R.id.bt_Backhome_reset);

        et_EmailAddress =  root.findViewById(R.id.et_Reset_Email);

        progressBar_Reset =  root.findViewById(R.id.progressBar_ResetEmail);
        progressBar_Reset.setVisibility(View.GONE);

        bt_Reset =  root.findViewById(R.id.bt_ResetPassword);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void clickListener(final View root) {
        bt_Reset.setOnClickListener(v -> resetPassword(root));

        bt_Backhome_reset.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_forgotPassword_to_login);
        });
    }

    private void resetPassword(View root) {
        String email = et_EmailAddress.getText().toString().trim();
        boolean error = false;

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

        if(error){
            return;
        }

        progressBar_Reset.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Check your email to reset your password!", Toast.LENGTH_LONG).show();

                Navigation.findNavController(root).navigate(R.id.action_forgotPassword_to_login);
            }
            else {
                Toast.makeText(getContext(),"Try again! Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });
    }
}