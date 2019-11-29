package com.mobiletemple.photopeople.BottomNavigation;

import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mobiletemple.photopeople.ChatNotification.Token;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.UserAdapter;
import com.mobiletemple.photopeople.model.ChatList;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.Users;

import java.util.ArrayList;
import java.util.List;


public class ChattingFragment extends Fragment {

    public ChattingFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<Users> mUser;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    List<ChatList> userlist;
    SessionManager session;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_chatting, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        backImage.setVisibility(View.GONE);
        filter.setVisibility(View.GONE);

        mTitle.setText("Chat List");

        recyclerView=view.findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userlist=new ArrayList<>();
        session=new SessionManager(getActivity().getApplicationContext());

        reference= FirebaseDatabase.getInstance().getReference("TimelineChatList")
                .child(session.getLoginSession().get(SessionManager.KEY_USERID));

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    ChatList chatList=snapshot.getValue(ChatList.class);

                    userlist.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        uodateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void chatList()
    {
        mUser=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users")
                ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Users user=snapshot.getValue(Users.class);
                    for (ChatList chatList:userlist)
                    {

                        if (user.getUserId().equals(chatList.getId()))
                        {
                            mUser.add(user);
                        }
                    }


                }

                userAdapter=new UserAdapter(getContext(),mUser,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uodateToken(String token)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(session.getLoginSession().get(SessionManager.KEY_USERID)).setValue(token1);

    }



}
