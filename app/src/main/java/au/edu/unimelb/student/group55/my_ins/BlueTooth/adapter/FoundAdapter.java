package au.edu.unimelb.student.group55.my_ins.BlueTooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import au.edu.unimelb.student.group55.my_ins.BlueTooth.entity.DatasEntity;
import au.edu.unimelb.student.group55.my_ins.R;


public class FoundAdapter extends BaseAdapter {
    private Context context;
    private BluetoothDevice device;
    public FoundAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return DatasEntity.mBluetoothDevices.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return DatasEntity.mBluetoothDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (null == view){
            view = LayoutInflater.from(context).inflate(R.layout.bt2_itemlistview,null);
            holderView =new HolderView(view);
            view.setTag(holderView);
        }else{
            holderView = (HolderView) view.getTag();
        }
        device = DatasEntity.mBluetoothDevices.get(i);
        switch (device.getType()){
            case 1:
                holderView.imageView.setImageResource(R.drawable.home_title_android);
                break;

            case 2:
                holderView.imageView.setImageResource(R.drawable.home_title_qian);
                break;

            case 3:
                holderView.imageView.setImageResource(R.drawable.home_title_ios);
                break;
            default:
                holderView.imageView.setImageResource(R.drawable.home_title_qian);
                break;
        }
        if (device.getName()==null||device.getName().equals("null")){
            holderView.textView.setText(device.getAddress());
        }else{
            holderView.textView.setText(device.getName()+"  "+device.getAddress());
        }

        return view;
    }
    HolderView holderView;
    private class HolderView{
        ImageView imageView;

        TextView textView;

        public HolderView(View view) {
            this.imageView = view.findViewById(R.id.devices_img);
            this.textView = view.findViewById(R.id.address_tv);
        }
    }


}
