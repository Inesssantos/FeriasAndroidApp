package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ferias.R;
import com.example.ferias.data.traveler.Booking;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Statistics extends Fragment {
    private PieChart chart_total_profits;
    private TextView total_profits;

    private PieChart chart_total_bookings;
    private TextView total_bookings;

    private ToggleButton bt_typeChart;

    private String hotelKey;

    private List<Booking> bookingList;

    public Statistics(Bundle bundle) {
        hotelKey = bundle.getString("hotelKey");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.hotel_manager_hotelview_stats, container, false);


        initializeElements(root);

        getChartValues();

        clickListeners();

        return root;
    }

    private void initializeElements(View root) {
        chart_total_profits = root.findViewById(R.id.chart_total_profits);
        total_profits = root.findViewById(R.id.hotelProfits);
        setupChartProfits();

        chart_total_bookings = root.findViewById(R.id.chart_total_bookings);
        total_bookings = root.findViewById(R.id.hotelBookings);
        setupChartBookings();

        bt_typeChart = root.findViewById(R.id.bt_typeChart);
    }

    private void clickListeners() {
        bt_typeChart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ArrangeByMonth(bookingList);
        });
    }

    private void setupChartProfits() {
        chart_total_profits.setDrawHoleEnabled(true);
        chart_total_profits.setEntryLabelTextSize(12);
        chart_total_profits.setEntryLabelColor(Color.WHITE);
        chart_total_profits.setCenterText("Earning by month");
        chart_total_profits.setCenterTextSize(20);
        chart_total_profits.getDescription().setEnabled(false);

        chart_total_profits.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chart_total_profits.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart_total_profits.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);

    }

    private void setupChartBookings() {
        chart_total_bookings.setDrawHoleEnabled(true);
        chart_total_bookings.setEntryLabelTextSize(12);
        chart_total_bookings.setEntryLabelColor(Color.WHITE);
        chart_total_bookings.setCenterText("Bookings by month");
        chart_total_bookings.setCenterTextSize(20);
        chart_total_bookings.getDescription().setEnabled(false);

        chart_total_bookings.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chart_total_bookings.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart_total_bookings.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);

    }

    private void getChartValues() {
        List<String> bookingKeys = new ArrayList<>();
        DatabaseReference databaseReferenceHotelKey = FirebaseDatabase.getInstance().getReference().child("Hotel/" + hotelKey +"/bookings");

        databaseReferenceHotelKey.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    bookingKeys.add(ds.getValue().toString());
                }
                getBookingValues(bookingKeys);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getBookingValues(List<String> bookingKeys) {
        bookingList = new ArrayList<>();
        DatabaseReference databaseReferenceHotelKey = FirebaseDatabase.getInstance().getReference().child("Booking/");

        databaseReferenceHotelKey.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(bookingKeys.contains(ds.getKey()))
                        bookingList.add(ds.getValue(Booking.class));
                }
                ArrangeByMonth(bookingList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ArrangeByMonth(List<Booking> bookingList) {
        LinkedHashMap<String,Float> valuesProfitsByMonth = new LinkedHashMap<>();
        LinkedHashMap<String, Float> valuesBookingsByMonth = new LinkedHashMap<>();
        List<String>months = Arrays.asList(getResources().getStringArray(R.array.months));

        for(int i = 0; i<12; i++ )
        {
            valuesProfitsByMonth.put(months.get(i),Float.valueOf(0));
            for(int j=0 ;j <bookingList.size();j++)
            {
                if(bookingList.get(j).getEnterDate().getMonth() == i)
                {
                    Float value = valuesProfitsByMonth.get(months.get(i)) + bookingList.get(j).getPrice();
                    valuesProfitsByMonth.put(months.get(i),value);
                }
            }
        }

        for(int i = 0; i<12; i++ )
        {
            valuesBookingsByMonth.put(months.get(i),Float.valueOf(0));
            for(int j=0 ;j <bookingList.size();j++)
            {

                if(bookingList.get(j).getEnterDate().getMonth() == i)
                {
                    float value =  valuesBookingsByMonth.get(months.get(i)) + 1;
                    valuesBookingsByMonth.put(months.get(i),value);
                }
            }
        }

        if(bt_typeChart.isChecked()){
            LinkedHashMap<String,Float> valuesProfitsBySeasons = new LinkedHashMap<>();
            LinkedHashMap<String, Float> valuesBookingsBySeasons = new LinkedHashMap<>();

            List<String>seasons = Arrays.asList(getResources().getStringArray(R.array.seasons));
            List<String>seasons_moths = Arrays.asList(getResources().getStringArray(R.array.seasons_moths));

            for(int i=0; i < 4; i++){
                valuesProfitsBySeasons.put(seasons.get(i),Float.valueOf(0));
                valuesBookingsBySeasons.put(seasons.get(i),Float.valueOf(0));
            }

            for(int i=0; i < 4; i++){
                List<String> months_list = new ArrayList<>();
                months_list.addAll(Arrays.asList(seasons_moths.get(i).split(",")));

                for(String month : months_list){
                    float value_profit = valuesProfitsBySeasons.get(seasons.get(i)) + valuesProfitsByMonth.get(month);
                    float value_booking =  valuesBookingsBySeasons.get(seasons.get(i)) + valuesBookingsByMonth.get(month);
                    valuesProfitsBySeasons.put(seasons.get(i),value_profit);
                    valuesBookingsBySeasons.put(seasons.get(i),value_booking);
                }
            }
            chart_total_profits.setCenterText("Earning by season");
            chart_total_bookings.setCenterText("Bookings by season");

            loadDatatoChart("profits",valuesProfitsBySeasons);

            loadDatatoChart("bookings",valuesBookingsBySeasons);
        }
        else{
            chart_total_profits.setCenterText("Earning by month");
            chart_total_bookings.setCenterText("Bookings by month");

            loadDatatoChart("profits",valuesProfitsByMonth);

            loadDatatoChart("bookings",valuesBookingsByMonth);
        }
    }

    private void loadDatatoChart(String type, LinkedHashMap<String, Float> values) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        float total = 0;

        for (LinkedHashMap.Entry<String,Float> entry : values.entrySet()) {
            total += entry.getValue();
            if(entry.getValue() != 0)
                entries.add(new PieEntry(entry.getValue(),entry.getKey()));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        switch(type)
        {
            case "profits":
                for (int color: ColorTemplate.PASTEL_COLORS) {
                    colors.add(color);
                }
                break;
            case "bookings":
                for (int color: ColorTemplate.COLORFUL_COLORS) {
                    colors.add(color);
                }
                break;
        }

        /*for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }*/

        PieDataSet dataSet = new PieDataSet(entries, "Months");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new DefaultValueFormatter(0));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        switch (type){
            case "profits":
                chart_total_profits.setData(data);
                chart_total_profits.invalidate();

                chart_total_profits.animateY(1400, Easing.EaseInOutQuad);

                total_profits.setText(String.valueOf(total));
            break;
            case "bookings":
                chart_total_bookings.setData(data);
                chart_total_bookings.invalidate();

                chart_total_bookings.animateY(1400, Easing.EaseInOutQuad);

                total_bookings.setText(String.valueOf(total));
            break;
        }

    }
}
