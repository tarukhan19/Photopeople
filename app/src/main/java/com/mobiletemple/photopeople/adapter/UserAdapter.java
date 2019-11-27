package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.TimeLine.TimelineChatActivity;
import com.mobiletemple.photopeople.userauth.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolderProgressAdapter>{

    private Context mcontex;
    private List<Users> mprogressData;
    private boolean isChat;

    public UserAdapter(Context mcontex, List<Users> mprogressData, boolean isChat) {
        this.mcontex = mcontex;
        this.mprogressData = mprogressData;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.user_item,parent,false);
        return new ViewHolderProgressAdapter(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProgressAdapter holder, final int position)
    {
        holder.username.setText(mprogressData.get(position).getName());
        Glide.with(mcontex).load(R.drawable.default_new_img).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontex, TimelineChatActivity.class);
                intent.putExtra("userid",mprogressData.get(position).getUserId());
                intent.putExtra("from","chat");
                Log.e("userid",mprogressData.get(position).getUserId());
                mcontex.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mprogressData.size();
    }

    public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder
    {

        TextView username;
        CircleImageView imageView,img_on,img_off;
        public ViewHolderProgressAdapter(View itemView) {
            super(itemView);
            username =(TextView)itemView.findViewById(R.id.name);
            imageView =(CircleImageView) itemView.findViewById(R.id.userimage);
            img_on =(CircleImageView) itemView.findViewById(R.id.img_on);
            img_off =(CircleImageView) itemView.findViewById(R.id.img_off);


        }
    }
}
