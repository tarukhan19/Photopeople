package com.mobiletemple.photopeople.Chat;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolderProgressAdapter>{

    private Context mcontex;
    private List<ChatDTO> chats;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    String imageURL;
    FirebaseUser fuser;
    SessionManager session;

    public ChatAdapter(Context mcontex, List<ChatDTO> chats) {
        this.mcontex = mcontex;
        this.chats = chats;
        session = new SessionManager(mcontex);

    }

    @NonNull
    @Override
    public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==MSG_TYPE_RIGHT) {
            View view;
            LayoutInflater mInflater = LayoutInflater.from(mcontex);
            view = mInflater.inflate(R.layout.chatitem_right, parent, false);
            return new ViewHolderProgressAdapter(view);
        }
        else
        {
            View view;
            LayoutInflater mInflater = LayoutInflater.from(mcontex);
            view = mInflater.inflate(R.layout.chatitem_left, parent, false);
            return new ViewHolderProgressAdapter(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProgressAdapter holder, final int position)
    {

        ChatDTO chat=chats.get(position);
        holder.showmesage.setText(chat.getMessage());

//        if (position==chats.size()-1)
//        {
//            if (chat.isIsseen())
//            {
//                holder.readstatus.setText("Seen");
//            }
//            else
//            {
//                holder.readstatus.setText("Delivered");
//            }
//        }
//
//        else
//        {
//            holder.readstatus.setVisibility(View.GONE);
//        }
//
//
////        Log.e("image",mprogressData.get(position).getImageurl());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder{

        TextView showmesage,readstatus;
        CircleImageView imageView;
        public ViewHolderProgressAdapter(View itemView) {
            super(itemView);
            showmesage =(TextView)itemView.findViewById(R.id.showmessage);
           // imageView =(CircleImageView) itemView.findViewById(R.id.userimage);
            //readstatus=itemView.findViewById(R.id.readstatus);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(session.getLoginSession().get(SessionManager.KEY_USERID)))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
