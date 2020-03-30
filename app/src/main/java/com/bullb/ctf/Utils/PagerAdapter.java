package com.bullb.ctf.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bullb.ctf.API.Response.MyTargetPageResponse;

import java.util.List;

/**
 * Created by oscar on 10/5/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private MyTargetPageResponse myTarget;

    public PagerAdapter(FragmentManager fm, List<Fragment> fragments){
        super(fm);
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
