package com.sorcererxw.intentinterceptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sorcererxw.intentinterceptor.utils.DataUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * @description:
 * @author: Sorcerer
 * @date: 2016/12/4
 */

public class Xposed implements IXposedHookLoadPackage {
    String TAG = "拦截器-";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        final String packageName = lpparam.packageName;
        findAndHookMethod("android.app.Activity", lpparam.classLoader,
                "startActivityForResult", Intent.class, int.class, Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Intent intent = (Intent) param.args[0];
                        Context context = (Context) param.thisObject;
                        XposedBridge.log("replaceHookedMethod");

                        String dataString = intent.getDataString();
                        if (false) {
//                if (!TextUtils.isEmpty(dataString)) {
                            XposedBridge.log(TAG + "--" + dataString);
                            Toast.makeText(context, "--" + dataString, Toast.LENGTH_SHORT).show();
                            param.setResult(null);
                        } else {
                            int requestCode = (int) param.args[1];
                            Bundle bundle = (Bundle) param.args[2];
                            String str = DataUtil.parser(intent, requestCode, bundle,
                                    param.thisObject.getClass().getName());
                            Log.e("日志拦截", str);

                            Intent intent1 = new Intent();
                            intent1.setAction("GET_INTENT");
                            intent1.putExtra("info", str);
                            ((Context) param.thisObject).sendBroadcast(intent1);
//                param.setResult(XposedBridge.invokeOriginalMethod(param.method, context, param.args));
                        }
                    }
                });
    }
}
