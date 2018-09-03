package au.edu.unimelb.student.group55.my_ins;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPrefs;
    private Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        //setContentView(R.layout.login_layout);
        sharedPrefs = this.getPreferences(MODE_PRIVATE);

        if(sharedPrefs.getBoolean("firststart", true)) {

            final SharedPreferences.Editor editor = sharedPrefs.edit();
            setContentView(R.layout.login_layout);
            login_button = (Button) findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putBoolean("firststart", false);
                    editor.commit();
                    setContentView(R.layout.activity_main);
                }
            });

        }
        else{
            setContentView(R.layout.activity_main);
        }


    }
}
