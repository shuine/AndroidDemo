package tomato.pluginlib;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * @author yeshuxin on 17-3-30.
 */

public class PluginManager {

    private Context mContext;
    private final HashMap<String, PluginPackage> mAppContainer = new HashMap<>();
    private String mNativeLibDir;
    private static PluginManager mInstance;

    private PluginManager(Context context) {
        mContext = context.getApplicationContext();
        mNativeLibDir = mContext.getDir("native_lib", Context.MODE_PRIVATE).getAbsolutePath();

    }

    public static PluginManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PluginManager.class) {
                if (mInstance == null) {
                    mInstance = new PluginManager(context);
                }
            }
        }

        return mInstance;
    }

    public PluginPackage loadApp(String path) {

        PluginPackage pluginPackage = null;
        if (TextUtils.isEmpty(path)) {
            return pluginPackage;
        }
        PackageInfo packageInfo = LoaderUtils.getPackageInfo(mContext, path);
        if (packageInfo == null) {
            return pluginPackage;
        }

        pluginPackage = mAppContainer.get(packageInfo.packageName);
        if (pluginPackage != null) {
            return pluginPackage;
        }

        DexClassLoader loader = LoaderUtils.getDexClassLoader(mContext, path, mNativeLibDir);
        AssetManager assetManager = LoaderUtils.getAssetManager(path);
        Resources resources = LoaderUtils.getResources(mContext, assetManager);

        pluginPackage = new PluginPackage(loader, resources, packageInfo);

        mAppContainer.put(packageInfo.packageName, pluginPackage);
        return pluginPackage;
    }

    public int startPluginActivity(Context context, PluginIntent intent) {
        return startPluginActivity(context, intent, -1);
    }

    public int startPluginActivity(Context context, PluginIntent intent, int requestCode) {

        String packageName = intent.getPluginPackage();
        if (TextUtils.isEmpty(packageName)) {
            return -1;
        }

        PluginPackage pluginPackage = mAppContainer.get(packageName);
        if (pluginPackage == null) {

            return -1;
        }

        final String className = getPluginActivityFullPath(intent, pluginPackage);
        Class<?> clazz = loadPluginClass(pluginPackage.classLoader, className);

        if (clazz == null) {
            return -1;
        }

        Class<? extends Activity> activityClass = getProxyActivityClass(clazz);
        intent.putExtra(PluginConstants.EXTRA_PACKAGE,packageName);
        intent.putExtra(PluginConstants.EXTRA_CLASS,className);
        intent.setClass(mContext,activityClass);
        performStartActivityForResult(context,intent,requestCode);
        return 0;
    }

    private String getPluginActivityFullPath(PluginIntent intent, PluginPackage pluginPackage) {
        String className = intent.getPluginClass();
        className = (className == null ? pluginPackage.mainActivity : className);
        if (className.startsWith(".")) {
            className = intent.getPluginPackage() + className;
        }
        return className;
    }

    private Class<?> loadPluginClass(ClassLoader classLoader, String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }

    public PluginPackage getPluginPackage(String packageName) {
        if (mAppContainer != null && mAppContainer.size() > 0) {
            return mAppContainer.get(packageName);
        }
        return null;
    }

    private Class<? extends Activity> getProxyActivityClass(Class<?> clazz) {
        Class<? extends Activity> activityClass = null;
        if (PluginActivity.class.isAssignableFrom(clazz)) {
            activityClass = ProxyActivity.class;
        }

        return activityClass;
    }

    private void performStartActivityForResult(Context context,PluginIntent intent,int requestCode){
        if(context instanceof Activity){
            ((Activity) context).startActivityForResult(intent,requestCode);
        }else {
            context.startActivity(intent);
        }
    }
}
