package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.hotel_manager.Hotel;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.ui.common.profile.PageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

public class Hotel_View extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceHotel;
    private DatabaseReference databaseReferenceUser;

    private HotelManager user;

    private String hotelId;
    private Hotel hotel;

    private ImageView iv_hotel_view_photo;
    private TextView tv_hotel_view_name ;
    private RatingBar rb_hotel_view_stars;
    private ImageButton bt_hotel_view_edit, bt_hotel_view_delete, bt_hotel_view_back;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                bt_hotel_view_back.performClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.hotel_manager_fragment_hotel_view, container, false);

        hotelId = getArguments().getString("Hotel Id");
        initializeElements(root);

        getUserDate();
        loadDatatoElements(root);

        clickListeners(root);

        return root;

    }

    private void initializeElements(View root) {
        iv_hotel_view_photo = root.findViewById(R.id.iv_hotel_view_photo);
        tv_hotel_view_name = root.findViewById(R.id.tv_hotel_view_name);
        rb_hotel_view_stars = root.findViewById(R.id.rb_hotel_view_stars);
        bt_hotel_view_edit = root.findViewById(R.id.bt_hotel_view_edit);
        bt_hotel_view_delete = root.findViewById(R.id.bt_hotel_view_delete);

        bt_hotel_view_back = root.findViewById(R.id.bt_hotel_view_back);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Hotel Manager").child(firebaseUser.getUid());
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("Hotel");

        tabLayout= root.findViewById(R.id.manager_hotelViewer_tab_layout);
        viewPager= root.findViewById(R.id.manager_hotelViewer_pager);

        pageAdapter = new PageAdapter(getParentFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putSerializable("hotelKey", hotelId);
        pageAdapter.addFragment(new BookingsViewer(bundle), "Last bookings");
        pageAdapter.addFragment(new Statistics(bundle),"Statistics");
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void getUserDate(){
        try {
            user = (HotelManager) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDatatoElements(View root) {
        if(!getArguments().isEmpty()) {
            hotel = (Hotel) getArguments().getSerializable("Hotel");
            if (hotel != null) {
                tv_hotel_view_name.setText(hotel.getName());

                rb_hotel_view_stars.setRating(hotel.getStars());

                Glide.with(this)
                .load(hotel.getCoverPhoto())
                .placeholder(R.drawable.admin_backgrounf_pic)
                .fitCenter()
                .into(iv_hotel_view_photo);

            } else {
                Navigation.findNavController(root).navigate(R.id.action_hotel_view_to_hotel_manage);
            }
        }
        else {
            Navigation.findNavController(root).navigate(R.id.action_hotel_view_to_hotel_manage);
        }
    }

    private void clickListeners(View root) {
        bt_hotel_view_back.setOnClickListener(v -> {
            Navigation.findNavController(getView()).navigate(R.id.action_hotel_view_to_hotel_manage);
        });

        bt_hotel_view_edit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Hotel", hotel);
            bundle.putString("Hotel Id", hotelId);
            bundle.putString("PreviousFragment","Hotel_View");
            Navigation.findNavController(root).navigate(R.id.action_hotel_view_to_hotel_edit, bundle);
        });

        bt_hotel_view_delete.setOnClickListener(v -> {
            StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(hotel.getCoverPhoto());
            imageDeleteCover.delete();

            for(String url : hotel.getOtherPhotos()){
                StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                imageDeleteOthers.delete();
            }

            databaseReferenceHotel.child(hotelId).removeValue();

            user.removeHotelbyObject(hotelId);

            databaseReferenceUser.setValue(user);

            bt_hotel_view_back.performClick();
        });
    }

}