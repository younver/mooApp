package com.example.inekfirebase.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inekfirebase.R;
import com.example.inekfirebase.ThereProfileActivity;
import com.example.inekfirebase.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;

    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public AdapterPost.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPost.MyHolder holder, int position) {
        //get data
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescription();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();

        //convert timestamp to dd/MM/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.uNameTextView.setText(uName);
        holder.pTimeTextView.setText(pTime);
        holder.pTitleTextView.setText(pTitle);
        holder.pDescriptionTextView.setText(pDescription);

        //set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_image_black).into(holder.uPictureImageView);
        }
        catch (Exception e){

        }

        //set post image
        //if there is no image ,.e. pImage.equals("noImage") then hide ImageView
        if (pImage.equals("noImage")){
            //hide imageView
            holder.pImageImageView.setVisibility(View.GONE);
        }
        else {
            try {
                Picasso.get().load(pImage).into(holder.pImageImageView);
            }
            catch (Exception e){

            }
        }


        //handle button clicks,
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*click to go to ThereProfileActivity with uid of clicked user,
                * which will be used to show user specific data/posts*/
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_posts.xml
        ImageView uPictureImageView, pImageImageView;
        TextView uNameTextView, pTimeTextView, pTitleTextView, pDescriptionTextView, pLikesTextView;
        ImageButton moreButton;
        Button likeButton, commentButton, shareButton;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uPictureImageView = itemView.findViewById(R.id.uPictureImageView);
            pImageImageView = itemView.findViewById(R.id.pImageImageView);
            uNameTextView = itemView.findViewById(R.id.uNameTextView);
            pTimeTextView = itemView.findViewById(R.id.pTimeTextView);
            pTitleTextView = itemView.findViewById(R.id.pTitleTextView);
            pDescriptionTextView = itemView.findViewById(R.id.pDescriptionTextView);
            pLikesTextView = itemView.findViewById(R.id.pLikesTextView);
            moreButton = itemView.findViewById(R.id.moreButton);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
