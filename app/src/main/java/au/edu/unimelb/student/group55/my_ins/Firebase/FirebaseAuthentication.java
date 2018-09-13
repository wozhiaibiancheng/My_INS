package au.edu.unimelb.student.group55.my_ins.Firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import au.edu.unimelb.student.group55.my_ins.R;

public class FirebaseAuthentication {


    private static final String TAG = "FirebaseMethods";

    //firebase
    private com.google.firebase.auth.FirebaseAuth myAuth;
    private com.google.firebase.auth.FirebaseAuth.AuthStateListener myAuthListener;
    private String userID;

    private Context myContext;

    public FirebaseAuthentication(Context context) {
        myAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        myContext = context;

        if(myAuth.getCurrentUser() != null){
            userID = myAuth.getCurrentUser().getUid();
        }
    }

    public void registerNewEmail(final String email, String password, final String username){
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(myContext, R.string.fail_to_auth,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            userID = myAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }

}
