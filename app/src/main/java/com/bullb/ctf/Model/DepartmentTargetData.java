package com.bullb.ctf.Model;


import com.bullb.ctf.Model.BaseModel;
import com.bullb.ctf.Model.DepartmentTarget;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserSale;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.Utils.SharedUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class DepartmentTargetData extends BaseModel {
    private ArrayList<DepartmentTarget> targets;
    private ArrayList<User> users;
    private ArrayList<Department> departments;


    private double amountA;
    private double amountF;
    private double amountE;
    private double amountM;
    private double amountAll, grossProfitAll;
    private double decomposedA, decomposedE, decomposedF, decomposedM;

    private double grossProfitA, grossProfitE, grossProfitF, grossProfitM;

    private double roundResult(double num){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_DOWN);
        return Double.parseDouble(df.format(num));
    }

    public double getAmountM() {
        return amountM;
    }

    public double getAmountE() {
        return amountE;
    }

    public double getAmountF() {
        return amountF;
    }

    public double getAmountA() {
        return amountA;
    }

    public double getAmountAll() {
        return getGivenAmountAll();
//        return roundResult(amountA + amountF + amountE + amountM);
    }

    public double getGivenAmountAll() {
        return amountAll;
    }

    public double getGrossProfitA() {
        return grossProfitA;
    }

    public double getGrossProfitF() {
        return grossProfitF;
    }

    public double getGrossProfitE() {
        return grossProfitE;
    }

    public double getGrossProfitM() {
        return grossProfitM;
    }

    public double getGrossProfitAll() {
        return roundResult(grossProfitA + grossProfitE + grossProfitF + grossProfitM);
    }

    public double getGivenGrossProfitAll() {
        return grossProfitAll;
    }

    public double getGrossProfitRateM(DepartmentSalesData salesData) {
        return salesData.getGrossProfitM()*100 / grossProfitM;
    }

    public double getGrossProfitRateA(DepartmentSalesData salesData) {
        return salesData.getGrossProfitA()*100 / grossProfitA;
    }

    public double getGrossProfitRateF(DepartmentSalesData salesData) {
        return salesData.getGrossProfitF()*100 / grossProfitF;
    }

    public double getGrossProfitRateE(DepartmentSalesData salesData) {
        return salesData.getGrossProfitE()*100 / grossProfitE;
    }

    public double getGrossProfitAllRate(DepartmentSalesData salesData) {
        return salesData.getGrossProfitAll()*100 / getGrossProfitAll();
    }

    public double getCompleteRateM(DepartmentSalesData salesData) {
        return salesData.getAmountM()*100/amountM;
    }

    public double getCompleteRateA(DepartmentSalesData salesData) {
        return salesData.getAmountA()*100/amountA;
    }

    public double getCompleteRateF(DepartmentSalesData salesData) {
        return salesData.getAmountF()*100/amountF;
    }

    public double getCompleteRateE(DepartmentSalesData salesData) {
        return salesData.getAmountE()*100/amountE;
    }

    public double getCompleteRateAll(DepartmentSalesData salesData) {
        return salesData.getAmountAll()*100/getAmountAll();
    }


    public double getCompleteDecomposedRateM(DepartmentSalesData salesData) {
        return salesData.getAmountM()*100/getDecomposedTargetM();
    }

    public double getCompleteDecomposedRateA(DepartmentSalesData salesData) {
        return salesData.getAmountA()*100/getDecomposedTargetA();
    }

    public double getCompleteDecomposedRateF(DepartmentSalesData salesData) {
        return salesData.getAmountF()*100/getDecomposedTargetF();
    }

    public double getCompleteDecomposedRateE(DepartmentSalesData salesData) {
        return salesData.getAmountE()*100/getDecomposedTargetE();
    }

    public double getCompleteDecomposedRateAll(DepartmentSalesData salesData) {
        return salesData.getAmountAll()*100/getDecomposedTargetAll();
    }

    public double getDecomposedTargetM() {
        return decomposedM;
    }

    public double getDecomposedTargetA(){
        return  decomposedA;
    }

    public double getDecomposedTargetF(){
        return  decomposedF;
    }

    public double getDecomposedTargetE(){
        return  decomposedE;
    }

    public double getDecomposedTargetAll(){
        return  roundResult(decomposedA + decomposedE + decomposedF + decomposedM);
    }

    public void calculateTarget(String fromDate) {
        amountA= amountF = amountE= amountM =0;
        grossProfitA =grossProfitF = grossProfitE = grossProfitM = 0;
        for (DepartmentTarget target : targets) {
            try {
                if (target.type.equals(UserTarget.TYPE_A)) {
                    if (SharedUtils.isCurrentYear(fromDate, target.from_date)) {
                        amountA += target.amount;
                        grossProfitA += target.gross_profit;
                    }
                } else if (target.type.equals(UserTarget.TYPE_F)) {
                    if (SharedUtils.isCurrentYear(fromDate, target.from_date)) {
                        amountF += target.amount;
                        grossProfitF += target.gross_profit;
                    }
                } else if (target.type.equals(UserTarget.TYPE_E)) {
                    if (SharedUtils.isCurrentYear(fromDate, target.from_date)) {
                        amountE += target.amount;
                        grossProfitE += target.gross_profit;
                    }
                } else if (target.type.equals(UserTarget.TYPE_M)) {
                    if (SharedUtils.isCurrentYear(fromDate, target.from_date)) {
                        amountM += target.amount;
                        grossProfitM += target.gross_profit;
                    }
                } else if (target.type.equals(UserTarget.TYPE_ALL)){
                    if (SharedUtils.isCurrentYear(fromDate, target.from_date)) {
                        amountAll += target.amount;
                        grossProfitAll += target.gross_profit;
                    }
                }
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
        for (User user: users){
            if (user.user_targets != null) {
                for (UserTarget userTarget : user.user_targets) {
                    try {
                        if (userTarget.type.equals(UserTarget.TYPE_A)) {
                            if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                                decomposedA += userTarget.base_amount;
                                decomposedA += userTarget.manager_addition;
                            }
                        } else if (userTarget.type.equals(UserTarget.TYPE_F)) {
                            if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                                decomposedF += userTarget.base_amount;
                                decomposedF += userTarget.manager_addition;
                            }
                        } else if (userTarget.type.equals(UserTarget.TYPE_E)) {
                            if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                                decomposedE += userTarget.base_amount;
                                decomposedE += userTarget.manager_addition;
                            }
                        } else if (userTarget.type.equals(UserTarget.TYPE_M)) {
                            if (SharedUtils.isCurrentYear(fromDate, userTarget.from_date)) {
                                decomposedM += userTarget.base_amount;
                                decomposedM += userTarget.manager_addition;
                            }
                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    public DepartmentTargetData(ArrayList<DepartmentTarget> targets, ArrayList<User> users, String fromDate){
        if (users != null) {
            this.users = users;
        }else{
            this.users = new ArrayList<>();
        }
        if (targets != null) {
            this.targets = targets;
        }else{
            this.targets = new ArrayList<>();
        }
        calculateTarget(fromDate);
    }


}
