package com.gb.pocketmessenger.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gb.pocketmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoginFragment extends Fragment {
    EditText loginFromUI, password;
    TextView exception;
    private FirebaseAuth mAuth;
    final String TAG = "loginTAG";
    private Map<String,String> users = new HashMap<>();
    DatabaseReference databaseReference;
    String login;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginFromUI = view.findViewById(R.id.login_et);
        password = view.findViewById(R.id.password_et);
        exception = view.findViewById(R.id.exception);
        view.findViewById(R.id.button_login).setOnClickListener(v -> checkLogin(loginFromUI.getText().toString(), password.getText().toString()));
        view.findViewById(R.id.button_register).setOnClickListener(v -> loadRegisterFragment());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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
    public void getUsersFromDB(DataSnapshot dataSnapshot){
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (users != null){
                if (users.size() == 0){
                    users.put(ds.getKey(), (String) ds.getValue());
                }else {
                    if (!users.containsKey(ds.getKey())) {
                        users.put(ds.getKey(), (String) ds.getValue());
                    }
                }
            }
        }
    }

    private void checkLogin(String login, String pass) {
        String email = users.get(login);
        Log.d(TAG, "firstWay" + login);
        mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "success");
                if (users.size() > 1){
                    loadChatListFragment(login);
                }else {
                    exception.setText("No one chats");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "fail");
                        exception.setText(e.getMessage());
                    }
                });
    }

    private void loadChatListFragment(String login){
        databaseReference.getKey();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.putString("from", login);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatList chatList = new ChatList();
        chatList.setArguments(arguments);
        transaction.replace(R.id.login_container, chatList);
        transaction.commit();
    }

    private void loadRegisterFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_container, new RegisterFragment());
        transaction.commit();
    }
}
