package com.example.practisedoneed.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.practisedoneed.fragment.PostDetailsFragment;
import com.example.practisedoneed.fragment.donateFragment;
import com.example.practisedoneed.fragment.profileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class donateAdapter extends RecyclerView.Adapter<donateAdapter.ViewHolder> {

    public Context mContext;
    public List<donatePost> mPost;
    private FirebaseUser firebaseUser;

    public donateAdapter(Context mContext, List<donatePost> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_item,parent,false);

        return new donateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final donatePost post = mPost.get(position);
        Glide.with(mContext).load(post.getImage())
//                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.post_image);

        holder.title.setText(post.getTitle());
        if(post.getDonator().equals(firebaseUser.getUid())){
            holder.save.setVisibility(View.GONE);
        }else {
            holder.save.setVisibility(View.VISIBLE);
        }
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",post.getDonator());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profileFragment())
                        .addToBackStack("profile")
                        .commit();
            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",post.getId());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailsFragment())
                        .addToBackStack("details")
                        .commit();
                //try2
            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getId()).setValue("true");
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getId()).removeValue();
                }
            }
        });


        isSaved(post.getId(), holder.save);
        publisherInfo(holder.image_profile, holder.username, post.getDonator());


    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image, save;
        public TextView username, title, quantity, category, location, description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.usernamepost);
            title = itemView.findViewById(R.id.title);
            quantity = itemView.findViewById(R.id.quantity);
            category = itemView.findViewById(R.id.category);
            location = itemView.findViewById(R.id.location);
            description = itemView.findViewById(R.id.description);

        }
    }

    private  void  publisherInfo(final ImageView image_profile, final TextView donator , String userid ){


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