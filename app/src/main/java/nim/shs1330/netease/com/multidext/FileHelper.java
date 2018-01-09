package nim.shs1330.netease.com.multidext;

/**
 * Created by shs1330 on 2017/10/18.
 */

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 用于控制file的路径
 */
public class FileHelper {
    //存放data/data/<packagename>/file
    private static File mBase = null;

    /**
     * 将文件从assets复制到
     * @param sourceName data/data/<packagename>/files/sourceName文件夹下
     */
    public static void extractAssets(String sourceName){
        AssetManager am = Client.getContext().getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = Client.getContext().getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 返回对应plugin包名路径下的基础路径
     * @param packageName
     * @return data/data/<packageName>/files/plugin/<PlugInPackageName>
     */
    public static File getBasePluginDir(String packageName)
    {
        if (mBase == null){
            mBase = Client.getContext().getFileStreamPath("plugin");
            enforeFileExists(mBase);
        }
        return enforeFileExists(new File(mBase, packageName));
    }

    /**
     * 返回opt file
     * @param packagName
     * @return data/data/<packageName>/files/plugin/<PlugInPackageName>/odex
     */
    public static File getOptDir(String packagName){
        return  enforeFileExists(new File(getBasePluginDir(packagName), "odex"));
    }

    /**
     *
     * @param packagName
     * @return data/data/<packageName>/files/plugin/<PlugInPackageName>/lib
     */
    public static File getPluginLibDir(String packagName){
        return  enforeFileExists(new File(getBasePluginDir(packagName), "lib"));
    }

    /**
     * 确保文件存在
     * @param file
     * @return
     */
    private static File enforeFileExists(File file){
        if (!file.exists()){
            file.mkdir();
        }
        return file;
    }


}
