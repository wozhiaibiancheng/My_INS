package au.edu.unimelb.student.group55.my_ins.BlueTooth;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import au.edu.unimelb.student.group55.my_ins.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DeviceList extends Fragment {
    final String TAG = "DeviceListFragment";

    ListView listView;
    MyListAdapter listAdapter;
    List<BluetoothDevice> deviceList = new ArrayList<>();

    BluetoothAdapter bluetoothAdapter;
    MyBtReceiver btReceiver;
    IntentFilter intentFilter;

    BlueToothActivity blueToothActivity;
    Handler uiHandler;

    ClientBlueTooth clientThread;
    ServerBlueTooth serverThread;

    public void onRequestPermissionsResult(int rqCode, String permissions[], int[] grantResults) {

        switch (rqCode) {
            case ParaValues.MY_PERMISSION_REQUEST_CONSTANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {

            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bt_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.device_list_view);
        listAdapter = new MyListAdapter();
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        blueToothActivity = (BlueToothActivity) getActivity();
        uiHandler = blueToothActivity.getUiHandler();

    }

    @Override
    public void onResume() {
        super.onResume();


        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnBtIntent, ParaValues.REQUEST_ENABLE_BT);
        }

        intentFilter = new IntentFilter();
        btReceiver = new MyBtReceiver();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(btReceiver, intentFilter);


        if (bluetoothAdapter.isEnabled()) {
            showBondDevice();

            if (serverThread != null) {
                serverThread.cancel();
            }
            serverThread = new ServerBlueTooth(bluetoothAdapter, uiHandler);
            new Thread(serverThread).start();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (serverThread != null) {
                    serverThread.cancel();
                    serverThread=null;
                }
                BluetoothDevice device = deviceList.get(position);

                clientThread = new ClientBlueTooth(bluetoothAdapter, device, uiHandler);
                new Thread(clientThread).start();


                Message message = new Message();
                message.what = ParaValues.Message_TO_Server;
                message.obj = device;
                uiHandler.sendMessage(message);

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(btReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bt_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_visibility:
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
                startActivityForResult(enableIntent, ParaValues.REQUEST_ENABLE_VISIBILITY);
                break;
            case R.id.discovery:
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                if (Build.VERSION.SDK_INT >= 6.0) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ParaValues.MY_PERMISSION_REQUEST_CONSTANT);
                }

                bluetoothAdapter.startDiscovery();
                break;
            case R.id.disconnect:
                bluetoothAdapter.disable();
                deviceList.clear();
                listAdapter.notifyDataSetChanged();
                toast("Bluetooth switched off");
                break;
        }
        return super.onOptionsItemSelected(item);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ParaValues.REQUEST_ENABLE_BT: {
                if (resultCode == RESULT_OK) {
                    showBondDevice();
                }
                break;
            }
            case ParaValues.REQUEST_ENABLE_VISIBILITY: {
                if (resultCode == 600) {
                    toast("Bluetooth visible");
                } else if (resultCode == RESULT_CANCELED) {
                    toast("Bluetooth Settings visible failed. Please try again");
                }
                break;
            }
        }
    }

    private void showBondDevice() {
        deviceList.clear();
        Set<BluetoothDevice> tmp = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d :
                tmp) {
            deviceList.add(d);
        }
        listAdapter.notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toast(String str) {
        Toast.makeText(getContext(),str,Toast.LENGTH_SHORT);
    }


    public void writeData(String dataSend) {

        if (serverThread != null) {
            serverThread.write(dataSend);
        } else if (clientThread != null) {
            clientThread.write(dataSend);
        }
    }



    private class MyListAdapter extends BaseAdapter {

        public MyListAdapter() {
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.bt_device, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                viewHolder.deviceMac = (TextView) convertView.findViewById(R.id.device_mac);
                viewHolder.deviceState = (TextView) convertView.findViewById(R.id.device_state);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            int code = deviceList.get(position).getBondState();
            String name = deviceList.get(position).getName();
            String mac = deviceList.get(position).getAddress();
            String state;
            if (name == null || name.length() == 0) {
                name = "Unnamed devices";
            }
            if (code == BluetoothDevice.BOND_BONDED) {
                state = "ready";
                viewHolder.deviceState.setTextColor(getResources().getColor(R.color.black));
            } else {
                state = "new";
                viewHolder.deviceState.setTextColor(getResources().getColor(R.color.grey));
            }
            if (mac == null || mac.length() == 0) {
                mac = "Unknown mac address";
            }
            viewHolder.deviceName.setText(name);
            viewHolder.deviceMac.setText(mac);
            viewHolder.deviceState.setText(state);
            return convertView;
        }

    }


    static class ViewHolder {
        public TextView deviceName;
        public TextView deviceMac;
        public TextView deviceState;
    }


    private class MyBtReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                toast("start search ");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                toast("end search");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isNewDevice(device)) {
                    deviceList.add(device);
                    listAdapter.notifyDataSetChanged();
//                    Log.e(TAG, "---------------- " + device.getName());
                }
            }
        }
    }


    private boolean isNewDevice(BluetoothDevice device){
        boolean repeatFlag = false;
        for (BluetoothDevice d :
                deviceList) {
            if (d.getAddress().equals(device.getAddress())){
                repeatFlag=true;
            }
        }
        if (device.getBondState() != BluetoothDevice.BOND_BONDED && !repeatFlag)
            return true;
        else return false;

//        return device.getBondState() != BluetoothDevice.BOND_BONDED && !repeatFlag;
    }
}
