package com.example.ferias.ui.traveler.favorites;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favorites extends Fragment {

    private final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Hotel");
    Dialog d;
    private TextView remove, cancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_favorites_to_traveler_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_favorites_hotels, container, false);

        d = new Dialog(getContext());
        d.setContentView(R.layout.dialog_remove_from_favorites);
        d.setCancelable(true);
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        remove = d.findViewById(R.id.remove_req);
        cancel = d.findViewById(R.id.cancel_req);


        clickListener(root);
        getFavsList(root);

        return root;
    }


    private void getFavsList(View root) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Traveler/" + user.getUid());

        dbUsers.child("Favs").addListenerForSingleValueEvent(new ValueEventListener() {
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(keys.contains(ds.getKey())){
                        Hotel hotel = ds.getValue(Hotel.class);

                        list.add(hotel);
                    }
                }
                // Lookup the recyclerview in activity layout
                RecyclerView rvHotels = root.findViewById(R.id.bookingsList_recyclerView);
                rvHotels.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvHotels, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //go-to hotel page
                        Bundle bundle = new Bundle();
                        bundle.putString("clickDetails", keys.get(position));
                        bundle.putString("PreviousFragment","Favorites");
                        Navigation.findNavController(root).navigate(R.id.action_favorites_to_traveler_hotelview, bundle);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        d.show();
                    }
                }));
                // Create adapter passing in the sample user data
                MyViewHolderClassFavs adapter = new MyViewHolderClassFavs(list);
                // Attach the adapter to the recyclerview to populate items
                rvHotels.setAdapter(adapter);
                // Set layout manager to position the items
                rvHotels.setLayoutManager(new GridLayoutManager(getContext(),2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListener(View root) {

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Remove",Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

    }
}