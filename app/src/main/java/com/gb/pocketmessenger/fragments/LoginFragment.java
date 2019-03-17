package com.gb.pocketmessenger.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.db.ConnectionDB;
import com.gb.pocketmessenger.models.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginFragment extends Fragment {
    TextView login, password;
    User user = new User("1");

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        login = view.findViewById(R.id.login_et);
        password = view.findViewById(R.id.password_et);
        view.findViewById(R.id.button_login).setOnClickListener(v -> checkLogin(login.getText().toString(), password.getText().toString()));
        view.findViewById(R.id.button_register).setOnClickListener(v -> loadRegisterFragment());
        return view;
    }

    private void checkLogin(String login, String pass) {
        Dologin dologin = new Dologin(login, pass);
        dologin.execute("");
    }

    //TODO REFACTOR
    private void loadChatMessagesFragment(String login) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.putString("USERNAME", login);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setArguments(arguments);
        transaction.replace(R.id.login_container, chatMessages);
        transaction.commit();
    }

    private void loadRegisterFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_container, new RegisterFragment());
        transaction.commit();
    }

    public class Dologin extends AsyncTask<String,String,String> {
        String login;
        String password;
        String z="";
        boolean isSuccess=false;
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        ConnectionDB connectionDB = new ConnectionDB();

        public Dologin(String login, String password) {
            this.login = login;
            this.password = password;
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
            try {
                Connection con = connectionDB.CONN();
                Statement stmt = con.createStatement();
                String create = "create table auth if not exist";
                stmt.execute(create);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            if (login.trim().equals("") || password.trim().equals(""))
                z = "Please enter all fields....";
            else {
                try {
                    Connection con = connectionDB.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String request = "select * from auth where login = '" + login + "' and password = '" + password + "'";
                        try {
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery(request);
                            if (rs.next()){
                                z = "Auth successfull";
                                isSuccess = true;
                            } else {
                                z = "Auth failed. User not exists";
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
                loadChatMessagesFragment(login);
            }
        }
    }
}
