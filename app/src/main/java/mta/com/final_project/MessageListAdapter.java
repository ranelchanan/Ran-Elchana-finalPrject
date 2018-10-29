package mta.com.final_project;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import mta.com.final_project.databinding.MsgItemBinding;
import mta.com.final_project.model.Message;

public class MessageListAdapter extends FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder> {
    private RecycleItemClick recycleItemClick;
    private static final String TAG = "MessageListAdapter";


    public MessageListAdapter(FirebaseRecyclerOptions<Message> options) {
        super(options, true);
    }


    public interface RecycleItemClick {
        void onItemClick(String userId, Message message, int position);
    }

    public void setRecycleItemClick(RecycleItemClick recycleItemClick) {
        this.recycleItemClick = recycleItemClick;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_item, parent, false);
        return new MessageViewHolder(view);
    }


    @Override
    protected void onChildUpdate(Message model,
                                 ChangeEventType type,
                                 DataSnapshot snapshot,
                                 int newIndex,
                                 int oldIndex) {

        super.onChildUpdate(model, type, snapshot, newIndex, oldIndex);
    }

    @Override
    protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {
        holder.bind(model);
    }


    @Override
    protected boolean filterCondition(Message model, String filterPattern) {

        return model.getToUser().equals(filterPattern);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        MsgItemBinding mBinding;
        private DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("users");
        String currentUserId;

        MessageViewHolder(View view) {
            super(view);

            mBinding = DataBindingUtil.bind(view);
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


        public void bind(Message item) {

            mBinding.setItem(item);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
            mBinding.messageDate.setText(sdf.format(item.getSendDate()));
            userDB.child(item.getFromUser()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String usernameStr = dataSnapshot.child("username").getValue(String.class);
                    String userPhotoUrlStr = dataSnapshot.child("photoUrl").getValue(String.class);


                    Picasso.get()
                            .load(userPhotoUrlStr)
                            .placeholder(R.drawable.no_user_image)
                            .into(mBinding.MessageUserImage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


        }

    }
}
