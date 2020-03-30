package com.bullb.ctf.Model;

import com.bullb.ctf.Utils.SharedUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class DepartmentSalesData extends BaseModel {
    private ArrayList<DepartmentSales> sales;

    private double amountA;
    private double amountF;
    private double amountE;
    private double amountM;
    private double lastAmountA;
    private double lastAmountF;
    private double lastAmountE;
    private double lastAmountM;
    private double grossProfitA, grossProfitE, grossProfitF, grossProfitM;
    private double salesCompleteRate;

    private double roundResult(double num) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_DOWN);

        try {
            if (Double.isInfinite(num) || Double.isNaN(num)) {
                return num;
            } else {
                return Double.parseDouble(df.format(num));
            }
        } catch (NumberFormatException e) {
            return num;
        }
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
        return roundResult(amountA + amountF + amountE + amountM);
    }

    public double getLastAmountAll() {
        return roundResult(lastAmountA + lastAmountF + lastAmountE + lastAmountM);
    }


    public double getGrossProfitE() {
        return grossProfitE;
    }

    public double getGrossProfitA() {
        return grossProfitA;
    }

    public double getGrossProfitF() {
        return grossProfitF;
    }

    public double getGrossProfitM() {
        return grossProfitM;
    }

    public double getGrossProfitAll() {
        return roundResult(grossProfitA + grossProfitE + grossProfitF + grossProfitM);
    }

    public double getGrowthRate() {
        return roundResult((getAmountAll() - getLastAmountAll()) * 100 / getLastAmountAll());
    }

    public double getSalesCompleteRate() {
        return salesCompleteRate * 100;
    }

    public double getCompareLastAllRatio() {
        return getAmountAll() * 100 / getLastAmountAll();
    }

    public double getCompareLastARatio() {
        return amountA * 100 / lastAmountA;
    }

    public double getCompareLastFRatio() {
        return amountF * 100 / lastAmountF;
    }

    public double getCompareLastERatio() {
        return amountE * 100 / lastAmountE;
    }

    public double getCompareLastMRatio() {
        return amountM * 100 / lastAmountM;
    }

    public void calculateTarget(String fromDate) {
        amountA = amountF = amountE = amountF = lastAmountA = lastAmountE = lastAmountF = lastAmountM = 0;
        if (sales != null) {
            for (DepartmentSales sale : sales) {
                try {
                    if (sale.type.equals(UserTarget.TYPE_A)) {
                        if (SharedUtils.isCurrentYear(fromDate, sale.from_date)) {
                            amountA += sale.amount;
                            grossProfitA += sale.gross_profit;
                        } else {
                            lastAmountA += sale.amount;
                        }
                    } else if (sale.type.equals(UserTarget.TYPE_F)) {
                        if (SharedUtils.isCurrentYear(fromDate, sale.from_date)) {
                            amountF += sale.amount;
                            grossProfitF += sale.gross_profit;
                        } else {
                            lastAmountF += sale.amount;
                        }
                    } else if (sale.type.equals(UserTarget.TYPE_E)) {
                        if (SharedUtils.isCurrentYear(fromDate, sale.from_date)) {
                            amountE += sale.amount;
                            grossProfitE += sale.gross_profit;
                        } else {
                            lastAmountE += sale.amount;
                        }
                    } else if (sale.type.equals(UserTarget.TYPE_M)) {
                        if (SharedUtils.isCurrentYear(fromDate, sale.from_date)) {
                            amountM += sale.amount;
                            grossProfitM += sale.gross_profit;
                        } else {
                            lastAmountM += sale.amount;
                        }
                    } else if (sale.type.equals(UserTarget.TYPE_ALL)) {
                        if (SharedUtils.isCurrentYear(fromDate, sale.from_date)) {
                            salesCompleteRate = sale.sale_target_completion_rate;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public DepartmentSalesData(ArrayList<DepartmentSales> sales, String fromDate) {
        this.sales = sales;
        calculateTarget(fromDate);
    }
}