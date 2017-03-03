package ljun.testlibrary;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ljun on 2017/2/4 0004.
 */

public class T {

    public static void s(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
