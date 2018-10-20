package au.edu.unimelb.student.group55.my_ins.ActivityFeed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityLikes;
import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityPosts;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.SquareImageView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FeedPostsAdapter extends ArrayAdapter<ActivityPosts> {

    private LayoutInflater myInflater;
    private int layoutResource;
    private Context myContext;

    public FeedPostsAdapter(@NonNull Context context, int resource, @NonNull List<ActivityPosts> objects) {
        super( context, resource, objects );

        myInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        myContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView username, post, time;
        CircleImageView profileImage;
        SquareImageView post_image;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final FeedPostsAdapter.ViewHolder holder;
        final ImageLoader imageLoader = ImageLoader.getInstance();
        final String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (convertView == null) {
            convertView = myInflater.inflate( layoutResource, parent, false );
            holder = new FeedPostsAdapter.ViewHolder();

            holder.profileImage = (CircleImageView) convertView.findViewById( R.id.profile_photo );
            holder.username = (TextView) convertView.findViewById( R.id.username );
            holder.post = (TextView) convertView.findViewById( R.id.post );
            holder.post_image = (SquareImageView) convertView.findViewById( R.id.post_image );
            holder.time = (TextView) convertView.findViewById( R.id.time );

            convertView.setTag( holder );

        } else {
            holder = (FeedPostsAdapter.ViewHolder) convertView.getTag();
        }

        final String userID = getItem( position ).getUserID();
        final String imageUrl = getItem( position ).getImageUrl();
        final String dateCreated = getItem( position ).getDateCreated();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        imageLoader.displayImage( imageUrl, holder.post_image );

        holder.time.setText( dateCreated );

        Query query = reference
                .child( "user_account_settings")
                .orderByChild( "user_id")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    //The event if user name is being clicked
                    holder.username.setText( singleSnapshot.getValue( UserAccountSetting.class ).getUsername() );
                    holder.username.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (userID.equals( currentUID )){
                                Intent myProfileIntent = new Intent(myContext, ProfileActivity.class);
                                myContext.startActivity( myProfileIntent );
                            }else{
//                                Intent intent = new Intent( myContext, ViewProfileActivity.class);
//                                intent.putExtra( "calling activity",
//                                        "FeedActivityFollowing");
//                                intent.putExtra( "intent user", followedUser);
//                                myContext.startActivity(intent);
                            }

                        }
                    } );

                    //The event when the user profile image is clicked
                    imageLoader.displayImage( singleSnapshot.getValue( UserAccountSetting.class ).getProfile_pic(),
                            holder.profileImage );
                    holder.profileImage.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (userID.equals( currentUID )){
                                Intent myProfileIntent = new Intent(myContext, ProfileActivity.class);
                                myContext.startActivity( myProfileIntent );
                            }else{
//                                Intent intent = new Intent( myContext, ViewProfileActivity.class);
//                                intent.putExtra( "calling activity",
//                                        "FeedActivityFollowing");
//                                intent.putExtra( "intent user", followedUser);
//                                myContext.startActivity(intent);
                            }
                        }
                    } );

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return convertView;
    }
}
