package au.edu.unimelb.student.group55.my_ins.Home;

//import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

//import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;

import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;


public class HomeActivity extends AppCompatActivity {

    public static final String MySharedPrefs = "MyPrefs";

    private static final String TAG = "Home Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"on create");
        setContentView(R.layout.activity_main);
        setBottom();
//        setPager();

    }

//    add camera and home tab in nav bar
    private void setPager(){
        SectionAdapter sectionAdapter = new SectionAdapter(getSupportFragmentManager());
//        sectionAdapter.addFragment(new CameraFragment());
//        sectionAdapter.addFragment(new HomeFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.body);
        viewPager.setAdapter(sectionAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
    }

    //    set up bottom view
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(HomeActivity.this,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }


}


