package mta.com.final_project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import mta.com.final_project.model.AnimalCardDetails;
import mta.com.final_project.model.Message;

public class ShowMessagesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MessageListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerOptions<Message> messageFirebaseRecyclerOptions;
    private DatabaseReference animalListDetailsDB;
    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_show_message, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.show_message);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference MessageList =
                FirebaseDatabase.getInstance().getReference().child("Message").child(currentUserId);
        messageFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(MessageList, Message.class)
                .setLifecycleOwner(this)
                .build();
        mAdapter =new MessageListAdapter(messageFirebaseRecyclerOptions){
            @Override
            protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {

                holder.bind(model);
               // onClickDeleteItemHandler(holder, model);

                onClickCardItemHandler(holder, model);  //this method is being invoked after clicking one of the cards
            }



        };;


        mRecyclerView.setAdapter(mAdapter);
        return  view;
    }

    private void onClickCardItemHandler (MessageListAdapter.MessageViewHolder holder,final  Message model)
    {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessage(model);
            }
        });
    }

    public void openMessage(Message Model) {

        SendMessageFragment newFragment = SendMessageFragment.newInstance(Model,false);
        newFragment.setTargetFragment(this, 123);
        newFragment.show(this.getFragmentManager(), "Show Message");
    }

}
