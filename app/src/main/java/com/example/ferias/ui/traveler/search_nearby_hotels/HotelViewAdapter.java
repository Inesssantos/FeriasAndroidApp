package com.example.ferias.ui.traveler.search_nearby_hotels;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;

import java.util.List;

public class HotelViewAdapter extends RecyclerView.Adapter<HotelViewAdapter.HotelViewHolder> {

    private List<Hotel> mHotelList;
    private HotelViewAdapter.OnItemClickListener mListener;
    private FragmentActivity fragment;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(HotelViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView hotel_item_Photo;
        TextView hotel_item_Name, hotel_item_City, hotel_item_Price;
        RatingBar hotel_item_Rating;

        public HotelViewHolder(View itemView, final HotelViewAdapter.OnItemClickListener listener) {
            super(itemView);
            hotel_item_Photo = itemView.findViewById(R.id.search_listPhoto);

            hotel_item_Name = itemView.findViewById(R.id.search_listName);
            hotel_item_City = itemView.findViewById(R.id.search_listCity);
            hotel_item_Price = itemView.findViewById(R.id.search_listPrice);

            hotel_item_Rating = itemView.findViewById(R.id.search_listRating);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public HotelViewAdapter(List<Hotel> exampleList, FragmentActivity fragment) {
        mHotelList = exampleList;
        this.fragment = fragment;
    }

    @Override
    public HotelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.traveler_search_list_item_layout, parent, false);
        HotelViewAdapter.HotelViewHolder hlvh = new HotelViewAdapter.HotelViewHolder(v, mListener);
        return hlvh;
    }

    @Override
    public void onBindViewHolder(HotelViewAdapter.HotelViewHolder holder, int position) {
        Hotel currentItem = mHotelList.get(position);

        Glide.with(fragment)
                .load(currentItem.getCoverPhoto())
                .fitCenter()
                .into(holder.hotel_item_Photo);

        holder.hotel_item_Name.setText(currentItem.getName());
        holder.hotel_item_City.setText(currentItem.getAddress().getCity());
        holder.hotel_item_Price.setText(Float.toString(currentItem.getPrice()));
        holder.hotel_item_Rating.setRating(currentItem.getStars());

    }
    @Override
    public int getItemCount() {
        return mHotelList.size();
    }
}

