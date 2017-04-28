package tomato.pluginlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author yeshuxin on 17-3-31.
 */

public class PluginActivity extends Activity implements DLPlugin{

    private Activity mProxyAct;
    private Activity that;
    private PluginManager mPluginManager;
    private PluginPackage mPluginPackage;
    private int mFrom;

    @Override
    public void attach(Activity proxyActivity, PluginPackage pluginPackage) {

        mProxyAct = proxyActivity;
        that = proxyActivity;
        mPluginPackage = pluginPackage;
    }

    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            this.mFrom = savedInstanceState.getInt("extra.from", 0);
        }

        if(this.mFrom == 0) {
            super.onCreate(savedInstanceState);
            this.mProxyAct = this;
            this.that = mProxyAct;
        }

        this.mPluginManager = PluginManager.getInstance(this.that);
        Log.d("DLBasePluginActivity", "onCreate: from= " + (this.mFrom == 0?"DLConstants.FROM_INTERNAL":"FROM_EXTERNAL"));
    }

    public void setContentView(View view) {
        if(this.mFrom == 0) {
            super.setContentView(view);
        } else {
            this.mProxyAct.setContentView(view);
        }

    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if(this.mFrom == 0) {
            super.setContentView(view, params);
        } else {
            this.mProxyAct.setContentView(view, params);
        }

    }

    public void setContentView(int layoutResID) {
        if(this.mFrom == 0) {
            super.setContentView(layoutResID);
        } else {
            this.mProxyAct.setContentView(layoutResID);
        }

    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if(this.mFrom == 0) {
            super.addContentView(view, params);
        } else {
            this.mProxyAct.addContentView(view, params);
        }

    }

    public View findViewById(int id) {
        return this.mFrom == 0?super.findViewById(id):this.mProxyAct.findViewById(id);
    }

    public Intent getIntent() {
        return this.mFrom == 0?super.getIntent():this.mProxyAct.getIntent();
    }

    public ClassLoader getClassLoader() {
        return this.mFrom == 0?super.getClassLoader():this.mProxyAct.getClassLoader();
    }

    public Resources getResources() {
        return this.mFrom == 0?super.getResources():this.mProxyAct.getResources();
    }

    public String getPackageName() {
        return this.mFrom == 0?super.getPackageName():this.mPluginPackage.packageName;
    }

    public LayoutInflater getLayoutInflater() {
        return this.mFrom == 0?super.getLayoutInflater():this.mProxyAct.getLayoutInflater();
    }

    public MenuInflater getMenuInflater() {
        return this.mFrom == 0?super.getMenuInflater():this.mProxyAct.getMenuInflater();
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return this.mFrom == 0?super.getSharedPreferences(name, mode):this.mProxyAct.getSharedPreferences(name, mode);
    }

    public Context getApplicationContext() {
        return this.mFrom == 0?super.getApplicationContext():this.mProxyAct.getApplicationContext();
    }

    public WindowManager getWindowManager() {
        return this.mFrom == 0?super.getWindowManager():this.mProxyAct.getWindowManager();
    }

    public Window getWindow() {
        return this.mFrom == 0?super.getWindow():this.mProxyAct.getWindow();
    }

    public Object getSystemService(String name) {
        return this.mFrom == 0?super.getSystemService(name):this.mProxyAct.getSystemService(name);
    }

    public void finish() {
        if(this.mFrom == 0) {
            super.finish();
        } else {
            this.mProxyAct.finish();
        }

    }

    public void onBackPressed() {
        if(this.mFrom == 0) {
            super.onBackPressed();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(this.mFrom == 0) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void onStart() {
        if(this.mFrom == 0) {
            super.onStart();
        }

    }

    public void onRestart() {
        if(this.mFrom == 0) {
            super.onRestart();
        }

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(this.mFrom == 0) {
            super.onRestoreInstanceState(savedInstanceState);
        }

    }

    public void onSaveInstanceState(Bundle outState) {
        if(this.mFrom == 0) {
            super.onSaveInstanceState(outState);
        }

    }

    public void onNewIntent(Intent intent) {
        if(this.mFrom == 0) {
            super.onNewIntent(intent);
        }

    }

    public void onResume() {
        if(this.mFrom == 0) {
            super.onResume();
        }

    }

    public void onPause() {
        if(this.mFrom == 0) {
            super.onPause();
        }

    }

    public void onStop() {
        if(this.mFrom == 0) {
            super.onStop();
        }

    }

    public void onDestroy() {
        if(this.mFrom == 0) {
            super.onDestroy();
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mFrom == 0?super.onTouchEvent(event):false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mFrom == 0?super.onKeyUp(keyCode, event):false;
    }

    public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams params) {
        if(this.mFrom == 0) {
            super.onWindowAttributesChanged(params);
        }

    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if(this.mFrom == 0) {
            super.onWindowFocusChanged(hasFocus);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return this.mFrom == 0?super.onCreateOptionsMenu(menu):true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return this.mFrom == 0?this.onOptionsItemSelected(item):false;
    }

    public int startPluginActivity(PluginIntent intent) {
        return this.startPluginActivityForResult(intent, -1);
    }

    public int startPluginActivityForResult(PluginIntent intent, int requestCode) {
        if(this.mFrom == 1 && intent.getPluginPackage() == null) {
            intent.setPackageName(this.mPluginPackage.packageName);
        }

        return this.mPluginManager.startPluginActivity(this.that, intent, requestCode);
    }

    /*public int startPluginService(PluginIntent intent) {
        if(this.mFrom == 1 && dlIntent.getPluginPackage() == null) {
            intent.setPluginPackage(this.mPluginPackage.packageName);
        }

        return this.mPluginManager.startPluginService(this.that, intent);
    }*/

   /* public int stopPluginService(PluginIntent intent) {
        if(this.mFrom == 1 && intent.getPluginPackage() == null) {
            intent.setPluginPackage(this.mPluginPackage.packageName);
        }

        return this.mPluginManager.stopPluginService(this.that, dlIntent);
    }
*/
   /* public int bindPluginService(PluginIntent dlIntent, ServiceConnection conn, int flags) {
        if(this.mFrom == 1 && dlIntent.getPluginPackage() == null) {
            dlIntent.setPluginPackage(this.mPluginPackage.packageName);
        }

        return this.mPluginManager.bindPluginService(this.that, dlIntent, conn, flags);
    }*/

  /*  public int unBindPluginService(PluginIntent dlIntent, ServiceConnection conn) {
        if(this.mFrom == 1 && dlIntent.getPluginPackage() == null) {
            dlIntent.setPluginPackage(this.mPluginPackage.packageName);
        }

        return this.mPluginManager.unBindPluginService(this.that, dlIntent, conn);
    }*/
}
