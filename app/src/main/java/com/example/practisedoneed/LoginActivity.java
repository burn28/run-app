package com.example.practisedoneed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class LoginActivity extends AppCompatActivity {

    EditText password,email;
    Button login;
    TextView text_signup,forgotPassword;
    FirebaseAuth mauth;
    DatabaseReference reference;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        password = findViewById(R.id.login_password);
        email = findViewById(R.id.login_email);
        login = findViewById(R.id.LoginSubmit_btn);
        text_signup = findViewById(R.id.txt_signUp);
        mauth = FirebaseAuth.getInstance();
        forgotPassword = findViewById(R.id.forgotPassword);

        text_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPassword.class));
            }
        });


        Log.d(TAG, "email");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(LoginActivity.this, homePage.class));
//                finish();
                pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please wait...");
                pd.show();
                //  String str_username= username.getText().toString();
                //  String str_fullname= fullname.getText().toString();
                String str_email= email.getText().toString();
                String str_password= password.getText().toString();


                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){

                    Toast.makeText(LoginActivity.this,"All Filds are required",Toast.LENGTH_SHORT).show();

                }else if(str_password.length()<6) {
                    Toast.makeText(LoginActivity.this,"Password must have 6 characters",Toast.LENGTH_SHORT).show();

                }else {

                    mauth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()){
                                if (mauth.getCurrentUser().isEmailVerified()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(mauth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        pd.dismiss();

                                        Intent intent = new Intent(LoginActivity.this, homePage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        pd.dismiss();
                                    }
                                });

                                }

                                else{
                                    Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this,"Authentication Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }

        });


    }
}
