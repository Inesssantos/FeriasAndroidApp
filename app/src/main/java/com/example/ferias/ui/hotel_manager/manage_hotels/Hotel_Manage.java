package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.hotel_manager.Hotel;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class Hotel_Manage extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceHotel;
    private DatabaseReference databaseReferenceUser;

    private HotelManager user;

    private ImageButton bt_hotel_manage_back;

    private ArrayList<Hotel> mHotelList;
    private RecyclerView mRecyclerView;
    private HotelListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                bt_hotel_manage_back.performClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.hotel_manager_fragment_manage_hotels, container, false);

        initializeElements(root);

        clickListeners();

        getUserDate();

        getListHotel(root);

        return root;
    }

    private void initializeElements(View root) {
        mHotelList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Hotel Manager").child(firebaseUser.getUid());
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("Hotel");

        bt_hotel_manage_back = root.findViewById(R.id.bt_hotel_manage_back);

        mRecyclerView = root.findViewById(R.id.rc_hotel_list);
        //mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());

    }

    private void clickListeners() {
        bt_hotel_manage_back.setOnClickListener(v -> {
                Navigation.findNavController(getView()).navigate(R.id.action_hotel_manage_to_hotel_manager_home);
        });
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

    private void getListHotel(View root){
        Hotel addHotel = new Hotel();
        addHotel.setName("Add Hotel");
        mHotelList.add(addHotel);

        if(!user.getHotels().isEmpty()){
            for(String hotelID : user.getHotels()){
                databaseReferenceHotel.child(hotelID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Hotel hotel = snapshot.getValue(Hotel.class);
                        if(hotel!=null){
                            mHotelList.add(hotel);
                        }
                        if((mHotelList.size()-1) == user.getHotels().size()){
                            buildRecyclerView(root);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", error.getMessage());
                    }
                });
            }
        }
    }

    private void buildRecyclerView(View root) {
        mAdapter = new HotelListAdapter(mHotelList, getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new HotelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position == 0){
                    Navigation.findNavController(root).navigate(R.id.action_hotel_manage_to_hotel_registration);
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Hotel", mHotelList.get(position));
                    bundle.putString("Hotel Id",user.getHotels().get(position-1));
                    Navigation.findNavController(root).navigate(R.id.action_hotel_manage_to_hotel_view, bundle);
                }
            }

            @Override
            public void onSeeClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Hotel", mHotelList.get(position));
                bundle.putString("Hotel Id",user.getHotels().get(position-1));
                Navigation.findNavController(root).navigate(R.id.action_hotel_manage_to_hotel_view, bundle);
            }

            @Override
            public void onEditClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Hotel", mHotelList.get(position));
                bundle.putString("Hotel Id",user.getHotels().get(position-1));
                bundle.putString("PreviousFragment","Hotel_Manage");
                Navigation.findNavController(root).navigate(R.id.action_hotel_manage_to_hotel_edit, bundle);
            }

            @Override
            public void onDeleteClick(int position) {
                StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(mHotelList.get(position).getCoverPhoto());
                imageDeleteCover.delete();

                for(String url : mHotelList.get(position).getOtherPhotos()){
                    StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    imageDeleteOthers.delete();
                }

                databaseReferenceHotel.child(user.getHotels().get(position-1)).removeValue();

                user.removeHotelbyIndex(position-1);

                databaseReferenceUser.setValue(user);
            }
        });
    }
}
