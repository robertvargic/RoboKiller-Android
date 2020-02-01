package co.teltech.callblocker.utils;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class DataPersistUtil {

    public static void store(Context context, String filename, Object object) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Logger.e(e, e.getMessage());
        } catch (IOException e) {
            Logger.e(e, e.getMessage());
        }
    }

    public static <T extends Object> T load(Context context, String filename, Class<T> type) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            T object = (T) is.readObject();
            is.close();
            fis.close();

            return object;
        } catch (FileNotFoundException e) {
            Logger.e(e, e.getMessage());
        } catch (IOException e) {
            Logger.e(e, e.getMessage());
        } catch (ClassNotFoundException e) {
            Logger.e(e, e.getMessage());
        }
        return null;
    }

}
