package au.edu.unimelb.student.group55.my_ins.Home;

//import android.content.SharedPreferences;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

//import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;

import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.UniversalImageLoader;

// The home activity displays user posts in a list view
// Contains two fragments
// The post in one fragment is ordered by time, the other is ordered by location
public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 12;
    private static final String TAG = "Home Activity";

    private Context mContext = HomeActivity.this;

    //firebase
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;

    private static final int HOME_FRAGMENT = 1;

    //widgets
    private ViewPager myViewPager;
    private FrameLayout myFrameLayout;
    private RelativeLayout myRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"on create");
        setContentView(R.layout.activity_main);

        myViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        myFrameLayout = (FrameLayout) findViewById(R.id.container);
        myRelativeLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        setupFirebaseAuth();

        initImageLoader();
        setBottom();
        requestPermission();

    }

    // Request user permission to access location information
    public void requestPermission() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding

            if (ContextCompat.checkSelfPermission( this,
                    Manifest.permission.ACCESS_COARSE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d( "TAG", "Whatever2" );

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions( this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST );
            } else if (ContextCompat.checkSelfPermission( this,
                    Manifest.permission.ACCESS_FINE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions( this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST );
            }
        }
    }

    public void onCommentThreadSelected(PhotoInformation photoInformation){

        CommentFragment fragment  = new CommentFragment();
        Bundle args = new Bundle();
        args.putParcelable("photo", photoInformation);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("View Comments");
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        myRelativeLayout.setVisibility( View.GONE);
        myFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        myRelativeLayout.setVisibility(View.VISIBLE);
        myFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }


    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    //   home tab in nav bar
    private void setPager(){
        SectionAdapter sectionAdapter = new SectionAdapter(getSupportFragmentManager());
        sectionAdapter.addFragment(new HomeFragment());
        sectionAdapter.addFragment( new HomeFragmentWithLocation() );

        myViewPager.setAdapter(sectionAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(myViewPager);


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_location);
    }

    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(HomeActivity.this,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    // Check the firebase authentication
    private void setupFirebaseAuth(){

        myAuth = FirebaseAuth.getInstance();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
//                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myViewPager.setCurrentItem(HOME_FRAGMENT);
//                    checkCurrentUser(myAuth.getCurrentUser());
                    setPager();

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(myAuthListener);
//        myViewPager.setCurrentItem(HOME_FRAGMENT);
//        checkCurrentUser(myAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myAuthListener != null) {
            myAuth.removeAuthStateListener(myAuthListener);
        }
    }


}


