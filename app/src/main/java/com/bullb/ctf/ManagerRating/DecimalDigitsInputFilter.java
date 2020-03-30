package com.bullb.ctf.ManagerRating;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oscarlaw on 2/11/2016.
 */

public class DecimalDigitsInputFilter implements InputFilter {

     private int maxDigitsBeforeDecimalPoint=4;
     private int maxDigitsAfterDecimalPoint=1;

    public DecimalDigitsInputFilter(int maxDigitsBeforeDecimalPoint,int maxDigitsAfterDecimalPoint) {
        this.maxDigitsAfterDecimalPoint = maxDigitsAfterDecimalPoint;
        this.maxDigitsBeforeDecimalPoint = maxDigitsBeforeDecimalPoint;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source
                .subSequence(start, end).toString());
        if (!builder.toString().matches(
                "(([1-9]{1})([0-9]{0,"+(maxDigitsBeforeDecimalPoint-1)+"})?)?(\\.[0-9]{0,"+maxDigitsAfterDecimalPoint+"})?"

        )) {
            if(source.length()==0)
                return dest.subSequence(dstart, dend);
            return "";
        }

        return null;
    }

}