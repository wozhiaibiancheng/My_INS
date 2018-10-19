//package au.edu.unimelb.student.group55.my_ins.BlueTooth;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.content.Intent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BlueToothController {
//    private BluetoothAdapter mAdapter;
//    public BlueToothController(){
//        mAdapter=BluetoothAdapter.getDefaultAdapter();
//    }
//
//    /**
//     * judge if this phone support bluetooth function
//     * @return
//     */
//    public boolean isSupportBlueTooth(){
//        if(mAdapter!=null){
//            return true;
//        }
//        else
//            return false;
//    }
//    public boolean getBlueToothState(){
//        assert (mAdapter!=null);
//        return mAdapter.isEnabled();
//
//    }
//    public void enableVisibly(Context context){
//        Intent discoverIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,500);
//        context.startActivity(discoverIntent);
//
//    }
//    public void findDevice(){
//        assert (mAdapter!=null);
//        mAdapter.startDiscovery();
//    }
//    public List<BluetoothDevice> getBonderDeviceList(){
//        return new ArrayList<>(mAdapter.getBondedDevices());
//    }
//    public  void  turnOnBlueTooth(Activity activity,int requestCode){
//        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        activity.startActivityForResult(intent,requestCode);
////        mAdapter.enable();
//    }
//
//
//    public void turnOffBlueTooth() {
//        mAdapter.disable();
//    }
//}
