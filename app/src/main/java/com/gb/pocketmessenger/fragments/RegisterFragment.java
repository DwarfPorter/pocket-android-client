package com.gb.pocketmessenger.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.db.ConnectionDB;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.sql.DataSource;

public class RegisterFragment extends Fragment {
    private EditText loginEditext;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button registerButton;
    private Button cancelButton;
    ConnectionDB connectionDB;
    ProgressDialog progressDialog;

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
        registerButton.setOnClickListener(v ->
                sendRegisterData(loginEditext.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString()));
        connectionDB = new ConnectionDB();
        progressDialog = new ProgressDialog(getContext());
        return view;
    }

    private void sendRegisterData(String login, String email, String password) {
        Doregister doregister = new Doregister(login, email, password);
        doregister.execute("");
    }

        public class Doregister extends AsyncTask<String, String, String> {
        String login, email, password;

            public Doregister(String login, String email, String password) {
                this.login = login;
                this.email = email;
                this.password = password;
            }
            String z = "";
            boolean isSuccess = false;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                if (login.trim().equals("") || email.trim().equals("") || password.trim().equals(""))
                    z = "Please enter all fields....";
                else {
                    try {
                        Connection con = connectionDB.CONN();
                        if (con == null) {
                            z = "Please check your internet connection";
                        } else {

                            String request = "select * from auth where login = '" + login + "'";
                            try {
                                Statement stmt = con.createStatement();
                                ResultSet rs = stmt.executeQuery(request);
                                if (!rs.next()){
                                    String query = "insert into auth values('" + login + "', '" +email + "', '"+ password +"')";
                                    stmt.executeUpdate(query);
                                    z = "Register successfull";
                                    isSuccess = true;
                                } else {
                                    z = "Register failed. User exists";
                                    isSuccess = false;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();

                            }
                        }
                    } catch (Exception ex) {
                        isSuccess = false;
                        z = "Exceptions" + ex;
                    }
                }
                progressDialog.setMessage(z);
                progressDialog.show();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return z;
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.hide();
                if (isSuccess){
                    loadChatMessagesFragment();
                }
            }
        }

    private void loadChatMessagesFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_container, new ChatMessages());
        transaction.commit();
    }
    }