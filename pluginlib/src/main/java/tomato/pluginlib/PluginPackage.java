package tomato.pluginlib;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * @author yeshuxin on 17-3-30.
 */

public class PluginPackage {

    public String packageName;
    public String mainActivity;
    public DexClassLoader classLoader;
    public AssetManager assetManager;
    public Resources resources;
    public PackageInfo packageInfo;

    public PluginPackage(DexClassLoader loader, Resources res, PackageInfo info) {
        classLoader = loader;
        resources = res;
        packageInfo = info;
    }
}
