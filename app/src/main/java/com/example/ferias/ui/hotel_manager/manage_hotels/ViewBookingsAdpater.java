package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.data.common.User;
import com.example.ferias.data.traveler.Booking;
import com.example.ferias.data.traveler.Traveler;
import com.example.ferias.ui.traveler.favorites.MyViewHolderClassFavs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewBookingsAdpater extends RecyclerView.Adapter<ViewBookingsAdpater.ViewHolder>{
    private final List<Booking> mBookings;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,start,end, adults,kids, total;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.manager_out_name);
            start= itemView.findViewById(R.id.manager_out_start);
            end= itemView.findViewById(R.id.manager_out_end);
            adults= itemView.findViewById(R.id.manager_out_adults);
            kids= itemView.findViewById(R.id.manager_out_kids);
            total= itemView.findViewById(R.id.manager_out_total);
            pic= itemView.findViewById(R.id.manager_out_pic);
        }
    }

    public ViewBookingsAdpater(List<Booking> mBookings) {
        this.mBookings = mBookings;
    }

    @NonNull
    @Override
    public ViewBookingsAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookingView = inflater.inflate(R.layout.hotel_manager_client_booking_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bookingView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = mBookings.get(position);

        getUserDetails(booking.getUserID(),holder);

        holder.adults.setText(Float.toString(booking.getnAdults()));
        holder.kids.setText(Float.toString(booking.getnChildren()));
        holder.total.setText(Float.toString(booking.getPrice()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");//formating according to my need
        holder.start.setText(formatter.format(booking.getEnterDate()));
        holder.end.setText(formatter.format(booking.getExitDate()));


    }

    private void getUserDetails(String userID, ViewHolder holder) {
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Traveler/" + userID);

        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Traveler user = snapshot.getValue(Traveler.class);
                    holder.name.setText(user.getName());
                    if(!user.getImage().isEmpty())
                        Picasso.get().load(user.getImage()).into(holder.pic);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }


}
