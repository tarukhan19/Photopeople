package com.mobiletemple.photopeople.TimeLine;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.mobiletemple.photopeople.R;

import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TimelineChatActivity extends AppCompatActivity {

    CircleImageView imageView;
    TextView usernameTV,username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;
    String userid,from;
    ImageButton btn_send;
    EditText text_send;
    SessionManager session;
    TimelineChatAdapter chatAdapter;
    List<TimelineChat> chats;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    ApiInterface apiInterface;
    boolean notify=false;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        filter.setVisibility(View.GONE);

        mTitle.setText("Chat");

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TimelineChatActivity.this,HomePage.class);
                intent.putExtra("from","chat");
                startActivity(intent);

            }
        });



        session=new SessionManager(getApplicationContext());

        intent=getIntent();
        userid=intent.getStringExtra("userid");
        from=intent.getStringExtra("from");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        usernameTV=findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        recyclerView=findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(TimelineChatActivity.this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                finish();            }
//        });

        apiInterface= Client.getClient("https://fcm.googleapis.com/").create(ApiInterface.class);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message=text_send.getText().toString();
                if (!message.isEmpty())
                {
                    sendMessage(session.getLoginSession().get(SessionManager.KEY_USERID),userid,message);
                    text_send.setText("");
                }
            }
        });


        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                readMessage(session.getLoginSession().get(SessionManager.KEY_USERID),userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }


    private void seenMessage(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("TimelineChats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    TimelineChat chat=snapshot.getValue(TimelineChat.class);
                    if (chat.getReceiver().equals(session.getLoginSession().get(SessionManager.KEY_USERID))  &&
                            chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
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
        reference.child("TimelineChats").push().setValue(hashMap);
        final DatabaseReference chatreference=FirebaseDatabase.getInstance().getReference("TimelineChatList").
                child(receiver).
                child(session.getLoginSession().get(SessionManager.KEY_USERID));

        chatreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {chatreference.child("id").setValue(session.getLoginSession().get(SessionManager.KEY_USERID));}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg= message;
        reference=FirebaseDatabase.getInstance().getReference("Users");
               // child(receiver);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user=dataSnapshot.getValue(Users.class);
                if (notify)
                {
                    sendNotification(userid,msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(final String receiver, final String msg)
    {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Token token=snapshot.getValue(Token.class);
                    Data data=new Data("timelinechat",session.getLoginSession().get(SessionManager.KEY_USERID),
                            R.mipmap.ic_launcher,"",msg,"New Message",userid);


                    Sender sender=new Sender(data,token.getToken());
                    apiInterface.sendNotification(sender).enqueue(new Callback<MyResponse>()
                    {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            Log.e("response",response.body().success+"");

                            if (response.code()==200)
                            {
                                if (response.body().success!=1)
                                {
                                    Toast.makeText(TimelineChatActivity.this, "failed", Toast.LENGTH_SHORT).show();
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


    private void readMessage(final String myid, final String userid)
    {
        chats=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("TimelineChats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    TimelineChat chat=snapshot.getValue(TimelineChat.class);
                    assert chat != null;
                    if (chat.getReceiver().equalsIgnoreCase(myid)  && chat.getSender().equalsIgnoreCase(userid)  ||
                            chat.getReceiver().equalsIgnoreCase(userid)  && chat.getSender().equalsIgnoreCase(myid))
                    {
                        chats.add(chat);
                    }

                    chatAdapter=new TimelineChatAdapter(TimelineChatActivity.this,chats);
                    recyclerView.setAdapter(chatAdapter);


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
        Intent intent=new Intent(TimelineChatActivity.this,HomePage.class);
        intent.putExtra("from","chat");
        startActivity(intent);
    }

    private void  currentUser(String userid)
    {
        SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }
//    private void status(String status)
//    {
//        reference=FirebaseDatabase.getInstance().getReference("Users").child(session.getLoginSession().get(SessionManager.KEY_USERID));
//        HashMap<String,Object> hashMap=new HashMap<>();
//        hashMap.put("status",status);
//        reference.updateChildren(hashMap);
//    }

    @Override
    protected void onResume() {
        super.onResume();
       // status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        //status("offline");
        currentUser("none");
    }
}
