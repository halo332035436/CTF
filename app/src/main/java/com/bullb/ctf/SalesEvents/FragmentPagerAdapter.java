package com.bullb.ctf.SalesEvents;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedUtils;

import java.util.ArrayList;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    int PAGE_COUNT = 4;
    private String tabTitles[];
    private Context context;
    private ArrayList<PageFragment> pageFragments = new ArrayList<>();
    private PageFragment currentFragment;

    public FragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        pageFragments.add(PageFragment.newInstance(1, Campaign.District));
        pageFragments.add(PageFragment.newInstance(2, Campaign.A));
        pageFragments.add(PageFragment.newInstance(3, Campaign.F));
        pageFragments.add(PageFragment.newInstance(4, Campaign.E));
        if(ServerPreference.getServerVersion(this.context).equals(ServerPreference.SERVER_VERSION_HK)) {
            PAGE_COUNT = 5;
            pageFragments.add(PageFragment.newInstance(5, Campaign.M));
        }
    }

    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (PageFragment)object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return pageFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        if(SharedUtils.serverIsHongKong(context)){
            tabTitles = new String[] {context.getString(R.string.integrate), context.getString(R.string.a_type) ,context.getString(R.string.f_type), context.getString(R.string.e_type) ,context.getString(R.string.m_type)};
        }else{
            tabTitles = new String[] {context.getString(R.string.district), context.getString(R.string.a_type) ,context.getString(R.string.f_type), context.getString(R.string.e_type) ,context.getString(R.string.m_type)};
        }
        return tabTitles[position];
    }
}