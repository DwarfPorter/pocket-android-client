package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gb.pocketmessenger.Adapters.ChatListAdapter;
import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Dialog;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.utils.ImgLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import java.util.ArrayList;
import java.util.List;

public class ChatList extends Fragment implements ChatListAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    ChatListAdapter adapter;
    String from;
    List<String> users = new ArrayList<>();
    private DatabaseReference databaseReference;
    final String TAG = "checkTAG";


    public static ChatList newInstance () {
        return new ChatList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            from = bundle.getString("from");
            Log.d(TAG, "chatlist " + from);
            if (users.contains(from)){
                users.remove(from);
            }

        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getUsersFromDB(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
    }

    private void getUsersFromDB(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (!users.contains(ds.getKey())){
                if (!ds.getKey().equals(from)){
                    users.add( ds.getKey());
                }
                else {
                    users.remove(from);
                }
            }
        }
    }


    private void initUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        users.removeAll(users);
        adapter = new ChatListAdapter(users);
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        initUi();
        addListener();
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
    private void loadChatMessagesFragment(String from, String to) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.putString("USERNAME", from);
        arguments.putString("TO", to);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setArguments(arguments);
        transaction.replace(R.id.login_container, chatMessages);
        transaction.commit();
    }

    private void addListener() {
        adapter.setOnItemClickListener((view, position) -> {
            loadChatMessagesFragment(from, users.get(position));
        });
    }
}
