package com.example.ferias.ui.traveler.favorites;

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

import java.util.List;
public class MyViewHolderClassFavs extends RecyclerView.Adapter<MyViewHolderClassFavs.ViewHolder>{
    private final List<Hotel> mHotels;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        RatingBar rating;
        ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hotellist_item_name);
            price = itemView.findViewById(R.id.hotellist_item_price);
            photo = itemView.findViewById(R.id.hotellist_item_img);
            rating = itemView.findViewById(R.id.RatingBarFavs);
        }
    }

    public MyViewHolderClassFavs(List<Hotel> hotels) {
        this.mHotels=hotels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View favoriteView = inflater.inflate(R.layout.traveler_favorites_hotel_list_item, parent, false);
        favoriteView.setFocusable(true);
        favoriteView.setClickable(true);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(favoriteView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hotel hotel = mHotels.get(position);
        holder.name.setText(hotel.getName());
        holder.price.setText(Float.toString(hotel.getPrice()));
        holder.rating.setRating(hotel.getStars());
        Picasso.get().load(hotel.getCoverPhoto()).into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return mHotels.size();
    }
}
