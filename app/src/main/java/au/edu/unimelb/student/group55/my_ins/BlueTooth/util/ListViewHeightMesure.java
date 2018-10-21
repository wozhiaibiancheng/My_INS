package au.edu.unimelb.student.group55.my_ins.BlueTooth.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class ListViewHeightMesure {



    public static void setAdapterHeight(ListView listView){

        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0,j =listAdapter.getCount(); i < j ; i++) {
            View listItem = listAdapter.getView(i , null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height = totalHeight + listView.getPaddingBottom()
                + listView.getPaddingTop()
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));


        listView.setLayoutParams(params);
    }
}
