package au.edu.unimelb.student.group55.my_ins.ActivityFeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import au.edu.unimelb.student.group55.my_ins.Firebase.ActivityFollowing;
import au.edu.unimelb.student.group55.my_ins.Firebase.UserAccountSetting;
import au.edu.unimelb.student.group55.my_ins.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingAdapter extends ArrayAdapter<ActivityFollowing> {

    private LayoutInflater myInflater;
    private int layoutResource;
    private Context myContext;

    public FollowingAdapter(@NonNull Context context, int resource, @NonNull List<ActivityFollowing> objects) {
        super( context, resource, objects );

        myInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        myContext = context;
        layoutResource = resource;

    }

    private static class ViewHolder{
        TextView username, isFollowing, anotherUsername;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final FollowingAdapter.ViewHolder holder;
        final ImageLoader imageLoader = ImageLoader.getInstance();

        if(convertView == null){
            convertView = myInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.profileImage = (CircleImageView) convertView.findViewById( R.id.profile_photo);
            holder.username = (TextView) convertView.findViewById( R.id.username );
            holder.isFollowing = (TextView) convertView.findViewById( R.id.is_following);
            holder.anotherUsername = (TextView) convertView.findViewById( R.id.another_username );

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String followerID = getItem( position ).getFollowerID();
        String followedID = getItem( position ).getFollowID();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child( myContext.getString(R.string.dbname_user_account_settings))
                .orderByChild( "user_id")
                .equalTo(followedID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    // currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    //The event if user name is being clicked
                    holder.username.setText( singleSnapshot.getValue( UserAccountSetting.class ).getUsername() );
                    holder.username.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent( myContext, ProfileActivity.class);
//                            intent.putExtra( "calling activity",
//                                    "Home Activity");
//                            intent.putExtra( "intent user", holder.username);
//                            myContext.startActivity(intent);
                        }
                    } );

                    //The event when the user profile image is clicked
                    imageLoader.displayImage( singleSnapshot.getValue( UserAccountSetting.class ).getProfile_pic(),
                            holder.profileImage );
                    holder.profileImage.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent( myContext, ProfileActivity.class);
//                            intent.putExtra( "calling activity",
//                                    "Home Activity");
//                            intent.putExtra( "intent user", holder.user);
//                            myContext.startActivity(intent);
                        }
                    } );



                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query2 = reference
                .child( myContext.getString(R.string.dbname_user_account_settings))
                .orderByChild( "user_id")
                .equalTo(followerID);

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    // currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    //The event if user name is being clicked
                    holder.anotherUsername.setText( singleSnapshot.getValue( UserAccountSetting.class ).getUsername() );
                    holder.anotherUsername.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent( myContext, ProfileActivity.class);
//                            intent.putExtra( "calling activity",
//                                    "Home Activity");
//                            intent.putExtra( "intent user", holder.username);
//                            myContext.startActivity(intent);
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
