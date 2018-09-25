package au.edu.unimelb.student.group55.my_ins.PhotoNLibrary;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import au.edu.unimelb.student.group55.my_ins.Home.CameraFragment;
import au.edu.unimelb.student.group55.my_ins.Home.HomeFragment;
import au.edu.unimelb.student.group55.my_ins.Home.SectionAdapter;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.Utils.bottomNavTool;

public class BottomDialog extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "BottomDialog Activity";

    Dialog mCameraDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_main);

        setPager();
        setBottom();
        setDialog();
    }

    //    add camera and home tab in nav bar
    private void setPager(){
        SectionAdapter sectionAdapter = new SectionAdapter(getSupportFragmentManager());
        sectionAdapter.addFragment(new CameraFragment());
        sectionAdapter.addFragment(new HomeFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.body);
        viewPager.setAdapter(sectionAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
    }

    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        bottomNavTool.setBottomNav(bottomNavigationViewEx);
        bottomNavTool.enableNav(BottomDialog.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choose_img:
                Intent chooseImage = new Intent( this, PhotoLibraryActivity.class );
                chooseImage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( chooseImage );
                break;
            case R.id.btn_open_camera:
                Intent openCamera = new Intent( this, PhotoLibraryActivity.class );
                openCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity( openCamera );
                break;
            case R.id.btn_cancel:
                mCameraDialog.cancel();
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
