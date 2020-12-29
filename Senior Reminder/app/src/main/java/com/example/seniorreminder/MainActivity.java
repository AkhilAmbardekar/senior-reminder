package com.example.seniorreminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Login activity for senior reminder app
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.2
 */
public class MainActivity extends AppCompatActivity {

    private EditText usernameET;
    private EditText passwordET;

    private Button loginBtn;
    private Button actBTN;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    /**
     * saves user input and initializes firebase for login authentication
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLoginActivity();
        isUserLoggedIn();


        loginBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * checks inputted username and password when they
             * are inputted by the user
             *
             * @param v
             */
            @Override
            public void onClick(View v){
                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();
                checkPw(username, password);
            }
        });

        actBTN.setOnClickListener(new View.OnClickListener() {
            /**
             * takes user to account creation page
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(startIntent);
            }
        });
    }

// alltattimail@gmail.com; welcome

    /**
     * determines whether user is logged in and takes user to corresponding activity
     */
    private void isUserLoggedIn(){
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser loggedIn = firebaseAuth.getCurrentUser();



        if(loggedIn != null){
            finish();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Sets up layout for MainActivity (login)
     */
    private void setupLoginActivity(){
        setContentView(R.layout.activity_main);

        usernameET = (EditText)findViewById(R.id.usernameET);
        passwordET = (EditText)findViewById(R.id.passwordET);
        loginBtn = (Button)findViewById(R.id.loginBTN);
        actBTN = (Button)findViewById(R.id.actBTN);
    }

    /**
     * verifies the username and password with firebase
     *
     * @param usernameET user inputted username
     * @param passwordET user inputted password
     */
    private void checkPw(String usernameET, String passwordET) {

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(usernameET, passwordET).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            /**
             * removes progress bar depending on whether login is successful
             *
             * @param task
             */
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Username and password authenticated", Toast.LENGTH_SHORT).show();
                    Intent startIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    // to pass information to other activity: startIntent.putExtra("com.example.seniorreminder.SOMETHING", "random information");
                    //if(getIntent().hasExtra("com.example.seniorreminder.SOMETHING")) {getIntent().getExtras().getString("com.example.seniorreminder.SOMETHING"}
                    // use uri method to go to website android studio for beginners 2
                    startActivity(startIntent);
                } else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login was unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
