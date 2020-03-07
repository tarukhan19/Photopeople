package com.mobiletemple.photopeople.TimeLine;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineChatAdapter extends RecyclerView.Adapter<TimelineChatAdapter.ViewHolderProgressAdapter>{

    private Context mcontex;
    private List<TimelineChat> chats;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    FirebaseUser fuser;
    SessionManager mSessionManager;

    public TimelineChatAdapter(Context mcontex, List<TimelineChat> chats) {
        this.mcontex = mcontex;
        this.chats = chats;
        mSessionManager = new SessionManager(mcontex.getApplicationContext());
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
    public void onBindViewHolder(@NonNull ViewHolderProgressAdapter holder, final int position) {

        TimelineChat chat=chats.get(position);
        holder.showmesage.setText(chat.getMessage());


        if (position==chats.size()-1)
        {
            if (chat.isIsseen())
            {
                holder.readstatus.setText("Seen");
            }
            else
            {
                holder.readstatus.setText("Delivered");
            }
        }

        else
        {
        holder.readstatus.setVisibility(View.GONE);
        }


//        Log.e("image",mprogressData.get(position).getImageurl());
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
            imageView =(CircleImageView) itemView.findViewById(R.id.profileimage);
            readstatus=itemView.findViewById(R.id.readstatus);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(mSessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
