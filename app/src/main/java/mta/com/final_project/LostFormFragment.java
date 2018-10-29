package mta.com.final_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapButtonGroup;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import mta.com.final_project.model.AnimalCardDetails;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.TAG;


public class LostFormFragment extends Fragment {
    private View view;
    private BootstrapDropDown animalTypeSpinner;
    private BootstrapDropDown animalSizeSpinner;
    private CheckBox isDangerousSpinner;
    private CheckBox IHaveIt;
    private TextView titleTextView;
    private ImageButton cameraIconImageButton;
    private ImageView addPhotoImageView;
    private ProgressBar progressBar;
    private BootstrapButton submitButton;
    private EditText descriptionEditText;
    private DatabaseReference animalDetailsDB;
    private StorageReference mStorageRef;
    private Uri photoUri;
    private DatabaseReference userDB;
    private BootstrapButton currentLocationRadioButton;
    private BootstrapButton enterLocationRadioButton;
    private EditText enterLocationEditText;
    private BootstrapButtonGroup radioGroup;
    //    private Geocoder geocoder;
    private String animalTypeStr;
    private Boolean isDangerousStr;
    private String animalSizeStr;
    private String descriptionStr;
    private String titleStr;
    private String animalPhotoUrlStr;
    private String userIdStr;
    private String timeAndDateStr;
    private String locationStr;
    private String animalNameStr;
    private String animalChipNumberStr;
    private EditText animalNameEditText;
    private EditText animalChipNumberEditText;
    private Place selectPlace;
    private PlaceDetectionClient mPlaceDetectionClient;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Location currentLocation;
    private  String currentAdders;
    private  boolean isCurrentLocation;
    private boolean isLost=true;
    public LostFormFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lost_form, container, false);
        isCurrentLocation =true;
        autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                selectPlace = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        initViews();
        onChooseLocation();
        onClickSubmitHandler();
        onClickAddPhotoHandler();
        onClickCameraIconHandler();

