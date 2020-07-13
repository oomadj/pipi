package com.miraclink.content.setting;

import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.model.User;

public interface SettingsContract {
    interface Presenter {
        void queryUser(IUserDatabaseManager iUserDatabaseManager, String id);
    }

    interface IView {
        void setUserView(User user);
    }
}
