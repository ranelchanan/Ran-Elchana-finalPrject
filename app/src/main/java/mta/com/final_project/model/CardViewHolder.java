package mta.com.final_project.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import mta.com.final_project.R;

/**
 * Created by rgerman on 9/26/2018.
 */

public class CardViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private TextView deleteTextView;


    public CardViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        deleteTextView = view.findViewById(R.id.deleteCardTextView_cardView);
    }

    public void setCardAnimalPhoto(String imageUrl){
        ImageView animalImageView = view.findViewById(R.id.animalImageView_cardView);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.no_animal_image)
                .into(animalImageView);

    }

    public void setCardUserPhoto(String imageUrl) {
        ImageView userImageView = view.findViewById(R.id.userImageView_cardView);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.no_user_image)
                .into(userImageView);
    }

    public void setCardUserName(String username) {
        TextView userNameTextView = view.findViewById(R.id.usernameTextView_cardView);
        userNameTextView.setText(username);
    }

    public void setCardTitle(String title){
        TextView titleTextView = view.findViewById(R.id.titleTextView_cardView);
        titleTextView.setText(title);
    }

    public void setCardDescription(String description){
        TextView userStatusTextView = view.findViewById(R.id.animalDescriptionTextView_cardView);
        userStatusTextView.setText(description);
    }

    public void setCardTimeAndDate(String timeAndDate) {
        TextView timeAndDateTextView = view.findViewById(R.id.timeAndDate_cardView);
        timeAndDateTextView.setText(timeAndDate);
    }

    public void setCardLocation(String location) {
        TextView locationTextView = view.findViewById(R.id.locationTextView_cardView);
        locationTextView.setText(location);
    }

    public void setDeleteButton() {
        deleteTextView.setVisibility(View.VISIBLE);
    }

    public TextView getDeleteTextView() {
        return deleteTextView;
    }
}