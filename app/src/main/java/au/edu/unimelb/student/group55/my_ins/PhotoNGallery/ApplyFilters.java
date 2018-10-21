package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import au.edu.unimelb.student.group55.my_ins.Profile.EditProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


// This class allows user to either select a photo from library or take a photo with camera
// User can crop their image and apply filters on the image
// After these procedures, the brightness and contrast of the image can also be changed
// the final image is stored in the external storage and the path of the image is passed to photo upload service

public class ApplyFilters extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 12;
    private ImageView imageView;
    private Bitmap selectedImage;
    private String FILE_NAME = "/test.jpg";
    public static final String WORKING_DIRECTORY = "MyINS/test.jpg";
    private String imagePath;

    private EditText userInputEditText;
    private String postMessage;

    public String cropPath;
    private static final String TAG = "Photo Activity";

    private LocationManager lm;
    LocationListener locationListener;

    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        checkGPSSettings();
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
                        photoUploadService.putExtra( "longitude", longitude );
                        photoUploadService.putExtra( "latitude", latitude );
                        startService( photoUploadService );
                        finish();
                    }
                }else {
                    Intent intent = new Intent( ApplyFilters.this, EditProfileActivity.class );
                    intent.putExtra( "profilePicPath", imagePath );
                    Log.d(TAG,"imagePath: " + imagePath);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void checkGPSSettings() {
        lm = (LocationManager) getSystemService( Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onLocationChanged(Location location) {
                double mlongitude = location.getLongitude();
                double mlatitude = location.getLatitude();

                longitude = String.valueOf( mlongitude );
                latitude = String.valueOf( mlatitude );
            }
        };




        boolean GPSEnabled = lm.isProviderEnabled( LocationManager.GPS_PROVIDER);

        String[] permissionsArray = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (GPSEnabled) {
            // Android 6.0+
            if (Build.VERSION.SDK_INT >= 23) {
                if (!checkPermissions(this, permissionsArray)) {
                    // request code 1
                    ActivityCompat.requestPermissions(this, permissionsArray,
                            1);
                } else {
                    // Permission has already been granted
                    startLocalisation();
                }
            } else {
                // no runtime check
                startLocalisation();
            }
        } else {
            Toast.makeText(this, "GPS Not Enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            // request code 2
            startActivityForResult(intent, 2);
        }
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void startLocalisation() {

        // parameters of location service
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // cellular or WIFI network can localise me
        String providerNET = LocationManager.NETWORK_PROVIDER;

        // gps signal often naive
        String providerGPS = LocationManager.GPS_PROVIDER;


        // must call this before using getLastKnownLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }

        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled) {
            lm.requestLocationUpdates(providerGPS, 0, 0, locationListener);
            gps_loc = lm.getLastKnownLocation(providerGPS);
        }
        if (network_enabled){
            lm.requestLocationUpdates(providerNET, 0, 0, locationListener);
            net_loc = lm.getLastKnownLocation(providerNET);
        }

        if (gps_loc != null && net_loc != null) {
            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        if (finalLoc != null) {

            double mlatitude = finalLoc.getLatitude();
            double mlongitude = finalLoc.getLongitude();

            longitude = String.valueOf( mlongitude );
            latitude = String.valueOf( mlatitude );

        } else {
        }

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