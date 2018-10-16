


package au.edu.unimelb.student.group55.my_ins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import au.edu.unimelb.student.group55.my_ins.Home.CameraFragment;
import au.edu.unimelb.student.group55.my_ins.Home.HomeFragment;
import au.edu.unimelb.student.group55.my_ins.Home.PlaceHolder;
import au.edu.unimelb.student.group55.my_ins.Home.SectionAdapter;
import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.UniversalImageLoader;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

public class MainActivity extends AppCompatActivity {

    public static final String MySharedPrefs = "MyPrefs";
    private Context context = MainActivity.this;
    SharedPreferences sharedPrefs;
    //private Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoader();
        setContentView(R.layout.activity_main);
        setBottom();
        setPager();
        //setContentView(R.layout.login_layout);
        sharedPrefs = this.getSharedPreferences(MySharedPrefs, Context.MODE_PRIVATE);

        if(sharedPrefs.getBoolean("firststart", true)) {

//            final SharedPreferences.Editor editor = sharedPrefs.edit();
//            editor.putBoolean( "firststart", false );

            Intent intent_login = new Intent( this, LoginActivity.class );
            startActivity( intent_login );
            //finish();
            startActivity(getIntent());

        }
    }

    //    set up bottom view
    private void setBottom(){
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(MainActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }


    //    add camera and home tab in nav bar
    private void setPager(){
        SectionAdapter sectionAdapter = new SectionAdapter(getSupportFragmentManager());
        sectionAdapter.addFragment(new CameraFragment());
        sectionAdapter.addFragment(new HomeFragment());
        sectionAdapter.addFragment(new PlaceHolder());

        ViewPager viewPager = (ViewPager)findViewById(R.id.body);
        viewPager.setAdapter(sectionAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ins);
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

}

