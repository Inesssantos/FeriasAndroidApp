package com.example.ferias.ui.hotel_manager.manage_hotels;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.ferias.R;
import com.example.ferias.data.GenerateUniqueIds;
import com.example.ferias.data.common.Address;
import com.example.ferias.data.common.MyCoordinates;
import com.example.ferias.data.hotel_manager.Hotel;
import com.example.ferias.data.hotel_manager.HotelFeature;
import com.example.ferias.data.hotel_manager.HotelMoods;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Hotel_Edit extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String hotelID;
    private Hotel hotel;

    private EditText et_Name,  et_TotalRooms, et_Price;

    private TextView tv_Hotel_Stars ;
    private RatingBar rb_Stars;

    private CountryCodePicker ccp_PhoneCode;
    private EditText et_Phone;

    private CountryCodePicker ccp_country;
    private EditText et_City, et_Address, et_ZipCode;

    private ExtendedFloatingActionButton bt_Features;
    private TextView tv_Features_Selected;
    private HotelFeature hotelFeatures;

    private EditText et_Description;

    private TextView tv_Hotel_Moods_Title;
    private LinearLayout ll_Hotel_Moods;
    private HotelMoods hotelMoods;

    private LinearLayout ll_Hotel_Photos;
    private LinearLayout ll_Hotel_Cover_Photo;
    private TextView tv_title_cover_photo, tv_title_others_photo;
    private Dialog dialog;

    private static final int PICK_COVER_IMAGE_REQUEST = 1;
    private static final int PICK_OTHERS_IMAGE_REQUEST = 2;
    private Uri coverPhoto;
    private ImageSlider mainslider;
    private List<SlideModel> slideModelList;
    private List<Uri> othersphotos;
    private TextView tv_popupMenu;

    private LinearLayout ll_Hotel_GPS_Address;
    private GoogleMap map;
    private MapView mMapView;
    private SearchView searchView;
    private LatLng coordinates;

    private ImageButton bt_hotel_edit_back;

    private MaterialButton bt_RegisterHotel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                String fragment = getArguments().getString("PreviousFragment");

                switch (fragment){
                    case "Hotel_View":
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Hotel", hotel);
                        bundle.putString("Hotel Id",hotelID);
                        Navigation.findNavController(getView()).navigate(R.id.action_hotel_edit_to_hotel_view, bundle);
                    break;

                    case "Hotel_Manage":
                        Navigation.findNavController(getView()).navigate(R.id.action_hotel_edit_to_hotel_manage);
                    break;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.hotel_manager_fragment_hotel_edit, container, false);

        initializeElements(root);

        loadDatatoElements(root);

        clickListeners();

        return root;
    }

    private void initializeElements(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        et_Name = root.findViewById(R.id.et_Hotel_Name);

        tv_Hotel_Stars = root.findViewById(R.id.tv_Hotel_Stars);
        rb_Stars = root.findViewById(R.id.rb_Hotel_Stars);

        ccp_PhoneCode = root.findViewById(R.id.ccp_PhoneCode_Hotel);
        et_Phone=root.findViewById(R.id.et_Phone_Hotel);
        ccp_PhoneCode.registerCarrierNumberEditText(et_Phone);

        et_TotalRooms = root.findViewById(R.id.et_Hotel_Total_Rooms);
        et_Price = root.findViewById(R.id.et_Hotel_Price_Rooms);

        ll_Hotel_GPS_Address = root.findViewById(R.id.ll_Hotel_GPS_Address);
        ccp_country = root.findViewById(R.id.ccp_Hotel_Country);
        et_City = root.findViewById(R.id.et_Hotel_City);
        et_Address = root.findViewById(R.id.et_Hotel_Address);
        et_ZipCode = root.findViewById(R.id.et_Hotel_ZipCode);

        et_Description = root.findViewById(R.id.et_Description_Hotel);

        bt_Features = root.findViewById(R.id.bt_Features);
        tv_Features_Selected = root.findViewById(R.id.tv_Features_Selected);

        tv_Hotel_Moods_Title = root.findViewById(R.id.tv_Hotel_Moods_Title);
        ll_Hotel_Moods = root.findViewById(R.id.ll_Hotel_Moods);

        ll_Hotel_Cover_Photo = root.findViewById(R.id.ll_Hotel_Cover_Photo);
        tv_title_cover_photo = root.findViewById(R.id.tv_title_cover_photo);
        ll_Hotel_Photos = root.findViewById(R.id.ll_Hotel_Photos);
        tv_title_others_photo = root.findViewById(R.id.tv_title_others_photo);
        othersphotos = new ArrayList<>();

        bt_hotel_edit_back = root.findViewById(R.id.bt_hotel_edit_back);

        bt_RegisterHotel = root.findViewById(R.id.bt_RegisterHotel);
    }

    private void loadDatatoElements(View root) {
        if(!getArguments().isEmpty()){
            hotelID = getArguments().getString("Hotel Id");

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Hotel").child(hotelID);
            storageReference = FirebaseStorage.getInstance().getReference("Hotel").child(hotelID);

            hotel = (Hotel) getArguments().getSerializable("Hotel");
            if(hotel != null){
                et_Name.setText(hotel.getName());
                rb_Stars.setRating(hotel.getStars());

                ccp_PhoneCode.setFullNumber(hotel.getPhone());

                et_TotalRooms.setText(String.valueOf(hotel.getTotal_Rooms()));
                et_Price.setText(String.valueOf(hotel.getPrice()));

                ccp_country.setDefaultCountryUsingNameCode(hotel.getAddress().getCountry());
                ccp_country.resetToDefaultCountry();
                et_City.setText(hotel.getAddress().getCity());
                et_Address.setText(hotel.getAddress().getAddress());
                et_ZipCode.setText(hotel.getAddress().getZipcode());

                if(hotel.getAddress().getCoordinates() != null){
                    coordinates = hotel.getAddress().getCoordinates();
                }

                et_Description.setText(hotel.getDescription());

                hotelFeatures = hotel.getFeature();
                hotelMoods = hotel.getMoods();

                if(!hotel.getCoverPhoto().isEmpty()){
                    coverPhoto = Uri.parse(hotel.getCoverPhoto());
                }
                else{
                    coverPhoto = null;
                }


                for(String photo : hotel.getOtherPhotos()){
                    othersphotos.add(Uri.parse(photo));
                }

            }
            else{
                Navigation.findNavController(root).navigate(R.id.action_hotel_edit_to_hotel_manage);
            }
        }
        else{
            Navigation.findNavController(root).navigate(R.id.action_hotel_edit_to_hotel_manage);
        }
    }

    private void clickListeners() {
        bt_hotel_edit_back.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Hotel", hotel);
            bundle.putString("Hotel Id",hotelID);
            Navigation.findNavController(getView()).navigate(R.id.action_hotel_edit_to_hotel_view, bundle);
        });

        bt_RegisterHotel.setOnClickListener(v -> {
            verifyData();
        });

        ll_Hotel_GPS_Address.setOnClickListener(v -> openMapDialog());

        hotel_features_listern();

        hotel_moods_listern();

        hotel_photos_listern();
    }

    public void hotel_features_listern(){
        if(hotelFeatures == null){
            hotelFeatures = new HotelFeature(getResources().getStringArray(R.array.Features));

            tv_Features_Selected.setText("Features");
        }

        String[] featuresKeys = new String[hotelFeatures.getHotelFeature()];
        boolean[] featuresValues = new boolean[hotelFeatures.getHotelFeature()];
        ArrayList<Integer> featuresSelected = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Boolean> entry : hotelFeatures.getFeatures().entrySet()){
            featuresKeys[index] = entry.getKey();
            featuresValues[index] = entry.getValue();
            if(entry.getValue()){
                featuresSelected.add(index);
            }
            index++;
        }

        String featurename = "";
        for (int i = 0; i < featuresSelected.size(); i++) {
            featurename = featurename + featuresKeys[featuresSelected.get(i)];
            if (i != featuresSelected.size() - 1) {
                featurename = featurename + ", ";
            }
        }
        tv_Features_Selected.setText(featurename);

        bt_Features.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
            mBuilder.setTitle("Selected features of hotel");
            mBuilder.setMultiChoiceItems(featuresKeys, featuresValues, (dialogInterface, position, isChecked) -> {
                if(isChecked){
                    featuresSelected.add(position);
                    hotelFeatures.setFeatures_Value(featuresKeys[position],true);
                }else{
                    hotelFeatures.setFeatures_Value(featuresKeys[position],false);
                    featuresSelected.remove(position);
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("Ok", (dialogInterface, which) -> {
                String item = "";
                for (int i = 0; i < featuresSelected.size(); i++) {
                    item = item + featuresKeys[featuresSelected.get(i)];
                    if (i != featuresSelected.size() - 1) {
                        item = item + ", ";
                    }
                }
                Log.e("Features", hotelFeatures.toString());
                tv_Features_Selected.setText(item);

            });

            mBuilder.setNegativeButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss());

            mBuilder.setNeutralButton("Clear all", (dialogInterface, which) -> {
                for (int i = 0; i < featuresValues.length; i++) {
                    featuresValues[i] = false;
                    featuresSelected.clear();
                }
                tv_Features_Selected.setText("");
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });
    }

    private void hotel_moods_listern() {
        ExtendedFloatingActionButton moods;
        if(hotelMoods == null){
            hotelMoods = new HotelMoods(getResources().getStringArray(R.array.Moods), getResources().getStringArray(R.array.MoodsIcons));
        }

        for (Map.Entry<String, Boolean> entry : hotelMoods.getMoods().entrySet()){
            moods = new ExtendedFloatingActionButton(getContext());

            moods.setId(entry.getKey().hashCode());
            moods.setText(entry.getKey());

            moods.setCheckable(true);
            if(entry.getValue()){
                moods.setChecked(true);
                moods.setBackgroundColor(Color.parseColor("#FF3F51B5"));
            }
            moods.setChecked(entry.getValue());

            moods.setMaxLines(3);
            moods.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            String drawable = hotelMoods.getMoods_Icon(entry.getKey());
            int id = getContext().getResources().getIdentifier(drawable, "drawable", getContext().getPackageName());
            moods.setIcon(ContextCompat.getDrawable(getContext(), id));
            moods.setIconTint(null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(30, 15, 30, 5);
            moods.setLayoutParams(params);

            ExtendedFloatingActionButton finalbutton = moods;
            finalbutton.setOnClickListener(v -> {
                if(finalbutton.isChecked()){
                    finalbutton.setChecked(true);
                    finalbutton.setBackgroundColor(Color.parseColor("#FF3F51B5"));
                    hotelMoods.setMoods_Value(entry.getKey(),true);
                }
                else{
                    finalbutton.setChecked(false);
                    finalbutton.setBackgroundColor(Color.parseColor("#15194A"));
                    hotelMoods.setMoods_Value(entry.getKey(),false);
                }
                Log.e("Moods", hotelMoods.toString());
            });

            ll_Hotel_Moods.addView(finalbutton);

        }
    }

    private void hotel_photos_listern() {
        ll_Hotel_Cover_Photo.setOnClickListener(v -> {
            openUploadDialog(PICK_COVER_IMAGE_REQUEST);
        });

        ll_Hotel_Photos.setOnClickListener(v -> {
            openUploadDialog(PICK_OTHERS_IMAGE_REQUEST);
        });
    }

    private void openUploadDialog(int requestCode) {
        slideModelList  =new ArrayList<>();

        if(othersphotos == null){
            othersphotos = new ArrayList<>();
        }

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_dialog_upload_photos);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(false);

        dialog.create();

        mainslider= dialog.findViewById(R.id.hotel_slider_upload);
        mainslider.bringToFront();

        tv_popupMenu = dialog.findViewById(R.id.tv_popupMenu);

        Button bt_chose = dialog.findViewById(R.id.bt_chose_photos);
        Button bt_save = dialog.findViewById(R.id.bt_save_photos);
        Button bt_reset = dialog.findViewById(R.id.bt_reset);

        ImageButton bt_close = dialog.findViewById(R.id.bt_close_upload_photo);

        switch (requestCode){
            case PICK_COVER_IMAGE_REQUEST:
                if(coverPhoto != null){
                    slideModelList.add(new SlideModel(String.valueOf(coverPhoto),"", ScaleTypes.FIT));
                }
                break;
            case PICK_OTHERS_IMAGE_REQUEST:
                if(!othersphotos.isEmpty()){
                    for(Uri photo : othersphotos){
                        slideModelList.add(new SlideModel(String.valueOf(photo),"", ScaleTypes.FIT));
                    }
                }
                break;
        }
        mainslider.setImageList(slideModelList, ScaleTypes.FIT);
        mainslider.stopSliding();
        sliderClick(requestCode);

        bt_chose.setOnClickListener(v -> openFileChooser(requestCode));

        bt_save.setOnClickListener(v -> {
            slideModelList.clear();
            dialog.dismiss();
        });

        bt_reset.setOnClickListener(v -> {
            switch (requestCode){
                case PICK_COVER_IMAGE_REQUEST:
                    StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(hotel.getCoverPhoto());
                    imageDeleteCover.delete();
                    coverPhoto = null;
                break;

                case PICK_OTHERS_IMAGE_REQUEST:
                    for(String url : hotel.getOtherPhotos()){
                        StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        imageDeleteOthers.delete();
                    }
                    othersphotos.clear();
                break;
            }
            slideModelList.clear();
            mainslider.setImageList(slideModelList, ScaleTypes.FIT);
            mainslider.stopSliding();
            sliderClick(requestCode);
        });

        bt_close.setOnClickListener(v -> {
            slideModelList.clear();
            mainslider.setImageList(slideModelList, ScaleTypes.FIT);
            mainslider.stopSliding();
            sliderClick(requestCode);
            dialog.cancel();
        });

        dialog.show();
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        switch (requestCode){
            case PICK_OTHERS_IMAGE_REQUEST:
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                break;
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri photoUri;

        if(resultCode == getActivity().RESULT_OK){
            if (requestCode == PICK_OTHERS_IMAGE_REQUEST) {
                if (data.getClipData() != null) {
                    //Multiple
                    int total_items = data.getClipData().getItemCount();
                    for(int i=0; i < total_items; i++){
                        photoUri = data.getClipData().getItemAt(i).getUri();
                        othersphotos.add(photoUri);
                        slideModelList.add(new SlideModel(String.valueOf(photoUri),"",ScaleTypes.FIT));
                    }
                    mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                    mainslider.stopSliding();
                }
                else if (data.getData() != null) {
                    //Single
                    photoUri = data.getData();
                    othersphotos.add(photoUri);
                    slideModelList.add(new SlideModel(String.valueOf(photoUri),"",ScaleTypes.FIT));
                    mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                    mainslider.stopSliding();
                }
                sliderClick(PICK_OTHERS_IMAGE_REQUEST);
            }

            if (requestCode == PICK_COVER_IMAGE_REQUEST) {
                //Single
                slideModelList.clear();
                coverPhoto = data.getData();
                slideModelList.add(new SlideModel(String.valueOf(coverPhoto),"",ScaleTypes.FIT));
                mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                mainslider.stopSliding();

                sliderClick(PICK_COVER_IMAGE_REQUEST);
            }

        }
    }

    private void sliderClick(int requestCode) {
        mainslider.setItemClickListener(e -> {
            Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, tv_popupMenu, Gravity.CENTER,0,0);

            //parm 2 is menu id, param 3 is position of this menu item in menu items list, param 4 is title of the menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "View Full");
            popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_view_full);
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Delete");
            popupMenu.getMenu().getItem(1).setIcon(R.drawable.ic_delete_data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true);
            }

            final float[] rotate = {0};

            popupMenu.setOnMenuItemClickListener(item -> {
                ImageView iv_photo_full = dialog.findViewById(R.id.iv_photo_full);

                switch (item.getItemId()){
                    case 0:
                        ConstraintLayout cl_upload_photo_home = dialog.findViewById(R.id.cl_upload_photo_home);
                        cl_upload_photo_home.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_PARENT; // LayoutParams: android.view.ViewGroup.LayoutParams
                        cl_upload_photo_home.requestLayout();//It is necesary to refresh the screen

                        switch (requestCode){
                            case PICK_COVER_IMAGE_REQUEST:
                                Glide.with(this)
                                        .load(coverPhoto)
                                        .centerInside()
                                        .into(iv_photo_full);
                                break;

                            case PICK_OTHERS_IMAGE_REQUEST:
                                Glide.with(this)
                                        .load(othersphotos.get(e))
                                        .centerInside()
                                        .into(iv_photo_full);
                                break;
                        }

                        ConstraintLayout cl_upload_photo = dialog.findViewById(R.id.cl_upload_photo);
                        cl_upload_photo.setVisibility(View.GONE);

                        ConstraintLayout cl_photo_full = dialog.findViewById(R.id.cl_photo_full);
                        cl_photo_full.setVisibility(View.VISIBLE);

                        ImageButton bt_close_full = dialog.findViewById(R.id.bt_close_full);
                        ImageButton bt_rotate_left = dialog.findViewById(R.id.bt_rotate_left);
                        ImageButton bt_rotate_right = dialog.findViewById(R.id.bt_rotate_right);

                        bt_close_full.setOnClickListener(v -> {
                            cl_upload_photo_home.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                            cl_photo_full.setVisibility(View.GONE);
                            cl_upload_photo.setVisibility(View.VISIBLE);
                        });

                        bt_rotate_right.setOnClickListener(v -> {
                            rotate[0] += 90;
                            if(rotate[0] >= 360){
                                rotate[0] = 0;
                            }

                            iv_photo_full.setRotation(rotate[0]);
                            switch (requestCode){
                                case PICK_COVER_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(coverPhoto)
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;

                                case PICK_OTHERS_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(othersphotos.get(e))
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;
                            }

                        });

                        bt_rotate_left.setOnClickListener(v -> {
                            rotate[0] -= 90;
                            if(rotate[0] <= -360){
                                rotate[0] = 0;
                            }

                            iv_photo_full.setRotation(rotate[0]);
                            switch (requestCode){
                                case PICK_COVER_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(coverPhoto)
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;

                                case PICK_OTHERS_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(othersphotos.get(e))
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;
                            }

                        });

                        bt_rotate_right.performClick();
                        bt_rotate_left.performClick();
                        break;

                    case 1:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("Delete");
                        dialog.setMessage("Delete this image");
                        dialog.setCancelable(true);

                        dialog.setPositiveButton("Yes", (dialog1, id) -> {
                            Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                            switch (requestCode){
                                case PICK_COVER_IMAGE_REQUEST:
                                    StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(coverPhoto));
                                    imageDeleteCover.delete();

                                    coverPhoto = null;
                                    slideModelList.clear();
                                break;

                                case PICK_OTHERS_IMAGE_REQUEST:
                                    StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(othersphotos.get(e)));
                                    imageDeleteOthers.delete();

                                    othersphotos.remove(e);
                                    slideModelList.remove(e);
                                break;
                            }

                            mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                        });

                        dialog.setNegativeButton("No", (dialog2, id) -> {
                            // User cancelled the dialog
                            dialog2.dismiss();
                        });

                        AlertDialog alert = dialog.create();
                        alert.show();
                        break;
                }

                return true;
            });

            popupMenu.show();
        });
    }

    private void verifyData(){

        boolean error = false;
        ////////////// PERSONAL DATA /////////////////
        String name = et_Name.getText().toString().trim();
        float starts = rb_Stars.getRating();
        String phone = "";
        ////////////// ROOMS DATA /////////////////
        String rooms = et_TotalRooms.getText().toString().trim();
        int total_rooms = 0;

        String price = et_Price.getText().toString().trim();
        float price_room = 0;
        ////////////// ADDRESS /////////////////
        String country = ccp_country.getSelectedCountryNameCode();
        String city = et_City.getText().toString().trim();
        String address_string = et_Address.getText().toString().trim();
        String zip_code = et_ZipCode.getText().toString().trim();

        ////////////// DESCRIPTION /////////////////
        String description = et_Description.getText().toString().trim();


        if(name.isEmpty()){
            et_Name.setError("Hotel name is required");
            et_Name.requestFocus();
            error = true;
        }
        else{
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        if(starts == 0){
            tv_Hotel_Stars.setError("Hotel stars is required");
            tv_Hotel_Stars.requestFocus();
            error = true;
        }


        if(ccp_PhoneCode.isValidFullNumber()){
            phone = ccp_PhoneCode.getFormattedFullNumber();
        }
        if(phone.isEmpty() || !ccp_PhoneCode.isValidFullNumber()){
            et_Phone.setError("Phone is required or is not valid");
            et_Phone.requestFocus();
            error = true;
        }


        if(!rooms.isEmpty()){
            total_rooms = Integer.parseInt(et_TotalRooms.getText().toString().trim());
            if(total_rooms <= 1){
                et_TotalRooms.setError("Total rooms is required and greater than 1");
                et_TotalRooms.requestFocus();
                error = true;
            }
        }
        else {
            et_TotalRooms.setError("Total rooms is required and greater than 1");
            et_TotalRooms.requestFocus();
            error = true;
        }


        if(!price.isEmpty()){
            price_room = Float.parseFloat(et_Price.getText().toString().trim());
            if(price_room <= 1){
                et_Price.setError("Price the rooms is required and greater than 1");
                et_Price.requestFocus();
                error = true;
            }
        }
        else {
            et_Price.setError("Price the rooms is required and greater than 1");
            et_Price.requestFocus();
            error = true;
        }

        if(city.isEmpty()){
            Toast.makeText(getContext(), "You did not enter a city", Toast.LENGTH_LONG).show();
            et_City.setError("City name is required");
            et_City.requestFocus();
            error = true;
        }

        if(address_string.isEmpty()){
            et_Address.setError("Address name is required");
            et_Address.requestFocus();
            error = true;
        }

        if(zip_code.isEmpty()){
            et_ZipCode.setError("Zip-Code is required");
            et_ZipCode.requestFocus();
            error = true;
        }

        if(tv_Features_Selected.getText().toString().trim().isEmpty()){
            tv_Features_Selected.setError("Select at least one feature");
            tv_Features_Selected.requestFocus();
            error = true;
        }

        if(description.isEmpty() || description.length() < 20){
            et_Description.setError("Description is required");
            et_Description.requestFocus();
            error = true;
        }

        if(!hotelMoods.getMoods_Verification()){
            tv_Hotel_Moods_Title.setError("Select at least one mood");
            tv_Hotel_Moods_Title.requestFocus();
            error = true;
        }

        if(coverPhoto == null){
            tv_title_cover_photo.setError("Select cover photo");
            tv_title_cover_photo.requestFocus();
            error = true;
        }

        if(othersphotos.isEmpty()){
            tv_title_others_photo.setError("Select others photos");
            tv_title_others_photo.requestFocus();
            error = true;
        }

        if(error){
            return;
        }

        Address address = new Address(country, city, address_string, zip_code,coordinates.latitude,coordinates.longitude);

        hotel.setName(name);
        hotel.setPhone(phone);
        hotel.setAddress(address);
        hotel.setStars(starts);
        hotel.setTotal_Rooms(total_rooms);
        hotel.setPrice(price_room);
        hotel.setDescription(description);
        hotel.setMoods(hotelMoods);
        hotel.setFeature(hotelFeatures);

        changeOnFirebase();

    }

    private void changeOnFirebase(){
        databaseReference.setValue(hotel).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                Toast.makeText(getContext(),"Hotel has ben registered successfully!", Toast.LENGTH_LONG).show();
                changePhotosOnFirebase();
            }
            else {
                Toast.makeText(getContext(),"Failed to register changes! Try again!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void changePhotosOnFirebase() {
        if (othersphotos != null && coverPhoto != null) {
            // Code for showing progressDialog while uploading
            boolean upadatel_cover = false;
            boolean upadatel_others = false;
            if (!String.valueOf(coverPhoto).toLowerCase().startsWith("https://")) {
                upadatel_cover = true;
            }
            for(Uri photo : othersphotos) {
                if (!String.valueOf(photo).toLowerCase().startsWith("https://")) {
                    upadatel_others = true;
                }
            }
            if(upadatel_cover && upadatel_others){
                uploadCoverPhoto(true);
            }
            if(upadatel_cover && !upadatel_others){
                uploadCoverPhoto(false);
            }
            if(!upadatel_cover && upadatel_others){
                uploadOthersPhotos();
            }
            if(!upadatel_cover && !upadatel_others){
                bt_hotel_edit_back.performClick();
            }


        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadCoverPhoto(boolean value){
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");

        if (!String.valueOf(coverPhoto).toLowerCase().startsWith("https://")) {
            progressDialog.show();

            if(!hotel.getCoverPhoto().isEmpty()){
                StorageReference imageDeleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(hotel.getCoverPhoto());
                imageDeleteRef.delete();
            }
            StorageReference fileReference = storageReference.child("Cover").child(GenerateUniqueIds.generateId() + "." + getFileExtension(coverPhoto));
            StorageTask<UploadTask.TaskSnapshot> mUploadCoverTask = fileReference.putFile(coverPhoto)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Success, Image uploaded
                            hotel.setCoverPhoto(uri.toString());
                            databaseReference.setValue(hotel);

                            progressDialog.dismiss();

                            if(value){
                                uploadOthersPhotos();
                            }
                            else{
                                bt_hotel_edit_back.performClick();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error", e.getMessage());
                        progressDialog.dismiss();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    private void uploadOthersPhotos(){
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");

        List<String> listothersphotos = new ArrayList<>();

        for(Uri photo : othersphotos){
            if (!String.valueOf(photo).toLowerCase().startsWith("https://")) {
                progressDialog.show();

                StorageReference fileOthers = storageReference.child("Others").child(GenerateUniqueIds.generateId() + "." + getFileExtension(photo));
                StorageTask<UploadTask.TaskSnapshot> mUploadOtherTask = fileOthers.putFile(photo)
                        .addOnSuccessListener(taskSnapshot -> {
                            fileOthers.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Success, Image uploaded
                                listothersphotos.add(uri.toString());

                                if(listothersphotos.size() == othersphotos.size()){
                                    hotel.setOtherPhotos(listothersphotos);
                                    databaseReference.setValue(hotel);
                                    progressDialog.dismiss();

                                    bt_hotel_edit_back.performClick();
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Error", e.getMessage());
                            //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        });
            }
            else{
                listothersphotos.add(photo.toString());
            }
        }
    }

    private void openMapDialog() {

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_dialog_maps);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(false);

        dialog.create();

        ImageButton bt_close = dialog.findViewById(R.id.bt_close_map_dialog);

        searchView = dialog.findViewById(R.id.sv_location);

        TextView textView = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.WHITE);

        ImageView searchClose = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null));
        searchClose.setColorFilter(Color.WHITE);

        ImageView searchMag = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null));
        searchMag.setColorFilter(Color.WHITE);

        mMapView = dialog.findViewById(R.id.google_map);
        MapsInitializer.initialize(getActivity());

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();

        mMapView.getMapAsync(googleMap -> {
            map = googleMap;

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 40);
                return;
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            if(coordinates != null){
                map.clear();
                map.addMarker(new MarkerOptions().position(coordinates).title("My hotel"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates,17));
            }

            map.setOnMapClickListener(latLng -> {
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title("My hotel"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

                List<android.location.Address> addressList;
                Address address = new Address();

                Geocoder geocoder = new Geocoder(getContext(),Locale.getDefault());
                try {
                    addressList =  geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
                    coordinates = latLng;

                    List<String> list_Address = new ArrayList<>();

                    if(addressList != null && !addressList.isEmpty()){
                        for(int i = 0; i < addressList.size(); i++){
                            String text = "";
                            if(addressList.get(i).getThoroughfare() != null){
                                text = addressList.get(i).getThoroughfare();
                                if(addressList.get(i).getLocality() != null){
                                    text += ", " + addressList.get(i).getLocality();
                                }
                                if(!list_Address.contains(text) && !text.isEmpty()){
                                    list_Address.add(text);
                                }
                            }
                        }

                        String countryCode = addressList.get(0).getCountryCode();

                        final String[] locality = {""};
                        final String[] zipCode = {addressList.get(0).getPostalCode()};
                        final String[] fullAddress = {""};

                        address.setCountry(countryCode);
                        address.setCity(addressList.get(0).getAdminArea());

                        if(list_Address.size() == 1 ){
                            fullAddress[0] = list_Address.get(list_Address.size()-1) + locality[0];
                            address.setAddress(fullAddress[0]);
                        }
                        else if(list_Address.size() > 1 ){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Select Address");

                            builder.setItems(list_Address.toArray(new String[0]), (address_dialog, which) -> {
                                (new Thread(() -> {
                                    try {
                                        String where = URLEncoder.encode("{" +
                                                "    \"postalCode\": {" +
                                                "        \"$regex\": \""+ zipCode[0] +"\"" +
                                                "    }" +
                                                "}", "utf-8");
                                        URL url = new URL("https://parseapi.back4app.com/classes/Worldzipcode_" + countryCode + "?limit=1000&excludeKeys=accuracy,adminCode1,adminCode2&where=" + where);
                                        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                                        urlConnection.setRequestProperty("X-Parse-Application-Id", "02O9dt2WcZk6zsN0oznH2fu27l2nRBt7PFnaowGm"); // This is your app's application id
                                        urlConnection.setRequestProperty("X-Parse-REST-API-Key", "QRFhbtw4gnptSnBk2upE5vIiSj2f9jmKGQJJcAyF"); // This is your app's REST API key
                                        try {
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                            StringBuilder stringBuilder = new StringBuilder();
                                            String line;
                                            while ((line = reader.readLine()) != null) {
                                                stringBuilder.append(line);
                                            }
                                            JSONObject data = new JSONObject(stringBuilder.toString()); // Here you have the data that you need
                                            JSONArray test = data.getJSONArray("results");

                                            for(int i=0; i < test.length(); i++){
                                                JSONObject testdata = test.getJSONObject(i);

                                                JSONObject coordinates = (JSONObject) testdata.get("geoPosition");

                                                float[] distance = new float[2];
                                                Location.distanceBetween( latLng.latitude, latLng.longitude, coordinates.getDouble("latitude"), coordinates.getDouble("longitude"), distance);

                                                if( distance[0] < 1000  ){
                                                    zipCode[0] = testdata.getString("postalCode");

                                                    locality[0] = " , " + testdata.getString("placeName") + " , " + testdata.getString("adminName3") + " , " + testdata.getString("adminName2");
                                                }
                                            }

                                        } finally {
                                            urlConnection.disconnect();
                                            fullAddress[0] = list_Address.get(which) + locality[0];
                                            address.setAddress(fullAddress[0]);
                                            address.setZipcode(zipCode[0]);
                                            address.setCoordinates(latLng.latitude, latLng.longitude);

                                            getActivity().runOnUiThread(() -> {
                                                et_City.setText(address.getCity());
                                                et_Address.setText(address.getAddress());
                                                et_ZipCode.setText(address.getZipcode());

                                                ccp_country.setDefaultCountryUsingNameCode(address.getCountry());
                                                ccp_country.resetToDefaultCountry();

                                                dialog.dismiss();
                                            });
                                        }
                                    } catch (Exception e) {
                                        Log.e("MainActivity", e.toString());
                                    }
                                })).start();
                            });
                            // create and show the alert dialog
                            AlertDialog address_dialog = builder.create();
                            address_dialog.show();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

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
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        bt_close.setOnClickListener(v -> {
            dialog.cancel();
        });

        dialog.show();
    }
}