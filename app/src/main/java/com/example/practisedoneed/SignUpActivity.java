package com.example.practisedoneed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText username ,phoneNumber,password,email,address,reconfirmPassword;
    Button register;
    TextView text_login;
    FirebaseAuth mauth;
    DatabaseReference reference;
    ProgressDialog pd;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_sign_up);

        username = findViewById(R.id.signUp_username);
        email = findViewById(R.id.signUp_email);
        address = findViewById(R.id.signUp_address);
        phoneNumber = findViewById(R.id.signUp_phoneNumber);
        password = findViewById(R.id.signUp_password);
        reconfirmPassword = findViewById(R.id.signUp_reconfirmPassword);

        register = findViewById(R.id.signUpSubmit_btn);
        text_login = findViewById(R.id.txt_login);
        mauth = FirebaseAuth.getInstance();
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd = new ProgressDialog(SignUpActivity.this);
                pd.setMessage("Please wait...");
                pd.show();
                String str_username= username.getText().toString();
                String str_email= email.getText().toString();
                String str_address = address.getText().toString();
                String str_phoneNumber= phoneNumber.getText().toString();
                String str_password= password.getText().toString();
                String str_reconfirmPassword= reconfirmPassword.getText().toString();

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(
                        str_phoneNumber) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)||TextUtils.isEmpty(str_address)||TextUtils.isEmpty(str_reconfirmPassword)){

                    Toast.makeText(SignUpActivity.this,"All Filds are required",Toast.LENGTH_SHORT).show();


                }else if(str_password.length()<6) {
                    Toast.makeText(SignUpActivity.this,"Password must have 6 characters",Toast.LENGTH_SHORT).show();

                }else if(!str_password.equals(str_reconfirmPassword)){
                    Toast.makeText(SignUpActivity.this,"Password must same on both field",Toast.LENGTH_SHORT).show();
                }

                else {

                    register(str_username,str_email,str_address,str_phoneNumber,str_password);

                }

            }


        });




    }

    private  void register (final String username , final String email , String address, String phoneNumber, String password){

        mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){

                    FirebaseUser firebaseUser = mauth.getCurrentUser();
                    String userid  = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    HashMap<String,Object> hashMap = new HashMap<>();

                    hashMap.put("id",userid);
                    hashMap.put("username",username);
                    hashMap.put("address",address);
                    hashMap.put("phone",phoneNumber);
                    hashMap.put("email",email);
                    hashMap.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/practise-doneed.appspot.com/o/profile-user.png?alt=media&token=759bcc21-773e-4ddd-95e9-4415026080c8");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()) {

//                                            Toast.makeText(SignUpActivity.this, "User register succesfully. Please verify your email id ", Toast.LENGTH_LONG).setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0).show();

                                            Toast toast= Toast.makeText(getApplicationContext(),
                                                    "User register succesfully. Please verify your email id", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();pd.dismiss();
                                            Intent intent = new Intent(SignUpActivity.this ,LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        }
                    });
                }else {
                    pd.dismiss();
                    Toast.makeText(SignUpActivity.this,"You can not register with this email and password",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}