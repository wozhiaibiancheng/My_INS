package au.edu.unimelb.student.group55.my_ins.BlueTooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import au.edu.unimelb.student.group55.my_ins.BlueTooth.adapter.FoundAdapter;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.adapter.PairedAdapter;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.broadcast.BTBroadcastReceiver;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.entity.DatasEntity;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.BluetoothUtil;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.Comment;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.DialogUtil;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.ListViewHeightMesure;
import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.ToastUtil;
import au.edu.unimelb.student.group55.my_ins.R;

public class BlueToothMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch btnSearch;
    private TextView mMobileName;
    private LinearLayout swipe;
    private ListView mLvallDevices;
    private ListView mLvPairedDevices;
    private LinearLayout switch_ll;
    private ImageView rotate_img;
    private LinearLayout name_ll;
    //adapter
    private PairedAdapter mPairedAdapter;
    private FoundAdapter mGetarrayAdapter;
    //bluetooth
    private BTBroadcastReceiver receiver;
    private Animation rotate;
    private TextView nonebound_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt2_activity_main);
        findViewById();

        reswipeAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new BTBroadcastReceiver(mHandler);
        registerReceiver(receiver, BluetoothUtil.makeFilters());
        reswipeAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BluetoothUtil.mBluetoothAdapter().isDiscovering()) {
            BluetoothUtil.mBluetoothAdapter().cancelDiscovery();
            setTitle("bluetooth");
            rotate_img.clearAnimation();
        }
        unregisterReceiver(receiver);
    }


    private void findViewById() {
        swipe = (LinearLayout) findViewById(R.id.swipe);
        rotate_img = (ImageView) findViewById(R.id.rotate_img);
        swipe.setOnClickListener(this);
        btnSearch = (Switch) findViewById(R.id.btnSearch);
        mMobileName = (TextView) findViewById(R.id.mobilename);
        mLvPairedDevices = (ListView) findViewById(R.id.alreadyDevices);
        mLvallDevices = (ListView) findViewById(R.id.allDevices);

        switch_ll = (LinearLayout) findViewById(R.id.switch_ll);
        switch_ll.setOnClickListener(this);
        name_ll = (LinearLayout) findViewById(R.id.name_ll);
        name_ll.setOnClickListener(this);


        mPairedAdapter = new PairedAdapter(this);
        mLvPairedDevices.setAdapter(mPairedAdapter);
        mLvPairedDevices.setOnItemClickListener(paireItemListener);

        mGetarrayAdapter = new FoundAdapter(this);
        mLvallDevices.setAdapter(mGetarrayAdapter);
        mLvallDevices.setOnItemClickListener(allItemListener);
        rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        nonebound_tv = (TextView) findViewById(R.id.nonebound_tv);
    }


    private ListView.OnItemClickListener allItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Comment.bluetoothDevice = mGetarrayAdapter.getItem(i);
            if (BluetoothUtil.mBluetoothAdapter().isDiscovering()) {
                BluetoothUtil.mBluetoothAdapter().cancelDiscovery();
                setTitle("bluetooth");
                rotate_img.clearAnimation();
            }
            BluetoothUtil.connectBound();
        }
    };

    private ListView.OnItemClickListener paireItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Comment.bluetoothDevice = mPairedAdapter.getItem(i);
            if (BluetoothUtil.mBluetoothAdapter().isDiscovering()) {
                BluetoothUtil.mBluetoothAdapter().cancelDiscovery();
                setTitle("bluetooth");
                rotate_img.clearAnimation();
            }
            startActivity(new Intent(BlueToothMainActivity.this, BluetoothManngerActivity.class));
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_ll:
                BluetoothUtil.switchBluetooth(this);
                break;
            case R.id.swipe:
                clearAdapter();
                reswipeAdapter();
                break;
            case R.id.name_ll:
                startActivity(new Intent(BlueToothMainActivity.this, NameActivity.class).putExtra("name", BluetoothUtil.mBluetoothAdapter().getName()));
                break;
        }
    }



    private void reswipeAdapter() {
        if (BluetoothUtil.mBluetoothAdapter() == null) {
            ToastUtil.showShort(this, "Local bluetooth is not available");
            return;
        }
        if (BluetoothUtil.isOpenBluetooth()) {
            btnSearch.setChecked(true);
            setTitle("Bluetooth device search...");
            rotate_img.startAnimation(rotate);
            BluetoothUtil.startGetBound();
            mPairedAdapter.notifyDataSetChanged();
            ListViewHeightMesure.setAdapterHeight(mLvPairedDevices);
        }
        mMobileName.setText(BluetoothUtil.mBluetoothAdapter().getName() == null ? "Unknown devices" : BluetoothUtil.mBluetoothAdapter().getName());
    }


    private void clearAdapter() {
        DatasEntity.mPairedDevices.clear();
        DatasEntity.mBluetoothDevices.clear();
        mPairedAdapter.notifyDataSetChanged();
        mGetarrayAdapter.notifyDataSetChanged();
        ListViewHeightMesure.setAdapterHeight(mLvallDevices);
        ListViewHeightMesure.setAdapterHeight(mLvPairedDevices);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Comment.SWITCH:
                    switch ((int) msg.obj) {
                        case BluetoothAdapter.STATE_OFF:
                            nonebound_tv.setText("enable devices(" + DatasEntity.mBluetoothDevices.size() + ")");
                            btnSearch.setChecked(false);
                            clearAdapter();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            reswipeAdapter();
                            break;
                    }
                    break;
                case Comment.FOUND:
                    DatasEntity.mBluetoothDevices.add((BluetoothDevice) msg.obj);
                    nonebound_tv.setText("enable devices(" + DatasEntity.mBluetoothDevices.size() + ")");
                    mGetarrayAdapter.notifyDataSetChanged();
                    ListViewHeightMesure.setAdapterHeight(mLvallDevices);
                    break;

                case Comment.FINISHED:
                    setTitle("bluetooth");
                    rotate_img.clearAnimation();
                    break;

                case Comment.BOND:
                    switch ((int) msg.obj) {
                        case BluetoothDevice.BOND_BONDING:
                            DialogUtil.ShowProgress(BlueToothMainActivity.this, "In the match...");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            DialogUtil.CancelProgress();
                            clearAdapter();
                            reswipeAdapter();
                            ToastUtil.showShort(BlueToothMainActivity.this,"Connecting...");
                            BluetoothUtil.connectSocket(mHandler);
                            break;
                        case BluetoothDevice.BOND_NONE:
                            ToastUtil.showShort(BlueToothMainActivity.this, "Canceling connection");
                            break;
                    }
                    break;
                case Comment.CONNECT:
                    ToastUtil.showShort(BlueToothMainActivity.this,"Connection Successfully！");
                    break;

            }
        }
    };

}
