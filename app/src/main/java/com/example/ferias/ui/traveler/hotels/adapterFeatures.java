package com.example.ferias.ui.traveler.hotels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.ui.traveler.favorites.MyViewHolderClassFavs;

import java.util.ArrayList;

public class adapterFeatures extends RecyclerView.Adapter<adapterFeatures.ViewHolder> {
    ArrayList<String> listFeatures;
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.single_featureView_Name);
        }
    }

    public adapterFeatures(ArrayList<String> listFeatures) {
        this.listFeatures=listFeatures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View featureView = inflater.inflate(R.layout.feature_single_view, parent, false);// inflate the layout
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(featureView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(listFeatures.get(position));
        holder.name = setDrawable(listFeatures.get(position),holder.name);
    }

    @Override
    public int getItemCount() {
        return listFeatures.size();
    }

    private TextView setDrawable(String s ,TextView feature) {
        Drawable img;
        switch(s)
        {
            case "Restaurant":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_restaurant,0,0,0);
                break;
            case "Service Room":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_room_service,0,0,0);
                break;
            case "Pub":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_pub,0,0,0);
                break;
            case "Lunch":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_lunch,0,0,0);
                break;
            case "Dinner":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_dinner,0,0,0);
                break;
            case "Breakfast":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_breakfast,0,0,0);
                break;
            case "24h Reception":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_24reception,0,0,0);
                break;
            case "Air conditioner":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_airconditioner,0,0,0);
                break;
            case "Wifi":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_wifi,0,0,0);
                break;
            case "Outside Pool":
            case "Inside Pool":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_outsidepool,0,0,0);
                break;
            case "SPA":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_spa,0,0,0);
                break;
            case "Garden":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_garden,0,0,0);
                break;
            case "Gymnasium":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_gym,0,0,0);
                break;
            case "Sauna":
                feature.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_features_sauna,0,0,0);
                break;
        }
        return feature;
    }
}
