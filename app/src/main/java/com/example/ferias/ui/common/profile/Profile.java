package com.example.ferias.ui.common.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ferias.R;
import com.example.ferias.data.GenerateUniqueIds;
import com.example.ferias.data.common.User;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.traveler.Traveler;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class Profile extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private User user;
    private String typeUser;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;

    private ImageView iv_ProfileImage;
    private ImageButton bt_ProfileImageEdit , bt_ProfileImageSave;
    private ProgressBar pb_ProfileImage;
    private Uri profileImageUri;
    private StorageTask mUploadTask;

    private ImageButton bt_Backhome_Profile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                bt_Backhome_Profile.performClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeElements(root);

        clickListeners(root);

        loadDatatoElements();

        return root;
    }

    private void loadDatatoElements() {
        if(!getArguments().isEmpty()){
            user = (User) getArguments().getSerializable("User");
            if(user != null){
                if(user instanceof Traveler){
                    typeUser = "Traveler";
                    user = (Traveler) getArguments().getSerializable("User");
                }

                if(user instanceof HotelManager){
                    typeUser = "Hotel Manager";
                    user = (HotelManager) getArguments().getSerializable("User");
                }

                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child(typeUser).child(firebaseUser.getUid());
                storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(typeUser);

                if(!user.getImage().isEmpty()){
                    Glide.with(this)
                    .load(user.getImage())
                    .placeholder(R.drawable.profile_pic_example)
                    .fitCenter()
                    .into(iv_ProfileImage);
                }

            }
        }
    }

    private void initializeElements(View root) {
        viewPager = root.findViewById(R.id.pager);
        tabLayout = root.findViewById(R.id.tab_layout);

        pageAdapter = new PageAdapter (getParentFragmentManager());

        pageAdapter.addFragment(new PersonalData(),"Personal Data");
        pageAdapter.addFragment(new Preferences(), "Preferences");
        pageAdapter.addFragment(new Security(), "Security");

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        iv_ProfileImage = root.findViewById(R.id.iv_ProfileImage);

        bt_ProfileImageEdit = root.findViewById(R.id.bt_ProfileImageEdit);
        bt_ProfileImageSave = root.findViewById(R.id.bt_ProfileImageSave);
        bt_ProfileImageSave.setVisibility(View.GONE);
        pb_ProfileImage = root.findViewById(R.id.pb_ProfileImage);
        pb_ProfileImage.setVisibility(View.GONE);

        bt_Backhome_Profile = root.findViewById(R.id.bt_Backhome_Profile);
    }

    private void clickListeners(View root) {

        bt_Backhome_Profile.setOnClickListener(v -> {
            switch (typeUser){
                case "Traveler":
                    Navigation.findNavController(root).navigate(R.id.action_profile_to_traveler_home);
                break;
                case "Hotel Manager":
                    Navigation.findNavController(root).navigate(R.id.action_profile_to_hotel_manager_home);
                break;
            }

        });

        bt_ProfileImageEdit.setOnClickListener(v -> openFileChooser());

        bt_ProfileImageSave.setOnClickListener(v -> saveProfileImage());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void saveProfileImage() {
        if (profileImageUri != null) {
            if(!user.getImage().isEmpty()){
                StorageReference imageDeleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getImage());
                imageDeleteRef.delete();
            }

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            String imageId = GenerateUniqueIds.generateId() + "." + getFileExtension(profileImageUri);

            StorageReference fileReference = storageReference.child(imageId);
            mUploadTask = fileReference.putFile(profileImageUri)
            .addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Success, Image uploaded
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                    user.setImage(uri.toString());
                    databaseReference.setValue(user);

                    progressDialog.dismiss();

                    bt_ProfileImageSave.setVisibility(View.GONE);
                    pb_ProfileImage.setVisibility(View.GONE);

                });
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                pb_ProfileImage.setVisibility(View.GONE);
                progressDialog.dismiss();
            })
            .addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null){
            profileImageUri = data.getData();

            Glide.with(this).load(profileImageUri).error(R.drawable.profile_pic_example).fitCenter().into(iv_ProfileImage);

            pb_ProfileImage.setVisibility(View.VISIBLE);
            bt_ProfileImageSave.setVisibility(View.VISIBLE);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}