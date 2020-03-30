package com.bullb.ctf.PerformanceEnquiry.TypeA;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.R;
import com.bullb.ctf.SalesEvents.PageFragment;
import com.bullb.ctf.ServerPreference;

import java.util.ArrayList;

public class PerformancePagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private String tabTitles[];
    private Context context;
    private ArrayList<PerformanceDetailFragment> pageFragments = new ArrayList<>();

    public PerformancePagerAdapter(FragmentManager fm, Context context, String fromDate, String toDate, int currentpos, String pageType) {
        super(fm);
        this.context = context;
        pageFragments.add(PerformanceDetailFragment.newInstance(0,fromDate, toDate, currentpos, pageType));
        pageFragments.add(PerformanceDetailFragment.newInstance(1,fromDate, toDate,currentpos,pageType));
        pageFragments.add(PerformanceDetailFragment.newInstance(2,fromDate, toDate,currentpos,pageType));
        pageFragments.add(PerformanceDetailFragment.newInstance(3,fromDate, toDate,currentpos,pageType));
        if (ServerPreference.getServerVersion(context).equals(ServerPreference.SERVER_VERSION_HK)) {
            pageFragments.add(PerformanceDetailFragment.newInstance(4,fromDate, toDate,currentpos,pageType));
        }
    }

    @Override
    public int getCount() {
        return pageFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return pageFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        tabTitles = new String[] {context.getString(R.string.all), context.getString(R.string.A_cat) ,context.getString(R.string.F_cat), context.getString(R.string.E_cat), context.getString(R.string.M_cat)};
        return tabTitles[position];
    }
}