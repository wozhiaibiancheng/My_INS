package au.edu.unimelb.student.group55.my_ins.LoginNRegister;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;

import au.edu.unimelb.student.group55.my_ins.Home.HomeActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private Context myContext;
    private EditText myEmail, myPassword;

    SharedPreferences prefs;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        myEmail = (EditText) findViewById(R.id.input_email);
        myPassword = (EditText) findViewById(R.id.input_password);
        myContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        FirebaseAuth();
        initialization();

    }

    private boolean checkInput(String string){
        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void initialization(){

        //initialize the button for logging in
        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = myEmail.getText().toString();
                String password = myPassword.getText().toString();

                if(checkInput(email) && checkInput(password)){
                    Toast.makeText(myContext, "You need to input all the required information!", Toast.LENGTH_SHORT).show();
                }else{
                    myAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, getString(R.string.fail_to_auth),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
//                                        Toast.makeText(LoginActivity.this, getString(R.string.succ_to_auth),
//                                                Toast.LENGTH_SHORT).show();
                                        Toast.makeText(myContext, "You have successfully Logged in", Toast.LENGTH_SHORT).show();
//                                        SharedPreferences prefs = getSharedPreferences(MainActivity.MySharedPrefs, Context.MODE_PRIVATE);
//                                        prefs.edit().putBoolean( "firststart", false );


                                        prefs = getSharedPreferences(MainActivity.MySharedPrefs, Context.MODE_PRIVATE);
                                        prefs.edit().putBoolean( "firststart", false );
                                        prefs.edit().commit();

                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }

            }
        });

        TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

        if(myAuth.getCurrentUser() != null){

//            SharedPreferences prefs = getSharedPreferences(MainActivity.MySharedPrefs, Context.MODE_PRIVATE);
//            prefs.edit().putBoolean( "firststart", false );
//            //editor.putBoolean( "firststart", false );
//            LoginActivity.this.finish();
            finish();
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void FirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        myAuth = FirebaseAuth.getInstance();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(myAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myAuthListener != null) {
            myAuth.removeAuthStateListener(myAuthListener);
        }
    }
}
