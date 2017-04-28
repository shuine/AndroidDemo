package tomato.pluginlib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @author yeshuxin on 17-3-30.
 */

public class LoaderUtils {
    public static String pluginFolder = Environment.getExternalStorageDirectory() + "/dynamicLib";


    public static PackageInfo getPackageInfo(Context context, String path) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    public static Drawable getAppIcon(Context context, String path) {
        Drawable drawable = null;
        if (context == null || TextUtils.isEmpty(path)) {
            return drawable;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = getPackageInfo(context, path);
        ApplicationInfo applicationInfo = info.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            applicationInfo.sourceDir = path;
            applicationInfo.publicSourceDir = path;
        }
        drawable = packageManager.getApplicationIcon(applicationInfo);
        return drawable;
    }

    public static String getAppLabel(Context context, String path) {
        String name = null;
        if (context == null || TextUtils.isEmpty(path)) {
            return name;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = getPackageInfo(context, path);
        ApplicationInfo applicationInfo = info.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            applicationInfo.sourceDir = path;
            applicationInfo.publicSourceDir = path;
        }
        name = packageManager.getApplicationLabel(applicationInfo).toString();
        return name;
    }

    public static DexClassLoader getDexClassLoader(Context context, String path, String libPath) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File dexFile = context.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader loader = new DexClassLoader(path, dexFile.getAbsolutePath(), libPath, context.getClassLoader());
        return loader;
    }

    public static AssetManager getAssetManager(String path) {
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method addAsset = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAsset.invoke(assetManager, path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  assetManager;
    }

    public static Resources getResources(Context context,AssetManager assetManager){
        Resources res = context.getResources();
        Resources resources = new Resources(assetManager,res.getDisplayMetrics(),res.getConfiguration());
        return resources;
    }

}

