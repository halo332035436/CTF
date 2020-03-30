package com.bullb.ctf.Widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.Model.Descendants;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.SalesRanking.FilterRecyclerViewAdapter;
import com.bullb.ctf.SalesRanking.SalesRankingActivity;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class FilterView extends RelativeLayout implements View.OnClickListener{
    private User user;

    private boolean isShow = false;
    private Point size;
    private ImageView dismissBtn, doneBtn;
    private View viewRoot;
    private CommonTabLayout tabLayout;
    private FilterDoneListener doneListener;
    private RelativeLayout rankingLayout, unitLayout, recyclerviewLayout;
    private SquareTextView rankbranchesText, rankSmallDistrictText, rankDistrictText, rankLargeDistrictText, unitStaffText, unitBranchesText, unitSmallDistrctText, unitDistText;
    private RecyclerView recyclerView;
    private TextView is_empty;
    private ProgressBar progressBar;

    private int category;
    private int selectedRank = -1;
    private int selectedUnit = -1;
    private ArrayList<SquareTextView> rankTextArr;
    private ArrayList<SquareTextView> unitTextArr;

    private FilterRecyclerViewAdapter adapter;
    private Descendants[] descendants;
    private Descendants[] list0;
    private Descendants[] list1;
    private Descendants[] list2;
    private Descendants[] list3;


    private boolean isSet = false;

    public FilterView(Context context) {
        super(context);
        if(!isInEditMode())
            init();
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init();
    }

    public FilterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            init();
    }

    private void init() {
        inflate(getContext(), R.layout.content_filter, this);
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        dismissBtn = (ImageView)findViewById(R.id.dismiss_btn);
        doneBtn = (ImageView)findViewById(R.id.done_btn);
        tabLayout = (CommonTabLayout)findViewById(R.id.period_tablayout);
        rankingLayout = (RelativeLayout)findViewById(R.id.ranking_layout);
        rankbranchesText = (SquareTextView)findViewById(R.id.rank_branches_text);
        rankSmallDistrictText = (SquareTextView)findViewById(R.id.rank_small_district_text);
        rankDistrictText = (SquareTextView)findViewById(R.id.rank_district_text);
        rankLargeDistrictText = (SquareTextView)findViewById(R.id.rank_large_district_text);
        unitStaffText = (SquareTextView)findViewById(R.id.unit_staff_text);
        unitBranchesText = (SquareTextView)findViewById(R.id.unit_branches_text);
        unitSmallDistrctText = (SquareTextView)findViewById(R.id.unit_small_district_text);
        unitDistText = (SquareTextView)findViewById(R.id.unit_district_text);
        unitLayout = (RelativeLayout)findViewById(R.id.samllest_unit_layout);
        recyclerView = (RecyclerView)findViewById(R.id.filter_recyclerview);
        is_empty = (TextView) findViewById(R.id.is_empty);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        recyclerviewLayout = (RelativeLayout)findViewById(R.id.recyclerview_layout);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            this.setVisibility(INVISIBLE);
        } else {
            ViewHelper.setTranslationY(this, (float)size.y);
        }

        //dispatchKeyEventPreIme will work only if layout is in focus
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        //disable background control
        this.setOnClickListener(this);

        setTabBar();

        user = SharedPreference.getUser(getContext());
        if (user == null) return;

        if (user.type.equals(User.USER_TYPE_A) || user.type.equals(User.USER_TYPE_B)){
            rankingLayout.setVisibility(GONE);
            unitLayout.setVisibility(GONE);
        }

        dismissBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.type.equals(User.USER_TYPE_C)){
                    if (adapter.getSelected() == -1) {
                        Toast.makeText(getContext(), R.string.alert_title_please_select_rank, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selectedUnit == -1){
                        Toast.makeText(getContext(), R.string.alert_title_please_select_unit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                dismiss();
                doneListener.filterDoneCallBack(category, selectedRank, selectedUnit);
            }
        });

        setActionControl();
    }

    private void setTabBar(){
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        tabEntities.add(new TabEntity(getContext().getString(R.string.target_type_all)));
        tabEntities.add(new TabEntity(getContext().getString(R.string.a_type)));
        tabEntities.add(new TabEntity(getContext().getString(R.string.f_type)));
        tabEntities.add(new TabEntity(getContext().getString(R.string.e_type)));
        tabEntities.add(new TabEntity(getContext().getString(R.string.m_type)));

        tabLayout.setTabData(tabEntities);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                category = position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void setActionControl(){
        adapter = new FilterRecyclerViewAdapter(getContext(), descendants);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(ContextCompat.getColor(getContext(),R.color.disable_grey
        )).build());
        recyclerView.setAdapter(adapter);

        setRank();
        setUnit();
    }

    private void setRank() {
        rankTextArr = new ArrayList<>();
        rankTextArr.add(rankbranchesText);
        rankTextArr.add(rankSmallDistrictText);
        rankTextArr.add(rankDistrictText);
        rankTextArr.add(rankLargeDistrictText);
        for (int i = 0; i< rankTextArr.size(); i++){
            final SquareTextView tempText = rankTextArr.get(i);
            tempText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = rankTextArr.indexOf(tempText);
                    if (!tempText.isSelected()){
                        recyclerviewLayout.setVisibility(VISIBLE);
                        unitLayout.setVisibility(VISIBLE);
                        tempText.setSelected(true);
                        for (SquareTextView textView: rankTextArr){
                            if (!textView.equals(tempText)){
                                textView.setSelected(false);
                            }
                        }
                        selectedRank = pos;
                        for (int j = 0; j < unitTextArr.size(); j++){
                            SquareTextView textView = unitTextArr.get(j);
                            if (j< pos + 1)
                                textView.setVisibility(VISIBLE);
                            else
                                textView.setVisibility(INVISIBLE);
                        }
                        if (selectedRank < selectedUnit)
                            unitTextArr.get(0).performClick();
                    }
                    ((SalesRankingActivity)getContext()).setSelectedRank(selectedRank);
                    if (!isArrayListExist(pos)) {
                        recyclerView.setVisibility(GONE);
                        is_empty.setVisibility(INVISIBLE);
                        ((SalesRankingActivity) getContext()).getDepartmentDescendants(pos, user.department_id, false);
                    }
                    else{
                        hideProgressBar();
                        ((SalesRankingActivity) getContext()).cancelGetDepartmentDescendantsTask();
                        setDescendantsList(pos,getArrayList(pos));
                    }

                }
            });
        }
    }

    private void setUnit() {
        unitTextArr = new ArrayList<>();
        unitTextArr.add(unitStaffText);
        unitTextArr.add(unitBranchesText);
        unitTextArr.add(unitSmallDistrctText);
        unitTextArr.add(unitDistText);
        for (SquareTextView textView: unitTextArr){
            final SquareTextView tempText = textView;
            tempText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!tempText.isSelected()){
                        tempText.setSelected(true);
                        for (SquareTextView textView: unitTextArr){
                            if (!textView.equals(tempText)){
                                textView.setSelected(false);
                            }
                        }
                        selectedUnit = unitTextArr.indexOf(tempText);
                    }
//                    ((SalesRankingActivity)getContext()).setSelectedUnit(selectedUnit);
                }
            });
        }
    }

    public boolean isArrayListExist(int pos){
        if (pos ==0 && list0 == null)
            return false;
        else if (pos == 1 && list1 == null)
            return false;
        else  if (pos == 2 && list2 == null)
            return false;
        else  if (pos == 3 && list3 == null)
            return false;
        else
            return true;
    }

    public void setArrayList(int pos,Descendants[] descendants ){
        if (pos ==0 )
            list0 = descendants;
        else if (pos == 1 )
            list1 = descendants;
        else  if (pos == 2 )
            list2 = descendants;
        else if (pos == 3)
            list3 = descendants;
    }

    public Descendants[] getArrayList(int pos){
        if (pos ==0)
            return list0;
        else if (pos == 1)
            return list1;
        else  if (pos == 2)
            return list2;
        else if (pos ==3)
            return list3;
        else
            return null;
    }

    public void setDescendantsList(int pos, Descendants[] descendants) {
        isSet = true;
        setArrayList(pos, descendants);

        if (descendants.length == 0){
            recyclerView.setVisibility(GONE);
            is_empty.setVisibility(VISIBLE);
            return;
        }

        recyclerView.setVisibility(VISIBLE);
        is_empty.setVisibility(GONE);

        adapter.setData(descendants);
        adapter.setSelected(-1);

//        if (descendants.length > 5){
//            ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
//            params.height= SharedUtils.dpToPx(150);
//            recyclerView.setLayoutParams(params);
//        } else {
//            ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
//            params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//            recyclerView.setLayoutParams(params);
//        }
//        adapter.setSelected(0);
    }

    public void addFilterDoneListener(FilterDoneListener listener){
        doneListener = listener;
    }

    public interface FilterDoneListener {
        void filterDoneCallBack(int category, int selectedRank, int selectedUnit);
    }

    public void resetFilter(){
        tabLayout.setCurrentTab(category);

        this.selectedRank = -1;
        this.selectedUnit = -1;

        for (SquareTextView rankText : rankTextArr){
            rankText.setSelected(false);
        }

        for(SquareTextView unitText: unitTextArr){
            unitText.setSelected(false);
        }

        recyclerviewLayout.setVisibility(GONE);
        unitLayout.setVisibility(GONE);
        adapter.setSelected(-1);
        this.category = category;

//        this.selectedUnit = selectedUnit;
//        this.selectedRank = selectedRank;

//        if (user.type.equals(User.USER_TYPE_C)) {
//            rankTextArr.get(selectedRank).performClick();
//            unitTextArr.get(selectedUnit).performClick();
//        }
    }

    public void show(View viewRoot){
        this.requestFocus();

        // get the center for the clipping circle
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            int[] locations = new int[2];
            viewRoot.getLocationOnScreen(locations);

            this.viewRoot = viewRoot;

            int cx = (locations[0] + viewRoot.getWidth() / 2);
            int cy = (locations[1] + viewRoot.getHeight() / 2);

            // get the final radius for the clipping circle
            int finalRadius = Math.max(this.getWidth(), this.getHeight());
            int startRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight()) / 2;


            // create the animator for this view (the start radius is zero)
            Animator anim = null;
            anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, startRadius, finalRadius);


            // make the view visible and start the animation
            this.setVisibility(View.VISIBLE);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            anim.start();
        }
        else{
            ViewPropertyAnimator.animate(this).translationY(0).alpha(1).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();

        }

        isShow = true;
    }

    public void dismissImediately(){
        ViewPropertyAnimator.animate(this).translationY(size.y).setDuration(1).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        isShow = false;
    }

    public void dismiss(){
        // get the center for the clipping circle
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            int[] locations = new int[2];
            viewRoot.getLocationOnScreen(locations);
            int cx = (locations[0] + viewRoot.getWidth() / 2);
            int cy = (locations[1] + viewRoot.getHeight() / 2);
            // get the initial radius for the clipping circle
            int finalRadius = Math.max(this.getWidth(), this.getHeight());
            int startRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight()) / 2;


            // create the animation (the final radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(this, cx, cy, finalRadius, startRadius);


            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    FilterView.this.setVisibility(View.INVISIBLE);
                }
            });

            // start the animation
            anim.setInterpolator(new DecelerateInterpolator());
            anim.start();
        } else {
            ViewPropertyAnimator.animate(this).translationY(size.y).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
        isShow = false;
    }

    public boolean isShow(){
        //disable background control
        return isShow;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (isShow){
                dismiss();
                return true;
            }
            // do your stuff
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dismiss_btn:
                dismiss();
                break;
        }
    }

    public void showProgressBar(){
        progressBar.setVisibility(VISIBLE);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(GONE);
    }
}
