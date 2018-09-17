package au.edu.unimelb.student.group55.my_ins.Home;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionAdapter extends FragmentPagerAdapter {
    private static final String TAG = "SectionAdapter";

    private final List<Fragment> fragmentList = new ArrayList<>();

    public SectionAdapter(FragmentManager fm){
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

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }


}
