package com.example.ferias.ui.traveler.search_hotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class adapterFilteredResults extends RecyclerView.Adapter<adapterFilteredResults.ViewHolder>{
    private final List<Hotel> filteredHotels;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, city, price;
        ImageView photo;
        RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.search_listName);
            city=itemView.findViewById(R.id.search_listCity);
            price=itemView.findViewById(R.id.search_listPrice);
            photo=itemView.findViewById(R.id.search_listPhoto);
            rating=itemView.findViewById(R.id.search_listRating);
        }

        @Override
        public String toString() {
            return "MyViewHolderClass{" +
                    "name=" + name +
                    ", city=" + city +
                    ", price=" + price +
                    '}';
        }
    }

    public adapterFilteredResults(Set<Hotel> hotels) {
        this.filteredHotels= new ArrayList<>(hotels);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        // if you dont like mine layout (Martin) i kept the old one in case
        View FavsView = inflater.inflate(R.layout.traveler_search_list_item_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(FavsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = filteredHotels.get(position);
        holder.name.setText(hotel.getName());
        holder.city.setText(hotel.getAddress().getCity());
        holder.price.setText(Float.toString(hotel.getPrice()));
        Picasso.get().load(hotel.getCoverPhoto()).into(holder.photo);
        holder.rating.setRating(hotel.getStars());
    }

    @Override
    public int getItemCount() {
        return filteredHotels.size();
    }

}