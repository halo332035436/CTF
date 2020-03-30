package com.bullb.ctf.Model;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class LandingMenuItem implements Serializable {
    public int menuIcon;
    public String menuTitle;
    public boolean enable;

    public LandingMenuItem(int menuIcon, String menuTitle, boolean enable){
        this.menuIcon = menuIcon;
        this.menuTitle = menuTitle;
        this.enable = enable;
    }
}
