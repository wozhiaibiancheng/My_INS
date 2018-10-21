package au.edu.unimelb.student.group55.my_ins.BlueTooth.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import au.edu.unimelb.student.group55.my_ins.BlueTooth.util.BluetoothUtil;
import au.edu.unimelb.student.group55.my_ins.R;


public class NameActivity extends AppCompatActivity {

    EditText editText;

    String mDevicename="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bt2_activity_name);

        editText = (EditText) findViewById(R.id.name);
        if (getIntent()!=null){
            editText.setText(mDevicename=getIntent().getStringExtra("name"));
        }



        Button button = (Button) findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothUtil.mBluetoothAdapter().setName(mDevicename);
                finish();
            }
        });
    }
}
