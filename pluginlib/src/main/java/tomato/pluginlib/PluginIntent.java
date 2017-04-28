package tomato.pluginlib;

import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author yeshuxin on 17-3-31.
 */

public class PluginIntent extends Intent {

    private String pluginPackage;
    private String pluginClass;

    public PluginIntent() {
        super();
    }

    public PluginIntent(String p, String c) {
        super();
        pluginPackage = p;
        pluginClass = c;
    }

    public String getPluginPackage() {
        return pluginPackage;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    @Override
    public Intent putExtra(String name, Parcelable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }

    @Override
    public Intent putExtra(String name, Serializable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }

    private void setupExtraClassLoader(Object value) {
        ClassLoader pluginLoader = value.getClass().getClassLoader();
        setExtrasClassLoader(pluginLoader);
    }

    public void setPackageName(String packageInfo) {
        pluginPackage = packageInfo;
    }
}
