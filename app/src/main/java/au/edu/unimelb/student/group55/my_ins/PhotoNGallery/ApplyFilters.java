package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import au.edu.unimelb.student.group55.my_ins.Profile.EditProfileActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;


// This class allows user to either select a photo from library or take a photo with camera
// User can crop their image and apply filters on the image
// After these procedures, the brightness and contrast of the image can also be changed
// the final image is stored in the external storage and the path of the image is passed to photo upload service

public class ApplyFilters extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap selectedImage;
    private String FILE_NAME = "/test.jpg";
    public static final String WORKING_DIRECTORY = "MyINS/test.jpg";
    private String imagePath;

    private EditText userInputEditText;
    private String postMessage;

    public String cropPath;
    private static final String TAG = "Photo Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
//        String tempPath = applicationFolder();
//        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
//        String format = s.format(new Date());
//        Toast.makeText(this, currentDate, Toast.LENGTH_SHORT).show();
//        imagePath = tempPath + "/" + format + ".jpg";
        imagePath = applicationFolder();

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }
    public void notice(){
        Toast.makeText(this, "Please say something for your post", Toast.LENGTH_SHORT).show();
    }


    public String applicationFolder(){
        //change the result image name to ensure image is stored properly
        String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "test.jpg";
        return temp;
    }

    public void startImageFilter(String sourcePath, String destinationPath){
//        String workingDirectory = path + filename;
        EditImageActivity.start(this, sourcePath, destinationPath, 100, true);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the result image path from image crop procedure

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    cropPath = resultUri.getPath();
                    // After having cropped the image, start the next procedure
                    // start to add filters etc.
                    startImageFilter( cropPath, imagePath );
                }
                catch(Exception e){
                    Toast.makeText(ApplyFilters.this, "failed to read image data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            else{
                finish();
            }
        }

        // After having finished the filters & brightness contrast setting
        // return the image data to image view
        if (requestCode == 100) { // same code you used while starting

            // Set the corresponding factors of the library class
            String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //Read the image to bitmap from storage
            if(isImageEdit == false){
                selectedImage = BitmapFactory.decodeFile(cropPath, options);
                imagePath = cropPath;
            }
            else{
                selectedImage = BitmapFactory.decodeFile(newFilePath, options);
                imagePath = newFilePath;
            }
            writePosts( selectedImage );
        }

    }

    public void writePosts(Bitmap selectedImage){
        setContentView( R.layout.photo_gallery );
        imageView = (ImageView) findViewById(R.id.imageView);
        // show the image in the corresponding image View
        imageView.setImageBitmap( selectedImage );

        userInputEditText = (EditText) findViewById(R.id.post_message);
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
                if(isRootTask()){
                    postMessage = userInputEditText.getText().toString();
                    if (postMessage.matches("")) {
                        notice();
                        return;
                    }

                    if(postMessage == ""){
                        Toast.makeText(ApplyFilters.this, "Please say something about your post~", Toast.LENGTH_SHORT).show();
                    }else{


                        // Upload the image in the background to avoid stop front-end UI
                        Intent photoUploadService = new Intent( ApplyFilters.this, PhotoUploadService.class );
                        photoUploadService.putExtra( "file path", imagePath );
                        photoUploadService.putExtra( "post message", postMessage );
                        startService( photoUploadService );
                        finish();
                    }
                }else {
                    Intent intent = new Intent( ApplyFilters.this, EditProfileActivity.class );
                    intent.putExtra( "profilePicPath", imagePath );
                    Log.d(TAG,"imagePath: " + imagePath);
//                    photoUploadService.putExtra( "post message", postMessage );
//                    startService( photoUploadService );
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean isRootTask(){
        int task = getIntent().getFlags();
        Log.d(TAG,"Flag task: " + task);
        if(task == 0){
            return true;
        }else{
            return false;
        }
    }

}
