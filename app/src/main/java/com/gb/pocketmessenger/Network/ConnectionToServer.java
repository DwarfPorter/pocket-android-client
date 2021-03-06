package com.gb.pocketmessenger.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.models.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ConnectionToServer extends AsyncTask<String, Void, String> {

    private String action;
    private User user;
    private PocketDao pocketDao;
    private static final String TAG = "tar";

    public ConnectionToServer(String action, User user, PocketDao pocketDao) {
        this.action = action;
        this.user = user;
        this.pocketDao = pocketDao;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return connectToServer(urls[0], action, user, pocketDao);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String connectToServer(String myUrl, String action, User user, PocketDao pocketDao) throws IOException {
        String data = "";
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("account_name", user.getLogin());
            if (user.geteMail() != null)
                userJson.put("email", user.geteMail());
            userJson.put("password", user.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (action) {
            case "REGISTER":
                try {
                    URL url = new URL(myUrl + "/v1/users/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(userJson.toString());
                    wr.flush();
                    connection.connect();
                    int responseCode = connection.getResponseCode();

                    if (responseCode == 201) {
                        data = getConnectionData(connection);
                    } else if (responseCode == 409) {
                        data = "Такая учетная запись существует!";
                    } else if (responseCode == 400) {
                        data = "Неверные учетные данные";
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
            case "LOGIN":
                try {
                    URL url = new URL(myUrl + "/v1/auth/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    userJson.remove("email");
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(userJson.toString());
                    wr.flush();
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        data = getConnectionData(connection);
                    } else {
                        data = "ОШИБКА ЛОГИНА";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;

            case "GET_ID":
                try {
                    URL url = new URL(myUrl + "/v1/users/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Token", user.getToken());
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        data = getConnectionData(connection);
                    } else if (responseCode == 401) {
                        data = "Unauthorized!";
                    } else if (responseCode == 404) {
                        data = "Token not found!";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
            case "ADD_CONTACT":
                try {
                    URL url = new URL(myUrl + "/v1/users/contacts/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    String token = pocketDao.getUser().getToken();
                    connection.setRequestProperty("Token", token);

                    JSONObject contact = new JSONObject();
                    try {
                        if (user.geteMail() != null)
                            contact.put("contact", user.geteMail());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(contact.toString());
                    wr.flush();
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 201) {
                        data = getConnectionData(connection);
                        Log.d(TAG, "connectToServer ADD_CONTACT data: " + data);
                    } else if (responseCode == 404) {
                        data = "User does not exists";
                    } else if (responseCode == 409) {
                        data = "Contact already in list";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
            case "GET_CONTACTS":
                try {
                    URL url = new URL(myUrl + "/v1/users/contacts/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    String token = pocketDao.getUser().getToken();
                    connection.setRequestProperty("Token", token);
                    connection.connect();

                    data = getConnectionData(connection);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
            case "GET_USER_BY_ID":
                try {
                    String id = user.getId();
                    String token = pocketDao.getUser().getToken();
                    URL url = new URL(myUrl + "/v1/users/" + id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Token", token);
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        data = getConnectionData(connection);
                    } else if (responseCode == 404) {
                        data = "User not found!";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
        }
        return data;
    }

    private String getConnectionData(HttpURLConnection connection) throws IOException {

        String data;
        InputStream inputstream = connection.getInputStream();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int read;
        while ((read = inputstream.read()) != -1) {
            bos.write(read);
        }
        byte[] result = bos.toByteArray();
        bos.close();

        data = new String(result);
        return data;
    }
}
