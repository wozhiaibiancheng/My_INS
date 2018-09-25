package au.edu.unimelb.student.group55.my_ins.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import au.edu.unimelb.student.group55.my_ins.Discovery.DiscoverActivity;
import au.edu.unimelb.student.group55.my_ins.Feed.FeedActivity;
import au.edu.unimelb.student.group55.my_ins.Home.HomeActivity;
import au.edu.unimelb.student.group55.my_ins.PhotoNLibrary.BottomDialog;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.Upload.UploadActivity;

public class bottomNavTool {
    private static final String TAG = "bottomNavTool activity";

    public static void setBottomNav(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG,"setting nav view");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNav(final Context context, BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        Intent home = new Intent(context, HomeActivity.class);
                        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(home);
                        break;
                    case R.id.ic_search:
                        Intent discovery = new Intent(context, DiscoverActivity.class);
                        discovery.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(discovery);
                        break;
                    case R.id.ic_add:
                        Intent bottomDialog = new Intent(context, BottomDialog.class);
                        bottomDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity( bottomDialog );
//                        Intent upload = new Intent(context, UploadActivity.class);
//                        context.startActivity(upload);
                        break;
                    case R.id.ic_like:
                        Intent feed = new Intent(context, FeedActivity.class);
                        feed.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(feed);
                        break;
                    case R.id.ic_person:
                        Intent profile = new Intent(context, ProfileActivity.class);
                        profile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(profile);
                        break;
                }
                return false;
            }
        });
    }

}
