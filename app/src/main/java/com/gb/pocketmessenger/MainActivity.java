package com.gb.pocketmessenger;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gb.pocketmessenger.fragments.LoginFragment;
import com.gb.pocketmessenger.fragments.TabsFragment;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkingToken()) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.login_container, LoginFragment.newInstance());
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //TODO:  check token
    private boolean checkingToken() {
        return false;
    }

}
