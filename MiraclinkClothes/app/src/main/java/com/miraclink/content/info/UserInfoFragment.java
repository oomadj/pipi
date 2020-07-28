package com.miraclink.content.info;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miraclink.R;
import com.miraclink.content.ContentActivity;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.networks.NetworkUtil;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.utils.Utils;
import com.miraclink.widget.EditLineLayout;


// info page
public class UserInfoFragment extends Fragment implements View.OnClickListener, UserInfoContract.IView {
    private static final String TAG = UserInfoFragment.class.getSimpleName();
    private EditLineLayout editLineLayoutId, editLineLayoutName, editLineLayoutAge, editLineLayoutSex, editLineLayoutHeight, editLineLayoutWeight, editLineLayoutDeviceId;
    private Button btSave;

    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;
    private UserInfoContract.Presenter presenter;
    private int sex = 0;
    private AlertDialog alertDialog;
    ContentActivity activity;
    private boolean isNewBuild = false;  //new build user

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (ContentActivity) getActivity();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isNewBuild) {
            editLineLayoutId.getInfoEditText().setText("");
            editLineLayoutName.getInfoEditText().setText("");
            editLineLayoutAge.getInfoEditText().setText("");
            editLineLayoutWeight.getInfoEditText().setText("");
            editLineLayoutHeight.getInfoEditText().setText("");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.fragment_user_info, container, false);
        return messageLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParam();
        initView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.USER_CHANGED);
        getContext().registerReceiver(receiver, filter);
        presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    private void initParam() {
        LogUtil.i(TAG, "info-get id:" + SharePreUtils.getCurrentID(getContext()));
        iUserDatabaseManager = UserDatabaseManager.getInstance(getContext(), AppExecutors.getInstance());
        presenter = new UserInfoPresenter(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.USER_CHANGED)) {
                    presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
                }
            }
        };
    }

    private void initView(View view) {
        editLineLayoutId = view.findViewById(R.id.layoutUserInfoFragmentId);
        editLineLayoutName = view.findViewById(R.id.layoutUserInfoFragmentName);
        editLineLayoutAge = view.findViewById(R.id.layoutUserInfoFragmentAge);
        editLineLayoutSex = view.findViewById(R.id.layoutUserInfoFragmentSex);
        editLineLayoutSex.setOnClickListener(this);
        editLineLayoutSex.getInfoEditText().setFocusable(false);
        editLineLayoutHeight = view.findViewById(R.id.layoutUserInfoFragmentHeight);
        editLineLayoutWeight = view.findViewById(R.id.layoutUserInfoFragmentWeight);
        editLineLayoutDeviceId = view.findViewById(R.id.layoutUserInfoFragmentDeviceId);
        btSave = view.findViewById(R.id.btUserInfoFragmentSave);
        btSave.setOnClickListener(this);
    }

    //TODO save user data to net and database
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btUserInfoFragmentSave:
                //TODO save or update user to network
                //if (NetworkUtil.getConnectivityEnable(getContext())) {

                //} else {
                if (isNewBuild) {
                    User user = new User();
                    user.setID(editLineLayoutId.getInfoEditText().getEditableText().toString());
                    user.setName(editLineLayoutName.getInfoEditText().getEditableText().toString());
                    user.setAge(Integer.valueOf(editLineLayoutAge.getInfoEditText().getText().toString()));
                    user.setSex(sex);
                    user.setHeight(Integer.valueOf(editLineLayoutHeight.getInfoEditText().getEditableText().toString()));
                    user.setWeight(Integer.valueOf(editLineLayoutWeight.getInfoEditText().getEditableText().toString()));
                    presenter.insertUser(iUserDatabaseManager, user);

                    activity.setTabSelection(1, false);
                } else {
                    presenter.updateUser(iUserDatabaseManager, editLineLayoutName.getInfoEditText().getEditableText().toString(), Integer.valueOf(editLineLayoutAge.getInfoEditText().getText().toString()), sex,
                            Integer.valueOf(editLineLayoutHeight.getInfoEditText().getEditableText().toString()), Integer.valueOf(editLineLayoutWeight.getInfoEditText().getEditableText().toString()),
                            editLineLayoutId.getInfoEditText().getEditableText().toString());

                    activity.setTabSelection(1, false);
                }
                //}
                break;
            case R.id.layoutUserInfoFragmentSex:
                showList();
                break;
            default:
                break;
        }
    }

    public void showList() {
        final String[] items = {getString(R.string.man), getString(R.string.woman)};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        //alertBuilder.setTitle("test");
        alertBuilder.setItems(items, (dialogInterface, i) -> {
            sex = i;
            editLineLayoutSex.getInfoEditText().setText(items[i]);
            alertDialog.dismiss();
        });
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public void addValues(boolean b) {
        isNewBuild = b;

    }

    @Override
    public void setUserInfoView(User user) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    editLineLayoutId.getInfoEditText().setText(user.getID() + "");
                    editLineLayoutName.getInfoEditText().setText(user.getName() + "");
                    editLineLayoutAge.getInfoEditText().setText(user.getAge() + "");
                    editLineLayoutHeight.getInfoEditText().setText(user.getHeight() + "");
                    editLineLayoutWeight.getInfoEditText().setText(user.getWeight() + "");
                    if (user.getSex() == 0) {
                        sex = 0;
                        //editLineLayoutSex.getInfoEditText()
                    } else {
                        sex = 1;
                    }
                }

            }
        });
    }
}
