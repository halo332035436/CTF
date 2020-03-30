package com.bullb.ctf.Model;


import android.content.Context;

import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class SalesData extends BaseModel{
    private ArrayList<UserSale> user_sales;
    private Sales sale;
    private Sales last_year_sales;

    //SalesPerformance
    private double salesA;
    private double salesF;
    private double salesE;
    private double salesM = 0;
    private double totalSales;
    private double lastYearSales;


    private double lastA;
    private double lastF;
    private double lastE;
    private double lastM = 0;
    private double grossProfitA, grossProfitE, grossProfitF, grossProfitM = 0;
    private double salesCompletionRate;


    public double getSalesA() {
        return salesA;
    }

    public double getSalesF() {
        return salesF;
    }

    public double getSalesE() {
        return salesE;
    }

    public double getSalesM() {
        return salesM;
    }

    public double getCompareLastAllRatio() {
        return getTotalSales()*100/ getLastTotalSales() ;
    }

    public double getCompareLastARatio() {
        return salesA*100/ lastA ;
    }
    public double getCompareLastFRatio() {
        return salesF*100/ lastF ;
    }
    public double getCompareLastERatio() {
        return salesE*100/ lastE ;
    }
    public double getCompareLastMRatio() {
        return salesM*100/ lastM ;
    }


    public double getRate() {
        double rate = totalSales*100/lastYearSales;
        return rate;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public double getGrossProfitTotal() {
        return grossProfitA + grossProfitF + grossProfitE + grossProfitM;
    }


    public double getLastTotalSales() {
        return lastA + lastE + lastF + lastM;
    }

    public double getGrowthRate(){
        return (getTotalSales() - getLastTotalSales())*100/getLastTotalSales();
    }

    public double getLastA() {
        return lastA;
    }

    public double getLastF() {
        return lastF;
    }

    public double getLastE() {
        return lastE;
    }

    public double getLastM() {
        return lastM;
    }

    public double getSalesCompletionRate() {
        return salesCompletionRate * 100;
    }


    public void calculatePageSales(Context context, String fromDate) {
        User user = SharedPreference.getUser(context);
        if (user.type.equals(User.USER_TYPE_A)) {
            if (user_sales != null) {
                for (UserSale userSale : user_sales) {
                    try {
                        if (userSale.from_date != null) {
                            if (userSale.type.equals(UserSale.TYPE_A)) {
                                if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                                    salesA += userSale.amount;
                                } else {
                                    lastA += userSale.amount;
                                }
                            } else if (userSale.type.equals(UserSale.TYPE_F)) {
                                if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                                    salesF += userSale.amount;
                                } else {
                                    lastF += userSale.amount;
                                }
                            } else if (userSale.type.equals(UserSale.TYPE_E)) {
                                if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                                    salesE += userSale.amount;
                                } else {
                                    lastE += userSale.amount;
                                }
                            } else if (userSale.type.equals(UserSale.TYPE_M)) {
                                if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                                    salesM += userSale.amount;
                                } else {
                                    lastM += userSale.amount;
                                }
                            }
                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (user.type.equals(User.USER_TYPE_B) || user.type.equals(User.USER_TYPE_C)) {
            if (sale != null) {
                salesA = sale.A;
                salesF = sale.F;
                salesE = sale.E;
                salesM = sale.M;
            }
            if (last_year_sales != null) {
                lastA = last_year_sales.A;
                lastF = last_year_sales.F;
                lastE = last_year_sales.E;
                lastM = last_year_sales.M;
            }

        }
        totalSales = salesA +salesF + salesE + salesM;
        lastYearSales = lastA + lastF+ lastE + lastM;
    }



    public void calculateSales(String fromDate) {
        if (user_sales != null) {
            for (UserSale userSale : user_sales) {
                try {
                    if (userSale.type.equals(UserTarget.TYPE_A)) {
                        if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                            salesA += userSale.amount;
                            grossProfitA += userSale.gross_profit;
                        } else {
                            lastA += userSale.amount;
                        }
                    } else if (userSale.type.equals(UserTarget.TYPE_F)) {
                        if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                            salesF += userSale.amount;
                            grossProfitF += userSale.gross_profit;
                        } else {
                            lastF += userSale.amount;
                        }
                    } else if (userSale.type.equals(UserTarget.TYPE_E)) {
                        if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                            salesE += userSale.amount;
                            grossProfitE += userSale.gross_profit;
                        } else {
                            lastE += userSale.amount;
                        }
                    } else if (userSale.type.equals(UserTarget.TYPE_M)) {
                        if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                            salesM += userSale.amount;
                            grossProfitM += userSale.gross_profit;
                        } else {
                            lastM += userSale.amount;
                        }
                    } else if (userSale.type.equals(UserSale.TYPE_ALL)){
                        if (SharedUtils.isCurrentYear(fromDate, userSale.from_date)) {
                            salesCompletionRate = userSale.sale_target_completion_rate;
                        }
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }

        totalSales = salesA +salesF + salesE + salesM;
    }

    public SalesData(ArrayList<UserSale> user_sales, Sales sale, Sales last_year_sales, Context context, String fromDate) {
        this.user_sales = user_sales;
        this.sale = sale;
        this.last_year_sales = last_year_sales;
        calculatePageSales(context, fromDate);
    }

    public SalesData(ArrayList<UserSale> user_sales, String fromDate) {
        if (user_sales != null) {
            this.user_sales = user_sales;
        }else{
            this.user_sales = new ArrayList<>();
        }
        calculateSales(fromDate);
    }


}
