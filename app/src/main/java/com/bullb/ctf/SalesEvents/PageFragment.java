package com.bullb.ctf.SalesEvents;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.CampaignResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageFragment extends Fragment implements ObservableScrollViewCallbacks {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TYPE = "TYPE";

    private String type;
    private int mPage;
    private ApiService apiService;
    private Call<BaseResponse> getCampaignTask;
    private ObservableRecyclerView rv;
    private KeyTools keyTools;
    private ArrayList<Campaign> campaigns;
    private MyRecyclerViewAdapter adapter;
    private boolean isSet = false;
    private ProgressBar progress;
    private ImageView imageView;

    public static PageFragment newInstance(int page, String type) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(TYPE, type);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        type = getArguments().getString(TYPE);
        Log.d("init", "init" + type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout
        View view = inflater.inflate(R.layout.fragment_sales_events, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = (ObservableRecyclerView) view.findViewById(R.id.recyclerView);
        progress = (ProgressBar)view.findViewById(R.id.progress);
        imageView = (ImageView)getActivity().findViewById(R.id.banner_image);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiService = ServiceGenerator.createService(ApiService.class, getActivity());
        keyTools = KeyTools.getInstance(getActivity());
        initUi();
        if (type.equals(Campaign.District))
            setCampaigns();
    }

    private void initUi(){
        rv.setNestedScrollingEnabled(false);

        // Add some Dummy data
        campaigns = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(campaigns, getActivity());
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setScrollViewCallbacks(this);

    }

    private void getCampaign(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCampaign();
            }
        };

        if (type.equals(Campaign.District)){
            getCampaignTask  = apiService.getCampaignTask("Bearer " + SharedPreference.getToken(getActivity()),"others");
        }
        else{
            getCampaignTask  = apiService.getCampaignTask("Bearer " + SharedPreference.getToken(getActivity()), type);

        }
        progress.setVisibility(View.VISIBLE);
        getCampaignTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getCampaignTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        try {
                            JSONArray jsonArray = new JSONArray(data);
                            if (jsonArray != null) {
                                String encodeJson = "{campaigns:" + jsonArray.toString() + "}";
                                Log.d("json", encodeJson);
                                CampaignResponse temp = new Gson().fromJson(encodeJson, CampaignResponse.class);
                                campaigns.addAll(temp.campaigns);
                                adapter.notifyDataSetChanged();
                                isSet = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    SharedUtils.handleServerError(getActivity(), response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getCampaignTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(getActivity(), retry);
                }
            }
        });
    }


    public void setCampaigns(){
        if (!isSet){
            getCampaign();
        }
    }


    private void getSearchCampaign(final String query){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSearchCampaign(query);
            }
        };

        if (type.equals(Campaign.District)){
            if(query!=null){
                getCampaignTask  = apiService.getCampaignTask("Bearer " + SharedPreference.getToken(getActivity()),"others",query);
            }else{
                getCampaignTask  = apiService.getCampaignTask("Bearer " + SharedPreference.getToken(getActivity()),"others");
            }
        }
        else{
            if(query!=null){
                getCampaignTask = apiService.getCampaignTask("Bearer " + SharedPreference.getToken(getActivity()), type,query);
            }else {
                getCampaignTask = apiService.getCampaignTask("Bearer " + SharedPreference.getToken(getActivity()), type);
            }
        }
        progress.setVisibility(View.VISIBLE);
        getCampaignTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getCampaignTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        try {
                            JSONArray jsonArray = new JSONArray(data);
                            if (jsonArray != null) {
                                String encodeJson = "{campaigns:" + jsonArray.toString() + "}";
                                Log.d("json", encodeJson);
                                CampaignResponse temp = new Gson().fromJson(encodeJson, CampaignResponse.class);
                                campaigns.addAll(temp.campaigns);
                                adapter.notifyDataSetChanged();
                                isSet = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    SharedUtils.handleServerError(getActivity(), response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getCampaignTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(getActivity(), retry);
                }
            }
        });
    }

    public void searchCampaign(String query){
        campaigns.clear();
        adapter.notifyDataSetChanged();
        getSearchCampaign(query);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        if (getCampaignTask != null){
            getCampaignTask.cancel();
        }
        super.onDetach();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        if (scrollY< SharedUtils.getCampaignBannerHeight(getActivity())) {
////            ViewHelper.setTranslationY(imageView, (float)(-scrollY));
//
//            ViewGroup.LayoutParams params = imageView.getLayoutParams();
//            params.height = params.height - scrollY;
//            imageView.setLayoutParams(params);
//        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
