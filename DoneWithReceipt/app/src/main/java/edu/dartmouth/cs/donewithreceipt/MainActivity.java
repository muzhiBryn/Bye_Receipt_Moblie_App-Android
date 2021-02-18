package edu.dartmouth.cs.donewithreceipt;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity.lifecycle";
    public static final int CONFIRM_FOR_RESULT = 1;

    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private BottomNavAdapter myViewPageAdapter;
    BottomNavigationView navigation;
    HomeFragment mHomeFragment;
    HistoryFragment mHistoryFragment;
    ChartsFragment mChartsFragment;
    MenuItem mConfirmBtn;

    // Helper objects for detecting taps and pinches.
    public ScaleGestureDetector scaleGestureDetector;
    public GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.main_viewpager);
        navigation = findViewById(R.id.bottom_nav_menu);

        //create a fragment list in order
        fragments = new ArrayList<>();
        mHomeFragment = HomeFragment.newInstance();
        mHistoryFragment = HistoryFragment.newInstance();
        mChartsFragment = ChartsFragment.newInstance();
        fragments.add(mHomeFragment);
        fragments.add(mHistoryFragment);
        fragments.add(mChartsFragment);

        //use FragmentPagerAdapter to bind the TabLayout (tabs with different titles)
        //and ViewPager (different pages of fragment) together.
        myViewPageAdapter = new BottomNavAdapter(getSupportFragmentManager(), fragments);

        // add the PagerAdapter to the viewPager
        viewPager.setAdapter(myViewPageAdapter);

        //底部navigation bar切换
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    //switch to home fragment
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        if (mConfirmBtn != null) {
                            mConfirmBtn.setVisible(true);
                        }
                        return true;
                    //switch to history fragment
                    case R.id.navigation_history:
                        viewPager.setCurrentItem(1);
                        if (mConfirmBtn != null) {
                            mConfirmBtn.setVisible(false);
                        }
                        return true;
                        //switch to charts fragment
                    case R.id.navigation_charts:
                        viewPager.setCurrentItem(2);
                        if (mConfirmBtn != null) {
                            mConfirmBtn.setVisible(false);
                        }
                        return true;
                }
                return false;
            }
        });
        Log.d(TAG, "onCreate()");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //create an action bar button右上角的下拉列表
    //The onCreate method is called first, and before it finishes onCreateOptionsMenu is called.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        mConfirmBtn = menu.findItem(R.id.confirm);
        return super.onCreateOptionsMenu(menu);
    }

    public void menuConfirmClicked(MenuItem item){
        if (mHomeFragment.historyEntry != null) {
            Intent intent = new Intent(this, ConfirmActivity.class);
            intent.putExtra(ConfirmActivity.ENTRY_TO_CONFIRM_KEY, mHomeFragment.historyEntry);
            //mHomeFragment是一个newInstance的方法，historyEntry是mHomeFragment里的一个public的object，
            // 这里不能使用HomeFragment.historyEntry，切记哟。
            //一个object是由一个类出来的，一个类变量
            //一个类中的static方法是属于类的变量/方法，可直接被整个类所调用，也可被该类的单独object调用
            startActivityForResult(intent, CONFIRM_FOR_RESULT);
        }
        else {
            Toast.makeText(this, "Please scan the date, store and amount first.", Toast.LENGTH_SHORT);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIRM_FOR_RESULT && resultCode == RESULT_OK) {
            // switch to history
            navigation.setSelectedItemId(R.id.navigation_history);
        }
    }


    //内部类
    private class BottomNavAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;

        public BottomNavAdapter(FragmentManager mgr, ArrayList<Fragment> fragments){
            super(mgr);
            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public Map<String, Double> calculatePercentage(){
        Map<String, Double> result = new HashMap<>();
        double grocery_total = 0;
        double restaurant_total = 0;
        double gas_total = 0;
        double other_total = 0;
        double department_store = 0;
        result.put("Grocery", grocery_total);
        result.put("Restaurant", restaurant_total);
        result.put("Gas", gas_total);
        result.put("Other", other_total);
        result.put("Department Store", department_store);

        List<HistoryEntry> allEntries = mHistoryFragment.getAllEntriesData();
        if (allEntries != null) {
            for (HistoryEntry entry : allEntries){
                if (entry.getStoreName().toLowerCase().startsWith("cvspharmacy")||entry.getStoreName().toLowerCase().startsWith("hanaford")||
                        entry.getStoreName().toLowerCase().startsWith("dartmouth co-op")||entry.getStoreName().toLowerCase().startsWith("price chopper")||
                        entry.getStoreName().toLowerCase().startsWith("bjs")){
                    grocery_total += entry.getSubtotal();
                    result.put("Grocery", grocery_total);
                }
                else if (entry.getStoreName().toLowerCase().startsWith("gas")||entry.getStoreName().toLowerCase().startsWith("oil")){
                    gas_total += entry.getSubtotal();
                    result.put("Gas", gas_total);
                }
                else if (entry.getStoreName().toLowerCase().startsWith("kohls")||entry.getStoreName().toLowerCase().startsWith("t.j.maxx")||entry.getStoreName().toLowerCase().startsWith("jcpenny")){
                    department_store += entry.getSubtotal();
                    result.put("Department Store", department_store);
                }
                else if (entry.getStoreName().toLowerCase().startsWith("kfc")||entry.getStoreName().toLowerCase().startsWith("mcdona")||entry.getStoreName().toLowerCase().startsWith("domino")){
                    restaurant_total += entry.getSubtotal();
                    result.put("Restaurant", restaurant_total);
                }
                else{
                    other_total += entry.getSubtotal();
                    result.put("Other", other_total);
                }
            }
        }

        double subTotal = result.get("Grocery") + result.get("Restaurant") + result.get("Gas") +
                result.get("Department Store") + result.get("Other");
        result.put("Grocery", grocery_total/subTotal);
        result.put("Restaurant", restaurant_total/subTotal);
        result.put("Gas", gas_total/subTotal);
        result.put("Department Store", department_store/subTotal);
        result.put("Other", other_total/subTotal);

        return result;
    }

}
