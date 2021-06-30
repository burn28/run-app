package com.example.practisedoneed.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;
import com.example.practisedoneed.adapter.donateAdapter;
import com.example.practisedoneed.adapter.postDetailsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    private RecyclerView recyclerView;
    private postDetailsAdapter postAdapter;
    private List<donatePost> postLists;
    private RecyclerView.LayoutManager linearLayoutManager;
    private String postId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details,container,false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId = preferences.getString("postId", "none");

        recyclerView = view.findViewById(R.id.postDetailsRecycler);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postLists = new ArrayList<>();
        postAdapter = new postDetailsAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        readPostDetails();
        return view;
    }

    private void readPostDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                postLists.clear();
                donatePost post = snapshot.getValue(donatePost.class);
                postLists.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}