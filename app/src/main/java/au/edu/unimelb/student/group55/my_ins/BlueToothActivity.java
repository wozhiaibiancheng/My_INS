package au.edu.unimelb.student.group55.my_ins.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import au.edu.unimelb.student.group55.my_ins.Discovery.DiscoverActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import au.edu.unimelb.student.group55.my_ins.SupportFunctions.BottomNavTool;

import java.util.ArrayList;
import java.util.List;

public class BlueToothActivity extends AppCompatActivity {
    private static final int ACTIVITY_NUM = 5;

    final String TAG = "BlueToothActivity";

    TabLayout tabLayout;
    ViewPager viewPager;
    MyPagerAdapter pagerAdapter;
    String[] titleList=new String[]{"Device List","Date Trans"};
    List<Fragment> fragmentList=new ArrayList<>();

    DeviceList deviceList;
    DataTrans dataTrans;

    BluetoothAdapter bluetoothAdapter;

    Handler uiHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case ParaValues.MSG_REV_A_CLIENT:
                    Log.e(TAG,"uihandler set device name, go to data frag");
                    BluetoothDevice clientDevice = (BluetoothDevice) msg.obj;
                    dataTrans.receiveClient(clientDevice);
                    viewPager.setCurrentItem(1);
                    break;
                case ParaValues.Message_TO_Server:
//                    Log.e(TAG,"--------- uihandler set device name, go to data frag");
                    BluetoothDevice serverDevice = (BluetoothDevice) msg.obj;
                    dataTrans.connectServer(serverDevice);
                    viewPager.setCurrentItem(1);
                    break;
                case ParaValues.MSG_SERVER_REV_NEW:
                    String newMsgFromClient = msg.obj.toString();
                    dataTrans.updateDataView(newMsgFromClient, ParaValues.REMOTE);
                    break;
                case ParaValues.Message_Client_New:
                    String newMsgFromServer = msg.obj.toString();
                    dataTrans.updateDataView(newMsgFromServer, ParaValues.REMOTE);
                    break;
                case ParaValues.MSG_WRITE_DATA:
                    String dataSend = msg.obj.toString();
                    dataTrans.updateDataView(dataSend, ParaValues.MYSELF);
                    deviceList.writeData(dataSend);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        setBottom();
        initUI();
    }
    private void setBottom(){
        Log.d(TAG,"bottom view setting");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom);
        BottomNavTool.setBottomNav(bottomNavigationViewEx);
        BottomNavTool.enableNav(BlueToothActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    public Handler getUiHandler(){
        return uiHandler;
    }


    private void initUI() {
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        viewPager= (ViewPager) findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText(titleList[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titleList[1]));

        deviceList =new DeviceList();
        dataTrans =new DataTrans();
        fragmentList.add( deviceList);
        fragmentList.add(dataTrans);

        pagerAdapter=new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList[position];
        }
    }


    public void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}

