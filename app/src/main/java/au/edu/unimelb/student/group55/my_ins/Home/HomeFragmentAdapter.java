package au.edu.unimelb.student.group55.my_ins.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityLikes;
import au.edu.unimelb.student.group55.my_ins.Firebase.Like;
import au.edu.unimelb.student.group55.my_ins.Firebase.Comment;
import au.edu.unimelb.student.group55.my_ins.Firebase.PhotoInformation;
import au.edu.unimelb.student.group55.my_ins.Firebase.User;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ViewProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.Heart;
import  au.edu.unimelb.student.group55.my_ins.SupportFunctions.SquareImageView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentAdapter extends ArrayAdapter<PhotoInformation>{

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final String TAG = "HomeFragmentAdapter";

    private LayoutInflater myInflater;
    private int myLayoutResource;
    private Context myContext;
    private DatabaseReference myReference;
    private String currentUsername = "";
    private String likeID;

    public HomeFragmentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PhotoInformation> objects) {
        super(context, resource, objects);
        myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myLayoutResource = resource;
        this.myContext = context;
        myReference = FirebaseDatabase.getInstance().getReference();
    }

    static class ViewHolder{
        CircleImageView myProfileImage;
        String likesString;
        TextView username, time, postMessage, likes, comments;
        SquareImageView image;
        ImageView heartRed, heartWhite, comment;

        UserAccountSetting settings = new UserAccountSetting();
        User user  = new User();
        StringBuilder users;
        String myLikesString;
        boolean likeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        PhotoInformation photo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        int ct = 0;

        if(convertView == null){
            convertView = myInflater.inflate( myLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById( R.id.username);
            holder.image = (SquareImageView) convertView.findViewById(R.id.post_image);
            holder.heartRed = (ImageView) convertView.findViewById(R.id.image_heart_red);
            holder.heartWhite = (ImageView) convertView.findViewById(R.id.image_heart);
            holder.comment = (ImageView) convertView.findViewById(R.id.speech_bubble);
            holder.likes = (TextView) convertView.findViewById(R.id.image_likes);
            holder.comments = (TextView) convertView.findViewById(R.id.image_comments_link);
            holder.postMessage = (TextView) convertView.findViewById(R.id.image_caption);
            holder.time = (TextView) convertView.findViewById(R.id.image_time_posted);
            holder.myProfileImage = (CircleImageView) convertView.findViewById(R.id.profile_photo);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.photo = getItem(position);
        holder.detector = new GestureDetector( myContext, new GestureListener(holder));
        holder.users = new StringBuilder();
        holder.heart = new Heart(holder.heartWhite, holder.heartRed);

        //get the current users username (need for checking likes string)
        getCurrentUsername();

        //get likes string
        getLikesString(holder);

        //set the postMessage
        holder.postMessage.setText(getItem(position).getPostMessage());

        //set the comment
        List<Comment> comments = getItem(position).getComments();
        holder.comments.setText("View all " + comments.size() + " comments");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) myContext).onCommentThreadSelected(getItem(position));

                //going to need to do something else?
                ((HomeActivity) myContext).hideLayout();

            }
        });

        //set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.time.setText(timestampDifference + " Days Ago");
        }else{
            holder.time.setText("Today");
        }

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImageUrl(), holder.image);


        //get the profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child( myContext.getString(R.string.dbname_user_account_settings))
                .orderByChild( "user_id")
                .equalTo(getItem(position).getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    //The event if user name is being clicked
                    holder.username.setText(singleSnapshot.getValue(UserAccountSetting.class).getUsername());
//                    holder.username.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent( myContext, ViewProfileActivity.class);
//                            intent.putExtra( "calling activity",
//                                    "Home Activity");
//                            intent.putExtra( "intent user", holder.user);
//                            myContext.startActivity(intent);
//                        }
//                    });

                    //The event when the user profile image is clicked
                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSetting.class).getProfile_pic(),
                            holder.myProfileImage );
