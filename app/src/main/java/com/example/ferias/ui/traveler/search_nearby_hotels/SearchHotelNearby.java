package com.example.ferias.ui.traveler.search_nearby_hotels;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchHotelNearby extends Fragment{

    private DatabaseReference databaseReference;

    private RecyclerView mRecyclerView;
    private HotelViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LatLng latLng;
    private Float nearbyDistance;

    private LinearLayout ll_SearchNearby;
    private TextView tv_searchNearbyTitle;
    private TextView tv_searchNearbyResults;

    private FloatingActionButton filterBtn;
    private EditText minPriceBtn, maxPriceBtn;
    private boolean fabsOn = false;

    private ExtendedFloatingActionButton partyMoodBtn;
    private ExtendedFloatingActionButton chillMoodBtn;
    private ExtendedFloatingActionButton adventureMoodBtn;
    private ExtendedFloatingActionButton sportsMoodBtn;
    private MaterialCardView filter_popup_cv;

    private LinkedHashMap<Hotel,String> searchResults = new LinkedHashMap<>();
    private LinkedHashMap<Hotel,String> filteredResults = new LinkedHashMap<>();

    //filter vars
    private Float minPrice =null, maxPrice=null;
    private boolean party=false,chill=false,adventure=false, sports=false;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_traveler_search_nearby_to_traveler_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_search_hotel, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        initializeElements(root);

        getLocation();

        clickListener(root);

        return root;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 40);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1);
        locationRequest.setFastestInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Inicialize location
                Location location = task.getResult();
                if(location != null){
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //Log.e("Location:"+"'"+location.getLatitude()+"'"+";"+"'"+location.getLongitude()+"'","SEARCH_HOTEL_NEARBY");
                    getListHotel();
                }
            }
        });

    }

    private void initializeElements(View root) {
        ll_SearchNearby = root.findViewById(R.id.ll_SearchNearby);
        ll_SearchNearby.setVisibility(View.VISIBLE);

        tv_searchNearbyTitle = root.findViewById(R.id.tv_searchNearbyTitle);

        tv_searchNearbyResults = root.findViewById(R.id.tv_searchNearbyResults);
        tv_searchNearbyResults.setVisibility(View.GONE);

        LinearLayout ll_Search = root.findViewById(R.id.ll_Search);
        ll_Search.setVisibility(View.GONE);

        mRecyclerView = root.findViewById(R.id.searchResults);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        filterBtn= root.findViewById(R.id.filter_btn);
        minPriceBtn = root.findViewById(R.id.min_price);
        maxPriceBtn = root.findViewById(R.id.max_price);
        partyMoodBtn = root.findViewById(R.id.searchFilter_party);
        chillMoodBtn = root.findViewById(R.id.searchFilter_Chill);
        adventureMoodBtn = root.findViewById(R.id.searchFilter_Adventure);
        sportsMoodBtn = root.findViewById(R.id.searchFilter_sports);
        filter_popup_cv = root.findViewById(R.id.filter_popup_cv);

        minPriceBtn.setVisibility(View.GONE);
        maxPriceBtn.setVisibility(View.GONE);
        partyMoodBtn.setVisibility(View.GONE);
        chillMoodBtn.setVisibility(View.GONE);
        adventureMoodBtn.setVisibility(View.GONE);
        sportsMoodBtn.setVisibility(View.GONE);
        filter_popup_cv.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hotel");

        nearbyDistance =  getArguments().getFloat("SearchRadius"); //User select distance

        //String[] gps = getArguments().getString("GPS").split(",");
        //latLng = new LatLng(Double.parseDouble(gps[0]),Double.parseDouble(gps[1]));

    }

    private void clickListener(View root) {
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fabsOn)
                {
                    filter_popup_cv.setVisibility(View.VISIBLE);
                    filter_popup_cv.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_in));
                    minPriceBtn.setVisibility(View.VISIBLE);
                    maxPriceBtn.setVisibility(View.VISIBLE);
                    partyMoodBtn.setVisibility(View.VISIBLE);
                    chillMoodBtn.setVisibility(View.VISIBLE);
                    adventureMoodBtn.setVisibility(View.VISIBLE);
                    sportsMoodBtn.setVisibility(View.VISIBLE);
                    fabsOn=true;
                }
                else
                {
                    filter_popup_cv.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    minPriceBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    minPriceBtn.setVisibility(View.GONE);
                    maxPriceBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    maxPriceBtn.setVisibility(View.GONE);
                    partyMoodBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    partyMoodBtn.setVisibility(View.GONE);
                    chillMoodBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    chillMoodBtn.setVisibility(View.GONE);
                    adventureMoodBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    adventureMoodBtn.setVisibility(View.GONE);
                    sportsMoodBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_out));
                    sportsMoodBtn.setVisibility(View.GONE);
                    filter_popup_cv.setVisibility(View.GONE);
                    fabsOn=false;
                }
            }
        });

        maxPriceBtn.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.	IME_ACTION_DONE) {
                if(!maxPriceBtn.getText().toString().isEmpty())
                    maxPrice= Float.parseFloat(maxPriceBtn.getText().toString());
                else
                    maxPrice=null;
                applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
                handled = true;
            }
            return handled;
        });

        minPriceBtn.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.	IME_ACTION_DONE) {
                if(!minPriceBtn.getText().toString().isEmpty())
                    minPrice= Float.parseFloat(minPriceBtn.getText().toString());
                else
                    minPrice=null;
                applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
                handled = true;
            }
            return handled;
        });
        partyMoodBtn.setOnClickListener(v -> {
            if(!party)
            {
                partyMoodBtn.setBackgroundResource(R.drawable.rounded_rectangle_active);
                party=true;
            }
            else
            {
                partyMoodBtn.setBackgroundResource(R.drawable.shape_search_1_active);
                party=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });
        chillMoodBtn.setOnClickListener(v -> {
            if(!chill)
            {
                chillMoodBtn.setBackgroundResource(R.drawable.rounded_rectangle_active);
                chill=true;
            }
            else
            {
                chillMoodBtn.setBackgroundResource(R.drawable.shape_search_1_active);
                chill=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });
        adventureMoodBtn.setOnClickListener(v -> {
            if(!adventure)
            {
                adventureMoodBtn.setBackgroundResource(R.drawable.rounded_rectangle_active);
                adventure=true;
            }
            else
            {
                adventureMoodBtn.setBackgroundResource(R.drawable.shape_search_1_active);
                adventure=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });
        sportsMoodBtn.setOnClickListener(v -> {
            if(!sports)
            {
                sportsMoodBtn.setBackgroundResource(R.drawable.rounded_rectangle_active);
                sports=true;
            }
            else
            {
                sportsMoodBtn.setBackgroundResource(R.drawable.shape_search_1_active);
                sports=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });
    }

    private void applyFilters(Float minPrice, Float maxPrice, boolean party, boolean chill, boolean adventure, boolean sports)
    {
        filteredResults = new LinkedHashMap<>();
        filteredResults.putAll(searchResults);

        if(minPrice != null){
            for (Map.Entry<Hotel,String> entry : searchResults.entrySet()) {
                if(entry.getKey().getPrice()<minPrice)
                {
                    filteredResults.remove(entry.getKey());
                }
            }
        }

        if(maxPrice != null){
            for (Map.Entry<Hotel,String> entry : searchResults.entrySet()) {
                if(entry.getKey().getPrice()> maxPrice)
                {
                    filteredResults.remove(entry.getKey());
                }
            }
        }
        if(party)
        {
            for (Map.Entry<Hotel,String> entry : searchResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("Nightlife, clubs and parties")==false)
                {
                    filteredResults.remove(entry.getKey());
                }
            }
        }
        if(chill)
        {
            for (Map.Entry<Hotel,String> entry : searchResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("Very chill, perfect to relax")==false)
                {
                    filteredResults.remove(entry.getKey());
                }
            }
        }
        if(adventure)
        {
            for (Map.Entry<Hotel,String> entry : searchResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("Ready to be explored! Embark on an adventure")==false)
                {
                    filteredResults.remove(entry.getKey());
                }
            }
        }
        if(sports)
        {
            for (Map.Entry<Hotel,String> entry : searchResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("You can do some sport activities")==false)
                {
                    filteredResults.remove(entry.getKey());
                }
            }
        }

        List<Hotel> mHotelList = new ArrayList<>();
        mHotelList.addAll(filteredResults.keySet());
        buildRecyclerView(mHotelList);
    }

    private void getListHotel(){

        List<Hotel> mHotelList = new ArrayList<>();
        searchResults = new LinkedHashMap<>();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Hotel hotel = snap.getValue(Hotel.class);
                    if(hotel!=null){
                        if(hotel.getAddress().getCoordinates() != null)
                            if(verifyNearbyHotel(hotel.getAddress().getCoordinates())){
                                searchResults.put(hotel,snap.getKey());
                            }
                    }
                }
                mHotelList.clear();
                mHotelList.addAll(searchResults.keySet());
                //Log.e("Enter getListHotel","get");
                buildRecyclerView(mHotelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void buildRecyclerView(List<Hotel> hotelList) {

        //Log.e("'"+hotelList.get(0).getName()+"'","Hotel Name Search Nearby");

        if(hotelList.isEmpty()){
            tv_searchNearbyResults.setVisibility(View.VISIBLE);
            filterBtn.setVisibility(View.GONE);
        }
        else{
            tv_searchNearbyResults.setVisibility(View.GONE);
            filterBtn.setVisibility(View.VISIBLE);
        }

        mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new HotelViewAdapter(hotelList, getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {
            Bundle bundle = new Bundle();
            bundle.putString("clickDetails", searchResults.get(hotelList.get(position)));
            bundle.putString("PreviousFragment","SearchNearby");
            Navigation.findNavController(getView()).navigate(R.id.action_traveler_search_nearby_to_traveler_hotelview, bundle);
        });

    }

    public boolean verifyNearbyHotel(LatLng hotelLocation){


        if(latLng != null){
            float[] distance = new float[2];

            Location.distanceBetween( hotelLocation.latitude, hotelLocation.longitude, latLng.latitude, latLng.longitude, distance);

            if( distance[0] < nearbyDistance){
                return true;
            }
        }

        return false ;
    }

}