package com.example.hy.wanandroid.bean;

import androidx.annotation.Nullable;

/**
 * 权限
 * Created by 陈健宇 at 2019/3/24
 */
public class Permission {
    public final String name;
    public final boolean granted;
    public final boolean shouldShowRequestPermissionRationable;

    public Permission(String name, boolean granted){
        this(name, granted, true);
    }

    public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationable){
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationable = shouldShowRequestPermissionRationable;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        final Permission permission = (Permission)obj;
        if(this.granted != permission.granted) return false;
        if(this.shouldShowRequestPermissionRationable != permission.shouldShowRequestPermissionRationable) return false;
        return this.name.equals(permission.name);
    }

    @Override
    public int hashCode() {
        int hash = name.hashCode();
        hash = 31 * hash + (granted ? 1 : 0);
        hash = 31 * hash + (shouldShowRequestPermissionRationable ? 1 : 0);
        return hash;
    }
}
