package tomato.pluginlib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import tomato.pluginlib.DLAttach;
import tomato.pluginlib.DLPlugin;
import tomato.pluginlib.PluginManager;
import tomato.pluginlib.ProxyImpl;

/**
 * @author yeshuxin on 17-3-31.
 */

public class ProxyActivity extends Activity implements DLAttach {

    private DLPlugin mLoaderActivity;
    private ProxyImpl mProxy = new ProxyImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxy.onCreate(getIntent());
    }

    @Override
    public void attach(DLPlugin proxy, PluginManager manager) {
        mLoaderActivity = proxy;
    }

    @Override
    public AssetManager getAssets() {
        return mProxy.getAssets() == null ? super.getAssets() : mProxy.getAssets();
    }

    @Override
    public Resources getResources() {
        Resources resources = mProxy.getResources() == null ? super.getResources() : mProxy.getResources();
        return resources;
    }

    @Override
    public Resources.Theme getTheme() {
        return mProxy.getTheme() == null ? super.getTheme() : mProxy.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mProxy.getClassLoader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLoaderActivity.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        mLoaderActivity.onStart();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        mLoaderActivity.onRestart();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mLoaderActivity.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLoaderActivity.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLoaderActivity.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLoaderActivity.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLoaderActivity.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLoaderActivity.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mLoaderActivity.onNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        mLoaderActivity.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mLoaderActivity.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        return mLoaderActivity.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        mLoaderActivity.onWindowAttributesChanged(params);
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mLoaderActivity.onWindowFocusChanged(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mLoaderActivity.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mLoaderActivity.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

}
