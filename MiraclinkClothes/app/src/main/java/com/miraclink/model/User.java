package com.miraclink.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User implements Parcelable {
    @PrimaryKey
    @NonNull
    private String ID;
    private String name;
    private int age;
    private int sex;
    private int height;
    private int weight;

    private int time;
    private int strong;
    private int rate;
    private int compose;
    private int mode;

    protected User(Parcel in) {
        ID = in.readString();
        name = in.readString();
        age = in.readInt();
        sex = in.readInt();
        height = in.readInt();
        weight = in.readInt();
        time = in.readInt();
        strong = in.readInt();
        rate = in.readInt();
        compose = in.readInt();
        mode = in.readInt();
    }

    public User() {
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getStrong() {
        return strong;
    }

    public void setStrong(int strong) {
        this.strong = strong;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getCompose() {
        return compose;
    }

    public void setCompose(int compose) {
        this.compose = compose;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeInt(sex);
        dest.writeInt(height);
        dest.writeInt(weight);
        dest.writeInt(time);
        dest.writeInt(strong);
        dest.writeInt(rate);
        dest.writeInt(compose);
        dest.writeInt(mode);
    }
}
