package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.db.ConnectionDB;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.PocketMessage;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.gb.pocketmessenger.models.User;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Statement;

public class ChatMessages extends Fragment implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    private static final int TOTAL_MESSAGES_COUNT = 50;
    ConnectionDB connectionDB;
    User user;

    //    public static final String REST_WS_CONNECT = "/v1/ws/";
//    public static final String WSS_POCKETMSG = "wss://pocketmsg.ru:8888/v1/ws/";


    protected ImageLoader imageLoader;
    private MessagesList messages;
    private MessagesListAdapter<Message> messageAdapter;
    private final String senderId = "0";    //TODO: get senderID


    public static ChatMessages newInstance(String dialogId) {
        //TODO: get messages
        return new ChatMessages();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            String usernameFromReg = bundle.getString("USERNAME");
            user = new User(usernameFromReg);
        }
        connectionDB = new ConnectionDB();
        try {
            Connection con = connectionDB.CONN();
            Statement stmt = con.createStatement();
            String create = "create table dialogs if not exist";
            stmt.execute(create);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        imageLoader = (imageView, url) -> Picasso.get().load(url).into(imageView);
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        messages = view.findViewById(R.id.messagesList);

        initAdapter();
        MessageInput input = view.findViewById(R.id.input);
        input.setInputListener((MessageInput.InputListener) this);
        input.setTypingListener(this);
        input.setAttachmentsListener((MessageInput.AttachmentsListener) this);
        return view;
    }

    public void newMessage(Message message) {
        Connection con = connectionDB.CONN();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(addMessageToDB(message));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        messageAdapter.addToStart(message, true);
    }

    String addMessageToDB (Message message){
        String query = "insert into dialogs values('" + message.getUser().getId() + "', '" + message.getAdress() + "', '"+ message.getText() +"', '" + message.getCreatedAt() + "')";
        return query;
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }


    private void initAdapter() {
        messageAdapter = new MessagesListAdapter<>(user.getName(), imageLoader);
//        messageAdapter.enableSelectionMode((MessagesListAdapter.SelectionListener) this);//       messageAdapter.setLoadMoreListener((MessagesListAdapter.OnLoadMoreListener) this);
//        messageAdapter.registerViewClickListener(R.id.messageUserAvatar,
//                (view, message) -> {
//
//                });
        messages.setAdapter(messageAdapter);
    }

    @Override
    public void onAddAttachments() {

    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Date apptDay = new Date();
        java.sql.Date sqlDate = new java.sql.Date(apptDay.getTime());
        newMessage(new Message(user, null, input.toString(), sqlDate));
        return true;
    }



//    private String getMessage(CharSequence input, String receiver) {
//        PocketMessage message = new PocketMessage(receiver, input.toString());
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        return gson.toJson(message);
//    }
}
