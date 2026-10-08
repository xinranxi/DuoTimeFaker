package com.duo.timezone;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.time.ZoneId;
import java.util.TimeZone;

public class MainHook implements IXposedHookLoadPackage {
    private static final String TARGET_TIMEZONE = "Asia/Tokyo";
    private static final String TAG = "[DuoTimeFaker] ";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"com.duolingo".equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log(TAG + "Active in process: " + lpparam.processName);

        // 1. Hook java.util.TimeZone.getDefault()
        try {
            XposedHelpers.findAndHookMethod(TimeZone.class, "getDefault", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(TimeZone.getTimeZone(TARGET_TIMEZONE));
                }
            });
            XposedBridge.log(TAG + "Hooked TimeZone.getDefault()");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook TimeZone.getDefault(): " + t.getMessage());
        }

        // 2. Hook java.time.ZoneId.systemDefault()
        try {
            XposedHelpers.findAndHookMethod(ZoneId.class, "systemDefault", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(ZoneId.of(TARGET_TIMEZONE));
                }
            });
            XposedBridge.log(TAG + "Hooked ZoneId.systemDefault()");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook ZoneId.systemDefault(): " + t.getMessage());
        }

        // 3. Hook android.icu.util.TimeZone.getDefault()
        try {
            Class<?> icuTzClass = XposedHelpers.findClassIfExists("android.icu.util.TimeZone", lpparam.classLoader);
            if (icuTzClass != null) {
                XposedHelpers.findAndHookMethod(icuTzClass, "getDefault", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object tokyoIcu = XposedHelpers.callStaticMethod(icuTzClass, "getTimeZone", TARGET_TIMEZONE);
                        if (tokyoIcu != null) {
                            param.setResult(tokyoIcu);
                        }
                    }
                });
                XposedBridge.log(TAG + "Hooked android.icu.util.TimeZone.getDefault()");
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook ICU TimeZone: " + t.getMessage());
        }

        // 4. Hook System.getProperty("user.timezone")
        try {
            XposedHelpers.findAndHookMethod(System.class, "getProperty", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if ("user.timezone".equals(param.args[0])) {
                        param.setResult(TARGET_TIMEZONE);
                    }
                }
            });
            XposedBridge.log(TAG + "Hooked System.getProperty('user.timezone')");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook System.getProperty: " + t.getMessage());
        }

        // 5. Hook Duolingo internal Time Provider (defpackage.luh)
        try {
            Class<?> luhClass = XposedHelpers.findClassIfExists("defpackage.luh", lpparam.classLoader);
            if (luhClass != null) {
                XposedHelpers.findAndHookMethod(luhClass, "f", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(ZoneId.of(TARGET_TIMEZONE));
                    }
                });
                XposedBridge.log(TAG + "Hooked Duolingo internal defpackage.luh.f()");
            }
        } catch (Throwable t) {
        }

        // 6. Hook ConnectionQuality (defpackage.ef8): 消灭 POOR 状态，放宽看门狗阈值
        try {
            Class<?> ef8Class = XposedHelpers.findClassIfExists("defpackage.ef8", lpparam.classLoader);
            Class<?> catEnumClass = XposedHelpers.findClassIfExists("com.duolingo.videocall.data.ConnectionQuality$Category", lpparam.classLoader);
            if (ef8Class != null && catEnumClass != null) {
                final Object goodCategory = Enum.valueOf((Class<Enum>) catEnumClass, "GOOD");
                XposedBridge.hookAllConstructors(ef8Class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // 无论真实公网延迟如何波动，永远将网络质量判定为 GOOD，彻底免疫看门狗掐断
                        if (param.args != null && param.args.length > 0) {
                            param.args[0] = goodCategory;
                        }
                    }
                });
                XposedBridge.log(TAG + "Hooked ConnectionQuality (Watchdog relaxed to always GOOD)!");
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook ConnectionQuality: " + t.getMessage());
        }
    }
}
