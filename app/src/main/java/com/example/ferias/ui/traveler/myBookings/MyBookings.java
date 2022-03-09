package com.example.ferias.ui.traveler.myBookings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.example.ferias.data.traveler.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MyBookings extends Fragment{
    private RecyclerView recyclerView;
    List<Booking>bookings;
    List<Hotel>hotels;
    ArrayList<String> keys;

    private DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_bookings_to_traveler_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_mybookings_hotels, container, false);

        initializeElements(root);
        clickListener(root);

        getBookingRefs(root);

        return root;
    }

    private void clickListener(View root) {
    }

    private void initializeElements(View root) {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Booking");
        recyclerView = root.findViewById(R.id.bookingsList_recyclerView);
    }

    private void getBookingRefs(View root) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Traveler/" + user.getUid());

        dbUsers.child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    keys = (ArrayList<String>)snapshot.getValue();
                }
                else keys = new ArrayList<>();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bookings = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(keys.contains(ds.getKey()))
                            bookings.add(ds.getValue(Booking.class));
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hotel");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Hotel hotels[] = new Hotel[bookings.size()];
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (int i = 0; i < bookings.size(); i++){
                        if(bookings.get(i).getHotelID().matches(ds.getKey()))
                        {
                            hotels[i] = ds.getValue(Hotel.class);
                        }
                    }
                }

                if(bookings.size() == 0){
                    Navigation.findNavController(root).navigate(R.id.action_bookings_self);
                }
                else{
                    adapterMyBookings adapterMyBookings = new adapterMyBookings(bookings, hotels);
                    recyclerView.setAdapter(adapterMyBookings);
                    // Set layout manager to position the items
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}




