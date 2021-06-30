package com.example.practisedoneed.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;

//import com.example.practisedoneed.databinding.FragmentHomeBinding;
import com.example.practisedoneed.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.practisedoneed.adapter.donateAdapter;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private donateAdapter postAdapter;
    private List<donatePost> postLists;

    RecyclerView.LayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private TextView doNeedTitle;
    SearchView searchView;
    String search;


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        doNeedTitle = toolbar.findViewById(R.id.text_doNeed);
        doNeedTitle.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recycler_viewhome);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);

        postLists = new ArrayList<>();
        postAdapter = new donateAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        readPost();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_toolbar, menu);
        searchView = (SearchView) menu.findItem(R.id.search_tool).getActionView();
        searchView.setQueryHint("Search Item");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                search = query;
                if (query != null) {
                    searchItem(query);
                } else {
                    readPost();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0){
                    readPost();
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                postLists.clear();
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                    donatePost post = Snapshot.getValue(donatePost.class);
                    postLists.add(post);
                }
                Collections.reverse(postLists);
                postAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchItem(String data) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("title").startAt(data).endAt(data + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                postLists.clear();
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                    donatePost post = Snapshot.getValue(donatePost.class);
                    postLists.add(post);
                }
                Collections.reverse(postLists);
                postAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}