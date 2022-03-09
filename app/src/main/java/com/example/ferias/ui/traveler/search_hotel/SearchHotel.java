package com.example.ferias.ui.traveler.search_hotel;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ferias.R;
import com.example.ferias.data.hotel_manager.Hotel;
import com.example.ferias.ui.traveler.favorites.RecyclerItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchHotel extends Fragment {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView mResultList;
    private TextView mResultInfo;

    private DatabaseReference databaseReference;
    private FirebaseRecyclerOptions <Hotel> options;
    private FirebaseRecyclerAdapter <Hotel,MyViewHolderClass> adapter;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_searchHotel_to_traveler_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_search_hotel, container, false);

        initializeElements(root);
        clickListener(root);

        setQuery(root);
        return root;
    }

    private void setQuery(View root) {
        Query query;
        String locality = "";

        if(getArguments() != null){
            String modsWanted = getArguments().getString("modsWanted");
            locality = getArguments().getString("inputText");

            if(modsWanted != null)
            {
                switch(modsWanted)
                {
                    case "Party":
                        query= databaseReference.orderByChild("moods/moods/Nightlife, clubs and parties").equalTo(true);
                        loadData(query,root);
                        return;
                    case "Chill":
                        query= databaseReference.orderByChild("moods/moods/Very chill, perfect to relax").equalTo(true);
                        loadData(query,root);
                        return;
                    case "Adventure":
                        query= databaseReference.orderByChild("moods/moods/Ready to be explored! Embark on an adventure").equalTo(true);
                        loadData(query,root);
                        return;
                    case "Sports":
                        query= databaseReference.orderByChild("moods/moods/You can do some sport activities").equalTo(true);
                        loadData(query,root);
                        return;
                }
            }
        }
        query= databaseReference.orderByChild("address/city").startAt(locality.toUpperCase()).endAt(locality.toLowerCase() + "\uf8ff");
        loadData(query,root);
    }

    private void clickListener(View root) {

        mSearchBtn.setOnClickListener(v -> {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            Query query = databaseReference.orderByChild("address/city").startAt(mSearchField.getText().toString().trim().toUpperCase()).endAt(mSearchField.getText().toString().trim().toLowerCase() + "\uf8ff");

            if(mSearchField.getText().toString().trim().isEmpty())
                mResultInfo.setText("All");
            else
                mResultInfo.setText(mSearchField.getText().toString());
            loadData(query,root);
        });

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

        mResultList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mResultList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //go-to hotel page
                Bundle bundle = new Bundle();
                List<String>keys=new ArrayList<>();

                if(filteredResults.isEmpty())
                    for(Map.Entry<Hotel,String> entry : searchResults.entrySet())
                        keys.add(entry.getValue());
                else
                    for(Map.Entry<Hotel,String> entry : filteredResults.entrySet())
                        keys.add(entry.getValue());

                bundle.putString("clickDetails", keys.get(position));
                bundle.putString("PreviousFragment","Search");
                Navigation.findNavController(root).navigate(R.id.action_search_hotel_to_traveler_hotelview, bundle);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        maxPriceBtn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
            }
        });
        minPriceBtn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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

    private void loadData(Query query,View root) {
        searchResults = new LinkedHashMap<>();
        filteredResults =new LinkedHashMap<>();
        options = new FirebaseRecyclerOptions.Builder<Hotel>().setQuery(query, Hotel.class).build();

        adapter= new FirebaseRecyclerAdapter<Hotel, MyViewHolderClass>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolderClass holder, int position, @NonNull Hotel model) {
                searchResults.put(model,getRef(position).getKey());

                holder.setName(model.getName());
                holder.setCity(model.getAddress().getCity());
                holder.setPrice(Float.toString(model.getPrice()));
                Picasso.get().load(model.getCoverPhoto()).into(holder.image);
            }

            @NonNull
            @Override
            public MyViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // if you dont like mine layout (Martin) i kept the old one in case
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.traveler_search_list_item_layout, parent,false);
                return new MyViewHolderClass(v);
            }
        };
        adapter.startListening();
        mResultList.setAdapter(adapter);
    }

    private void initializeElements(View root) {
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Hotel");

        LinearLayout ll_SearchNearby = root.findViewById(R.id.ll_SearchNearby);
        ll_SearchNearby.setVisibility(View.GONE);

        mSearchField = root.findViewById(R.id.search_input_location);
        mSearchBtn = root.findViewById(R.id.search_btn);
        mResultList = root.findViewById(R.id.searchResults);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mResultInfo = root.findViewById(R.id.searchResultsInfoText);

        if(getArguments() != null){
            if(getArguments().getString("inputText") != null){
                String local = getArguments().getString("inputText");
                if(local.isEmpty()){
                    mResultInfo.setText("All");
                }
                else{
                    mResultInfo.setText(local);
                }
            }
        }
        else{
            mResultInfo.setText("All");
        }



        filterBtn= root.findViewById(R.id.filter_btn);
        minPriceBtn = root.findViewById(R.id.min_price);
        maxPriceBtn = root.findViewById(R.id.max_price);
        partyMoodBtn = root.findViewById(R.id.searchFilter_party);
        chillMoodBtn = root.findViewById(R.id.searchFilter_Chill);
        adventureMoodBtn = root.findViewById(R.id.searchFilter_Adventure);
        sportsMoodBtn = root.findViewById(R.id.searchFilter_sports);
        filter_popup_cv = root.findViewById(R.id.filter_popup_cv);

        filter_popup_cv.setVisibility(View.GONE);
        minPriceBtn.setVisibility(View.GONE);
        maxPriceBtn.setVisibility(View.GONE);
        partyMoodBtn.setVisibility(View.GONE);
        chillMoodBtn.setVisibility(View.GONE);
        adventureMoodBtn.setVisibility(View.GONE);
        sportsMoodBtn.setVisibility(View.GONE);
    }

    private void applyFilters(Float minPrice, Float maxPrice, boolean party, boolean chill, boolean adventure, boolean sports)
    {
        filteredResults = new LinkedHashMap<>();
        filteredResults.putAll(searchResults);
        /*for(Map.Entry<Hotel,String> entry : searchResults.entrySet())
            filteredResults.put(entry.getKey(),entry.getValue());*/

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

        adapterFilteredResults adapterFilteredResults=new adapterFilteredResults(filteredResults.keySet());
        mResultList.setAdapter(adapterFilteredResults);
    }

    //SEARCH NEARBY HOTELS


}