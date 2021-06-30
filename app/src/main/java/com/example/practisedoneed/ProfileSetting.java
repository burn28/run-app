package com.example.practisedoneed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.practisedoneed.fragment.profileFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

public class ProfileSetting extends AppCompatActivity {

    Uri imageUrl;
    ImageView  image_added,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting2);
//        back = findViewById(R.id.back);
//
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ProfileSetting.this, homePage.class));
//            }
//        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUrl = result.getUri();
            image_added.setImageURI(imageUrl);

        }

        else {
            Toast.makeText(ProfileSetting.this,"Searching gone wrong!",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(ProfileSetting.this, MainActivity.class));
            finish();
        }
    }

    private void openFileChooser(){

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 1);

//startActivity will passed int requestCode which is PICK_IMAGE to onActivity after the activity end

    }

    private String getFileExtensions(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }


}