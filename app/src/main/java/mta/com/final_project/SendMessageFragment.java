package mta.com.final_project;


import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mta.com.final_project.model.Message;

public class SendMessageFragment extends DialogFragment {
    private BootstrapEditText mEditText;
    private BootstrapEditText TitleMessage;
    private BootstrapEditText prevText;
    private BootstrapButton send;
    private BootstrapButton replay;
    private Message data;
    private ImageButton closeWithOutSaving;
    private DatabaseReference MessageDB;

    private static boolean isSend;
    public SendMessageFragment() {
        MessageDB = FirebaseDatabase.getInstance().getReference().child("Message");
        // Empty constructor is required for DialogFragment

        // Make sure not to add arguments to the constructor

        // Use `newInstance` instead as shown below

    }



    public static SendMessageFragment newInstance(Message data,Boolean _isSend) {

        SendMessageFragment frag = new SendMessageFragment();

        frag.data=data;
        isSend =_isSend;
        return frag;

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_send_message, container);

    }

    public void closeDialog()
    {
        this.dismiss();
    }



    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        TitleMessage=(BootstrapEditText) view.findViewById(R.id.TitleMessage);
        mEditText = (BootstrapEditText) view.findViewById(R.id.textMessage);
        prevText = (BootstrapEditText) view.findViewById(R.id.PrevText);
        send = (BootstrapButton) view.findViewById(R.id.sendMessage);
        replay = (BootstrapButton) view.findViewById(R.id.replay);
        initData(!isSend);

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = new Message(data);
                isSend = true;
                initData(true);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDB =MessageDB.child(data.getToUser());
                data.setText(mEditText.getText().toString());
                data.setTitle(TitleMessage.getText().toString());
                String itemId = MessageDB.push().getKey();
                data.setMessageId(itemId);
                MessageDB.child(itemId).setValue(data).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getActivity(), "Message Send",
                                Toast.LENGTH_SHORT).show();
                        closeDialog();

                    }
                });
            }
        });
    }

    public void initData(Boolean inculdeData)
    {

            TitleMessage.setEnabled(isSend);
            mEditText.setEnabled(isSend);
            if(inculdeData)
            {
                TitleMessage.setText(data.getTitle());
                mEditText.setText(data.getText());
                prevText.setText(data.getPrevText());
            }
            if(data.getPrevText()!=null)
            {
                prevText.setVisibility(View.VISIBLE);
            }
        if(!isSend)
        {
            send.setVisibility(View.GONE);
            replay.setVisibility(View.VISIBLE);

        }
        if(isSend)
        {
            send.setVisibility(View.VISIBLE);
            replay.setVisibility(View.GONE);
        }
    }

}


