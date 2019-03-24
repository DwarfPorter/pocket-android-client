package com.gb.pocketmessenger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Message;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatMessages extends MvpAppCompatFragment {

    private static final int TOTAL_MESSAGES_COUNT = 50;
    String from;
    String to;
    protected ImageLoader imageLoader;
    private TextView messages;
    private MessagesListAdapter<Message> messageAdapter;
    Toolbar toolbar;
    DatabaseReference root;
    DatabaseReference rootReverse;
    private String temp_key;
    EditText input_et;
    Button btn_send_msg;
    String chatName;
    final String TAG = "checkTAG";


    public static ChatMessages newInstance(String dialogId) {
        return new ChatMessages();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            from = bundle.getString("USERNAME");
            to = bundle.getString("TO");
        }
        root = FirebaseDatabase.getInstance().getReference().child(from + "_" + to);
        rootReverse = FirebaseDatabase.getInstance().getReference().child(to + "_" + from);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        imageLoader = (imageView, url) -> Picasso.get().load(url).into(imageView);
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        messages = view.findViewById(R.id.messagesList);
        btn_send_msg = view.findViewById(R.id.send_msg);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(to);

//        initAdapter();
        input_et = view.findViewById(R.id.input_et);
        input_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager =
                        (InputMethodManager) getContext().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",from);
                map2.put("msg",input_et.getText().toString());

                message_root.updateChildren(map2);
                input_et.setText("");
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatConversation(dataSnapshot);
                Log.d(TAG, "appendChatConversation");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatConversation(dataSnapshot);
                Log.d(TAG, "appendChatConversation");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        rootReverse.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                appendChatConversation(dataSnapshot);
                Log.d(TAG, "appendChatConversation");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                appendChatConversation(dataSnapshot);
                Log.d(TAG, "appendChatConversation");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private String chat_msg,chat_user_name;

    private void appendChatConversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            messages.append(chat_user_name +" : "+chat_msg +" \n");
            Log.d(TAG, chat_user_name +" : "+chat_msg +" \n");
        }


    }
}
