package au.edu.unimelb.student.group55.my_ins.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.Utils.UniversalImageLoader;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfile Activity";
    private ImageView profilePic;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        Log.d(TAG, "OnCreate: Started");
        setProfilePic();

//        click cancel to go back to profile page
        TextView cancel = (TextView) findViewById(R.id.edit_profile_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked cancel");
                EditProfileActivity.this.finish();
            }
        });

    }

    private void setUpSettingList() {
        Log.d(TAG, "set up edit profile setting list");

    }

    private void setProfilePic() {
        Log.d(TAG, "set profile pic");
        String imgURL = "https://artinsights.com/wp-content/uploads/2013/11/20120919143022.jpg";
        UniversalImageLoader.setImage(imgURL, profilePic, null, "");

    }
}


