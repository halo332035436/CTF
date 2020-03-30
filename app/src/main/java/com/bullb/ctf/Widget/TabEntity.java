package com.bullb.ctf.Widget;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * Created by oscarlaw on 19/10/2016.
 */

public class TabEntity implements CustomTabEntity {
    public String title;
    public int selectedIcon;
    public int unSelectedIcon;

    public TabEntity(String title) {
        this.title = title;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}