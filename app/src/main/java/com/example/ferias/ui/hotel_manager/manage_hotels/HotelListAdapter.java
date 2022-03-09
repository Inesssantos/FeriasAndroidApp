package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;

import java.util.ArrayList;

public class HotelListAdapter extends RecyclerView.Adapter<HotelListAdapter.HotelListViewHolder> {

    private ArrayList<Hotel> mHotelList;
    private OnItemClickListener mListener;
    private FragmentActivity fragment;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeeClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class HotelListViewHolder extends RecyclerView.ViewHolder {
        public ImageView hotel_item_Photo;

        public TextView hotel_item_Name;

        public LinearLayout ll_hotel_item_buttons;

        public ImageButton hotel_item_See;
        public ImageButton hotel_item_Edit;
        public ImageButton hotel_item_Delete;

        public HotelListViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            hotel_item_Photo = itemView.findViewById(R.id.hotel_item_Photo);

            hotel_item_Name = itemView.findViewById(R.id.hotel_item_Name);

            ll_hotel_item_buttons = itemView.findViewById(R.id.ll_hotel_item_buttons);

            hotel_item_See = itemView.findViewById(R.id.hotel_item_See);
            hotel_item_Edit = itemView.findViewById(R.id.hotel_item_Edit);
            hotel_item_Delete = itemView.findViewById(R.id.hotel_item_Delete);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            hotel_item_See.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onSeeClick(position);
                    }
                }
            });

            hotel_item_Edit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            hotel_item_Delete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    public HotelListAdapter(ArrayList<Hotel> exampleList, FragmentActivity fragment) {
        mHotelList = exampleList;
        this.fragment = fragment;
    }

    @Override
    public HotelListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_manager_hotel_list_item, parent, false);
        HotelListViewHolder hlvh = new HotelListViewHolder(v, mListener);
        return hlvh;
    }

    @Override
    public void onBindViewHolder(HotelListViewHolder holder, int position) {
        Hotel currentItem = mHotelList.get(position);

        Glide.with(fragment)
        .load(currentItem.getCoverPhoto())
        .placeholder(R.drawable.ic_add)
        .fitCenter()
        .into(holder.hotel_item_Photo);

        holder.hotel_item_Name.setText(currentItem.getName());

        if(position == 0){
            holder.ll_hotel_item_buttons.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return mHotelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
