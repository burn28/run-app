package com.example.practisedoneed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.practisedoneed.fragment.HomeFragment;
import com.example.practisedoneed.fragment.profileFragment;
import com.example.practisedoneed.fragment.donateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class homePage extends AppCompatActivity {

    private Button logout;
    private BottomNavigationView bottomNavigationView ;
    private Fragment selectedFagrament = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String fragmentName = "home";
    private long pressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        bottomNavigationView  = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListner);
        Bundle intent = getIntent().getExtras();


        // Default Fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .addToBackStack("home")
                .commit();




    }


    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            selectedFagrament = new HomeFragment();
                            fragmentName="home";
                            break;
                        case R.id.navigation_donate:
                            selectedFagrament = new donateFragment();
                            fragmentName="donate";
                            break;
                        case R.id.navigation_profile:
                            selectedFagrament = new profileFragment();
                            fragmentName="profile";
                            break;

                    }
                        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                selectedFagrament)
                                .addToBackStack(fragmentName)
                                .commit();
                    return true;
                }
            };

    @Override
    public void onAttachFragment(@NonNull @NotNull Fragment fragment) {
        super.onAttachFragment(fragment);
        int index = fragmentManager.getBackStackEntryCount()-1;
        if(fragmentManager.getBackStackEntryCount()!=0){
            String tag = fragmentManager.getBackStackEntryAt(index).getName();
            if(tag.equals("home")){
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
            }else if(tag.equals("donate")){
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
            }else if(tag.equals("profile")){
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
            }
        }
    }

    //ni utk sync top toolbar dgn bottom
    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount()>1){

//            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
//            Log.i(TAG,);
            fragmentManager.popBackStack(0,0);
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        }
        else{
            if (pressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            pressedTime = System.currentTimeMillis();
        }
    }



    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
//    }

}