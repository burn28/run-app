package com.example.practisedoneed.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.User;
import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;
import com.example.practisedoneed.fragment.donateFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class postDetailsAdapter extends RecyclerView.Adapter<postDetailsAdapter.ViewHolder>{

    public Context mContext;
    public List<donatePost> mPosts;
    public FirebaseUser firebaseUser;


    public postDetailsAdapter(Context mContext, List<donatePost> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.details_item,parent,false);

        return new postDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUser = firebaseUser.getUid();
        final donatePost post = mPosts.get(position);
        Glide.with(mContext).load(post.getImage()).into(holder.postImage);

        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDescription());
        holder.quantity.setText(post.getQuantity());
        holder.category.setText(post.getCategory());
        holder.location.setText(post.getLocation());

        publisherInfo(holder.imageProfile, holder.username, post.getDonator());
        isSaved(post.getId(), holder.saveIcon);

        if(post.getDonator().equals(currentUser)){
            holder.chatBtn.setVisibility(View.GONE);
            holder.saveIcon.setVisibility(View.GONE);
            holder.optionIcon.setVisibility(View.VISIBLE);
        }

        holder.optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.edit_post){
                            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                            editor.putString("editPostID",post.getId());
                            editor.putString("imageUrl",post.getImage());
                            editor.apply();
                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                    , new donateFragment())
                                    .addToBackStack("donate")
                                    .commit();
                        }else if(item.getItemId()==R.id.del_post){

                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.post_option);
                popupMenu.show();
            }
        });
        holder.saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.saveIcon.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getId()).setValue("true");
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getId()).removeValue();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage, saveIcon, imageProfile, optionIcon;
        public TextView title,username,description,quantity,category,location;
        public Button chatBtn;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
            saveIcon = itemView.findViewById(R.id.save);
            optionIcon = itemView.findViewById(R.id.optionIcon);
            imageProfile = itemView.findViewById(R.id.image_profile);
            title = itemView.findViewById(R.id.post_title);
            username = itemView.findViewById(R.id.usernamepost);
            description = itemView.findViewById(R.id.description);
            quantity = itemView.findViewById(R.id.quantity);
            category = itemView.findViewById(R.id.category);
            location = itemView.findViewById(R.id.location);
            chatBtn = itemView.findViewById(R.id.chat_button);

        }
    }

    private  void  publisherInfo(final ImageView image_profile, final TextView donator , String userid){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);

                Glide.with(mContext).load(user.getImageUrl()).into(image_profile);

                donator.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    public void isSaved(String postId, ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
