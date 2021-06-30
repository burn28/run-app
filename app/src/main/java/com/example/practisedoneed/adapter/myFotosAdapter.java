package com.example.practisedoneed.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;
import com.example.practisedoneed.fragment.PostDetailsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class myFotosAdapter extends RecyclerView.Adapter<myFotosAdapter.ViewHolder>{

    public Context mContext;
    public List<donatePost> mPosts;

    public myFotosAdapter(Context mContext, List<donatePost> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item,parent,false);
        return new myFotosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        final donatePost post = mPosts.get(position);
        Glide.with(mContext).load(post.getImage()).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",post.getId());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailsFragment())
                        .addToBackStack("details")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.mypost_image);
        }
    }
}
