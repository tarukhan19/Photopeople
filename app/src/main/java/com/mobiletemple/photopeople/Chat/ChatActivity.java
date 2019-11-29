package com.mobiletemple.photopeople.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.ChatNotification.ApiInterface;
import com.mobiletemple.photopeople.ChatNotification.Client;
import com.mobiletemple.photopeople.ChatNotification.Data;
import com.mobiletemple.photopeople.ChatNotification.MyResponse;
import com.mobiletemple.photopeople.ChatNotification.Sender;
import com.mobiletemple.photopeople.ChatNotification.Token;
import com.mobiletemple.photopeople.EventDetails;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.InboxActivity;
import com.mobiletemple.photopeople.MyJobActivity;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.Users;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private String freelancerId, quoteId, studioId,sender,jobstatus;
    SessionManager session;

    private ImageView  sendIV;
    private EditText msgET;
    String pic,eventtype,shoottype,price,startdate,enddate,location,description,status,quoteid,studioid,recievertype,recieverid;
    boolean isConnected;
    Intent intent;
    private ChatAdapter chatADP;
    String from,from1;
    String msg;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Toolbar toolbar;
    ImageButton btn_send;
    EditText text_send;
    ChatAdapter chatAdapter;
    List<ChatDTO> chats;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    ApiInterface apiInterface;
    boolean notify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        session = new SessionManager(getApplicationContext());
        intent=getIntent();
        freelancerId=intent.getStringExtra("freelancerId");
        quoteId=intent.getStringExtra("quoteId");
        studioId=intent.getStringExtra("studioID");
        sender= session.getLoginSession().get(SessionManager.KEY_USER_TYPE);
        from=intent.getStringExtra("from");
        pic=intent.getStringExtra("pic");
        eventtype=intent.getStringExtra("eventtype");
        price=intent.getStringExtra("price");
        shoottype=intent.getStringExtra("shoottype");
        startdate=intent.getStringExtra("startdate");
        enddate=intent.getStringExtra("enddate");
        location=intent.getStringExtra("location");
        description=intent.getStringExtra("description");
        status=intent.getStringExtra("status");
        quoteid=intent.getStringExtra("quoteid");
        studioid=intent.getStringExtra("studioID");
        recievertype=intent.getStringExtra("recivertype");
        recieverid=intent.getStringExtra("RECIVERid");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Chat");
        filter.setVisibility(View.GONE);
        apiInterface=Client.getClient("https://fcm.googleapis.com/").create(ApiInterface.class);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(recieverid);
        recyclerView=findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (from.equalsIgnoreCase("eventdetail"))
                {

                    from1=intent.getStringExtra("from1");
                    Intent intent=new Intent(ChatActivity.this,EventDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("eventtype",eventtype);
                    intent.putExtra("shoottype",shoottype);
                    intent.putExtra("price",price);
                    intent.putExtra("startdate",startdate);
                    intent.putExtra("enddate",enddate);
                    intent.putExtra("location",location);
                    intent.putExtra("description",description);
                    intent.putExtra("status",status);
                    intent.putExtra("freelancerId",freelancerId);
                    intent.putExtra("quoteid",quoteId);
                    intent.putExtra("studioID", studioId);

                    if (from1.equalsIgnoreCase("home"))
                    {
                        intent.putExtra("from",  "home");

                    }
                    else {intent.putExtra("from",  "job");}
                    intent.putExtra("RECIVERid",recieverid);
                    intent.putExtra("recivertype",recievertype);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
                else if (from.equalsIgnoreCase("freedetail")){
                    jobstatus=intent.getStringExtra("status");
                    Intent intent=new Intent(ChatActivity.this,FreelancerDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from",  "hire");
                    intent.putExtra("status",  jobstatus);

                    intent.putExtra("studioID", studioid);
                    intent.putExtra("quoteid", quoteId);
                    intent.putExtra("freelancerId",  freelancerId);

                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
                else if (from.equalsIgnoreCase("hire")){
                    Intent intent=new Intent(ChatActivity.this,InboxActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","profile");

                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
                else if (from.equalsIgnoreCase("freejob")){
                    Intent intent=new Intent(ChatActivity.this,MyJobActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","profile");

                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
                else if (from.equalsIgnoreCase("home")){
                    Intent intent=new Intent(ChatActivity.this,HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","other");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }

                else
                {
                    Intent intent=new Intent(ChatActivity.this,HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","other");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }

            }
        });

        msgET = (EditText) findViewById(R.id.msgET);
        sendIV = (ImageView) findViewById(R.id.sendIV);
        sendIV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                msg = msgET.getText().toString().trim();
                notify=true;

                //Log.e("onStart",isConnected+"");
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else {  if (!msg.isEmpty())
                {
//                    Log.e("UID",firebaseUser.getUid());
                    sendMessage(session.getLoginSession().get(SessionManager.KEY_USERID),recieverid,msgET.getText().toString());
                    msgET.setText("");

                }}

            }
        });


        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {


                readMessage(session.getLoginSession().get(SessionManager.KEY_USERID),recieverid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void sendMessage(String sender, final String receiver, String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        //  reference.child("Chats").push().setValue(hashMap);

        reference.child("Chats/"+"jobid"+quoteId).push().setValue(hashMap);
        final DatabaseReference chatreference=FirebaseDatabase.getInstance().getReference("ChatList/"+"jobid"+quoteId)
                .child(receiver);

//        final DatabaseReference chatreference=FirebaseDatabase.getInstance().getReference("ChatList").child(receiver)

//                .child(firebaseUser.getUid());

        chatreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {chatreference.child("id").setValue(receiver);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg= message;
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user=dataSnapshot.getValue(Users.class);
                if (notify){
                    sendNotification(receiver,msg);}
                notify=false;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void readMessage(final String myid, final String userid)
    {

        Log.e("readmsg",myid+"   "+userid+" "+quoteId);
//        /3230   4145  338
        //3230   4145
        chats=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats/"+"jobid"+quoteId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    ChatDTO chat=snapshot.getValue(ChatDTO.class);
                    assert chat != null;
                    if (chat.getReceiver().equalsIgnoreCase(myid)  && chat.getSender().equalsIgnoreCase(userid)  ||
                        chat.getReceiver().equalsIgnoreCase(userid)  && chat.getSender().equalsIgnoreCase(myid))
                    {
                        chats.add(chat);
                    }

                    chatAdapter=new ChatAdapter(ChatActivity.this,chats);
                    recyclerView.setAdapter(chatAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void sendNotification(String receiver,final String msg)
    {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Token token=snapshot.getValue(Token.class);
                    Data data=new Data("chat",session.getLoginSession().get(SessionManager.KEY_USERID),
                            R.mipmap.ic_launcher,quoteId,msg,"New Message",recieverid);
                    Sender sender=new Sender(data,token.getToken());
                    apiInterface.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            Log.e("response",response.toString());

                            if (response.code()==200)
                            {
                                if (response.body().success!=1)
                                {
                                    Toast.makeText(ChatActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equalsIgnoreCase("eventdetail")){
            from1=intent.getStringExtra("from1");

            Intent intent=new Intent(ChatActivity.this,EventDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("eventtype",eventtype);
            intent.putExtra("shoottype",shoottype);
            intent.putExtra("price",price);
            intent.putExtra("startdate",startdate);
            intent.putExtra("enddate",enddate);
            intent.putExtra("location",location);
            intent.putExtra("description",description);
            intent.putExtra("status",status);
            intent.putExtra("freelancerId",freelancerId);
            intent.putExtra("quoteid",quoteId);
            intent.putExtra("studioID", studioId);
            intent.putExtra("RECIVERid",recieverid);
            intent.putExtra("recivertype",recievertype);
            if (from1.equalsIgnoreCase("home"))
            {
                intent.putExtra("from",  "home");

            }
            else {intent.putExtra("from",  "job");}
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
        else if (from.equalsIgnoreCase("freedetail")){
            jobstatus=intent.getStringExtra("status");

            Intent intent=new Intent(ChatActivity.this,FreelancerDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from",  "hire");
            intent.putExtra("status",  jobstatus);

            intent.putExtra("studioID", studioid);
            intent.putExtra("quoteid", quoteId);
            intent.putExtra("freelancerId",  freelancerId);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
        else if (from.equalsIgnoreCase("hire")){
            Intent intent=new Intent(ChatActivity.this,InboxActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","profile");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
        else if (from.equalsIgnoreCase("freejob")){
            Intent intent=new Intent(ChatActivity.this,MyJobActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","profile");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
        else if (from.equalsIgnoreCase("home")){
            Intent intent=new Intent(ChatActivity.this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","other");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }

        else
        {
            Intent intent=new Intent(ChatActivity.this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","other");
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
    }

    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        //Log.e("onStart",isConnected+"");
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else {
        }
        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        showSnack(isConnected);



    }



}



