package au.edu.unimelb.student.group55.my_ins.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.Firebase.Comment;

/**
 * Created by User on 5/28/2017.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<PhotoInformation> myPhotoInformations;
    private ArrayList<PhotoInformation> myPaginatedPhotos;
    private ArrayList<String> myFollowing;
    private ListView myListView;
    private HomeFragmentAdapter myAdapter;
    private int myResults;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.home_fragment, container, false);
        myListView = (ListView) view.findViewById(R.id.listView);
        myFollowing = new ArrayList<>();
        myPhotoInformations = new ArrayList<>();

        getFollowing();

        return view;
    }

    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    myFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
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

//                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            //                                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

//                            mUserList.add(singleSnapshot.getValue(User.class));
//                        }



//                        PhotoInformation photoInformation = new PhotoInformation();
//                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                        photoInformation.setPostMessage(objectMap.get(getString(R.string.post_message)).toString());
//                        photoInformation.setPhotoID(objectMap.get(getString(R.string.field_photo_id)).toString());
//                        photoInformation.setUserID(objectMap.get(getString(R.string.field_user_id)).toString());
//                        photoInformation.setDateCreated(objectMap.get(getString(R.string.field_date_created)).toString());
//                        photoInformation.setImageUrl(objectMap.get(getString(R.string.field_image_path)).toString());

//                        ArrayList<Comment> comments = new ArrayList<Comment>();
//                        for (DataSnapshot dSnapshot : singleSnapshot
//                                .child("comments").getChildren()){
//                            Comment comment = new Comment();
//                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                            comments.add(comment);
//                        }

//                        photoInformation.setComments(comments);
                        myPhotoInformations.add(singleSnapshot.getValue(PhotoInformation.class));
                    }
                    if(count >= following.size() - 1){
                        //display our photos
                        displayPhotos();
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

                if(iterations > 10){
                    iterations = 10;
                }

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

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(myPhotoInformations.size() > myResults && myPhotoInformations.size() > 0){

                int iterations;
                if(myPhotoInformations.size() > (myResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = myPhotoInformations.size() - myResults;
                }

                //add the new photos to the paginated results
                for(int i = myResults; i < myResults + iterations; i++){
                    myPaginatedPhotos.add( myPhotoInformations.get(i));
                }
                myResults = myResults + iterations;
                myAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

}





