//                    holder.myProfileImage.setOnClickListener( new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent( myContext, ViewProfileActivity.class);
//                            intent.putExtra( "calling activity",
//                                    "Home Activity");
//                            intent.putExtra( "intent user", holder.user);
//                            myContext.startActivity(intent);
//                        }
//                    });



                    holder.settings = singleSnapshot.getValue(UserAccountSetting.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                ((HomeActivity) myContext).onCommentThreadSelected(getItem(position));
//                                        "Home Activity");

                                //another thing?
                                ((HomeActivity) myContext).hideLayout();
                            }catch (Exception e){
                               Log.d(TAG,e.getMessage());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the user object
        Query userQuery = myReference
                .child( "users")
                .orderByChild( "user_id")
                .equalTo(getItem(position).getUserID());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(User.class).getUsername());

                    holder.user = singleSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }

        return convertView;
    }

    private boolean reachedEndOfList(int position){
        return position == getCount() - 1;
    }

    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        ViewHolder mHolder;
        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child( "posts")
                    .child( mHolder.photo.getUserID() )
                    .child(mHolder.photo.getPhotoID())
                    .child( "likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();
                        System.out.println("like by Current user?? " + mHolder.likeByCurrentUser);
                        //case1: Then user already liked the photo
                        if(mHolder.likeByCurrentUser){


                            myReference.child("posts")
                                    .child(mHolder.photo.getUserID())
                                    .child(mHolder.photo.getPhotoID())
                                    .child( "likes")
                                    .child(keyID)
                                    .removeValue();

                            try {
                                myReference.child("activity")
                                        .child("likes")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(likeID)
                                        .removeValue();
                            }catch (Exception e){
                                System.out.println("error: " + e.getMessage());
                            }

                            mHolder.heart.toggleLike();
                            getLikesString(mHolder);
                        }
                        //case2: The user has not liked the photo
                        else if(!mHolder.likeByCurrentUser){
                            //add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike(mHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike(final ViewHolder holder){

        String newLikeID = myReference.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myReference.child( "posts")
                .child(holder.photo.getUserID())
                .child(holder.photo.getPhotoID())
                .child( "likes")
                .child(newLikeID)
                .setValue(like);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String currentDate = String.valueOf( simpleDateFormat.format( calendar.getTime() ) );

        likeID = myReference.child( "activity" ).child( "likes" ).child(uid).push().getKey();

        ActivityLikes activityLikes = new ActivityLikes(  );
        activityLikes.setImageUrl( holder.photo.getImageUrl() );
        activityLikes.setPosterID( holder.photo.getUserID() );
        activityLikes.setLikerID( uid );
        activityLikes.setDateLiked( currentDate );

        myReference.child( "activity" )
                .child( "likes" )
                .child( uid )
                .child( likeID )
                .setValue( activityLikes );

        holder.heart.toggleLike();
        getLikesString(holder);
    }

    private void getCurrentUsername(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child( myContext.getString(R.string.dbname_users))
                .orderByChild( "user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserAccountSetting.class).getUsername();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLikesString(final ViewHolder viewHolder){

        try{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child( "posts")
                    .child( viewHolder.photo.getUserID() )
                    .child(viewHolder.photo.getPhotoID())
                    .child( "likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final Set<String> likedUsers = new HashSet();

                    viewHolder.users = new StringBuilder();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child( "users")
                                .orderByChild( "user_id")
                                .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                    Log.d(TAG, "onDataChange: found like: " +
                                            singleSnapshot.getValue(User.class).getUsername());

                                            likedUsers.add(singleSnapshot.getValue(User.class).getUsername());

                                    viewHolder.users.append(singleSnapshot.getValue(User.class).getUsername());
                                    viewHolder.users.append(",");
                                }


                                String[] splitUsers = viewHolder.users.toString().split(",");

                                if(likedUsers.contains(currentUsername)){
//                                if(viewHolder.users.toString().contains(viewHolder.user.getUsername() + ",")){
                                    viewHolder.likeByCurrentUser = true;
                                }else{
                                    viewHolder.likeByCurrentUser = false;
                                }

                                int length = splitUsers.length;
                                if(length == 1){
                                    viewHolder.likesString = "Liked by " + splitUsers[0];
                                }
                                else if(length == 2){
                                    viewHolder.likesString = "Liked by " + splitUsers[0]
                                            + " and " + splitUsers[1];
                                }
                                else if(length == 3){
                                    viewHolder.likesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + " and " + splitUsers[2];

                                }
                                else if(length == 4){
                                    viewHolder.likesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + ", " + splitUsers[2]
                                            + " and " + splitUsers[3];
                                }
                                else if(length > 4){
                                    viewHolder.likesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + ", " + splitUsers[2]
                                            + " and " + (splitUsers.length - 3) + " others";
                                }
                                Log.d(TAG, "onDataChange: likes string: " + viewHolder.likesString);
                                //setup likes string
                                setupLikesString(viewHolder, viewHolder.likesString);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        viewHolder.likesString = "";
                        viewHolder.likeByCurrentUser = false;
                        //setup likes string
                        setupLikesString(viewHolder, viewHolder.likesString);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            //If no one liked this post at present, show an empty message
            viewHolder.likesString = "No likes at present";
            viewHolder.likeByCurrentUser = false;
            //setup likes string
            setupLikesString(viewHolder, viewHolder.likesString);
        }
    }

    private void setupLikesString(final ViewHolder viewHolder, String likesString){
        //If myself liked this post
        if(viewHolder.likeByCurrentUser){

            viewHolder.heartWhite.setVisibility( View.GONE);
            viewHolder.heartRed.setVisibility(View.VISIBLE);
            viewHolder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return viewHolder.detector.onTouchEvent(event);
                }
            });
        }
        //Otherwise if I didn't like this post
        else{
            viewHolder.heartWhite.setVisibility(View.VISIBLE);
            viewHolder.heartRed.setVisibility(View.GONE);
            viewHolder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return viewHolder.detector.onTouchEvent(event);
                }
            });
        }
        viewHolder.likes.setText(likesString);
    }

    // Get how much time ago does this post being posted
    private String getTimestampDifference(PhotoInformation photoInformation){
        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
//        sdf.setTimeZone( TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photoInformation.getDateCreated();
        try{
            // Here we only care about how many days ago this post is posted
            // because it is easier to implement
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / (1000 * 60 * 60 * 24))));
        }catch (ParseException e){
            difference = "0";
        }
        return difference;
    }
}
