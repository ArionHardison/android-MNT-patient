package com.dietmanager.app.activities.fcm_chat;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.SplashActivity;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.chat_view)
    ListView mChatView;
    @BindView(R.id.message)
    EditText etMessage;

    public static String sender = "user";
    private String TAG = "ChatActivity";
    private ChatMessageAdapter mAdapter;
    private ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String senderID = "";
    private String chatPath = "";
    private boolean isChat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        chatPath = getIntent().getStringExtra("channel_name");
        senderID = getIntent().getStringExtra("channel_sender_id");
        isChat = getIntent().getBooleanExtra("isChat", false);
        initChatView(chatPath);
        back.setOnClickListener(v -> onBackPressed());
    }

    private void initChatView(String chatPath) {
        if (chatPath == null) {
            return;
        }

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String myText = etMessage.getText().toString().trim();
                if (myText.length() > 0) {
                    sendMessage(myText);
                }
                handled = true;
            }
            return handled;
        });

        mAdapter = new ChatMessageAdapter(getApplicationContext(), new ArrayList<>());
        mChatView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(chatPath);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                mAdapter.add(chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @OnClick(R.id.send)
    public void onViewClicked() {
        String myText = etMessage.getText().toString();
        if (myText.length() > 0) {
            sendMessage(myText);
        }
    }

    private void sendMessage(String messageStr) {
        ArrayList<Integer> readedMembers = new ArrayList<>();
        readedMembers.add(Integer.valueOf(senderID));
        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setTimestamp(new Date().getTime());
        chat.setRead(0);
        chat.setReadedMembers(readedMembers);
        chat.setType("text");
        chat.setImage(GlobalData.profileModel.getAvatar());
        chat.setName(GlobalData.profileModel.getName());
        chat.setText(messageStr);
        myRef.push().setValue(chat);
        sendMessageToServer(messageStr);
        etMessage.setText("");
    }

    private void sendMessageToServer(String message) {
        GlobalData.access_token = SharedHelper.getKey(getApplicationContext(), "access_token");
        HashMap<String, String> param = new HashMap<>();
        param.put("id", senderID);
        param.put("order_id", chatPath);
        param.put("message", message);
        Call<Object> call = apiInterface.chatPost(param);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                System.out.println(TAG + "Chat Success");
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.println(TAG + "Chat Failed");
            }
        });
    }

    private IntentFilter filter = new IntentFilter();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
            nManager.cancelAll();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //  filter.addAction("CHAT_NOTIFICATION");
        //  LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
