package tomato.pluginlib;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.lang.reflect.Constructor;

import static android.R.style.Theme;

/**
 * @author yeshuxin on 17-3-31.
 */

public class ProxyImpl {

    private Activity mProxyAct;

    private String mClass;
    private ActivityInfo mActInfo;
    private String mPackageName;
    private PluginPackage mPluginPackage;
    private PluginManager mPluginManager;
    private DLPlugin mPluginAct;

    private Theme mTheme;
    private AssetManager mAssetManager;
    private Resources mResources;

    public ProxyImpl(Activity activity) {

        mProxyAct = activity;
    }

    private void initActivityInfo() {
        PackageInfo packageInfo = mPluginPackage.packageInfo;
        if (packageInfo.activities != null && packageInfo.activities.length > 0) {
            if (mClass == null) {
                mClass = packageInfo.activities[0].name;
            }

            if (TextUtils.isEmpty(mClass)) {
                return;
            }

            int defaultTheme = packageInfo.applicationInfo.theme;
            for (ActivityInfo info : packageInfo.activities) {
                if (mClass.equals(info.name)){
                    mActInfo = info;
                    if(mActInfo.theme == 0){
                        mActInfo.theme = defaultTheme;
                    }else {
                        if(Build.VERSION.SDK_INT >= 14){
                            mActInfo.theme = android.R.style.Theme_DeviceDefault;
                        }else {
                            mActInfo.theme = Theme;
                        }
                    }
                }
            }
        }
    }

    private void handleActivityInfo(){

        if(mActInfo.theme > 0){
            mProxyAct.setTheme(mActInfo.getThemeResource());
        }

        Theme superTheme = mProxyAct.getTheme();
        mTheme = mPluginPackage.resources.newTheme();
        mTheme.setTo(superTheme);
        // Finals适配三星以及部分加载XML出现异常BUG
        try {
            mTheme.applyStyle(mActInfo.theme, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate(Intent intent){
        intent.setExtrasClassLoader(DLConfigs.sPluginClassloader);
        mPackageName = intent.getStringExtra(PluginConstants.EXTRA_PACKAGE);
        mClass = intent.getStringExtra(PluginConstants.EXTRA_CLASS);
        mPluginManager = PluginManager.getInstance(mProxyAct);
        mPluginPackage = mPluginManager.getPluginPackage(mPackageName);
        mAssetManager = mPluginPackage.assetManager;
        mResources = mPluginPackage.resources;

        initActivityInfo();
        handleActivityInfo();
        launchTargetAct();

    }

    public void launchTargetAct(){
        try {
            Class<?> localClass = getClassLoader().loadClass(mClass);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});
            Object instance = localConstructor.newInstance(new Object[]{});
            mPluginAct = (DLPlugin) instance;
            ((DLAttach)mProxyAct).attach(mPluginAct,mPluginManager);

            mPluginAct.attach(mProxyAct,mPluginPackage);

            Bundle bundle = new Bundle();
            bundle.putInt(PluginConstants.FROM,PluginConstants.FROM_EXTERNAL);
            mPluginAct.onCreate(bundle);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public AssetManager getAssets() {
        return mAssetManager;
    }

    public Resources getResources() {
        return mResources;
    }

    public Resources.Theme getTheme() {
        return mTheme;
    }

    public ClassLoader getClassLoader(){
        return mPluginPackage.classLoader;
    }


}
