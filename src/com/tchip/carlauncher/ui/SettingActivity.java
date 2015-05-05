package com.tchip.carlauncher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.CacheFragmentStatePagerAdapter;
import com.tchip.carlauncher.bean.ObservableScrollViewCallbacks;
import com.tchip.carlauncher.bean.ScrollState;
import com.tchip.carlauncher.util.ScrollUtils;
import com.tchip.carlauncher.view.ObservableScrollView;
import com.tchip.carlauncher.view.SlidingTabLayout;
import com.tchip.carlauncher.view.ViewPagerTabScrollViewFragment;

/**
 * SlidingTabLayout and SlidingTabStrip are from google/iosched:
 * https://github.com/google/iosched
 */
public class SettingActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private View mHeaderView;
    private View mToolbarView;
    private int mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, 4); // getResources().getDimension(R.dimen.toolbar_elevation)
        mToolbarView = findViewById(R.id.toolbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        // When the page is selected, other fragments' scrollY should be adjusted
        // according to the toolbar status(shown/hidden)
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                propagateToolbarState(toolbarIsShown());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        propagateToolbarState(toolbarIsShown());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }

        int toolbarHeight = mToolbarView.getHeight();
        final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        int scrollY = scrollView.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (toolbarIsShown() || toolbarIsHidden()) {
                // Toolbar is completely moved, so just keep its state
                // and propagate it to other pages
                propagateToolbarState(toolbarIsShown());
            } else {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private void propagateToolbarState(boolean isShown) {
        int toolbarHeight = mToolbarView.getHeight();

        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(isShown ? 0 : toolbarHeight);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            Fragment f = mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            ObservableScrollView scrollView = (ObservableScrollView) f.getView().findViewById(R.id.scroll);
            if (isShown) {
                // Scroll up
                if (0 < scrollView.getCurrentScrollY()) {
                    scrollView.scrollTo(0, 0);
                }
            } else {
                // Scroll down (to hide padding)
                if (scrollView.getCurrentScrollY() < toolbarHeight) {
                    scrollView.scrollTo(0, toolbarHeight);
                }
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }
    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
        propagateToolbarState(false);
    }

    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"Applepie", "Butter Cookie", "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop"};

        private int mScrollY;

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        protected Fragment createItem(int position) {
            Fragment f = new ViewPagerTabScrollViewFragment();
            if (0 <= mScrollY) {
                Bundle args = new Bundle();
                args.putInt(ViewPagerTabScrollViewFragment.ARG_SCROLL_Y, mScrollY);
                f.setArguments(args);
            }
            return f;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}





















//import java.util.ArrayList;
//import java.util.List;
//
//import com.tchip.carlauncher.R;
//import com.tchip.carlauncher.view.MyViewPager;
//import com.tchip.carlauncher.view.MyViewPagerContainer;
//import com.tchip.carlauncher.view.MyViewPager.TransitionEffect;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.view.PagerAdapter;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//
//public class SettingActivity extends Activity {
//	private View viewMain, viewVice;
//	private List<View> viewList;
//	private MyViewPager viewPager;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_setting);
//		
//		
//		LayoutInflater inflater = getLayoutInflater().from(this);
//		viewMain = inflater.inflate(R.layout.activity_setting_one, null);
//		viewVice = inflater.inflate(R.layout.activity_setting_two, null);
//
//		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
//		viewList.add(viewMain);
//		viewList.add(viewVice);
//		viewPager = (MyViewPager) findViewById(R.id.viewpager);
//		viewPager.setTransitionEffect(TransitionEffect.CubeOut);
//
//		viewPager.setPageMargin(10);
//		viewPager.setAdapter(pagerAdapter);
//	}
//	
//	PagerAdapter pagerAdapter = new PagerAdapter() {
//
//		@Override
//		public boolean isViewFromObject(View view, Object obj) {
//			if (view instanceof MyViewPagerContainer) {
//				return ((MyViewPagerContainer) view).getChildAt(0) == obj;
//			} else {
//				return view == obj;
//			}
//		}
//
//		@Override
//		public int getCount() {
//			return viewList.size();
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView(viewList.get(position));
//		}
//
//		@Override
//		public int getItemPosition(Object object) {
//			return super.getItemPosition(object);
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			container.addView(viewList.get(position));
//			viewPager.setObjectForPosition(viewList.get(position), position); // 动画需要
//			if (position == 0)
//				updateMainLayout();
//			else
//				updateViceLayout();
//			return viewList.get(position);
//		}
//
//	};
//	
//	private void updateMainLayout(){
//		
//	}
//	
//	private void updateViceLayout(){
//		
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		View decorView = getWindow().getDecorView();
//		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//	}
//
//}