//        onChooseItemFoundOrLostSpinnerHandler();
        return view;
    }


    private void initViews() {

        titleTextView = view.findViewById(R.id.titleEditText_lostFormFragment);
        currentLocationRadioButton = view.findViewById(R.id.currentLocationRadioButton_lostFormFragment);
        enterLocationRadioButton = view.findViewById(R.id.enterLocationRadioButton_lostFormFragment);

        radioGroup = view.findViewById(R.id.radioGroup_lostFormFragment);
        progressBar = view.findViewById(R.id.progressBar_lostFormFragment);
        submitButton = view.findViewById(R.id.submitButton_lostFormFragment);
        descriptionEditText = view.findViewById(R.id.descriptionEditText_lostFormFragment);
        cameraIconImageButton = view.findViewById(R.id.cameraIcon_lostFormFragment);
        addPhotoImageView = view.findViewById(R.id.addPhotoImageView_lostFormFragment);
        animalDetailsDB = FirebaseDatabase.getInstance().getReference().child("animalDetails");
        userIdStr = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        animalNameEditText = view.findViewById(R.id.animalNameEditText_lostFormFragment);
        animalChipNumberEditText = view.findViewById(R.id.chipNumberEditText_lostFormFragment);
        IHaveIt = view.findViewById(R.id.IHaveItCheckBox);
        getCurrentLocation();

        final BootstrapButton buttonFound = view.findViewById(R.id.LostRadioButton_publish);
        buttonFound.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {

            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                isLost = isChecked;
                int visablity = isLost ? View.VISIBLE : View.GONE;

                animalNameEditText.setVisibility(visablity);
                animalChipNumberEditText.setVisibility(visablity);
                isDangerousSpinner.setVisibility(visablity);

                int visablityFound = isLost ? View.GONE : View.VISIBLE;
                IHaveIt.setVisibility(visablityFound);

            }
        });
        initAnimalTypeSpinner();
        initAnimalSizeSpinner();
        initIsDangerousSpinner();

    }

    private void openCameraAndGallery() {
        progressBar.setVisibility(View.VISIBLE);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 3)
                .start(getContext(), LostFormFragment.this);
    }

    private void onClickAddPhotoHandler() {
        addPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraAndGallery();
            }
        });
    }

    private void onClickCameraIconHandler() {
        cameraIconImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraAndGallery();
            }
        });
    }


    private void initAnimalTypeSpinner() {
        animalTypeSpinner = view.findViewById(R.id.animalType_Post);
        animalTypeSpinner.setOnDropDownItemClickListener(new BootstrapDropDown.OnDropDownItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View v, int id) {
                //   category=spin.getResources().getResourceName(spin.getId());
                String[] dropDownItems = animalTypeSpinner.getDropdownData();
                String item = dropDownItems[id];
                animalTypeSpinner.setText(item);
                animalTypeStr = item;
            }
        });
    }

    private void initAnimalSizeSpinner() {

        animalSizeSpinner = view.findViewById(R.id.animalSize_Post);
        animalSizeSpinner.setOnDropDownItemClickListener(new BootstrapDropDown.OnDropDownItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View v, int id) {
                //   category=spin.getResources().getResourceName(spin.getId());
                String[] dropDownItems = animalSizeSpinner.getDropdownData();
                String item = dropDownItems[id];
                animalSizeSpinner.setText(item);
                animalSizeStr = item;
            }
        });
    ;
    }

    private void initIsDangerousSpinner() {
        isDangerousSpinner = view.findViewById(R.id.Dangerous);

    }

    private void onChooseLocation() {
        autocompleteFragment.getView().setVisibility(View.INVISIBLE);
        isCurrentLocation =true;
        currentLocationRadioButton.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {

            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                if (isChecked) {
                 isCurrentLocation =true;

                    autocompleteFragment.getView().setVisibility(View.INVISIBLE);


                }
            }
        });

        enterLocationRadioButton.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {

            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                if (isChecked) {
                    isCurrentLocation =false;

                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    autocompleteFragment.setHint("Must Enter A Loction");
                }
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    currentLocation = location;
                    try {
                        currentAdders = getAddress(location.getLatitude(), location.getLongitude());

                        Log.i("City location", currentAdders);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Not found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


        private String getCurrentLocation() {
            String address = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            } else {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try {
                    isCurrentLocation=true;
                    currentAdders = getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());
                } catch (Exception e) {

                    currentLocationRadioButton.setVisibility(View.GONE);
                    enterLocationRadioButton.setChecked(true);
                    autocompleteFragment.setUserVisibleHint(true);
                    isCurrentLocation =false;

                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Not found!", Toast.LENGTH_SHORT).show();

                }
            }

            return address;
//

//
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

    private void onClickSubmitHandler() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFormValues();

                if(!isCurrentLocation && selectPlace==null)
                {
                    autocompleteFragment.setHint("Must Enter A Loction");
                    return ;
                }

                if (titleStr.isEmpty()) {
                    titleTextView.setError("please enter a title");
                    titleTextView.requestFocus();
                } else {
                    uploadToDatabase();
                    clearFormFields();
                }
            }
        });
    }

    private void clearFormFields() {
        animalTypeSpinner.setText("animal type");
        animalSizeSpinner.setText("animal Size");
        animalTypeStr="";
        animalSizeStr="";
        isDangerousSpinner.setChecked(false);
        animalSizeSpinner.setText("");
        addPhotoImageView.setImageResource(R.drawable.no_animal_image);
        titleTextView.setText("");
        descriptionEditText.setText("");


        animalNameEditText.setText("");
        animalChipNumberEditText.setText("");
    }

    private void getFormValues() {
        descriptionStr = descriptionEditText.getText().toString().trim();


        isDangerousStr = isDangerousSpinner.isChecked();
        titleStr = titleTextView.getText().toString().trim();
        animalNameStr = animalNameEditText.getText().toString().trim();
        animalChipNumberStr = animalChipNumberEditText.getText().toString().trim();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        timeAndDateStr = df.format(Calendar.getInstance().getTime());


    }


    private void uploadToDatabase() {
        AnimalCardDetails animalCardDetails = new AnimalCardDetails();
        animalCardDetails.setTitle(titleStr);
        String FoundOrLost = isLost? getString(R.string.lost) : getString(R.string.found);
        animalCardDetails.setFoundOrLost(FoundOrLost);
        animalCardDetails.setAnimalSize(animalSizeStr);
        animalCardDetails.setAnimalType(animalTypeStr);
        if(isLost) {
            animalCardDetails.setIsDangerous(isDangerousStr);
            animalCardDetails.setAnimalName(animalNameStr);
            animalCardDetails.setChipNumber(animalChipNumberStr);
        }
        else
        {
            animalCardDetails.setiHaveIt(IHaveIt.isChecked());
        }
        animalCardDetails.setDescription(descriptionStr);
        animalCardDetails.setUserId(userIdStr);
        animalCardDetails.setAnimalPhotoUrl(animalPhotoUrlStr);
        animalCardDetails.setTimeAndDate(timeAndDateStr);
        animalCardDetails.setLocation(locationStr);


        animalCardDetails.setDate(Calendar.getInstance().getTime().getTime());
        if (isCurrentLocation) {

            animalCardDetails.setLoc(new mta.com.final_project.LatLng(currentLocation.getLatitude() , currentLocation.getLongitude()));
            animalCardDetails.setLocation(currentAdders);
        }
        else {
            animalCardDetails.setLoc(new mta.com.final_project.LatLng(selectPlace.getLatLng()));
            animalCardDetails.setLocation(selectPlace.getAddress().toString());
        }
        String DbName =  getString(R.string.found_animals_db);
        if (isLost)
        {
            DbName =  getString(R.string.lost_animals_db);
        }
        String lostAnimalItemId = animalDetailsDB.child(DbName).push().getKey();
        animalCardDetails.setItemId(lostAnimalItemId);

        animalDetailsDB.child(DbName).child(lostAnimalItemId).setValue(animalCardDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Animal's details updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.GONE);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoUri = result.getUri();
                try {
                    Picasso.get()
                            .load(photoUri)
                            .placeholder(R.drawable.no_user_image)
                            .into(addPhotoImageView);
                } catch (Exception e){
                    e.printStackTrace();
                }
                updateUserPhoto(photoUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateUserPhoto(Uri newPhotoUri){
        StorageReference userImageRef = mStorageRef.child("animalImages").child(userIdStr).child(newPhotoUri.getLastPathSegment() + "." + getFileExtension(newPhotoUri));
        userImageRef.putFile(newPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        animalPhotoUrlStr = uri.toString();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Uploaded failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    private String getFileExtension(Uri uri) { //This method gets the extension of the image (like JPG, PNG, ...)
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (autocompleteFragment!=null &&
                autocompleteFragment.getFragmentManager()!=null) {
            autocompleteFragment.getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();

        }

    }
}
