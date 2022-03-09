package com.example.ferias.ui.traveler.hotels;

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
import com.example.ferias.ui.hotel_manager.manage_hotels.HotelListAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;
public class adapterSimilarHotels extends RecyclerView.Adapter<adapterSimilarHotels.ViewHolder>{
    private final List<Hotel> mHotels;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, city, price;
        ImageView photo;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name=itemView.findViewById(R.id.Favs_listName);
            city=itemView.findViewById(R.id.Favs_listCity);
            price=itemView.findViewById(R.id.Favs_listPrice);
            photo=itemView.findViewById(R.id.imageViewInCardView);
            ratingBar=itemView.findViewById(R.id.ratingBarSimilar);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
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

    public adapterSimilarHotels(List<Hotel> hotels) {
        this.mHotels=hotels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View FavsView = inflater.inflate(R.layout.fav_list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(FavsView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = mHotels.get(position);
        holder.name.setText(hotel.getName());
        holder.city.setText(hotel.getAddress().getCity());
        holder.price.setText(Float.toString(hotel.getPrice()));
        holder.ratingBar.setRating(hotel.getStars());

        Picasso.get().load(hotel.getCoverPhoto()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return mHotels.size();
    }
}