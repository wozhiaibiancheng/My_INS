package au.edu.unimelb.student.group55.my_ins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import au.edu.unimelb.student.group55.my_ins.LoginNRegister.LoginActivity;

public class MainActivity extends AppCompatActivity {

    public static final String MySharedPrefs = "MyPrefs";
    SharedPreferences sharedPrefs;
    //private Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        //setContentView(R.layout.login_layout);
        sharedPrefs = this.getSharedPreferences(MySharedPrefs, Context.MODE_PRIVATE);

        if(sharedPrefs.getBoolean("firststart", true)) {

//            final SharedPreferences.Editor editor = sharedPrefs.edit();
//            editor.putBoolean( "firststart", false );

            Intent intent = new Intent( this, LoginActivity.class );
            startActivity( intent );
            //finish();
            startActivity(getIntent());

        }
//        else{
//            setContentView(R.layout.activity_main);
//        }




    }
}
