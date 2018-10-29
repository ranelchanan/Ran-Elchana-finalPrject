package mta.com.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.common.ChangeEventType;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import mta.com.final_project.FirebaseRecyclerAdapter;
import mta.com.final_project.model.AnimalCardDetails;
import mta.com.final_project.databinding.AnimalCardViewBinding;
import mta.com.final_project.model.CardViewHolder;
import mta.com.final_project.model.SearchParams;

public class AnimalListAdapter extends RecyclerView.Adapter<AnimalListAdapter.AnimalViewHolder> {
    private List<AnimalCardDetails> AnimalList;





    public AnimalListAdapter(List<AnimalCardDetails> animalList) {
        this.AnimalList = animalList;


    }

    @Override
    public AnimalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_card_view, parent, false);
        return new AnimalViewHolder(view);


    }

    @Override
    public void onBindViewHolder(AnimalViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return AnimalList.size();
    }

    public class AnimalViewHolder extends RecyclerView.ViewHolder {

        AnimalCardViewBinding mBinding;
        private DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("users");
        String currentUserId ;
        AnimalViewHolder(View view) {
            super(view);

             mBinding = DataBindingUtil.bind(view);
             currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }



        public void bind(AnimalCardDetails item) {

           if (item.getAnimalType()!=null && item.getAnimalType().equals("Nothing Found"))
           {
               mBinding.titleTextViewCardView.setText(item.getTitle());
               return;
           }
            Picasso.get()
                    .load(item.getAnimalPhotoUrl())
                    .placeholder(R.drawable.no_animal_image)
                    .into(mBinding.animalImageViewCardView);


            mBinding.animalDescriptionTextViewCardView.setText(item.getAnimalType());
            mBinding.locationTextViewCardView.setText(item.getLocation());
            mBinding.timeAndDateCardView.setText(item.getTimeAndDate());
            mBinding.titleTextViewCardView.setText(item.getTitle());
            mBinding.animalDescriptionTextViewCardView.setText(item.getDescription());
            mBinding.usernameTextViewCardView.setText(item.getUserName());
            if (Objects.equals(currentUserId, item.getUserId())) {
                 setDeleteButton();
                mBinding.SendMessageCardTextViewCardView.setVisibility(View.GONE);
            }

            userDB.child(item.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String usernameStr = dataSnapshot.child("username").getValue(String.class);
                    String userPhotoUrlStr = dataSnapshot.child("photoUrl").getValue(String.class);

                    if (userPhotoUrlStr != null && userPhotoUrlStr!="") {
                        Picasso.get()
                                .load(userPhotoUrlStr)
                                .placeholder(R.drawable.no_user_image)
                                .into(mBinding.userImageViewCardView);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


            public void setDeleteButton() {
                mBinding.deleteCardTextViewCardView.setVisibility(View.VISIBLE);
            }

            public TextView getDeleteTextView(){
                return  mBinding.deleteCardTextViewCardView;
            }

            public TextView getSendMessage()
            {
                return  mBinding.SendMessageCardTextViewCardView;
            }


        }

}
