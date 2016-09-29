package com.you.edu.live.teacher.presenter.helper;

import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

import java.util.List;

/**
 * 直播时，用户列表管理助手
 * 作者：XingRongJing on 2016/6/30.
 */
public class LiveUserHelper {
    private List<User> mUsers;
    private RecyclerViewAdapter<User> mUsersAdapter;

    public LiveUserHelper(List<User> mUsers, RecyclerViewAdapter<User>  adapter) {
        if (null == mUsers) {
            throw new IllegalArgumentException("List<User> cannot be null");
        }
        this.mUsers = mUsers;
        this.mUsersAdapter = adapter;
    }

    public void notifyUserChanged() {
        if (null != mUsersAdapter) {
            mUsersAdapter.notifyDataSetChanged();
        }
    }

    public RecyclerViewAdapter<User> getUserAdapter(){
        return mUsersAdapter;
    }

    private boolean isEmpty() {
        return null == mUsers ? true : mUsers.isEmpty();
    }

    public int getSize() {
        if (this.isEmpty()) {
            return 0;
        }
        return mUsers.size();
    }

    public User getUserById(int uid) {
        if (this.isEmpty()) {
            return null;
        }
        int size = this.getSize();
        for (int i = 0; i < size; i++) {
            User user = mUsers.get(i);
            if (uid == user.getUid()) {
                return user;
            }
        }
        return null;
    }

    /**
     * 添加到队尾
     *
     * @param user
     */
    public void addUser(User user) {
        if (null == user) {
            return;
        }
        if (null != mUsers) {
            mUsers.add(user);
            this.notifyUserChanged();
        }
    }

    /**
     * 添加到队头
     *
     * @param user
     */
    public void addUserFront(User user) {
        if (null == user) {
            return;
        }
        if (null != mUsers) {
            mUsers.add(0, user);
            this.notifyUserChanged();
        }
    }

    public void addUsers(List<User> users) {
        if (null == users || users.isEmpty()) {
            return;
        }
        if (null != mUsers) {
            mUsers.addAll(users);
            this.notifyUserChanged();
        }
    }

    public boolean removeUser(int uid) {
        User user = this.getUserById(uid);
        if (null == user || null == mUsers) {
            return false;
        }
        int index = mUsers.indexOf(user);
        if (-1 != index) {
            mUsers.remove(index);
            this.notifyUserChanged();
            return true;
        }
        return false;
    }

    public void removeAll() {
        if (null != mUsers) {
            mUsers.clear();
            this.notifyUserChanged();
        }
    }

    public boolean isExit(int uid) {
        User user = this.getUserById(uid);
        if (null == user || null == mUsers) {
            return false;
        }
        int index = mUsers.indexOf(user);
        return (-1 != index);
    }


}
