package com.example.practisedoneed.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;
//import com.example.practisedoneed.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class donateFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int RESULT_OK = -1;
    private Toolbar toolbar;
    private TextView donateTitle;
    private EditText postTitle;
    private EditText description;
    private EditText quantity;
    private Button chooseBtn;
    Uri imageUrl;
    String myUrl = "";
    ImageView image_add;
    Spinner categoriesSpinner;
    Spinner stateSpinner;
    String[] stateArray = {"State","Johor","Kedah","Kelantan","Melaka","Negeri Sembilan","Pahang","Penang","Perak","Perlis"
            ,"Sabah","Sarawak","Selangor","Terengganu","Kuala Lumpur","Labuan","Putrajaya"};
    String[] categoryArray = {"Categories","Home Equipment","Furniture","Computer","Smartphone","Technology","Cloth","Sport"};
    String category;
    String state;
    ArrayAdapter ad1;
    ArrayAdapter ad2;
    StorageTask uploadTask;
    StorageReference storageReference;
    String extension;
    private Context context;
    private String postId;
    private String editPostId;
    private String defaultImage;
    private FragmentManager fragmentManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate,container,false);
        setHasOptionsMenu(true);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId = preferences.getString("postId", "none");
        editPostId = preferences.getString("editPostID","none");
        defaultImage = preferences.getString("imageUrl","none");

        postTitle = view.findViewById(R.id.Tittle);
        description = view.findViewById(R.id.Description);
        quantity = view.findViewById(R.id.Quantity);
        toolbar = (Toolbar) view.findViewById(R.id.app_toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        assert appCompatActivity != null;
        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        donateTitle = toolbar.findViewById(R.id.text_Donate);
        donateTitle.setVisibility(View.VISIBLE);


        image_add = view.findViewById(R.id.image_add);

        categoriesSpinner = view.findViewById(R.id.categories_spinner);
        categoriesSpinner.setOnItemSelectedListener(this);
        ad1 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categoryArray);
        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(ad1);

        stateSpinner = view.findViewById(R.id.state_spinner);
        stateSpinner.setOnItemSelectedListener(this);
        ad2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, stateArray);
        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(ad2);

        chooseBtn = view.findViewById(R.id.choose_image_btn);
        chooseBtn.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        context = container.getContext();
        fragmentManager = getActivity().getSupportFragmentManager();

        int index = getActivity().getSupportFragmentManager().getBackStackEntryCount() - 2;
        if(!editPostId.equals("none")){
//            editPost();
            FragmentManager.BackStackEntry backEntry = getActivity().getSupportFragmentManager().getBackStackEntryAt(index);
//                    getParentFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            Log.i(TAG,tag);
            if(tag.equals("details")){
                editPost();
            }else {
                editPostId="none";
            }
        }

        return view;
    }

    @Override
    public void onClick(View j) {
        if(j.getId()==R.id.choose_image_btn){
            pickFromGallery();
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().setAspectRatio(5,4).start(requireContext(), this);

    }

    //get file extension(jpg/png)
    public static String getMimeType(Activity context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            //If scheme is a content
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            if (TextUtils.isEmpty(extension)) {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file
            // name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            if (TextUtils.isEmpty(extension)) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            }
        }
        return extension;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUrl = result.getUri();
                Picasso.get().load(imageUrl).into(image_add);
            }else {
                Toast.makeText(getActivity(),"Something gone wrong!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(),"Something gone wrong2!",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Posting");
        progressDialog.show();
        if(imageUrl != null){

            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "."+ getMimeType(getActivity(), imageUrl));
            uploadTask = fileReference.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){

                        throw  task.getException();

                    }
                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult();

                        myUrl = downloadUrl.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("id",postid);
                        hashMap.put("image",myUrl);
                        hashMap.put("title", postTitle.getText().toString());
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("quantity",quantity.getText().toString());
                        hashMap.put("location", state);
                        hashMap.put("category", category);
                        hashMap.put("donator", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);

                        progressDialog.dismiss();
                        fragmentManager.popBackStack(0,0);
                    }
                    else {
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "no Image Selected!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }



    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.donate_toolbar, menu);
        MenuItem update = menu.findItem(R.id.update_now);
        MenuItem donate = menu.findItem(R.id.donate_now);
        int index = getActivity().getSupportFragmentManager().getBackStackEntryCount() - 2;
        if(getActivity().getSupportFragmentManager().getBackStackEntryAt(index).getName().equals("details")){
            update.setVisible(true);
            donate.setVisible(false);
        }else{
            update.setVisible(false);
            donate.setVisible(true);
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();

            return true;
        }else if(item.getItemId() == R.id.donate_now){
            uploadImage();
        }else if(item.getItemId() == R.id.update_now){
            updatePost();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.categories_spinner){
            category = categoryArray[position];
        }else if(parent.getId()==R.id.state_spinner){
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            state = stateArray[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void editPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(editPostId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                donatePost post = snapshot.getValue(donatePost.class);
                Picasso.get().load(post.getImage()).into(image_add);
                postTitle.setText(post.getTitle());
                description.setText(post.getDescription());
                quantity.setText(post.getQuantity());

                String defaultCategory = post.getCategory();
                int categoryIndex = ad1.getPosition(defaultCategory);
                categoriesSpinner.setSelection(categoryIndex);

                String defaultState = post.getLocation();
                int stateIndex = ad2.getPosition(defaultState);
                stateSpinner.setSelection(stateIndex);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void updatePost() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Posting");
        progressDialog.show();
        if(imageUrl != null || !editPostId.equals("none")){
            //if new image inserted
            if(imageUrl != null){
                StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                        + "."+ getMimeType(getActivity(), imageUrl));
                uploadTask = fileReference.putFile(imageUrl);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){

                            throw  task.getException();

                        }
                        return fileReference.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){

                            Uri downloadUrl = task.getResult();

                            myUrl = downloadUrl.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

//                        String postid = reference.push().getKey();
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("id",editPostId);
                            hashMap.put("image",myUrl);
                            hashMap.put("title", postTitle.getText().toString());
                            hashMap.put("description",description.getText().toString());
                            hashMap.put("quantity",quantity.getText().toString());
                            hashMap.put("location", state);
                            hashMap.put("category", category);
                            hashMap.put("donator", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            reference.child(editPostId).setValue(hashMap);

                            progressDialog.dismiss();
                            fragmentManager.popBackStack(0,0);
                        }
                        else {
                            Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getActivity(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }else{
                //default image
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id",editPostId);
                hashMap.put("image",defaultImage);
                hashMap.put("title", postTitle.getText().toString());
                hashMap.put("description",description.getText().toString());
                hashMap.put("quantity",quantity.getText().toString());
                hashMap.put("location", state);
                hashMap.put("category", category);
                hashMap.put("donator", FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(editPostId).setValue(hashMap);
                progressDialog.dismiss();
                fragmentManager.popBackStack(0,0);
            }
        }
        else {
            Toast.makeText(getActivity(), "no Image Selected!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


}