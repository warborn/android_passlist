package mx.unam.passlist;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ivan on 22/05/2017.
 */

/**
 * Class that contains helper methods used to work with files
 */
public class FileUtils {
    // Returns a file located in the assets directory
    // Unpacks the file from the apk and copy it's content into a new file
    public static final File getFileFromAssets(Context context, String assetPath, String filename) {
        AssetManager am = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = am.open(assetPath);
        } catch(IOException e) {
            e.printStackTrace();
        }
        File internalStorageFile = new File(context.getFilesDir(), filename);
        File file = createFileFromInputStream(inputStream, internalStorageFile);
        return file;
    }

    // Fill a given file with the contents of an input stream
    private static final File createFileFromInputStream(InputStream inputStream, File newFile) {
        try{
            FileOutputStream outputStream = new FileOutputStream(newFile);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return newFile;
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
