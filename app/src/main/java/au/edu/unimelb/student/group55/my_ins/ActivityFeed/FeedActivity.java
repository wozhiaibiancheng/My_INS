package au.edu.unimelb.student.group55.my_ins.ActivityFeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import au.edu.unimelb.student.group55.my_ins.Firebase.FirebaseMethods;
import au.edu.unimelb.student.group55.my_ins.Home.HomeFragmentAdapter;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

public class FeedActivity extends AppCompatActivity  {

    private static final String TAG = "FeedActivity";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Context context;
    private ImageView bluetooth;


    private TextView followingView, likesView, postsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_feed );

        context = FeedActivity.this;

        followingView = (TextView) findViewById( R.id.feed_following_item );
        likesView = (TextView) findViewById( R.id.feed_likes_item );
        postsView = (TextView) findViewById( R.id.feed_posts_item );
        bluetooth = (ImageView)findViewById(R.id.bluetooth);

        FirebaseAuth();
        setBottom();
        activityNavigation();

        GestureDetector detector;
        detector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
//        bluetooth.setOnDragListener(bluetooth);
    }


    private void activityNavigation(){
        followingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followingFeed = new Intent( FeedActivity.this, FeedActivityFollowing.class );
                startActivity( followingFeed );
            }
        });

        likesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent likesFeed = new Intent( FeedActivity.this, FeedActivityLikes.class );
                startActivity( likesFeed );
            }
        });

        postsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postsFeed = new Intent( FeedActivity.this, FeedActivityPosts.class );
                startActivity( postsFeed );
            }
        });

    }


    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(FeedActivity.this,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }



    private class simpleGestureListener extends
            GestureDetector.SimpleOnGestureListener {

        /*****OnGestureListener的函数*****/
        public boolean onDown(MotionEvent e) {
            Log.i("MyGesture", "onDown");
            Toast.makeText(context, "onDown", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        public void onShowPress(MotionEvent e) {
            Log.i("MyGesture", "onShowPress");
//            Toast.makeText(context, "onShowPress", Toast.LENGTH_SHORT)
//                    .show();
        }

        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("MyGesture", "onSingleTapUp");
//            Toast.makeText(MainActivity.this, "onSingleTapUp",
//                    Toast.LENGTH_SHORT).show();
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("MyGesture", "onScroll:" + (e2.getX() - e1.getX()) + "   "
                    + distanceX);
//            Toast.makeText(MainActivity.this, "onScroll", Toast.LENGTH_LONG)
//                    .show();

            return true;
        }

        public void onLongPress(MotionEvent e) {
            Log.i("MyGesture", "onLongPress");
//            Toast.makeText(MainActivity.this, "onLongPress", Toast.LENGTH_LONG)
//                    .show();
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.i("MyGesture", "onFling");
            Toast.makeText(context, "onFling", Toast.LENGTH_LONG)
                    .show();
            return true;
        }
    }












        // Always check the login state of the user
    private void FirebaseAuth() {

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                } else {
                    // If we detect no user login information, navigate to the Login screen
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }



}
