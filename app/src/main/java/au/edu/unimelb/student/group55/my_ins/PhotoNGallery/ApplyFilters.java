package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

import au.edu.unimelb.student.group55.my_ins.MainActivity;
import au.edu.unimelb.student.group55.my_ins.R;

public class ApplyFilters extends AppCompatActivity {

    ImageView imageView;
    private Task<Uri> downloadUri;
    String uid;
    private DatabaseReference mDatabase;
    private String downloadLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.photo_gallery );

        imageView = (ImageView) findViewById(R.id.imageView);

        TextView shareClose = (TextView) findViewById(R.id.gallery_cancel);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView nextScreen = (TextView) findViewById(R.id.gallery_next);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ApplyFilters.this, MainActivity.class );
                startActivity( intent );
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();


                String date = DateFormat.getDateTimeInstance().format(new Date());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                }

                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference imagesRef = storage.getReference().child( uid ).child(date + ".jpg");

//                final StorageReference imagesRef = storage.getReference().child( "test.jpg");

                UploadTask uploadTask = imagesRef.putBytes(imageData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ApplyFilters.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri = imagesRef.getDownloadUrl();
                        downloadLink = downloadUri.toString();
                        Toast.makeText(ApplyFilters.this, "Upload image successfully", Toast.LENGTH_SHORT).show();
                    }
                });


                String location = "Location not available";
                String message = "message not available";
                //uid + date + location + message + url +

                String post_message = location + "," + message + "," + downloadLink;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child( uid ).child( date ).setValue( post_message );

//                Intent intentSensorService = new Intent(ApplyFilters.this, PhotoUploadService.class);
//                intentSensorService.putExtra( "resultImage", imageData );
//                startService(intentSensorService);


//                ApplyFilters.this.finish();
            }
        });

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    InputStream imageStream = getContentResolver().openInputStream(resultUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap( selectedImage );
                }
                catch(Exception e){

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
