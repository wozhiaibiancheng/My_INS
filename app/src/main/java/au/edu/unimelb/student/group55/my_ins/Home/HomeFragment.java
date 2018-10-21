package au.edu.unimelb.student.group55.my_ins.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.Firebase.Comment;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<PhotoInformation> myPhotoInformations;
    private ArrayList<PhotoInformation> myPaginatedPhotos;
    private ArrayList<String> myFollowing;
    private ListView myListView;
    private HomeFragmentAdapter myAdapter;
    private ProgressBar progressBar;
    private int myResults;
    //firebase
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.home_fragment, container, false);
        setupFirebaseAuth();
        myListView = (ListView) view.findViewById(R.id.listView);
        myFollowing = new ArrayList<>();
        myPhotoInformations = new ArrayList<>();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        getFollowing();

        return view;
    }

    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        clearAll();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child("user_id").getValue());

                    myFollowing.add(singleSnapshot.child("user_id").getValue().toString());
                }
                // Return list of user ID to myFollowing list
                myFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d(TAG, "onDataChange: found user: " +
                        myFollowing);
                //get the photos
                getPhotos(myFollowing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void clearAll(){
        if(myFollowing != null){
            myFollowing.clear();
        }
        if(myPhotoInformations != null){
            myPhotoInformations.clear();
            if(myAdapter != null){
                myAdapter.clear();
                myAdapter.notifyDataSetChanged();
            }
        }

        if(myPaginatedPhotos != null){
            myPaginatedPhotos.clear();
        }

        myFollowing = new ArrayList<>();
        myPhotoInformations = new ArrayList<>();
        myPaginatedPhotos = new ArrayList<>();
    }

    private void getPhotos(ArrayList<String> myFollowing){
        Log.d(TAG, "getPhotos: getting photos");

        final ArrayList<String > following = myFollowing;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for(int i = 0; i < following.size(); i++){
            final int count = i;
            Query query = reference
                    .child("posts")
                    .child(following.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        System.out.println("Invoked here successfully");

                        PhotoInformation photoInformation = new PhotoInformation();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photoInformation.setPostMessage(objectMap.get("postMessage").toString());
                        photoInformation.setPhotoID(objectMap.get("photoID").toString());
                        photoInformation.setUserID(objectMap.get("userID").toString());
                        photoInformation.setDateCreated(objectMap.get("dateCreated").toString());
                        photoInformation.setImageUrl(objectMap.get("imageUrl").toString());
                        photoInformation.setLongitude( objectMap.get( "longitude" ).toString() );
                        photoInformation.setLatitude( objectMap.get( "latitude" ).toString() );

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("comments").getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photoInformation.setComments(comments);
                        myPhotoInformations.add(photoInformation);
                    }
                    if(count >= following.size() - 1){
                        //display our photos
                        displayPhotos();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos(){
        myPaginatedPhotos = new ArrayList<>();
        if(myPhotoInformations != null){
            try{
                Collections.sort( myPhotoInformations, new Comparator<PhotoInformation>() {
                    @Override
                    public int compare(PhotoInformation o1, PhotoInformation o2) {
                        return o2.getDateCreated().compareTo(o1.getDateCreated());
                    }
                });

                int iterations = myPhotoInformations.size();

                myResults = 10;
                for(int i = 0; i < iterations; i++){
                    myPaginatedPhotos.add( myPhotoInformations.get(i));
                }

                myAdapter = new HomeFragmentAdapter(getActivity(), R.layout.home_fragment_cells, myPaginatedPhotos );
                myListView.setAdapter( myAdapter );

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }


    private void setupFirebaseAuth(){

        myAuth = FirebaseAuth.getInstance();

        myAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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





















