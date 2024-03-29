package com.example.inekfirebase.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inekfirebase.AddPostActivity;
import com.example.inekfirebase.PostDetailActivity;
import com.example.inekfirebase.R;
import com.example.inekfirebase.ThereProfileActivity;
import com.example.inekfirebase.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;

    String myUid;

    private DatabaseReference likesRef; //for likes database node
    private DatabaseReference postsRef; //reference of posts

    boolean mProcessLike=false;

    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public AdapterPost.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPost.MyHolder holder, final int position) {
        //get data
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescription();
        final String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes(); //contains total number of likes for a post
        String pComments = postList.get(position).getpComments(); //contains total number of likes for a post

        //convert timestamp to dd/MM/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.uNameTextView.setText(uName);
        holder.pTimeTextView.setText(pTime);
        holder.pTitleTextView.setText(pTitle);
        holder.pDescriptionTextView.setText(pDescription);
        holder.pLikesTextView.setText(pLikes + " Likes");   //e.g. 100 Likes
        holder.pCommentsTextView.setText(pComments + " Comments");   //e.g. 100 Comments
        //set likes for each post
        setLikes(holder, pId);

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
            //show imageView
            holder.pImageImageView.setVisibility(View.VISIBLE);
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
                showMoreOptions(holder.moreButton, uid, myUid, pId, pImage);
            }
        });
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;

                final String postIde = postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike){
                            if (snapshot.child(postIde).hasChild(myUid)){
                                //already liked, so remove like
                                postsRef.child(postIde).child("pLikes").setValue(""+(pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else{
                                // not liked, like it
                                postsRef.child(postIde).child("pLikes").setValue(""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start PostDetailActivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId); //will get detail of post using this id
                context.startActivity(intent);
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

    private void setLikes(final MyHolder holder, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(myUid)){
                    //user has liked this post
                   holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_blue, 0, 0,0);
                   holder.likeButton.setText("Liked");
                }
                else {
                    holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black, 0, 0,0);
                    holder.likeButton.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMoreOptions(ImageButton moreButton, String uid, String myUid, final String pId, final String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreButton, Gravity.END);

        //show delete option in only post(s) of currently signed in user
        if (uid.equals(myUid)){
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE, 2, 0, "View Detail");

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    //delete is clicked
                    deletePost(pId, pImage);
                }
                else if (id==1){
                    //edit is clicked
                    //start AddPostActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }
                else if (id==2){
                    //start PostDetailActivity
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId); //will get detail of post using this id
                    context.startActivity(intent);
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();

    }

    private void deletePost(String pId, String pImage) {
        Toast.makeText(context, "Deleting Post", Toast.LENGTH_SHORT).show();
        //post can be with or without image
        if (pImage.equals("noImage")){
            //post with image
             deletePostWithoutImage(pId);
        }
        else {
            deletePostWithImage(pId, pImage);
        }
    }

    private void deletePostWithImage(final String pId, String pImage) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting Post...");

        //delete image using url
        //delete from database using post id

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ds.getRef().removeValue();  //remove value from firebase where pId matches
                                }
                                //deleted
                                Toast.makeText(context, "Post Deleted.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         //failed, can't go further
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePostWithoutImage(String pId) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting Post...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();  //remove value from firebase where pId matches
                }
                //deleted
                Toast.makeText(context, "Post Deleted.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        TextView uNameTextView, pTimeTextView, pTitleTextView, pDescriptionTextView, pCommentsTextView, pLikesTextView;
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
            pCommentsTextView = itemView.findViewById(R.id.pCommentsTextView);
            moreButton = itemView.findViewById(R.id.moreButton);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
