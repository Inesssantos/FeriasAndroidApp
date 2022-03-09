package com.example.ferias.ui.traveler.home;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.traveler.Traveler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Home extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Traveler user;

    private GoogleSignInClient googleSignInClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng latLng;

    private ConstraintLayout cl_HomeUser;

    private ShapeableImageView bt_ProfileMenu;
    private LinearLayout profileMenu;
    private Button bt_EditProfile, bt_Logout;

    private TextView tv_NameMensage;
    private FloatingActionButton search_btn;
    private TextView textinput_location;

    private MaterialButton bt_search_nearby, bt_search_onMap;

    private ExtendedFloatingActionButton partyBtn;
    private ExtendedFloatingActionButton chillBtn;
    private ExtendedFloatingActionButton adventureBtn;
    private ExtendedFloatingActionButton sportsBtn;
    private ExtendedFloatingActionButton bookings_btn;

    private FloatingActionButton favsBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_logout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog.setCancelable(false);

                dialog.create();

                Button confirm = dialog.findViewById(R.id.bt_dialog_logout_Confirm);
                Button deny = dialog.findViewById(R.id.bt_dialog_logout_Deny);

                confirm.setOnClickListener(v -> {bt_Logout.performClick(); dialog.dismiss();});

                deny.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_home, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        readUserData();

        initializeElements(root);

        //getLocation();

        clickListener(root);

        loadDatatoElements();

        return root;
    }

    /*
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 40);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1);
        locationRequest.setFastestInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Inicialize location
                Location location = task.getResult();
                if(location != null){
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    Log.e("Location:"+"'"+location.getLatitude()+"'"+";"+"'"+location.getLongitude()+"'","HOME");
                }
            }
        });
    }
    */

    private void initializeElements(View root) {
        bt_ProfileMenu = root.findViewById(R.id.bt_ProfileMenu);

        profileMenu = root.findViewById(R.id.ll_profile_menu_User);
        profileMenu.setVisibility(View.GONE);

        bt_EditProfile = root.findViewById(R.id.bt_editProfile_User);

        bt_Logout = root.findViewById(R.id.bt_Logout_User);

        cl_HomeUser = root.findViewById(R.id.cl_Home_User);

        tv_NameMensage = root.findViewById(R.id.tv_NameMensage_User);

        search_btn = root.findViewById(R.id.home_search_btn);
        textinput_location= root.findViewById(R.id.textinput_location);

        partyBtn = root.findViewById(R.id.travelerParty_search);
        chillBtn = root.findViewById(R.id.travelerChill_search);
        adventureBtn = root.findViewById(R.id.travelerAdventure_search);
        sportsBtn = root.findViewById(R.id.travelerSport_search);
        bookings_btn = root.findViewById(R.id.my_bookings_btn);

        favsBtn=root.findViewById(R.id.my_favs_btn2);

        bt_search_nearby = root.findViewById(R.id.bt_search_nearby);
        bt_search_onMap = root.findViewById(R.id.bt_search_onMap);
    }

    private void clickListener(View root) {
        cl_HomeUser.setOnClickListener(v -> profileMenu.setVisibility(View.GONE));

        bt_ProfileMenu.setOnClickListener(v -> {
            if(profileMenu.getVisibility() == View.GONE){
                profileMenu.setVisibility(View.VISIBLE);
                profileMenu.bringToFront();
            }
            else{
                profileMenu.setVisibility(View.GONE);
            }

        });

        bt_EditProfile.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", user);
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_profile, bundle);
        });

        bt_Logout.setOnClickListener(v -> {

            if(user.isGoogle()  == true){
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.signOut();
                        }
                    }
                });
            }
            else{
                firebaseAuth.signOut();
            }

            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_login);
        });

        search_btn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("inputText", textinput_location.getText().toString());
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_traveler_search, bundle);
        });

        bt_search_nearby.setOnClickListener(v -> {
            //getLocation();
            Bundle bundle = new Bundle();
            //String gps = latLng.latitude + "," + latLng.longitude;
            //bundle.putString("GPS", gps);
            float distance =  user.getSearchRadius() != 0 ?  user.getSearchRadius() : 500;
            bundle.putFloat("SearchRadius", distance);
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_traveler_search_nearby,bundle);
        });

        bt_search_onMap.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_traveler_hotel_on_map);
        });

        partyBtn.setOnClickListener(v -> {SearchByMods(root,"Party");});
        chillBtn.setOnClickListener(v -> {SearchByMods(root,"Chill");});
        adventureBtn.setOnClickListener(v -> {SearchByMods(root,"Adventure");});
        sportsBtn.setOnClickListener(v -> {SearchByMods(root,"Sports");});

        favsBtn.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_favorites);
        });

        /*test bookings*/
        bookings_btn.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_bookings);
        });
    }

    private void readUserData() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            googleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Traveler").child(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Traveler.class);
                if(user != null){
                    loadDatatoElements();

                    try {
                        InternalStorage.writeObject(getContext(), "User", user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getUser:onCancelled",error.toException());
            }
        });

        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Traveler.class);
                if(user != null){
                    loadDatatoElements();

                    try {
                        InternalStorage.writeObject(getContext(), "User", user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getUser:onCancelled",error.toException());
            }
        });*/
    }

    private void loadDatatoElements(){
        if(user != null){
            tv_NameMensage.setText("Hi "+user.getName());

            Glide.with(this)
            .load(user.getImage())
            .placeholder(R.drawable.profile_pic_example)
            .fitCenter()
            .into(bt_ProfileMenu);
        }
    }

    private void SearchByMods(View root,String mod)
    {
        Bundle bundle = new Bundle();
        bundle.putString("modsWanted", mod);
        Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_traveler_search, bundle);
    }

}