package com.example.ferias.ui.hotel_manager.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.bumptech.glide.Glide;
import com.example.ferias.R;
import com.example.ferias.data.InternalStorage;
import com.example.ferias.data.hotel_manager.HotelManager;
import com.example.ferias.data.traveler.Booking;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Home extends Fragment {

    private FirebaseAuth firebaseAuth;
    private HotelManager user;

    private GoogleSignInClient googleSignInClient;

    private ConstraintLayout cl_HomeManager;

    private ImageView bt_ProfileMenu;
    private MaterialCardView bt_ManageHotels;

    private LinearLayout profileMenu;
    private Button bt_EditProfile, bt_Logout;

    private TextView tv_NameMensage;
    private AnyChartView chart;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_logout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog.setCancelable(false);

                dialog.create();

                Button confirm = dialog.findViewById(R.id.bt_dialog_logout_Confirm);
                Button deny = dialog.findViewById(R.id.bt_dialog_logout_Deny);

                confirm.setOnClickListener(v -> {bt_Logout.performClick(); dialog.dismiss();});

                deny.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.hotel_manager_fragment_home, container, false);

        readUserData();

        initializeElements(root);

        clickListener(root);

        loadDatatoElements();

        getHotels();

        return root;
    }

    private void initializeElements(View root) {
        cl_HomeManager = root.findViewById(R.id.cl_Home_Manager);

        bt_ProfileMenu = root.findViewById(R.id.bt_ProfileMenu_Manager);

        profileMenu = root.findViewById(R.id.ll_ProfileMenu_Manager);
        profileMenu.setVisibility(View.GONE);

        bt_EditProfile = root.findViewById(R.id.bt_EditProfile_Manager);

        bt_Logout = root.findViewById(R.id.bt_Logout_Manager);

        tv_NameMensage = root.findViewById(R.id.tv_NameMensage_Manager);

        bt_ManageHotels = root.findViewById(R.id.bt_ManageHotels);

        chart = root.findViewById(R.id.BarChart);
    }

    private void clickListener(View root) {
        cl_HomeManager.setOnClickListener(v -> profileMenu.setVisibility(View.GONE));

        bt_ProfileMenu.setOnClickListener(v -> {
            if(profileMenu.getVisibility() == View.GONE){
                profileMenu.setVisibility(View.VISIBLE);
                profileMenu.bringToFront();
            }
            else{
                profileMenu.setVisibility(View.GONE);
            }

        });

        bt_EditProfile.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", user);
            Navigation.findNavController(root).navigate(R.id.action_hotel_manager_home_to_profile, bundle);
        });

        bt_ManageHotels.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_hotel_manager_home_to_hotel_manage);
        });

        bt_Logout.setOnClickListener(v -> {
            if(user.isGoogle()){
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        firebaseAuth.signOut();
                    }
                });
            }
            else{
                firebaseAuth.signOut();
            }

            Navigation.findNavController(root).navigate(R.id.action_hotel_manager_home_to_login);
        });
    }

    private void readUserData() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            googleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Hotel Manager").child(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(HotelManager.class);
                if(user != null){
                    loadDatatoElements();
                    try {
                        InternalStorage.writeObject(getContext(), "User", user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getUser:onCancelled",error.toException());
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(HotelManager.class);
                if(user != null){
                    try {
                        InternalStorage.writeObject(getContext(), "User", user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getUser:onCancelled",error.toException());
            }
        });
    }

    private void loadDatatoElements(){
        if(user != null){
            tv_NameMensage.setText("Hi "+user.getName());

            Glide.with(this)
            .load(user.getImage())
            .placeholder(R.drawable.profile_pic_example)
            .fitCenter()
            .into(bt_ProfileMenu);
        }
    }

    private void getHotels() {
        HashMap<String, Float> hotels = new HashMap<>();
        DatabaseReference databaseReferenceHotelKey = FirebaseDatabase.getInstance().getReference().child("Hotel Manager/" + firebaseAuth.getUid() +"/hotels");

        databaseReferenceHotelKey.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    hotels.put(ds.getValue().toString(), Float.valueOf(0));
                }
                getHotelRevenues(hotels);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getHotelRevenues(HashMap<String, Float> hotels ) {
        DatabaseReference databaseReferenceHotelKey = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseReferenceHotelKey.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.child("hotelID").getValue().toString();
                    String price =ds.child("price").getValue().toString();
                    if(hotels.containsKey(ds.child("hotelID").getValue().toString()))
                    {
                        Float newValue= hotels.get(key) + Float.parseFloat(price);
                        hotels.put(key ,newValue);
                    }
                }
                changeKeyName(hotels);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeKeyName(HashMap<String, Float> hotels) {
        HashMap<String, Float> updatekeyNames = new HashMap<>();
        DatabaseReference databaseReferenceHotelKey = FirebaseDatabase.getInstance().getReference().child("Hotel");
        databaseReferenceHotelKey.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    String name =ds.child("name").getValue().toString();
                    if(hotels.containsKey(key))
                    {
                        updatekeyNames.put(name, hotels.get(key));
                    }
                }
                buildChat(updatekeyNames);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buildChat(HashMap<String, Float> updatekeyNames) {
        String color;
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for (LinkedHashMap.Entry<String,Float> entry : updatekeyNames.entrySet()) {
            data.add(new ValueDataEntry(entry.getKey() ,entry.getValue()));
        }

        Column column = cartesian.column(data);
        column.rendering().point("function() {\n" +
                "    // if missing (not correct data), then skipping this point drawing\n" +
                "    if (this.missing) {\n" +
                "return;\n" +
                "    }\n" +
                "\n" +
                "    // get shapes group\n" +
                "    var shapes = this.shapes || this.getShapesGroup(this.pointState);\n" +
                "    // calculate the left value of the x-axis\n" +
                "    var leftX = this.x - this.pointWidth / 2;\n" +
                "    // calculate the right value of the x-axis\n" +
                "    var rightX = leftX + this.pointWidth;\n" +
                "    // calculate the half of point width\n" +
                "    var rx = this.pointWidth / 2;\n" +
                "\n" +
                "    shapes['path']\n" +
                "    // resets all 'line' operations\n" +
                "    .clear()\n" +
                "    // draw column with rounded edges\n" +
                "    .moveTo(leftX, this.zero)\n" +
                "    .lineTo(leftX, this.value + rx)\n" +
                "    .circularArc(leftX + rx, this.value + rx, rx, rx, 180, 180)\n" +
                "    .lineTo(rightX, this.zero)\n" +
                "    // close by connecting the last point with the first straight line\n" +
                "    .close();\n" +
                "}");
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("€{%Value}{groupsSeparator: }");
        color= String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(),R.color.accent_color)));
        column.color(color);



        cartesian.animation(true);
        cartesian.yAxis(0).labels().format("€{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Hotels");
        cartesian.yAxis(0).title("Revenue");

        color= String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(),R.color.background_color)));
        cartesian.background().fill(color);
        cartesian.labels().width(200);
        cartesian.labels().height(200);
        chart.setChart(cartesian);

    }

}
