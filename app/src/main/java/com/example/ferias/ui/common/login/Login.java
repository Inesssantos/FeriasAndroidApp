package com.example.ferias.ui.common.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.traveler.Traveler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends Fragment {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    private Traveler traveler;
    private HotelManager manager;

    private EditText et_EmailAddress,et_Password;

    private CheckBox cb_Remeber;

    private Button bt_Login, bt_Login_Google, bt_Register;

    private ProgressBar progressBar_Login;

    private TextView tv_Forgot_Password;

    private final boolean autoLogin = true;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        checkPermissions();

        initializeElements(root);

        clickListener(root);

        getRemeberData();

        return root;
    }

    private void initializeElements(View root) {
        et_EmailAddress =  root.findViewById(R.id.etEmail_Login);
        et_Password =  root.findViewById(R.id.etPassword_Login);

        cb_Remeber = root.findViewById(R.id.cb_Remeber);

        bt_Login =  root.findViewById(R.id.bt_Login);

        progressBar_Login =  root.findViewById(R.id.progressBar_Login);
        progressBar_Login.setVisibility(View.GONE);

        tv_Forgot_Password =  root.findViewById(R.id.tv_Forgot_Password);

        bt_Login_Google =  root.findViewById(R.id.bt_Login_Google);

        bt_Register =  root.findViewById(R.id.bt_Register);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void clickListener(View root) {
        bt_Login.setOnClickListener(v -> verifyData());

        bt_Login_Google.setOnClickListener(v -> loginWithGoogle());

        bt_Register.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("IsGoogle", false);
            Navigation.findNavController(root).navigate(R.id.action_login_to_registration, bundle);
        });

        tv_Forgot_Password.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_login_to_forgotPassword);
        });
    }

    private void loginWithGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(),googleSignInOptions);

        Intent intent = googleSignInClient.getSignInIntent();

        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed,
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                if (isNewUser) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("IsGoogle", true);
                    Navigation.findNavController(getView()).navigate(R.id.action_login_to_registration, bundle);
                } else {
                    verifyTypeUser(firebaseAuth.getCurrentUser().getUid());
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
        });
    }

    private void verifyData(){
        String email = et_EmailAddress.getText().toString().trim();
        String password = et_Password.getText().toString().trim();
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

        if(password.isEmpty()){
            et_Password.setError("Password is required");
            et_Password.requestFocus();
            error = true;
        }

        if(error){
            return;
        }

        progressBar_Login.setVisibility(View.VISIBLE);

        login(email,password);
    }

    private void login(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                bt_Login.setEnabled(false);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(cb_Remeber.isChecked()){
                    saveRemeberData(email,password);
                }
                else{
                    saveRemeberData("","");
                }

                if(user.isEmailVerified()){
                    progressBar_Login.setVisibility(View.GONE);
                    verifyTypeUser(user.getUid());
                }
                else{
                    user.sendEmailVerification();
                    Toast.makeText(getContext(),"Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    bt_Login.setEnabled(true);
                }
            }
            else {
                Toast.makeText(getContext(),"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();
                bt_Login.setEnabled(true);
            }
        });
    }

    private void verifyTypeUser(String userId){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("Traveler").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                traveler = snapshot.getValue(Traveler.class);
                if(traveler != null){
                    Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_LONG).show();
                    Navigation.findNavController(getView()).navigate(R.id.action_login_to_traveler_home);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getTraveler:onCancelled",error.toException());
            }
        });

        database.child("Hotel Manager").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                manager = snapshot.getValue(HotelManager.class);
                if(manager != null){
                    Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_LONG).show();
                    Navigation.findNavController(getView()).navigate(R.id.action_login_to_hotel_manager_home);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getManager:onCancelled",error.toException());
            }
        });

    }

    private void saveRemeberData(String email, String password){
        try {
            InternalStorage.writeObject(getContext(), "Email", email);
            InternalStorage.writeObject(getContext(), "Password", password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRemeberData() {
        try {
            String email = (String) InternalStorage.readObject(getContext(), "Email");
            String password = (String) InternalStorage.readObject(getContext(), "Password");

            if(!email.isEmpty() && !password.isEmpty()){
                et_EmailAddress.setText(email);
                et_Password.setText(password);

                cb_Remeber.setChecked(true);

                /*if(autoLogin){
                    bt_Login.performClick();
                }*/
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 124);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 124: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
                    Log.e("Permissions Garated", "All"); // Now you call here what ever you want :)
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }   // permissions list of don't granted permission
                    Log.e("Permissions Denied", perStr);
                }
                return;
            }
        }
    }
}