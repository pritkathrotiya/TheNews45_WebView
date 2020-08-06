package androidx.appcompat.widget;

import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build.VERSION;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.core.graphics.drawable.WrappedDrawable;

public class DrawableUtils {
    public static final Rect INSETS_NONE = new Rect();
    private static final String TAG = "DrawableUtils";
    private static final String VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable";
    private static Class<?> sInsetsClazz;

    static {
        if (VERSION.SDK_INT >= 18) {
            try {
                sInsetsClazz = Class.forName("android.graphics.Insets");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    private DrawableUtils() {
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Rect getOpticalBounds(android.graphics.drawable.Drawable r14) {
        /*
            java.lang.Class<?> r0 = sInsetsClazz
            if (r0 == 0) goto L_0x0098
            android.graphics.drawable.Drawable r0 = androidx.core.graphics.drawable.DrawableCompat.unwrap(r14)     // Catch:{ Exception -> 0x0090 }
            r14 = r0
            java.lang.Class r0 = r14.getClass()     // Catch:{ Exception -> 0x0090 }
            java.lang.String r1 = "getOpticalInsets"
            r2 = 0
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch:{ Exception -> 0x0090 }
            java.lang.reflect.Method r0 = r0.getMethod(r1, r3)     // Catch:{ Exception -> 0x0090 }
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0090 }
            java.lang.Object r1 = r0.invoke(r14, r1)     // Catch:{ Exception -> 0x0090 }
            if (r1 == 0) goto L_0x008f
            android.graphics.Rect r3 = new android.graphics.Rect     // Catch:{ Exception -> 0x0090 }
            r3.<init>()     // Catch:{ Exception -> 0x0090 }
            java.lang.Class<?> r4 = sInsetsClazz     // Catch:{ Exception -> 0x0090 }
            java.lang.reflect.Field[] r4 = r4.getFields()     // Catch:{ Exception -> 0x0090 }
            int r5 = r4.length     // Catch:{ Exception -> 0x0090 }
            r6 = 0
        L_0x002b:
            if (r6 >= r5) goto L_0x008e
            r7 = r4[r6]     // Catch:{ Exception -> 0x0090 }
            java.lang.String r8 = r7.getName()     // Catch:{ Exception -> 0x0090 }
            r9 = -1
            int r10 = r8.hashCode()     // Catch:{ Exception -> 0x0090 }
            r11 = 3
            r12 = 2
            r13 = 1
            switch(r10) {
                case -1383228885: goto L_0x005d;
                case 115029: goto L_0x0053;
                case 3317767: goto L_0x0049;
                case 108511772: goto L_0x003f;
                default: goto L_0x003e;
            }     // Catch:{ Exception -> 0x0090 }
        L_0x003e:
            goto L_0x0066
        L_0x003f:
            java.lang.String r10 = "right"
            boolean r8 = r8.equals(r10)     // Catch:{ Exception -> 0x0090 }
            if (r8 == 0) goto L_0x003e
            r9 = 2
            goto L_0x0066
        L_0x0049:
            java.lang.String r10 = "left"
            boolean r8 = r8.equals(r10)     // Catch:{ Exception -> 0x0090 }
            if (r8 == 0) goto L_0x003e
            r9 = 0
            goto L_0x0066
        L_0x0053:
            java.lang.String r10 = "top"
            boolean r8 = r8.equals(r10)     // Catch:{ Exception -> 0x0090 }
            if (r8 == 0) goto L_0x003e
            r9 = 1
            goto L_0x0066
        L_0x005d:
            java.lang.String r10 = "bottom"
            boolean r8 = r8.equals(r10)     // Catch:{ Exception -> 0x0090 }
            if (r8 == 0) goto L_0x003e
            r9 = 3
        L_0x0066:
            if (r9 == 0) goto L_0x0084
            if (r9 == r13) goto L_0x007d
            if (r9 == r12) goto L_0x0076
            if (r9 == r11) goto L_0x006f
            goto L_0x008b
        L_0x006f:
            int r8 = r7.getInt(r1)     // Catch:{ Exception -> 0x0090 }
            r3.bottom = r8     // Catch:{ Exception -> 0x0090 }
            goto L_0x008b
        L_0x0076:
            int r8 = r7.getInt(r1)     // Catch:{ Exception -> 0x0090 }
            r3.right = r8     // Catch:{ Exception -> 0x0090 }
            goto L_0x008b
        L_0x007d:
            int r8 = r7.getInt(r1)     // Catch:{ Exception -> 0x0090 }
            r3.top = r8     // Catch:{ Exception -> 0x0090 }
            goto L_0x008b
        L_0x0084:
            int r8 = r7.getInt(r1)     // Catch:{ Exception -> 0x0090 }
            r3.left = r8     // Catch:{ Exception -> 0x0090 }
        L_0x008b:
            int r6 = r6 + 1
            goto L_0x002b
        L_0x008e:
            return r3
        L_0x008f:
            goto L_0x0098
        L_0x0090:
            r0 = move-exception
            java.lang.String r1 = "DrawableUtils"
            java.lang.String r2 = "Couldn't obtain the optical insets. Ignoring."
            android.util.Log.e(r1, r2)
        L_0x0098:
            android.graphics.Rect r0 = INSETS_NONE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.DrawableUtils.getOpticalBounds(android.graphics.drawable.Drawable):android.graphics.Rect");
    }

    static void fixDrawable(Drawable drawable) {
        if (VERSION.SDK_INT == 21) {
            if (VECTOR_DRAWABLE_CLAZZ_NAME.equals(drawable.getClass().getName())) {
                fixVectorDrawableTinting(drawable);
            }
        }
    }

    public static boolean canSafelyMutateDrawable(Drawable drawable) {
        if (VERSION.SDK_INT < 15 && (drawable instanceof InsetDrawable)) {
            return false;
        }
        if (VERSION.SDK_INT < 15 && (drawable instanceof GradientDrawable)) {
            return false;
        }
        if (VERSION.SDK_INT < 17 && (drawable instanceof LayerDrawable)) {
            return false;
        }
        if (drawable instanceof DrawableContainer) {
            ConstantState state = drawable.getConstantState();
            if (state instanceof DrawableContainerState) {
                for (Drawable child : ((DrawableContainerState) state).getChildren()) {
                    if (!canSafelyMutateDrawable(child)) {
                        return false;
                    }
                }
            }
        } else if (drawable instanceof WrappedDrawable) {
            return canSafelyMutateDrawable(((WrappedDrawable) drawable).getWrappedDrawable());
        } else {
            if (drawable instanceof DrawableWrapper) {
                return canSafelyMutateDrawable(((DrawableWrapper) drawable).getWrappedDrawable());
            }
            if (drawable instanceof ScaleDrawable) {
                return canSafelyMutateDrawable(((ScaleDrawable) drawable).getDrawable());
            }
        }
        return true;
    }

    private static void fixVectorDrawableTinting(Drawable drawable) {
        int[] originalState = drawable.getState();
        if (originalState == null || originalState.length == 0) {
            drawable.setState(ThemeUtils.CHECKED_STATE_SET);
        } else {
            drawable.setState(ThemeUtils.EMPTY_STATE_SET);
        }
        drawable.setState(originalState);
    }

    public static Mode parseTintMode(int value, Mode defaultMode) {
        if (value == 3) {
            return Mode.SRC_OVER;
        }
        if (value == 5) {
            return Mode.SRC_IN;
        }
        if (value == 9) {
            return Mode.SRC_ATOP;
        }
        switch (value) {
            case 14:
                return Mode.MULTIPLY;
            case 15:
                return Mode.SCREEN;
            case 16:
                return Mode.ADD;
            default:
                return defaultMode;
        }
    }
}
