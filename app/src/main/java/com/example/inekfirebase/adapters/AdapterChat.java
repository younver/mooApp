package com.example.inekfirebase.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inekfirebase.R;
import com.example.inekfirebase.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;    //May be import error!
    List<ModelChat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts: row_chat_left.xml for receiver, row_chat_right.xml for sender
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        //get data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();

        //convert time stamp to dd/mm/yyyy hh.mm am/pm
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy\nhh.mm aa", cal).toString();

        //set data
        holder.messageTextView.setText(message);
        holder.timeTextView.setText(dateTime);
        try{
            Picasso.get().load(imageUrl).into(holder.profileImageView);
        }
        catch (Exception e){

        }
        //hold to show delete dialog
        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //show delete message confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteMessage(position);
                    }
                });
                //cancel delete button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });
                //create and show dialog
                builder.create().show();
                return false;   //There may be problem!
            }
        });

        //set seen/delivered status of message
        if (position == chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeenTextView.setText("Seen");
            }
            else {
                holder.isSeenTextView.setText("Delivered");
            }
        }
        else{
            holder.isSeenTextView.setVisibility(View.GONE);
        }

    }

    private void deleteMessage(int position) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        /*Logic:
        * Get timestamp of clicked message
        * Compare the timestamp of the clicked message with all messages in Chats
        * Where both values matches delete that message*/
        String messageTimeStamp = chatList.get(position).getTimeStamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timeStamp").equalTo(messageTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    /*if you want to allow sender to delete only his message then
                    * compare sender value with current user's uid
                    * if they match means its the message of sender that is trying to delete*/
                    if (ds.child("sender").getValue().equals(myUID)){
                        /*We can do one of two things here;
                         * Remove the message from Chats
                         * Set the value of message "This message was deleted..."
                         * So do whatever you want*/

                        //1) Remove the message from Chats
                        //ds.getRef().removeValue();
                        //2) Set the value of message "This message was deleted..."
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "This message was deleted...");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "message deleted...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "You can delete only your messages...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView profileImageView;
        TextView messageTextView, timeTextView, isSeenTextView;
        LinearLayout messageLayout; //for click listener to show delete


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileImageView = itemView.findViewById(R.id.profile_picture);
            messageTextView = itemView.findViewById(R.id.message_textView);
            timeTextView = itemView.findViewById(R.id.time_textView);
            isSeenTextView = itemView.findViewById(R.id.isSeen_textView);
            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }
}
