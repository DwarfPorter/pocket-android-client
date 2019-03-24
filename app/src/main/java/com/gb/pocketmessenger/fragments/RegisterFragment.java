package com.gb.pocketmessenger.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.User;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegisterFragment extends Fragment {
    private EditText loginEditext;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button registerButton;
    private Button cancelButton;
    private FirebaseAuth mAuth;
    private TextView answer;
    Map<String, String> users = new HashMap<>();
    private FirebaseAuth.AuthStateListener mListener;
    final String TAG = "registerTAG";
    private DatabaseReference databaseReference;
    String loginN;
    DatabaseReference parent;
//    List<String> users = new ArrayList<>();


    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        loginEditext = view.findViewById(R.id.login_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        emailEditText = view.findViewById(R.id.email_edittext);
        registerButton = view.findViewById(R.id.register_ok_button);
        cancelButton = view.findViewById(R.id.register_cancel_button);
        answer = view.findViewById(R.id.server_response);
        registerButton.setOnClickListener(v ->
                sendRegisterData(loginEditext.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        checkAuthState();
        parent = FirebaseDatabase.getInstance().getReference();
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

    public void checkAuthState(){
        mListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){

                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mListener);
    }

    private void sendRegisterData(String login, String email, String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            answer.setText("Fields are empty");
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    answer.setText("Sign up success");
                    addUserToDB(login, email);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getMessage());
                            answer.setText(e.getMessage());
                        }
                    });
        }
    }

    private void addUserToDB(String login, String email) {
        users.put(login, email);
        databaseReference.child(login).setValue(users, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                loginN = login;
                if (users.size() > 1){
                    loadChatListFragment(users, email, login);
                } else {
                    answer.setText("No one chats");
                }
            }
        });
    }

    public void getUsersFromDB(DataSnapshot dataSnapshot){
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (users != null){
                if (users.size() == 0){
                    users.put(ds.getKey(), (String) ds.getValue());
                }else {
                    if (!users.containsKey(ds.getKey())){
                        users.put(ds.getKey(), (String) ds.getValue());
                        Log.d(TAG, (String) ds.getValue());
                    }
                }
            }
        }
    }

    private void loadChatListFragment(Map<String,String> users, String email, String login){
        users.remove(login);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.putString("from", login);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatList chatList = new ChatList();
        chatList.setArguments(arguments);
        transaction.replace(R.id.login_container, chatList);
        transaction.commit();
    }

    }