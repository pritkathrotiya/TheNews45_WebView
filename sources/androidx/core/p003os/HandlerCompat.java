package androidx.core.p003os;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;

/* renamed from: androidx.core.os.HandlerCompat */
public final class HandlerCompat {
    public static boolean postDelayed(Handler handler, Runnable r, Object token, long delayMillis) {
        if (VERSION.SDK_INT >= 28) {
            return handler.postDelayed(r, token, delayMillis);
        }
        Message message = Message.obtain(handler, r);
        message.obj = token;
        return handler.sendMessageDelayed(message, delayMillis);
    }

    private HandlerCompat() {
    }
}
