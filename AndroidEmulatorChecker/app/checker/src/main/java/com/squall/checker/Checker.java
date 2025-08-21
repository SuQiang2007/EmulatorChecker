package com.squall.checker;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Checker {

    private static final String TAG = "EmulatorChecker";

    private static final String[] PKG_NAMES = {"com.mumu.launcher", "com.ami.duosupdater.ui", "com.ami.launchmetro",
            "com.ami.syncduosservices", "com.bluestacks.home", "com.bluestacks.windowsfilemanager",
            "com.bluestacks.settings", "com.bluestacks.bluestackslocationprovider", "com.bluestacks.appsettings",
            "com.bluestacks.bstfolder", "com.bluestacks.BstCommandProcessor", "com.bluestacks.s2p", "com.bluestacks.setup",
            "com.bluestacks.appmart", "com.kaopu001.tiantianserver", "com.kpzs.helpercenter", "com.kaopu001.tiantianime",
            "com.android.development_settings", "com.android.development", "com.android.customlocale2", "com.genymotion.superuser",
            "com.genymotion.clipboardproxy", "com.uc.xxzs.keyboard", "com.uc.xxzs", "com.blue.huang17.agent", "com.blue.huang17.launcher",
            "com.blue.huang17.ime", "com.microvirt.guide", "com.microvirt.market", "com.microvirt.memuime", "cn.itools.vm.launcher",
            "cn.itools.vm.proxy", "cn.itools.vm.softkeyboard", "cn.itools.avdmarket", "com.syd.IME", "com.bignox.app.store.hd",
            "com.bignox.launcher", "com.bignox.app.phone", "com.bignox.app.noxservice", "com.android.noxpush", "com.haimawan.push",
            "me.haima.helpcenter", "com.windroy.launcher", "com.windroy.superuser", "com.windroy.launcher", "com.windroy.ime",
            "com.android.flysilkworm", "com.android.emu.inputservice", "com.tiantian.ime", "com.microvirt.launcher", "me.le8.androidassist",
            "com.vphone.helper", "com.vphone.launcher", "com.duoyi.giftcenter.giftcenter"};

    private static final String[] FILES = {"/data/data/com.android.flysilkworm", "/data/data/com.bluestacks.filemanager"};

    public static String checkFeaturesByHardware(Context context) {
        String result = "";
        String hardware = getProperty("ro.hardware");
        if (null == hardware)
            return "unknown";
        String tempValue = hardware.toLowerCase();
        Log.d(TAG,tempValue);
        if(tempValue.startsWith("cancro")){
            result = "MUMU模拟器";
        }else if(tempValue.contains("nox")){
            result = "夜神模拟器";
        }else if(tempValue.equals("android_x86")){
            result= "雷电模拟器";
        }else{
            List pathList = getInstalledSimulatorPackages(context);
            result =  getSimulatorBrand(pathList);
        }
        return result;
    }

    private static String getProperty(String propName) {
        String value = null;
        Object roSecureObj;
        try {
            roSecureObj = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, propName);
            if (roSecureObj != null) value = (String) roSecureObj;
        } catch (Exception e) {
            value = null;
        } finally {
            return value;
        }
    }


    private static List getInstalledSimulatorPackages(Context context) {
        ArrayList localArrayList = new ArrayList();
        try {
            for (int i = 0; i < PKG_NAMES.length; i++)
                try {
                    context.getPackageManager().getPackageInfo(PKG_NAMES[i], PackageManager.GET_ACTIVITIES);
                    localArrayList.add(PKG_NAMES[i]);
                } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
                }
            if (localArrayList.size() == 0) {
                for (int i = 0; i < FILES.length; i++) {
                    if (new File(FILES[i]).exists())
                        localArrayList.add(FILES[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localArrayList;
    }

    private static String getSimulatorBrand(List<String> list) {
        if (list.size() == 0)
            return "";
        String pkgName = list.get(0);
        if (pkgName.contains("mumu")) {
            return "mumu";
        } else if (pkgName.contains("ami")) {
            return "AMIDuOS";
        } else if (pkgName.contains("bluestacks")) {
            return "蓝叠";
        } else if (pkgName.contains("kaopu001") || pkgName.contains("tiantian")) {
            return "天天";
        } else if (pkgName.contains("kpzs")) {
            return "靠谱助手";
        } else if (pkgName.contains("genymotion")) {
            if (Build.MODEL.contains("iTools")) {
                return "iTools";
            } else if ((Build.MODEL.contains("ChangWan"))) {
                return "畅玩";
            } else {
                return "genymotion";
            }
        } else if (pkgName.contains("uc")) {
            return "uc";
        } else if (pkgName.contains("blue")) {
            return "blue";
        } else if (pkgName.contains("microvirt")) {
            return "逍遥";
        } else if (pkgName.contains("itools")) {
            return "itools";
        } else if (pkgName.contains("syd")) {
            return "手游岛";
        } else if (pkgName.contains("bignox")) {
            return "夜神";
        } else if (pkgName.contains("haimawan")) {
            return "海马玩";
        } else if (pkgName.contains("windroy")) {
            return "windroy";
        } else if (pkgName.contains("flysilkworm")) {
            return "雷电";
        } else if (pkgName.contains("emu")) {
            return "emu";
        } else if (pkgName.contains("le8")) {
            return "le8";
        } else if (pkgName.contains("vphone")) {
            return "vphone";
        } else if (pkgName.contains("duoyi")) {
            return "多益";
        }
        return "";
    }


    public static boolean isEmulator(Context context){
        return notHasLightSensorManager(context)
                ||isFeatures()
                ||checkIsNotRealPhone()
                ||checkPipes() ||isYeshenEmulator();
    }


    public static String getPhoneBrand(){
        return android.os.Build.BRAND;
    }

    public static String getPhoneModel(){
        return  android.os.Build.MODEL;
    }

    /*
     *用途:判断蓝牙是否有效来判断是否为模拟器
     *返回:true 为模拟器
     */
//    private static boolean notHasBlueTooth() {
//        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
//        if (ba == null) {
//            return true;
//        } else {
//            // 如果有蓝牙不一定是有效的。获取蓝牙名称，若为null 则默认为模拟器
//            String name = ba.getName();
//            if (TextUtils.isEmpty(name)) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//    }

    /*
     *用途:依据是否存在光传感器来判断是否为模拟器
     *返回:true 为模拟器
     */
    private static Boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null == sensor8) {
            return true;
        } else {
            return false;
        }
    }

    /*
     *用途:根据部分特征参数设备信息来判断是否为模拟器
     *返回:true 为模拟器
     */
    private static boolean isFeatures() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    /*
     *用途:根据CPU是否为电脑来判断是否为模拟器
     *返回:true 为模拟器
     */
    private static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /*
     *用途:根据CPU是否为电脑来判断是否为模拟器(子方法)
     *返回:String
     */
    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    /*
     *用途:检测模拟器的特有文件
     *返回:true 为模拟器
     */
    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};
    private static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                Log.v("Result:", "Find pipes!");
                return true;
            }
        }
        Log.i("Result:", "Not Find pipes!");
        return false;
    }

    //****************适配夜神模拟器*******************
    //获取 cpu 信息
    private static String getCpuInfo() {
        String[] abis = new String[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for (String abi : abis) {
            abiStr.append(abi);
            abiStr.append(',');
        }

        return abiStr.toString();
    }

    // 通过cpu判断是否模拟器 ,适配夜神
    private static boolean isYeshenEmulator() {
        String abiStr = getCpuInfo();
        if (abiStr != null && abiStr.length() > 0) {
            boolean isSupportX86 = false;
            boolean isSupportArm = false;

            if (abiStr.contains("x86_64") || abiStr.contains("x86")) {
                isSupportX86 = true;
            }
            if (abiStr.contains("armeabi") || abiStr.contains("armeabi-v7a") || abiStr.contains("arm64-v8a")) {
                isSupportArm = true;
            }
            if (isSupportX86 && isSupportArm) {
                //同时拥有X86和arm的判断为模拟器。
                return true;
            }
        }
        return false;
    }


}