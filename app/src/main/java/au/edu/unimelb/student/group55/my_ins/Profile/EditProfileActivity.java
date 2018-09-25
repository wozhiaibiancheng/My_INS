package au.edu.unimelb.student.group55.my_ins.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import au.edu.unimelb.student.group55.my_ins.R;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfile Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        Log.d(TAG, "OnCreate: Started");
    }

    private void setUpSettingList(){
        Log.d(TAG,"set up edit profile setting list");

    }
}
