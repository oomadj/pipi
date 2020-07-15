package com.miraclink.content.info;

import android.content.BroadcastReceiver;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miraclink.R;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.networks.NetworkUtil;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.utils.Utils;


// info page
public class UserInfoFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, UserInfoContract.IView {
    private static final String TAG = UserInfoFragment.class.getSimpleName();
    private TextView textAdd;
    private EditText editId, editName, editAge, editHeight, editWeight;
    private Button btSave;
    private RadioGroup rgSelectSex;
    private RadioButton rbMen, rbWomen;

    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;
    private UserInfoContract.Presenter presenter;
    private int sex = 0;

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
        LogUtil.i(TAG, "xzxinfo-get id:" + SharePreUtils.getCurrentID(getContext()));
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
        editId = view.findViewById(R.id.editUserInfoFragmentId);
        editName = view.findViewById(R.id.editUserInfoFragmentName);
        editAge = view.findViewById(R.id.editUserInfoFragmentAge);
        editHeight = view.findViewById(R.id.editUserInfoFragmentHeight);
        editWeight = view.findViewById(R.id.editUserInfoFragmentWeight);
        textAdd = view.findViewById(R.id.textUserInfoFragmentAdd);
        btSave = view.findViewById(R.id.btUserInfoFragmentSave);
        btSave.setOnClickListener(this);
        rgSelectSex = view.findViewById(R.id.rgUserInfoFragmentSex);
        rgSelectSex.setOnCheckedChangeListener(this);
        rbMen = view.findViewById(R.id.rbUserInfoFragmentMen);
        rbWomen = view.findViewById(R.id.rbUserInfoFragmentWomen);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getCheckedRadioButtonId() == R.id.rbUserInfoFragmentMen) {
            sex = 0;
        } else {
            sex = 1;
        }
    }

    //TODO save user data to net and database
    @Override
    public void onClick(View v) {
        if (NetworkUtil.getConnectivityEnable(getContext())) {

        } else {
            LogUtil.i(TAG, "test name:" + editName.getEditableText().toString());
            presenter.updateUser(iUserDatabaseManager, editName.getEditableText().toString(), Integer.valueOf(editAge.getText().toString()), sex,
                    Integer.valueOf(editHeight.getEditableText().toString()), Integer.valueOf(editWeight.getEditableText().toString()), editId.getEditableText().toString());
        }

    }

    @Override
    public void setUserInfoView(User user) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    editId.setText(user.getID() + "");
                    editName.setText(user.getName() + "");
                    editAge.setText(user.getAge() + "");
                    editHeight.setText(user.getHeight() + "");
                    editWeight.setText(user.getWeight() + "");
                    if (user.getSex() == 0) {
                        sex = 0;
                        rbMen.setChecked(true);
                    } else {
                        sex = 1;
                        rbWomen.setChecked(true);
                    }
                }
            }
        });
    }
}
