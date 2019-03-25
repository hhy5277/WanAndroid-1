package com.example.hy.wanandroid.config;
import android.os.Environment;

import com.example.commonlib.utils.FileUtil;

import java.io.File;

/**
 * 网路请求的base地址
 * Created by 陈健宇 at 2018/10/26
 */
public class Constant {

    //Url
    public final static String BASE_URL = "http://www.wanandroid.com/";
    public final static String BASE_APK_URL = "https://github.com/rain9155/WanAndroid/releases/download/";

    //HierarchyFragment
    public static final String KEY_HIERARCHY_PAGENUM = "hierarchyPageNum";
    public static final String KEY_PROJECT_ID = "projectId";

    //WeChatFragment
    public static final String KEY_WECHAT_ID = "wechatId";

    //HierarchyActivity
    public static final String KEY_HIERARCHY_ID = "hierarchyId";
    public static final String KEY_HIERARCHY_NAMES = "hierarchyNames";
    public static final String KEY_HIERARCHY_NAME = "hierarchyName";

    //MainActivity
    public static final long WAIT_TIME = 2000L; // 再点一次退出程序时间设置
    public static long TOUCH_TIME = 0;//第一次按下返回键的时间
    public static final int REQUEST_CODE_UNKNOWN_APP = 3;

    //ArticleActivity
    public static final String KEY_ARTICLE_FLAG = "articleFlag";
    public static final String KEY_DATA_RETURN = "dataReturn";
    public static final String KEY_ARTICLE_BEAN = "articleBean";

    //CollectionActivity
    public static final int REQUEST_SHOW_COLLECTIONS = 0;

    //HomeFragment
    public static final int REQUEST_COLLECT_ARTICLE = 1;
    public static final int REQUEST_REFRESH_ARTICLE = 2;

    //MineFragment
    public static final int REQUEST_PICK_IMAGE_CHOOSER = 5;
    public static final int REQUEST_CROP_IMAGE_ACTIVITY = 6;
    public static final int CHANGE_FACE = 0;
    public static final int CHANGE_BACK = 1;
    public static final int CHANGE_NO = 2;

    //CropperImageActivity
    public static final String KEY_IMAGE_URI = "imageUri";
    public static final String KEY_IMAGE_OPTIONS = "imageOptions";
    public static final String KEY_CROP_BUNDLE = "cropBundle";
    public static final String KEY_IS_FACE = "isFace";

    //BaseLoadState
    public static final int NORMAL_STATE = 0;
    public static final int LOADING_STATE = 1;
    public static final int ERROR_STATE = 2;

    //settingActivity
    public static final String SHAREDPREFERENCES_NAME = "prefs";
    public static final String KEY_PREFS_NODEMODE = "nightModeState";
    public static final String KEY_PREFS_CURRWNTITEM = "currentItem";
    public static final String KEY_PREFS_NOIMAGE= "noImage";
    public static final String KEY_PREFS_AUTOCACHE= "autoCache";
    public static final String KEY_PREFS_STATUSBAR = "statusBar";
    public static final String KEY_PREFS_NETWORK = "netWork";
    public static final String KEY_PREFS_AUTOUPDATA = "autoUpdata";


    //utils
    public static final String EMAIL_ADDRESS = "1847796089@qq.com";

    //bugly
    public static final String BUGLY_ID = "a0768ddf34";

    //path
    public static final String PATH_NET_CACHE = FileUtil.getCachePath(App.getContext(), "netData");
    public static final String PATH_IMAGE_FACE = FileUtil.getFilePath(App.getContext(), "image");
    public static final String PATH_IMAGE_BACKGROUND = FileUtil.getFilePath(App.getContext(), "image");
    public static final String PATH_APK_1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/WanAndroid.apk";
    public static final String PATH_APK_2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app-release.apk";
    public static final String FACE = "face.jpeg";
    public static final String BACK = "background.jpeg";

    //common
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 4;
    public static final String KEY_URL_APK = "urlApk";
    public static final String KEY_DOWNLOAD_ID = "downloadId";
    public static String NEW_VERSION_URL = "";


}
