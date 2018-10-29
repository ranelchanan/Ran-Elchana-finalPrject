package mta.com.final_project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import mta.com.final_project.model.AnimalCardDetails;
import mta.com.final_project.model.CardViewHolder;
import mta.com.final_project.model.Message;
import mta.com.final_project.model.SearchParams;


public class AnimalsListFragment extends Fragment {

    private final String FOUND = "found";
    private final String LOST = "lost";
    private SearchParams searchParams;

    private String typeOfListDB;
    private View view;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<AnimalCardDetails> animalDetailsOptions;
    private DatabaseReference animalListDetailsDB;
    private DatabaseReference userDB;
    private ProgressBar progressBar;
    private String currentUserId;
    private String description;
    private String username;
    private String userPhotoUrl;

    private AwesomeTextView noResults;
   private AnimalListAdapter firebaseRecyclerAdapter;
    private List<AnimalCardDetails> AnimalList = new ArrayList<AnimalCardDetails>();
    private List<AnimalCardDetails> FilterList = new ArrayList<AnimalCardDetails>();
    public AnimalsListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    AnimalsListFragment(String foundOrLost) {
        if (Objects.equals(foundOrLost, LOST)) {
            typeOfListDB = "lostAnimals";
        } else { // (Objects.equals(foundOrLost, FOUND)) {
            typeOfListDB = "foundAnimals";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_animals_list, container, false);
        initViews();
        return view;
    }

    private void initViews() {
        progressBar = view.findViewById(R.id.progressBar_lostAnimalsListFragment);
        recyclerView = view.findViewById(R.id.animalRecyclerView_lostAnimalsListFragment);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        noResults = view.findViewById(R.id.noResultFonud_Lost);
        animalListDetailsDB = FirebaseDatabase.getInstance().getReference().child("animalDetails").child(typeOfListDB);
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        AnimalListAdapter mAdapter = new AnimalListAdapter(FilterList)
        {
            @Override
            public void onBindViewHolder(@NonNull final AnimalViewHolder holder, int position) {
               AnimalCardDetails model = FilterList.get(position);

                holder.bind(model);
                onClickDeleteItemHandler(holder, model);

                onClickCardItemHandler(holder, model);  //this method is being invoked after clicking one of the cards
            }



        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        GetAnimalData();
    }


    private void getUserDetailsFromFirebase(final String userId, final CardViewHolder holder) {
        animalListDetailsDB.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                userDB.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.child("username").getValue(String.class);
                        userPhotoUrl = dataSnapshot.child("photoUrl").getValue(String.class);
                        holder.setCardUserName(username);
                        holder.setCardUserPhoto(userPhotoUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void onClickDeleteItemHandler(final AnimalListAdapter.AnimalViewHolder holder, final AnimalCardDetails model) {
        animalListDetailsDB.child(model.getItemId()).addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        holder.getDeleteTextView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Delete Post");
                                builder.setMessage("Are you sure you want to delete this post?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSnapshot.getRef().removeValue();
                                        FilterList.remove(holder.getAdapterPosition());
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });

                        holder.getSendMessage().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendMessageOpen(model);
                            }

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void sendMessageOpen(AnimalCardDetails item) {
        mta.com.final_project.model.Message message = new Message();
        message.setFromUser(currentUserId);
        message.setToUser(item.getUserId());
        message.setItemKey(item.getItemId());
        message.setSendDate(Calendar.getInstance().getTime());
        message.setItemType(typeOfListDB);
        SendMessageFragment newFragment = SendMessageFragment.newInstance(message,true);
        newFragment.setTargetFragment(this, 123);
        newFragment.show(this.getFragmentManager(), "Show Message");
    }

    private void onClickCardItemHandler(AnimalListAdapter.AnimalViewHolder holder, final AnimalCardDetails model) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardViewItemIntent = new Intent(getContext(), CardViewItemActivity.class);
                cardViewItemIntent.putExtra("userId", model.getUserId());
                cardViewItemIntent.putExtra("title", model.getTitle());
                cardViewItemIntent.putExtra("description", description);
                cardViewItemIntent.putExtra("animalPhotoUrl", model.getAnimalPhotoUrl());
                cardViewItemIntent.putExtra("timeAndDate", model.getTimeAndDate());
                cardViewItemIntent.putExtra("location", model.getLocation());
//                cardViewItemIntent.putExtra("username", username);
//                cardViewItemIntent.putExtra("userPhotoUrl", userPhotoUrl);
                startActivity(cardViewItemIntent);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();

    }


    private void GetAnimalData()
    {

        animalListDetailsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FilterList.clear();
                for (DataSnapshot DS : dataSnapshot.getChildren()) {

                    AnimalCardDetails animal = DS.getValue(AnimalCardDetails.class);

                    AnimalList.add(animal);
                    System.out.println(animal);
                }
                if(searchParams==null) {
                    FilterList.addAll(AnimalList);
                }
                else FilterAndSort();

                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void FilterAndSort() {
        Location searchCenter = new Location("");
        AnimalCardDetails animal = new AnimalCardDetails();
        FilterList.clear();
        if (searchParams.getRadius()!=null && searchParams.getRadius() > 0) {

            searchCenter.setLatitude(searchParams.getSelectPlace().getLatitude());
            searchCenter.setLongitude(searchParams.getSelectPlace().getLongitude());
        }
        for (AnimalCardDetails model : AnimalList) {
            Boolean ToAdd= true;


            if (searchParams.getFromDate() != null &&
                    model.getDate() < searchParams.getFromDate().getTime()) {
                ToAdd = false;
            }

            if (searchParams.getAnimalType() != null
                    && model.getAnimalType() != null
                    && (!model.getAnimalType().equals(searchParams.getAnimalType()))) {
                ToAdd = false;
            }

            if (searchParams.getRadius() != null
                    && model.loc !=null
                    && searchParams.getRadius() > 0) {

                Location animalLoc = new Location("");
                animalLoc.setLatitude(model.loc.getLatitude());
                animalLoc.setLongitude(model.loc.getLongitude());
                float distance = searchCenter.distanceTo(animalLoc) / 1000;
                if (distance > searchParams.getRadius()) {
                    ToAdd = false;
                }
            }
            if (ToAdd)
            {
                FilterList.add(model);
            }
        }

        if( FilterList.size()==0)
        {
            recyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
        else
        {
            Collections.sort(FilterList, animal.new dateComapre ());
            recyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
            recyclerView.getAdapter().notifyDataSetChanged();
        }

    }

    public void setSearchParams(SearchParams searchParams) {
        this.searchParams = searchParams;
    }
}


