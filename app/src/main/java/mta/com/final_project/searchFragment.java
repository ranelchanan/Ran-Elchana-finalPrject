package mta.com.final_project;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButtonGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mta.com.final_project.model.AnimalCardDetails;
import mta.com.final_project.model.SearchEngine;
import mta.com.final_project.model.SearchParams;

import static com.firebase.ui.auth.AuthUI.TAG;

public class searchFragment extends Fragment {

    private  int min;
    private  int hour;
    private int year;
    private int month;
    private int day;
    final Calendar calendar = Calendar.getInstance();
    private  String animalType;

    private LatLng selectPlace;

    private BootstrapButtonGroup foundOrLostselect;


    private View view;
    private BootstrapDropDown spin;
    private PlaceAutocompleteFragment autocompleteFragment;
    private BootstrapLabel SelectedTime;
    private BootstrapEditText raduios;
    private CheckBox checkBoxSave;
    private String CurrentUser;
    private boolean isLost =true ;
    DatabaseReference SearchEngineDB ;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
    public searchFragment()
    {

    }

    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SearchEngineDB = FirebaseDatabase.getInstance().getReference().child("SearchParams");
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
            view = inflater.inflate(R.layout.fragment_search, container, false);
            SelectedTime = view.findViewById(R.id.SelectedTime);
            raduios = view.findViewById(R.id.raduios);
            foundOrLostselect = view.findViewById(R.id.radioGroup_search);
        checkBoxSave = view.findViewById(R.id.saveSearchParams);


            spin = (BootstrapDropDown) view.findViewById(R.id.animalType);
            spin.setOnDropDownItemClickListener(new BootstrapDropDown.OnDropDownItemClickListener() {
                @Override
                public void onItemClick(ViewGroup parent, View v, int id) {
                    //   category=spin.getResources().getResourceName(spin.getId());
                    String[] dropDownItems = spin.getDropdownData();
                    String item = dropDownItems[id];
                    spin.setText(item);
                    animalType = item;
                }
            });

            final BootstrapButton buttonFound = view.findViewById(R.id.LostRadioButton);
            buttonFound.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {

                public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                    isLost = isChecked;
                }
            });

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final BootstrapButton button = view.findViewById(R.id.showTime);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showTime();
                }
            });
            final BootstrapButton buttondata = view.findViewById(R.id.showdate);
            buttondata.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDate();
                }
            });

            final BootstrapButton submit = view.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    submit();
                }
            });
             autocompleteFragment = (PlaceAutocompleteFragment)
                    getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName());
                    selectPlace =new LatLng(place.getLatLng());
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.

                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        setSerachEngine();
    return view;
    }


    public void setSerachEngine() {

        ValueEventListener userId = SearchEngineDB.orderByChild("UserId").equalTo(CurrentUser).limitToFirst(1)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot DS : dataSnapshot.getChildren()) {

                                    SearchEngine se = DS.getValue(SearchEngine.class);
                                    SearchParams searchParams = se.getSerachParams();
                                    if (searchParams.getFromDate() != null) {
                                        calendar.setTime(searchParams.getFromDate());
                                        SelectedTime.setText(sdf.format(calendar.getTime()));
                                    }
                                    if (searchParams.getAnimalType() != null) {
                                        animalType = searchParams.getAnimalType();
                                        spin.setText(animalType);
                                    }
                                    if (searchParams.getSelectPlace() != null) {
                                        selectPlace = searchParams.getSelectPlace();

                                        autocompleteFragment.setText(getAddress(selectPlace.getLatitude(), selectPlace.getLongitude()));
                                    }
                                    if (searchParams.getRadius() != null) {
                                        raduios.setText(searchParams.getRadius().toString());
                                    }
                                    checkBoxSave.setText("Keep  me infrom on this search");
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
    }



    private String getAddress(double lat, double lon) {
        String address = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public void  showDate()

    {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this,123);
       newFragment.show(this.getFragmentManager(), "datePicker");

    }

    public void  showTime()

    {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(this,123);
        newFragment.show(this.getFragmentManager(), "Time Picker");

    }


    public void getTimeResults(int _hour, int _mins)
    {
        min = _mins;
        hour =_hour;
        setDateTime();
    }

    public void getDataResult (int _year, int _month , int _day)
    {
        year = _year;
        month =_month;
        day= _day;
        setDateTime();
    }



    private  void setDateTime()
    {


        calendar.set(year, month, day, hour, min);
        SelectedTime.setText( sdf.format(calendar.getTime()));
       // selectDate =
    }



    public void submit() {
        SearchParams res = new SearchParams();
        if (year>0) {
            res.setFromDate(calendar.getTime());
        }
        if (selectPlace!=null)
        {
            if (raduios.getText().toString().isEmpty()) {
                raduios.setError("please enter a title");
                raduios.requestFocus();
                return;
            }
            res.setSelectPlace(selectPlace);
            res.setRadius(Integer.parseInt(raduios.getText().toString()));
        }

        if(animalType!=null)
        {
            res.setAnimalType(animalType);
        }

        if (checkBoxSave.isChecked())
        {
            SearchEngine se = new SearchEngine();
            res.setLost(isLost);
            se.setSerachParams(res);
            se.setUserId(CurrentUser);

            SearchEngineDB = FirebaseDatabase.getInstance().getReference().child("SearchParams");
            String itemId = SearchEngineDB.push().getKey();
            SearchEngineDB.child(itemId).setValue(se);

        }

        FoundAndLostTabsContainer f=  (FoundAndLostTabsContainer)getParentFragment();
        f.setSerachParams(res,isLost);



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(autocompleteFragment!=null && autocompleteFragment.getFragmentManager()!=null) {
            autocompleteFragment.getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        }

    }


}

