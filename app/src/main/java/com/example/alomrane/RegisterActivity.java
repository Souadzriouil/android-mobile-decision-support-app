package com.example.alomrane;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    TextView Alreadyhaveaccount;
    EditText inputName,inputEmail,inputPassword,inputConformPassword;
    Button btnregister;
    String fullNamePattern = "^[\\p{L} .'-]+$";
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Alreadyhaveaccount=findViewById(R.id.Alreadyhaveaccount);
        inputName=findViewById(R.id.inputName);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputConformPassword=findViewById(R.id.inputConformPassword);
        btnregister=findViewById(R.id.btnregister);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();



        Alreadyhaveaccount.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this,
                MainActivity.class)));


        btnregister.setOnClickListener(v -> PerforAuth());
    }

    private void PerforAuth() {
        String fullName = inputName.getText().toString();
        String email=inputEmail.getText().toString();
        String password=inputPassword.getText().toString();
        String conformPassword=inputConformPassword.getText().toString();

        if (!fullName.matches(fullNamePattern)) {
            inputName.setError("Please enter a valid full name");
            return;
        }else if(!email.matches(emailPattern))
        {
            inputEmail.setError("Enter Connext Email");
        }else if(password.isEmpty()||password.length()<6)
        {
            inputPassword.setError("Enter Proper Password");
        }else if(!password.contentEquals(conformPassword))
        {
            inputConformPassword.setError("Password Not match Both field");
        }else{
            progressDialog.setMessage("Please Wait White Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest);

                    //hihi
                    progressDialog.dismiss();
                    sendUserToNextActivity();
                    Toast.makeText(RegisterActivity.this, "Registration Successful ", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    private void sendUserToNextActivity() {
        Intent intent=new Intent(RegisterActivity.this,HomeActivity_singin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}