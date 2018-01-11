package nim.shs1330.netease.com.multidext;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;


/**
 * Created by shs1330 on 2018/1/8.
 */

public class CustomApplication extends Application {
    private static final String TAG = "CustomApplication";

    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class clazz = instance.getClass();

        while(clazz != null) {
            try {
                Field e = clazz.getDeclaredField(name);
                if(!e.isAccessible()) {
                    e.setAccessible(true);
                }

                return e;
            } catch (NoSuchFieldException var4) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            ApplicationInfo info = base.getPackageManager().getApplicationInfo(base.getPackageName(), PackageManager.GET_META_DATA);
            Log.d(TAG, "attachBaseContext: " + info.sourceDir);
            File sourceApk = new File(info.sourceDir);
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(
                            new FileInputStream(sourceApk)));
            findField(base.getClassLoader(), "dexElementsSuppressedExceptions");
            while (true) {
                ZipEntry entry = zis.getNextEntry();
                if ((entry == null)) {
                    zis.close();
                    break;
                }
                String name = entry.getName();
                Log.d(TAG, "attachBaseContext: " + info.sourceDir + "下" + name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        MultiDex.install(base);
        Client.init(base);

        FileHelper.extractAssets("app-debug.apk");
        File dexFile = getFileStreamPath("app-debug.apk");
        File optDexFile = getFileStreamPath("app-debug.dex");

        try {
            pathClassLoader(dexFile, getClassLoader(), optDexFile);

            Class c = Class.forName("nim.shs1330.netease.com.pluginone.MainActivity");
            Object o = c.newInstance();

            Log.d(TAG, "attachBaseContext: " + o.getClass());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void pathClassLoader(File apkFile, ClassLoader cl, File optDexFile) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, IOException, InvocationTargetException, InstantiationException {
        Class baseDexClassLoaderC = DexClassLoader.class.getSuperclass();
        Field pathListF = baseDexClassLoaderC.getDeclaredField("pathList");
        pathListF.setAccessible(true);
        Object pathListO = pathListF.get(cl);

        //dexPathList类型
        Class dexPathListC = pathListO.getClass();
        Field dexElementsF = dexPathListC.getDeclaredField("dexElements");
        dexElementsF.setAccessible(true);

        Object[] dexElementsO = (Object[]) dexElementsF.get(pathListO);
        //Element类型
        Class elementC = dexElementsO.getClass().getComponentType();
        //新数组
        Object[] newElements = (Object[]) Array.newInstance(elementC, dexElementsO.length + 1);

        //Element数组
        Constructor<?> constructor = elementC.getConstructor(File.class, boolean.class, File.class, DexFile.class);
        //插件中Apk的Element数组
        Object o = constructor.newInstance(
                apkFile,
                false,
                apkFile,
                DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
        Object[] toAddElementArray = new Object[] { o };

        System.arraycopy(dexElementsO, 0, newElements, 0, dexElementsO.length);
        System.arraycopy(toAddElementArray, 0, newElements, dexElementsO.length, toAddElementArray.length);

        dexElementsF.set(pathListO, newElements);
    }
}
