package com.example.ferias.ui.traveler.search_hotel_on_map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HotelOnMap extends Fragment implements OnMapReadyCallback {

    private DatabaseReference databaseReference;

    private static final int REQUEST_FINE_LOCATION = 40;

    private FloatingActionButton fab_parent_map, fab_satellite, fab_terrain, fab_normal;
    private TextView tv_satellite, tv_terrain, tv_normal;
    private Boolean isAllMapsFabsVisible;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;

    private SearchView searchView;

    private FloatingActionButton filterBtn;
    private EditText minPriceBtn, maxPriceBtn;
    private boolean fabsOn = false;

    private ExtendedFloatingActionButton partyMoodBtn;
    private ExtendedFloatingActionButton chillMoodBtn;
    private ExtendedFloatingActionButton adventureMoodBtn;
    private ExtendedFloatingActionButton sportsMoodBtn;
    private MaterialCardView filter_popup_cv;
    private Float minPrice =null, maxPrice=null;
    private boolean party=false,chill=false,adventure=false, sports=false;

    private LinkedHashMap<Hotel,String> hotelResults = new LinkedHashMap<>();
    private LinkedHashMap<Hotel,String> hotelfilteredResults = new LinkedHashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_traveler_hotel_on_map_to_traveler_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.traveler_fragment_hotel_on_map, container, false);

        initializeElements(root);

        clickListener(root);

        return root;
    }

    private void initializeElements(View root) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hotel");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);

        floatingActionButtonsMapStyleInitialize(root);

        searchViewInitialize(root);

        filtersInitialize(root);
    }

    private void floatingActionButtonsMapStyleInitialize(View root) {

        fab_parent_map = root.findViewById(R.id.fab_parent_map);
        fab_satellite = root.findViewById(R.id.fab_satellite);
        fab_terrain = root.findViewById(R.id.fab_terrain);
        fab_normal = root.findViewById(R.id.fab_normal);

        tv_satellite = root.findViewById(R.id.tv_satellite);
        tv_terrain = root.findViewById(R.id.tv_terrain);
        tv_normal = root.findViewById(R.id.tv_normal);

        fab_satellite.setVisibility(View.GONE);
        fab_terrain.setVisibility(View.GONE);
        fab_normal.setVisibility(View.GONE);

        tv_satellite.setVisibility(View.GONE);
        tv_terrain.setVisibility(View.GONE);
        tv_normal.setVisibility(View.GONE);

        tv_satellite.bringToFront();
        tv_terrain.bringToFront();
        tv_normal.bringToFront();

        isAllMapsFabsVisible = false;
    }

    private void filtersInitialize(View root) {
        filter_popup_cv= root.findViewById(R.id.filter_popup_cv_onMap);
        filterBtn= root.findViewById(R.id.filter_btn_onMap);
        minPriceBtn = root.findViewById(R.id.min_price_onMap);
        maxPriceBtn = root.findViewById(R.id.max_price_onMap);
        partyMoodBtn = root.findViewById(R.id.searchFilter_party_onMap);;
        chillMoodBtn = root.findViewById(R.id.searchFilter_Chill_onMap);;
        adventureMoodBtn = root.findViewById(R.id.searchFilter_Adventure_onMap);;
        sportsMoodBtn = root.findViewById(R.id.searchFilter_sports_onMap);;

        minPriceBtn.setVisibility(View.GONE);
        maxPriceBtn.setVisibility(View.GONE);
        partyMoodBtn.setVisibility(View.GONE);
        chillMoodBtn.setVisibility(View.GONE);
        adventureMoodBtn.setVisibility(View.GONE);
        sportsMoodBtn.setVisibility(View.GONE);
        filter_popup_cv.setVisibility(View.GONE);
    }

    private void searchViewInitialize(View root) {
        searchView = root.findViewById(R.id.sv_location_OnMap);

        TextView textView = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.WHITE);

        ImageView searchClose = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null));
        searchClose.setColorFilter(Color.WHITE);

        ImageView searchMag = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null));
        searchMag.setColorFilter(Color.WHITE);
    }

    private void clickListener(View root) {
        floatingActionButtonsMapStyleClickListener();

        filterListener();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString().trim();
                List<android.location.Address> addresses = null;

                if(location != null || !location.isEmpty()){
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addresses =  geocoder.getFromLocationName(location,1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(!addresses.isEmpty()){
                        android.location.Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void filterListener() {
        filterBtn.setOnClickListener(v -> {
            if(isAllMapsFabsVisible){
                fab_parent_map.performClick();
            }

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
        });

        maxPriceBtn.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(!maxPriceBtn.getText().toString().isEmpty())
                    maxPrice= Float.parseFloat(maxPriceBtn.getText().toString());
                else
                    maxPrice=null;
                applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        minPriceBtn.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(!minPriceBtn.getText().toString().isEmpty())
                    minPrice= Float.parseFloat(minPriceBtn.getText().toString());
                else
                    minPrice=null;
                applyFilters(minPrice,maxPrice,party,chill,adventure,sports);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        partyMoodBtn.setOnClickListener(v -> {
            if(!party)
            {
                partyMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.accent_color));
                party=true;
            }
            else
            {
                partyMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.main_color));
                party=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });

        chillMoodBtn.setOnClickListener(v -> {
            if(!chill)
            {
                chillMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.accent_color));
                chill=true;
            }
            else
            {
                chillMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.main_color));
                chill=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });

        adventureMoodBtn.setOnClickListener(v -> {
            if(!adventure)
            {
                adventureMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.accent_color));
                adventure=true;
            }
            else
            {
                adventureMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.main_color));
                adventure=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });

        sportsMoodBtn.setOnClickListener(v -> {
            if(!sports)
            {
                sportsMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.accent_color));
                sports=true;
            }
            else
            {
                sportsMoodBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.main_color));
                sports=false;
            }
            applyFilters(minPrice,maxPrice,party,chill,adventure,sports);
        });
    }

    private void floatingActionButtonsMapStyleClickListener(){
        fab_parent_map.setOnClickListener(view -> {
            if(fabsOn){
                filterBtn.performClick();
            }

            if (!isAllMapsFabsVisible) {
                fab_satellite.show();
                fab_terrain.show();
                fab_normal.show();

                tv_satellite.setVisibility(View.VISIBLE);
                tv_terrain.setVisibility(View.VISIBLE);
                tv_normal.setVisibility(View.VISIBLE);

                isAllMapsFabsVisible = true;

                filterBtn.setVisibility(View.GONE);
            } else {

                fab_satellite.hide();
                fab_terrain.hide();
                fab_normal.hide();

                tv_satellite.setVisibility(View.GONE);
                tv_terrain.setVisibility(View.GONE);
                tv_normal.setVisibility(View.GONE);

                isAllMapsFabsVisible = false;

                filterBtn.setVisibility(View.VISIBLE);
            }
        });

        fab_terrain.setOnClickListener(view -> {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            fab_parent_map.performClick();
        });

        fab_satellite.setOnClickListener(view -> {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            fab_parent_map.performClick();
        });

        fab_normal.setOnClickListener(view -> {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            fab_parent_map.performClick();
        });
    }

    private void applyFilters(Float minPrice, Float maxPrice, boolean party, boolean chill, boolean adventure, boolean sports)
    {
        hotelfilteredResults = new LinkedHashMap<>();
        for(Map.Entry<Hotel,String> entry : hotelResults.entrySet())
            hotelfilteredResults.put(entry.getKey(),entry.getValue());

        if(minPrice != null){
            for (Map.Entry<Hotel,String> entry : hotelResults.entrySet()) {
                if(entry.getKey().getPrice()<minPrice)
                {
                    hotelfilteredResults.remove(entry.getKey());
                }
            }
        }

        if(maxPrice != null){
            for (Map.Entry<Hotel,String> entry : hotelResults.entrySet()) {
                if(entry.getKey().getPrice()> maxPrice)
                {
                    hotelfilteredResults.remove(entry.getKey());
                }
            }
        }
        if(party)
        {
            for (Map.Entry<Hotel,String> entry : hotelResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("Nightlife, clubs and parties")==false)
                {
                    hotelfilteredResults.remove(entry.getKey());
                }
            }
        }
        if(chill)
        {
            for (Map.Entry<Hotel,String> entry : hotelResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("Very chill, perfect to relax")==false)
                {
                    hotelfilteredResults.remove(entry.getKey());
                }
            }
        }
        if(adventure)
        {
            for (Map.Entry<Hotel,String> entry : hotelResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("Ready to be explored! Embark on an adventure")==false)
                {
                    hotelfilteredResults.remove(entry.getKey());
                }
            }
        }
        if(sports)
        {
            for (Map.Entry<Hotel,String> entry : hotelResults.entrySet()) {
                if(entry.getKey().getMoods().getMoods().get("You can do some sport activities")==false)
                {
                    hotelfilteredResults.remove(entry.getKey());
                }
            }
        }

        if(mMap != null){
            mMap.clear();
            creatMarkers(hotelfilteredResults);
        }

    }


    private void getListHotel(){

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Hotel hotel = snap.getValue(Hotel.class);
                    if(hotel!=null ){
                        hotelResults.put(hotel,snap.getKey());
                    }
                }
                creatMarkers(hotelResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void creatMarkers(LinkedHashMap<Hotel,String> hotels){
        for(Map.Entry<Hotel, String> values: hotels.entrySet())
        {
            Hotel hotel = values.getKey();
            String snippet = hotel.getCoverPhoto() + "«" + hotel.getAddress().getCity() + "«" + hotel.getPrice() + "«" + hotel.getStars()+ "«" + values.getValue();
            MarkerOptions markerOptions = new MarkerOptions().position(hotel.getAddress().getCoordinates()).title(hotel.getName()).snippet(snippet);
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
            marker.hideInfoWindow();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        mMap = googleMap;
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                LatLng newlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newlatLng, 18));
            }
        });


        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        mMap.setOnInfoWindowClickListener(marker -> {
            String[] info = marker.getSnippet().split("«");
            Bundle bundle = new Bundle();
            bundle.putString("clickDetails", info[4]);
            bundle.putString("PreviousFragment","SearchOnMap");
            Navigation.findNavController(getView()).navigate(R.id.action_traveler_hotel_on_map_to_traveler_hotelview, bundle);
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.traveler_fragment_hotel_on_map_marker, null);

                ConstraintLayout cl_onMap = v.findViewById(R.id.cl_onMap);

                TextView name = v.findViewById(R.id.hotel_Name_onMap);
                TextView city = v.findViewById(R.id.hotel_City_onMap);
                TextView price = v.findViewById(R.id.hotel_Price_onMap);
                ImageView photo = v.findViewById(R.id.hotel_CoverPhoto_onMap);
                RatingBar ratingBar = v.findViewById(R.id.hotel_Rating_onMap);

                Log.e("Marker Info", marker.getSnippet());

                String[] info = marker.getSnippet().split("«");

                name.setText(marker.getTitle());

                Picasso.get().load(Uri.parse(info[0])).into(photo);

                city.setText(info[1]);
                price.setText(info[2]);
                ratingBar.setRating(Float.parseFloat(info[3]));

                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        getListHotel();
    }

}