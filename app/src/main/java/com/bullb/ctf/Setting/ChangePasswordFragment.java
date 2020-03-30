package com.bullb.ctf.Setting;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener, Validator.ValidationListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;


    private LinearLayout changePwLinearLayout;
    @Order(1)
    @NotEmpty(messageResId = R.string.error_empty_password)
    private EditText password;
    @Order(2)
    @Password(min = 6 , messageResId = R.string.error_invalid_new_password)
    private EditText newPassword;
    @Order(3)
    @ConfirmPassword(messageResId = R.string.error_invalid_confirm_password)
    private EditText confirmPassword;
    private ImageView doneBtn;
    private ApiService apiService;
    private Call<BaseResponse> changePwTask;
    private Validator validator;
    private KeyTools keyTools;
    private User user;
    private RelativeLayout loadingLayout;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePasswordFragment.
     */
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changePwLinearLayout = (LinearLayout)view.findViewById(R.id.change_pw_linear_layout);
        password = (EditText)view.findViewById(R.id.pw_edit_text);
        newPassword = (EditText)view.findViewById(R.id.new_pw_edittext);
        confirmPassword = (EditText)view.findViewById(R.id.confirm_pw_edittext);
        doneBtn = (ImageView)getActivity().findViewById(R.id.done_btn);
        loadingLayout = (RelativeLayout)getActivity().findViewById(R.id.loading_layout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        keyTools = KeyTools.getInstance(getActivity());
        user = SharedPreference.getUser(getActivity());
        doneBtn.setOnClickListener(this);
        apiService = ServiceGenerator.createService(ApiService.class, getActivity());
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void changePassword(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changePassword();
            }
        };

        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", user.id);
            jsonObject.put("password",password.getText().toString());
            jsonObject.put("new_password", newPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());

        SharedUtils.loading(loadingLayout, true);
        changePwTask = apiService.changePasswordTask("Bearer " + SharedPreference.getToken(getActivity()), dataMap);
        changePwTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.password_changed, Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    SharedUtils.handleServerError(getActivity(), response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetry(getActivity(), retry);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.done_btn:
                validator.validate();
                SharedUtils.hideKeyboard(getActivity(), changePwLinearLayout);
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        changePassword();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (errors.size()>0)
            Toast.makeText(getActivity(), errors.get(0).getCollatedErrorMessage(getActivity()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        doneBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        doneBtn.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    public void onDetach() {
        SharedUtils.hideKeyboard(getActivity(), changePwLinearLayout);
        if (changePwTask !=null){
            changePwTask.cancel();
        }
        super.onDetach();
    }

}
