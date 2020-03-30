package com.bullb.ctf.ManagerRating;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Score;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.ShareFragment.CircleFragment;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CalendarView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerRateFragment extends Fragment implements View.OnClickListener{
    private KeyTools keyTools;
    private ApiService apiService;
    private Call<BaseResponse> editScoreTask;
    private User user;
    private Score score;

    private TextView et_item_a, et_item_b, et_item_c, stafftotalScore;
    private TextView tv_title_item_a, tv_title_item_b, tv_title_item_c;
    private ViewPager viewPager;
    private static final int INPUT_ITEM_A = 1;
    private static final int INPUT_ITEM_B = 2;
    private static final int INPUT_ITEM_C = 3;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private LinearLayout not_empty;
    private TextView is_empty;

    public ManagerRateFragment() {
        // Required empty public constructor
    }

    public static ManagerRateFragment newInstance(Score s) {
        ManagerRateFragment fragment = new ManagerRateFragment();
        Bundle args = new Bundle();
        args.putSerializable("SCORE", s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = (Score) getArguments().getSerializable("SCORE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_rate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tv_title_item_a = (TextView) view.findViewById(R.id.tv_title_item_a);
        tv_title_item_b = (TextView) view.findViewById(R.id.tv_title_item_b);
        tv_title_item_c = (TextView) view.findViewById(R.id.tv_title_item_c);
        et_item_a = (TextView)view.findViewById(R.id.et_item_a);
        et_item_b = (TextView)view.findViewById(R.id.et_item_b);
        et_item_c = (TextView)view.findViewById(R.id.et_item_c);
        stafftotalScore = (TextView)view.findViewById(R.id.total_staff_score);
        not_empty = (LinearLayout) view.findViewById(R.id.not_empty);
        is_empty = (TextView) view.findViewById(R.id.is_empty);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Gson gson = new Gson();
        String kpi = "";
        if (score != null) {
            kpi = SharedUtils.addCommaToNum(score.getKpi());
        }

        CircleData data1 = new CircleData(getString(R.string.sales_performance), kpi, CircleData.TYPE_TEXT);
        fragments.add(CircleFragment.newInstance(gson.toJson(data1), null, 0));
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(0);
        picker.setMaxValue(100);

        keyTools = ((ManagerRateActivity) getActivity()).getKeyTools();
        user = ((ManagerRateActivity) getActivity()).getUser();
        apiService = ((ManagerRateActivity) getActivity()).getApiService();

        if (user.type.equals(User.USER_TYPE_A)) ((ManagerRateActivity) getActivity()).monthChange();

        if (user.type.equals(User.USER_TYPE_B)) {
            setData(score);
            et_item_a.setOnClickListener(this);
            et_item_b.setOnClickListener(this);
            et_item_c.setOnClickListener(this);
        }
    }

    public void setData(Score score){
        this.score = score;
        if (score == null){
            not_empty.setVisibility(View.GONE);
            is_empty.setVisibility(View.VISIBLE);
        }

        if (score != null){
            not_empty.setVisibility(View.VISIBLE);
            is_empty.setVisibility(View.GONE);

            updateScoresForUI(score);

            for (Fragment fragment : fragments){
                if (fragment instanceof CircleFragment){
                    try {
                        ((CircleFragment) fragment).putData(new CircleData(getString(R.string.sales_performance), SharedUtils.addCommaToNum(score.getKpi()), CircleData.TYPE_TEXT), null);
                    } catch (Exception e){
                        Log.e("Error", "Error in resetFilter", e);
                    }
                }
            }
        }
    }

    /**
     * Update A, B, C scores after API called
     * @param score
     */
    private void updateScoresForUI(Score score) {
        setTitles(score);
        et_item_a.setText(SharedUtils.addCommaToNum(score.getItemA()));
        et_item_b.setText(SharedUtils.addCommaToNum(score.getItemB()));
        et_item_c.setText(SharedUtils.addCommaToNum(score.getItemC()));
        stafftotalScore.setText(SharedUtils.addCommaToNum(score.getItemC() + score.getItemA() + score.getItemB() + score.kpi));
    }

    /**
     * Set the item A, B & C titles which are get from API
     * @param score
     */
    private void setTitles(Score score){
        setScoreDescription();
        tv_title_item_a.setText(score.descriptions.item_a);
        tv_title_item_b.setText(score.descriptions.item_b);
        tv_title_item_c.setText(score.descriptions.item_c);
    }

    @Override
    public void onClick(View view) {


        if (!((ManagerRateActivity)getActivity()).getCalendarView().isLast()) return;

        switch (view.getId()) {
            case R.id.et_item_a:
                showNumberInputDialog(INPUT_ITEM_A);
                break;
            case R.id.et_item_b:
                showNumberInputDialog(INPUT_ITEM_B);
                break;
            case R.id.et_item_c:
                showNumberInputDialog(INPUT_ITEM_C);
                break;
        }
    }

    private void setScoreDescription(){
        if(score.descriptions.item_a == null || TextUtils.isEmpty(score.descriptions.item_a)){
            score.descriptions.item_a = score.descriptions.default_item_a;
        }
        if(score.descriptions.item_b == null || TextUtils.isEmpty(score.descriptions.item_b)){
            score.descriptions.item_b = score.descriptions.default_item_b;
        }
        if(score.descriptions.item_c == null || TextUtils.isEmpty(score.descriptions.item_c)){
            score.descriptions.item_c = score.descriptions.default_item_c;
        }
    }

    public void showNumberInputDialog(final int type){
        String title = "";
        String input = "";

        setScoreDescription();
        switch (type){
            case INPUT_ITEM_A:
                title = score.descriptions.item_a;
                input = et_item_a.getText().toString();
                break;
            case INPUT_ITEM_B:
                title = score.descriptions.item_b;
                input = et_item_b.getText().toString();
                break;
            case INPUT_ITEM_C:
                title = score.descriptions.item_c;
                input = et_item_c.getText().toString();
                break;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.number_picker_dialog, null);
        dialogBuilder.setView(view);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);

        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        String[] valueSet = {"0","2","4","6","8","10"};
        np.setDisplayedValues(valueSet);
        np.setMinValue(0);
        np.setMaxValue(5);
        np.setValue(Integer.valueOf(input)/2);

        TextView titleText = (TextView)view.findViewById(R.id.tv_header);
        titleText.setText(title);
        Button cancel = (Button)view.findViewById(R.id.cancel);
        Button ok = (Button)view.findViewById(R.id.ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtils.hideKeyboard(getActivity(), view);
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtils.hideKeyboard(getActivity(), view);
                String kpi = String.format(Locale.CHINESE, "%.2f", (double)score.kpi);
                String a = score.item_a;
                String b = score.item_b;
                String c = score.item_c;
                switch (type){
                    case INPUT_ITEM_A:
                        a = String.valueOf(np.getValue()*2);
                        break;
                    case INPUT_ITEM_B:
                        b = String.valueOf(np.getValue()*2);
                        break;
                    case INPUT_ITEM_C:
                        c = String.valueOf(np.getValue()*2);
                        break;
                }
                EditScore(kpi, a, b, c);
                dialog.dismiss();
                ((ManagerRateActivity)getActivity()).setLoading(true);
            }
        });
    }

    /**
     * Call API service to update individual score
     * @param kpi Not editable
     * @param a Edit item A, B or C
     * @param b
     * @param c
     */
    private void EditScore(final String kpi, final String a, final String b, final String c){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditScore(kpi, a, b, c);
            }
        };

        JSONObject j = new JSONObject();
        try {
            j.put("item_a", a);
            j.put("item_b", b);
            j.put("item_c", c);
        } catch (JSONException e) {
            Log.e("Error", "JSON Build Error", e);
        }
        Map<String, String> dataMap = keyTools.encrypt(j.toString());

        editScoreTask = apiService.setScoresTask(
                "Bearer " + SharedPreference.getToken(getActivity()), score.id, dataMap);

        editScoreTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                ((ManagerRateActivity)getActivity()).setLoading(false);
                if (response.isSuccessful()) {
                    Log.d("debug", "onResponse");
                    updateScores(kpi, a, b, c);
                } else {
                    SharedUtils.handleServerError(getActivity(), response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                ((ManagerRateActivity)getActivity()).setLoading(false);
                SharedUtils.networkErrorDialogWithRetry(getActivity(), retry);
            }
        });
    }

    /**
     * Update Score Object and UI
     * @param kpi Not editable
     * @param a Edit item A, B or C
     * @param b
     * @param c
     */
    private void updateScores(String kpi, String a, String b, String c) {
        score.kpi = Double.parseDouble(kpi);
        score.setItemA(a);
        score.setItemB(b);
        score.setItemC(c);
        updateScoresForUI(score);
    }

    @Override
    public void onDestroy() {
        if (editScoreTask != null) editScoreTask.cancel();
        super.onDestroy();
    }
}
