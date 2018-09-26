package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import au.edu.unimelb.student.group55.my_ins.R;

public class BottomDialog extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "BottomDialog Activity";


    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    Dialog mCameraDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_main);

//        if(checkPermissionsArray( Permissions.PERMISSIONS)){
//            //setupViewPager();s
//            setDialog();
//        }else{
//            verifyPermissions(Permissions.PERMISSIONS);
//        }
        setDialog();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choose_img:
                Intent chooseImage = new Intent( this, PhotoGalleryActivity.class );
                //chooseImage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( chooseImage );
                break;
            case R.id.btn_open_camera:
                Intent openCamera = new Intent( this, TakingPhotoActivity.class );
                //openCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( openCamera );
                break;
            case R.id.btn_cancel:
                mCameraDialog.cancel();
                finish();
                break;
        }
    }

    private void setDialog() {
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f;
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }
}
