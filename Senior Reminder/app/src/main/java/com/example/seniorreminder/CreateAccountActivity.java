package com.example.seniorreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Account registration for senior reminder app
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.2
 */
public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";

    private EditText newUsernameET;
    private EditText newEmailET;
    private EditText newPasswordET;

    private Button accountBTN;
    private Button loginBTN;
    private FirebaseAuth firebaseAuth;

    /**
     * initializes firebase and allows user to create an
     * account through firebase
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupCreateAccountActivity();

        firebaseAuth = FirebaseAuth.getInstance();
        addUserToDatabase();

        accountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notEmpty() == Boolean.TRUE) {
                    addUserAccount();
                }
            }

        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });
    }

    /**
     * adds user input for email and password to login authentication database (firebase)
     */
    private void addUserAccount(){
        String emailET = newEmailET.getText().toString().trim();
        String passwordET = newPasswordET.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(emailET, passwordET).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account was successfully created",
                            Toast.LENGTH_SHORT).show();
                    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(startIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Account creation was unsuccessful",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * creates document in users collection in firestore when new user account is created
     */
    private void addUserToDatabase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * initializes CreateAccountActivity layout
     */
    private void setupCreateAccountActivity(){

        setContentView(R.layout.activity_create_account);

        newUsernameET = (EditText)findViewById(R.id.usernameET);
        newEmailET = (EditText)findViewById(R.id.emailET);
        newPasswordET = (EditText)findViewById(R.id.passwordET);
        accountBTN = (Button)findViewById(R.id.actBTN);
        loginBTN = (Button)findViewById(R.id.retLoginBTN);

    }

    /**
     * determines whether user has inputted username
     * or password
     *
     * @return true or false depending on whether fields are empty
     */
    private Boolean notEmpty() {
        Boolean notBlank = false;
        String usernameET = newUsernameET.getText().toString();
        String passwordET = newPasswordET.getText().toString();
        String emailET = newEmailET.getText().toString();

        if (usernameET.isEmpty() || passwordET.isEmpty() || emailET.isEmpty()) {
            Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT).show();
        } else {
            notBlank = true;
        }
        return notBlank;
    }
}
