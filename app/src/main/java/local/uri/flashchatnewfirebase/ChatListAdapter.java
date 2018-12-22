package local.uri.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapShotList;
    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mSnapShotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference databaseReference, String displayName) {
        mActivity = activity;
        mDatabaseReference = databaseReference.child("messages");
        mDisplayName = displayName;
        mDatabaseReference.addChildEventListener(mListener);

        mSnapShotList = new ArrayList<>();
    }

    static class ViewHolder {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }


    @Override
    public int getCount() {
        return mSnapShotList.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        DataSnapshot snapshot = mSnapShotList.get(i);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.chat_msg_row, viewGroup, false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) view.findViewById(R.id.author);
            holder.body = (TextView) view.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            view.setTag(holder);
        }

        final InstantMessage message = getItem(i);
        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRawAppearance(isMe, holder);

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);

        return view;
    }

    private void setChatRawAppearance(boolean isItMe, ViewHolder holder) {
        if (isItMe) {
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanUp() {
        mDatabaseReference.removeEventListener(mListener);
    }
}
