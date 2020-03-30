package com.bullb.ctf.Model;


import android.content.Context;
import android.util.Log;

import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.annotations.SerializedName;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by oscar on 7/1/16.
 */
public class UserTargetData extends BaseModel{
    private ArrayList<UserTarget> user_targets;



    private double target;
    private double decomposed_target;


    //Target
    private double userAdditionA;
    private double userAdditionF;
    private double userAdditionE;
    private double userAdditionM;


    private double baseA;
    private double baseF;
    private double baseE;
    private double baseM;


    private double defaultBaseA;
    private double defaultBaseF;
    private double defaultBaseE;
    private double defaultBaseM;


    private double managerAdditionA;
    private double managerAdditionF;
    private double managerAdditionE;
    private double managerAdditionM;

    private double roundResult(double num){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_DOWN);
        return Double.parseDouble(df.format(num));
    }

    public double getAdjust() {
        return roundResult(userAdditionA + userAdditionE + userAdditionF + userAdditionM);
    }

    public double getDistributedTarget() {
        return roundResult(getBaseAll() + managerAdditionA + managerAdditionF + managerAdditionE + managerAdditionM);
    }

    public double getAdjustedTarget() {
        return roundResult(getDistributedTarget() + getAdjust());
    }

    public double getTarget() {
        return target;
    }

    public ArrayList<UserTarget> getUserTargets() {
        return user_targets;
    }

    public double getDecomposed_target() {
        return decomposed_target;
    }

    public double getDistributedTarget_A() {
        return roundResult(baseA + managerAdditionA);
    }

    public double getDistributedTarget_F() {
        return roundResult(baseF + managerAdditionF);
    }

    public double getDistributedTarget_E() {
        return roundResult(baseE + managerAdditionE);
    }

    public double getDistributedTarget_M() {
        return roundResult(baseM + managerAdditionM);
    }

    public double getUserAdditionA() {
        return userAdditionA;
    }

    public double getUserAdditionF() {
        return userAdditionF;
    }

    public double getUserAdditionE() {
        return userAdditionE;
    }

    public double getUserAdditionM() {
        return userAdditionM;
    }

    public double getBaseA() {
        return baseA;
    }

    public double getBaseF() {
        return baseF;
    }

    public double getBaseE() {
        return baseE;
    }

    public double getBaseM() {
        return baseM;
    }

    public double getBaseAll() {
        return roundResult(baseA + baseE + baseF + baseM);
    }

    public double getDefaultBaseA() {
        return defaultBaseA;
    }

    public double getDefaultBaseF() {
        return defaultBaseF;
    }

    public double getDefaultBaseE() {
        return defaultBaseE;
    }

    public double getDefaultBaseM() {
        return defaultBaseM;
    }

    public double getMinBaseA() {
        return baseA == 0 ? defaultBaseA : baseA;
    }

    public double getMinBaseF() {
        return baseF == 0 ? defaultBaseF : baseF;
    }

    public double getMinBaseE() {
        return baseE == 0 ? defaultBaseE : baseE;
    }

    public double getMinBaseM() {
        return baseM == 0 ? defaultBaseM : baseM;
    }

    public double getMinBaseAll() { return roundResult(getMinBaseA() + getMinBaseF() + getMinBaseE() + getMinBaseM()); }

    public double getMinDistributedTarget() { return roundResult(getMinBaseAll()+ managerAdditionA + managerAdditionF + managerAdditionE + managerAdditionM); }

    public double getManagerAdditionA() {
        return managerAdditionA;
    }

    public double getManagerAdditionF() {
        return managerAdditionF;
    }

    public double getManagerAdditionE() {
        return managerAdditionE;
    }

    public double getManagerAdditionM() {
        return managerAdditionM;
    }


    public double getBaseOverDistributedRatio() {
        return getBaseAll()*100/ (getDistributedTarget());
    }

    public double getDistributedOverMaxRatio(){
        return getDistributedTarget() * 100 / (getBaseAll()*3);
    }



    public double getFirstRate(Context context, SalesData salesData) {
        User user = SharedPreference.getUser(context);
        double firstRate =0;
        if (salesData !=null) {
            if (user.type.equals(User.USER_TYPE_A)) {
                firstRate = salesData.getTotalSales() * 100 / getDistributedTarget();
            } else if (user.type.equals(User.USER_TYPE_B) || user.type.equals(User.USER_TYPE_C)) {
                firstRate = salesData.getTotalSales() * 100 / target;
            }
        }
        return firstRate;
    }

    public double getSecondRate(Context context, SalesData salesData) {
        User user = SharedPreference.getUser(context);
        double secondRate = 0;
        if (salesData !=null) {
            if (user.type.equals(User.USER_TYPE_A)) {
                secondRate = salesData.getTotalSales() * 100 / (getDistributedTarget() + getAdjust());
            }
            else if (user.type.equals(User.USER_TYPE_B) || user.type.equals(User.USER_TYPE_C)) {
                secondRate = salesData.getTotalSales() * 100 / decomposed_target;
            }
        }
        return secondRate;
    }

    public double getARate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesA()) + " " + String.valueOf(getDistributedTarget_A()));
        return salesData.getSalesA() * 100 / baseA;
    }

    public double getERate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesE()) + " " + String.valueOf(getDistributedTarget_E()));
        return  salesData.getSalesE() * 100 / baseE;
    }

    public double getFRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesF()) + " " + String.valueOf(getDistributedTarget_F()));
        return salesData.getSalesF() * 100 / baseF;
    }

    public double getMRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesM()) + " " + String.valueOf(getDistributedTarget_M()));
        return salesData.getSalesM() * 100 / baseM;
    }

    public double getAllRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getTotalSales()) + " " + String.valueOf(getDistributedTarget()));
        return salesData.getTotalSales() * 100 / getBaseAll();
    }

    public double getDistributedARate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesA()) + " " + String.valueOf(getDistributedTarget_A()));
        return salesData.getSalesA() * 100 / getDistributedTarget_A();
    }

    public double getDistributedERate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesE()) + " " + String.valueOf(getDistributedTarget_E()));
        return  salesData.getSalesE() * 100 / getDistributedTarget_E();
    }

    public double getDistributedFRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesF()) + " " + String.valueOf(getDistributedTarget_F()));
        return salesData.getSalesF() * 100 / getDistributedTarget_F();
    }

    public double getDistributedMRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesF()) + " " + String.valueOf(getDistributedTarget_F()));
        return salesData.getSalesM() * 100 / getDistributedTarget_M();
    }

    public double getDistributedAllRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getTotalSales()) + " " + String.valueOf(getDistributedTarget()));
        return salesData.getTotalSales() * 100 / getDistributedTarget();
    }

    public double getAdjustedARate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesA()) + " " + String.valueOf(getDistributedTarget_A()+ getUserAdditionA()));
        return salesData.getSalesA() * 100 / (getDistributedTarget_A()+ getUserAdditionA());
    }

    public double getAdjustedERate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesE()) + " " + String.valueOf(getDistributedTarget_E() + getUserAdditionE()));
        return  salesData.getSalesE() * 100 / (getDistributedTarget_E()+ getUserAdditionE());
    }

    public double getAdjustedFRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesF()) + " " + String.valueOf(getDistributedTarget_F()+ getUserAdditionF()));
        return  salesData.getSalesF() * 100 / (getDistributedTarget_F()+ getUserAdditionF());
    }

    public double getAdjustedMRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesF()) + " " + String.valueOf(getDistributedTarget_F()+ getUserAdditionF()));
        return  salesData.getSalesM() * 100 / (getDistributedTarget_M()+ getUserAdditionM());
    }

    public double getAdjustedAllRate(SalesData salesData){
//        Log.d("sales", String.valueOf(salesData.getSalesF()) + " " + String.valueOf(getDistributedTarget_F()+ getUserAdditionF()));
        return  salesData.getTotalSales() * 100 / getAdjustedTarget();
    }

    public double getPredictCommission(BonusRate bonusRate){
        return (getDistributedTarget_A()+ getUserAdditionA()) * bonusRate.getA() +
                (getDistributedTarget_F()+ getUserAdditionF()) * bonusRate.getF() +
                (getDistributedTarget_E()+ getUserAdditionE()) * bonusRate.getE() +
                (getDistributedTarget_M()+ getUserAdditionM()) * bonusRate.getM();
    }

    public double getBaseCommission(BonusRate bonusRate){
        return (getDistributedTarget_A() * bonusRate.getA()) +
                (getDistributedTarget_F() * bonusRate.getF()) +
                (getDistributedTarget_E()* bonusRate.getE()) +
                (getDistributedTarget_M()* bonusRate.getM());
    }

    public double getAdditionCommission(BonusRate bonusRate){
        return (getUserAdditionA() * bonusRate.getA()) +
                (getUserAdditionF() * bonusRate.getF()) +
                (getUserAdditionE() * bonusRate.getE()) +
                (getUserAdditionM() * bonusRate.getM());
    }



    public UserTarget getCurrentMonthTarget(String type, String fromDate) {
        if (user_targets != null) {
            for (UserTarget userTarget : user_targets) {
                try {
                    if (userTarget.type.equals(type)) {
                        if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                            return userTarget;
                        }
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setCurrentMonthUserTarget(double adjustA, double adjustF, double adjustE, double adjustM, String fromDate) {
        if (user_targets != null) {
            for (UserTarget userTarget : user_targets) {
                try {
                    if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                        if (userTarget.type.equals(UserTarget.TYPE_A)) {
                            userTarget.user_addition = adjustA;
                        } else if (userTarget.type.equals(UserTarget.TYPE_E)) {
                            userTarget.user_addition = adjustE;
                        } else if (userTarget.type.equals(UserTarget.TYPE_F)) {
                            userTarget.user_addition = adjustF;
                        } else if (userTarget.type.equals(UserTarget.TYPE_M)) {
                            userTarget.user_addition = adjustM;
                        }
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }
    }



    public void calculateTarget(String fromDate) {
        userAdditionA = 0;
        userAdditionE = 0;
        userAdditionF = 0;
        userAdditionM = 0;
        baseA = 0;
        baseE = 0;
        baseF = 0;
        baseM = 0;
        managerAdditionA = 0;
        managerAdditionE = 0;
        managerAdditionF = 0;
        managerAdditionM = 0;

        if (user_targets != null)
            for (UserTarget userTarget : user_targets) {
                try {
                    if (userTarget.type.equals(UserTarget.TYPE_A)) {
                        if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                            userAdditionA += userTarget.user_addition;
                            baseA += userTarget.base_amount;
                            managerAdditionA += userTarget.manager_addition;
                            defaultBaseA += userTarget.default_base_amount;

                        }
                    } else if (userTarget.type.equals(UserTarget.TYPE_F)) {
                        if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                            userAdditionF += userTarget.user_addition;
                            baseF += userTarget.base_amount;
                            managerAdditionF += userTarget.manager_addition;
                            defaultBaseF += userTarget.default_base_amount;

                        }
                    } else if (userTarget.type.equals(UserTarget.TYPE_E)) {
                        if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                            userAdditionE += userTarget.user_addition;
                            baseE += userTarget.base_amount;
                            managerAdditionE += userTarget.manager_addition;
                            defaultBaseE += userTarget.default_base_amount;
                        }
                    } else if (userTarget.type.equals(UserTarget.TYPE_M)) {
                        if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                            userAdditionM += userTarget.user_addition;
                            baseM += userTarget.base_amount;
                            managerAdditionM += userTarget.manager_addition;
                            defaultBaseM += userTarget.default_base_amount;
                        }
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
    }

    public UserTargetData(ArrayList<UserTarget> user_targets, double target, double decomposed_target, String fromDate){
        this.user_targets = user_targets;
        this.target = target;
        this.decomposed_target = decomposed_target;
        calculateTarget(fromDate);
    }

    public UserTargetData(ArrayList<UserTarget> user_targets, String fromDate){
        if (user_targets!= null) {
            this.user_targets = user_targets;
        }
        else{
            this.user_targets = new ArrayList<>();
        }
        calculateTarget(fromDate);
    }

}
