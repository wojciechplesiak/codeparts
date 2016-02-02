package com.example.plainviews;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {


    public static final int FIRST_TAB_INDEX = 0;
    public static final int SECOND_TAB_INDEX = 1;
    public static final int THIRD_TAB_INDEX = 2;
    public static final int FORTH_TAB_INDEX = 3;

    private TabLayout mTabLayout;
    private Menu mMenu;
  //  private RtlViewPager mViewPager;


  //  private TabsAdapter mTabsAdapter;
    private int mSelectedTab;
    private boolean mActivityResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);

    }

    private void createTabs() {

    }
}




