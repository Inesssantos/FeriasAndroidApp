package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.example.ferias.data.traveler.Booking;
import com.example.ferias.ui.traveler.favorites.MyViewHolderClassFavs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingsViewer extends Fragment {
    private RecyclerView lastBookings_rv;
    private DatabaseReference databaseReference;
    String hotelKey;

    public BookingsViewer(Bundle bundle) {
        hotelKey = bundle.getString("hotelKey");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.hotel_manager_last_bookings, container, false);

        initializeElements(root);
        getCurrentBookings(root);
        return root;
    }

    private void getCurrentBookings(View root) {
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Hotel/" + hotelKey);

        dbUsers.child("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    ArrayList<String> keys = (ArrayList<String>)snapshot.getValue();
                    populateRecyclerView(root,keys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateRecyclerView(View root, ArrayList<String> keys) {
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList bookings = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(keys.contains(ds.getKey())){
                        Booking booking = ds.getValue(Booking.class);

                        bookings.add(booking);
                    }
                }

                // Create adapter passing in the sample user data
                ViewBookingsAdpater adapter = new ViewBookingsAdpater(bookings);
                // Attach the adapter to the recyclerview to populate items
                lastBookings_rv.setAdapter(adapter);
                // Set layout manager to position the items
                lastBookings_rv.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeElements(View root) {
        lastBookings_rv = root.findViewById(R.id.manager_lastBooking_rv);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Booking");
    }
}
