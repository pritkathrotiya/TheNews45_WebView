package androidx.core.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import androidx.collection.LruCache;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.graphics.TypefaceCompat;
import androidx.core.graphics.TypefaceCompatUtil;
import androidx.core.provider.SelfDestructiveThread.ReplyCallback;
import androidx.core.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontsContractCompat {
    private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
    public static final String PARCEL_FONT_RESULTS = "font_results";
    static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    private static final SelfDestructiveThread sBackgroundThread = new SelfDestructiveThread("fonts", 10, BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS);
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() {
        public int compare(byte[] l, byte[] r) {
            if (l.length != r.length) {
                return l.length - r.length;
            }
            for (int i = 0; i < l.length; i++) {
                if (l[i] != r[i]) {
                    return l[i] - r[i];
                }
            }
            return 0;
        }
    };
    static final Object sLock = new Object();
    static final SimpleArrayMap<String, ArrayList<ReplyCallback<TypefaceResult>>> sPendingReplies = new SimpleArrayMap<>();
    static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(16);

    public static final class Columns implements BaseColumns {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";
    }

    public static class FontFamilyResult {
        public static final int STATUS_OK = 0;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        public FontFamilyResult(int statusCode, FontInfo[] fonts) {
            this.mStatusCode = statusCode;
            this.mFonts = fonts;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }
    }

    public static class FontInfo {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        public FontInfo(Uri uri, int ttcIndex, int weight, boolean italic, int resultCode) {
            this.mUri = (Uri) Preconditions.checkNotNull(uri);
            this.mTtcIndex = ttcIndex;
            this.mWeight = weight;
            this.mItalic = italic;
            this.mResultCode = resultCode;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResultCode() {
            return this.mResultCode;
        }
    }

    public static class FontRequestCallback {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_SECURITY_VIOLATION = -4;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;
        public static final int RESULT_OK = 0;

        @Retention(RetentionPolicy.SOURCE)
        public @interface FontRequestFailReason {
        }

        public void onTypefaceRetrieved(Typeface typeface) {
        }

        public void onTypefaceRequestFailed(int reason) {
        }
    }

    private static final class TypefaceResult {
        final int mResult;
        final Typeface mTypeface;

        TypefaceResult(Typeface typeface, int result) {
            this.mTypeface = typeface;
            this.mResult = result;
        }
    }

    private FontsContractCompat() {
    }

    static TypefaceResult getFontInternal(Context context, FontRequest request, int style) {
        try {
            FontFamilyResult result = fetchFonts(context, null, request);
            int i = -3;
            if (result.getStatusCode() == 0) {
                Typeface typeface = TypefaceCompat.createFromFontInfo(context, null, result.getFonts(), style);
                if (typeface != null) {
                    i = 0;
                }
                return new TypefaceResult(typeface, i);
            }
            if (result.getStatusCode() == 1) {
                i = -2;
            }
            return new TypefaceResult(null, i);
        } catch (NameNotFoundException e) {
            return new TypefaceResult(null, -1);
        }
    }

    public static void resetCache() {
        sTypefaceCache.evictAll();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007c, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x008d, code lost:
        sBackgroundThread.postAndReply(r2, new androidx.core.provider.FontsContractCompat.C01243());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0097, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Typeface getFontSync(final android.content.Context r8, final androidx.core.provider.FontRequest r9, final androidx.core.content.res.ResourcesCompat.FontCallback r10, final android.os.Handler r11, boolean r12, int r13, final int r14) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r9.getIdentifier()
            r0.append(r1)
            java.lang.String r1 = "-"
            r0.append(r1)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            androidx.collection.LruCache<java.lang.String, android.graphics.Typeface> r1 = sTypefaceCache
            java.lang.Object r1 = r1.get(r0)
            android.graphics.Typeface r1 = (android.graphics.Typeface) r1
            if (r1 == 0) goto L_0x0028
            if (r10 == 0) goto L_0x0027
            r10.onFontRetrieved(r1)
        L_0x0027:
            return r1
        L_0x0028:
            if (r12 == 0) goto L_0x0045
            r2 = -1
            if (r13 != r2) goto L_0x0045
            androidx.core.provider.FontsContractCompat$TypefaceResult r2 = getFontInternal(r8, r9, r14)
            if (r10 == 0) goto L_0x0042
            int r3 = r2.mResult
            if (r3 != 0) goto L_0x003d
            android.graphics.Typeface r3 = r2.mTypeface
            r10.callbackSuccessAsync(r3, r11)
            goto L_0x0042
        L_0x003d:
            int r3 = r2.mResult
            r10.callbackFailAsync(r3, r11)
        L_0x0042:
            android.graphics.Typeface r3 = r2.mTypeface
            return r3
        L_0x0045:
            androidx.core.provider.FontsContractCompat$1 r2 = new androidx.core.provider.FontsContractCompat$1
            r2.<init>(r8, r9, r14, r0)
            r3 = 0
            if (r12 == 0) goto L_0x005a
            androidx.core.provider.SelfDestructiveThread r4 = sBackgroundThread     // Catch:{ InterruptedException -> 0x0058 }
            java.lang.Object r4 = r4.postAndWait(r2, r13)     // Catch:{ InterruptedException -> 0x0058 }
            androidx.core.provider.FontsContractCompat$TypefaceResult r4 = (androidx.core.provider.FontsContractCompat.TypefaceResult) r4     // Catch:{ InterruptedException -> 0x0058 }
            android.graphics.Typeface r3 = r4.mTypeface     // Catch:{ InterruptedException -> 0x0058 }
            return r3
        L_0x0058:
            r4 = move-exception
            return r3
        L_0x005a:
            if (r10 != 0) goto L_0x005e
            r4 = r3
            goto L_0x0063
        L_0x005e:
            androidx.core.provider.FontsContractCompat$2 r4 = new androidx.core.provider.FontsContractCompat$2
            r4.<init>(r10, r11)
        L_0x0063:
            java.lang.Object r5 = sLock
            monitor-enter(r5)
            androidx.collection.SimpleArrayMap<java.lang.String, java.util.ArrayList<androidx.core.provider.SelfDestructiveThread$ReplyCallback<androidx.core.provider.FontsContractCompat$TypefaceResult>>> r6 = sPendingReplies     // Catch:{ all -> 0x0098 }
            boolean r6 = r6.containsKey(r0)     // Catch:{ all -> 0x0098 }
            if (r6 == 0) goto L_0x007d
            if (r4 == 0) goto L_0x007b
            androidx.collection.SimpleArrayMap<java.lang.String, java.util.ArrayList<androidx.core.provider.SelfDestructiveThread$ReplyCallback<androidx.core.provider.FontsContractCompat$TypefaceResult>>> r6 = sPendingReplies     // Catch:{ all -> 0x0098 }
            java.lang.Object r6 = r6.get(r0)     // Catch:{ all -> 0x0098 }
            java.util.ArrayList r6 = (java.util.ArrayList) r6     // Catch:{ all -> 0x0098 }
            r6.add(r4)     // Catch:{ all -> 0x0098 }
        L_0x007b:
            monitor-exit(r5)     // Catch:{ all -> 0x0098 }
            return r3
        L_0x007d:
            if (r4 == 0) goto L_0x008c
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0098 }
            r6.<init>()     // Catch:{ all -> 0x0098 }
            r6.add(r4)     // Catch:{ all -> 0x0098 }
            androidx.collection.SimpleArrayMap<java.lang.String, java.util.ArrayList<androidx.core.provider.SelfDestructiveThread$ReplyCallback<androidx.core.provider.FontsContractCompat$TypefaceResult>>> r7 = sPendingReplies     // Catch:{ all -> 0x0098 }
            r7.put(r0, r6)     // Catch:{ all -> 0x0098 }
        L_0x008c:
            monitor-exit(r5)     // Catch:{ all -> 0x0098 }
            androidx.core.provider.SelfDestructiveThread r5 = sBackgroundThread
            androidx.core.provider.FontsContractCompat$3 r6 = new androidx.core.provider.FontsContractCompat$3
            r6.<init>(r0)
            r5.postAndReply(r2, r6)
            return r3
        L_0x0098:
            r3 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0098 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.provider.FontsContractCompat.getFontSync(android.content.Context, androidx.core.provider.FontRequest, androidx.core.content.res.ResourcesCompat$FontCallback, android.os.Handler, boolean, int, int):android.graphics.Typeface");
    }

    public static void requestFont(final Context context, final FontRequest request, final FontRequestCallback callback, Handler handler) {
        final Handler callerThreadHandler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                try {
                    FontFamilyResult result = FontsContractCompat.fetchFonts(context, null, request);
                    if (result.getStatusCode() != 0) {
                        int statusCode = result.getStatusCode();
                        if (statusCode == 1) {
                            callerThreadHandler.post(new Runnable() {
                                public void run() {
                                    callback.onTypefaceRequestFailed(-2);
                                }
                            });
                        } else if (statusCode != 2) {
                            callerThreadHandler.post(new Runnable() {
                                public void run() {
                                    callback.onTypefaceRequestFailed(-3);
                                }
                            });
                        } else {
                            callerThreadHandler.post(new Runnable() {
                                public void run() {
                                    callback.onTypefaceRequestFailed(-3);
                                }
                            });
                        }
                    } else {
                        FontInfo[] fonts = result.getFonts();
                        if (fonts == null || fonts.length == 0) {
                            callerThreadHandler.post(new Runnable() {
                                public void run() {
                                    callback.onTypefaceRequestFailed(1);
                                }
                            });
                            return;
                        }
                        for (FontInfo font : fonts) {
                            if (font.getResultCode() != 0) {
                                final int resultCode = font.getResultCode();
                                if (resultCode < 0) {
                                    callerThreadHandler.post(new Runnable() {
                                        public void run() {
                                            callback.onTypefaceRequestFailed(-3);
                                        }
                                    });
                                } else {
                                    callerThreadHandler.post(new Runnable() {
                                        public void run() {
                                            callback.onTypefaceRequestFailed(resultCode);
                                        }
                                    });
                                }
                                return;
                            }
                        }
                        final Typeface typeface = FontsContractCompat.buildTypeface(context, null, fonts);
                        if (typeface == null) {
                            callerThreadHandler.post(new Runnable() {
                                public void run() {
                                    callback.onTypefaceRequestFailed(-3);
                                }
                            });
                        } else {
                            callerThreadHandler.post(new Runnable() {
                                public void run() {
                                    callback.onTypefaceRetrieved(typeface);
                                }
                            });
                        }
                    }
                } catch (NameNotFoundException e) {
                    callerThreadHandler.post(new Runnable() {
                        public void run() {
                            callback.onTypefaceRequestFailed(-1);
                        }
                    });
                }
            }
        });
    }

    public static Typeface buildTypeface(Context context, CancellationSignal cancellationSignal, FontInfo[] fonts) {
        return TypefaceCompat.createFromFontInfo(context, cancellationSignal, fonts, 0);
    }

    public static Map<Uri, ByteBuffer> prepareFontData(Context context, FontInfo[] fonts, CancellationSignal cancellationSignal) {
        HashMap<Uri, ByteBuffer> out = new HashMap<>();
        for (FontInfo font : fonts) {
            if (font.getResultCode() == 0) {
                Uri uri = font.getUri();
                if (!out.containsKey(uri)) {
                    out.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
        }
        return Collections.unmodifiableMap(out);
    }

    public static FontFamilyResult fetchFonts(Context context, CancellationSignal cancellationSignal, FontRequest request) throws NameNotFoundException {
        ProviderInfo providerInfo = getProvider(context.getPackageManager(), request, context.getResources());
        if (providerInfo == null) {
            return new FontFamilyResult(1, null);
        }
        return new FontFamilyResult(0, getFontFromProvider(context, request, providerInfo.authority, cancellationSignal));
    }

    public static ProviderInfo getProvider(PackageManager packageManager, FontRequest request, Resources resources) throws NameNotFoundException {
        String providerAuthority = request.getProviderAuthority();
        ProviderInfo info = packageManager.resolveContentProvider(providerAuthority, 0);
        if (info == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No package found for authority: ");
            sb.append(providerAuthority);
            throw new NameNotFoundException(sb.toString());
        } else if (info.packageName.equals(request.getProviderPackage())) {
            List<byte[]> signatures = convertToByteArrayList(packageManager.getPackageInfo(info.packageName, 64).signatures);
            Collections.sort(signatures, sByteArrayComparator);
            List<List<byte[]>> requestCertificatesList = getCertificates(request, resources);
            for (int i = 0; i < requestCertificatesList.size(); i++) {
                List<byte[]> requestSignatures = new ArrayList<>((Collection) requestCertificatesList.get(i));
                Collections.sort(requestSignatures, sByteArrayComparator);
                if (equalsByteArrayList(signatures, requestSignatures)) {
                    return info;
                }
            }
            return null;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Found content provider ");
            sb2.append(providerAuthority);
            sb2.append(", but package was not ");
            sb2.append(request.getProviderPackage());
            throw new NameNotFoundException(sb2.toString());
        }
    }

    private static List<List<byte[]>> getCertificates(FontRequest request, Resources resources) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, request.getCertificatesArrayResId());
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals((byte[]) signatures.get(i), (byte[]) requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shas = new ArrayList<>();
        for (Signature byteArray : signatures) {
            shas.add(byteArray.toByteArray());
        }
        return shas;
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x016d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static androidx.core.provider.FontsContractCompat.FontInfo[] getFontFromProvider(android.content.Context r21, androidx.core.provider.FontRequest r22, java.lang.String r23, android.os.CancellationSignal r24) {
        /*
            r1 = r23
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = r0
            android.net.Uri$Builder r0 = new android.net.Uri$Builder
            r0.<init>()
            java.lang.String r3 = "content"
            android.net.Uri$Builder r0 = r0.scheme(r3)
            android.net.Uri$Builder r0 = r0.authority(r1)
            android.net.Uri r11 = r0.build()
            android.net.Uri$Builder r0 = new android.net.Uri$Builder
            r0.<init>()
            android.net.Uri$Builder r0 = r0.scheme(r3)
            android.net.Uri$Builder r0 = r0.authority(r1)
            java.lang.String r3 = "file"
            android.net.Uri$Builder r0 = r0.appendPath(r3)
            android.net.Uri r3 = r0.build()
            r12 = 0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0168 }
            r4 = 16
            java.lang.String r8 = "font_variation_settings"
            r13 = 7
            java.lang.String r14 = "result_code"
            java.lang.String r15 = "font_italic"
            java.lang.String r5 = "font_weight"
            java.lang.String r6 = "font_ttc_index"
            java.lang.String r7 = "file_id"
            java.lang.String r9 = "_id"
            r10 = 0
            if (r0 <= r4) goto L_0x0094
            android.content.ContentResolver r4 = r21.getContentResolver()     // Catch:{ all -> 0x008f }
            java.lang.String[] r0 = new java.lang.String[r13]     // Catch:{ all -> 0x008f }
            r0[r10] = r9     // Catch:{ all -> 0x008f }
            r13 = 1
            r0[r13] = r7     // Catch:{ all -> 0x008f }
            r13 = 2
            r0[r13] = r6     // Catch:{ all -> 0x008f }
            r13 = 3
            r0[r13] = r8     // Catch:{ all -> 0x008f }
            r8 = 4
            r0[r8] = r5     // Catch:{ all -> 0x008f }
            r8 = 5
            r0[r8] = r15     // Catch:{ all -> 0x008f }
            r8 = 6
            r0[r8] = r14     // Catch:{ all -> 0x008f }
            java.lang.String r8 = "query = ?"
            r13 = 1
            java.lang.String[] r10 = new java.lang.String[r13]     // Catch:{ all -> 0x008f }
            java.lang.String r17 = r22.getQuery()     // Catch:{ all -> 0x008f }
            r16 = 0
            r10[r16] = r17     // Catch:{ all -> 0x008f }
            r17 = 0
            r19 = r5
            r5 = r11
            r20 = r6
            r6 = r0
            r0 = r7
            r7 = r8
            r8 = r10
            r10 = r9
            r9 = r17
            r13 = r10
            r10 = r24
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x008f }
            r16 = r2
            r12 = r4
            r10 = r13
            r1 = r19
            r13 = r20
            r2 = 1
            goto L_0x00d8
        L_0x008f:
            r0 = move-exception
            r16 = r2
            goto L_0x016b
        L_0x0094:
            r19 = r5
            r20 = r6
            r0 = r7
            r10 = r9
            android.content.ContentResolver r4 = r21.getContentResolver()     // Catch:{ all -> 0x0168 }
            java.lang.String[] r6 = new java.lang.String[r13]     // Catch:{ all -> 0x0168 }
            r13 = 0
            r6[r13] = r10     // Catch:{ all -> 0x0168 }
            r9 = 1
            r6[r9] = r0     // Catch:{ all -> 0x0168 }
            r7 = r20
            r5 = 2
            r6[r5] = r7     // Catch:{ all -> 0x0168 }
            r5 = 3
            r6[r5] = r8     // Catch:{ all -> 0x0168 }
            r8 = r19
            r5 = 4
            r6[r5] = r8     // Catch:{ all -> 0x0168 }
            r5 = 5
            r6[r5] = r15     // Catch:{ all -> 0x0168 }
            r5 = 6
            r6[r5] = r14     // Catch:{ all -> 0x0168 }
            java.lang.String r16 = "query = ?"
            java.lang.String[] r5 = new java.lang.String[r9]     // Catch:{ all -> 0x0168 }
            java.lang.String r17 = r22.getQuery()     // Catch:{ all -> 0x0168 }
            r5[r13] = r17     // Catch:{ all -> 0x0168 }
            r17 = 0
            r18 = r5
            r5 = r11
            r13 = r7
            r7 = r16
            r1 = r8
            r8 = r18
            r16 = r2
            r2 = 1
            r9 = r17
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0166 }
            r12 = r4
        L_0x00d8:
            if (r12 == 0) goto L_0x0155
            int r4 = r12.getCount()     // Catch:{ all -> 0x0166 }
            if (r4 <= 0) goto L_0x0155
            int r4 = r12.getColumnIndex(r14)     // Catch:{ all -> 0x0166 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0166 }
            r5.<init>()     // Catch:{ all -> 0x0166 }
            int r6 = r12.getColumnIndex(r10)     // Catch:{ all -> 0x0151 }
            int r0 = r12.getColumnIndex(r0)     // Catch:{ all -> 0x0151 }
            int r7 = r12.getColumnIndex(r13)     // Catch:{ all -> 0x0151 }
            int r1 = r12.getColumnIndex(r1)     // Catch:{ all -> 0x0151 }
            int r8 = r12.getColumnIndex(r15)     // Catch:{ all -> 0x0151 }
        L_0x00fd:
            boolean r9 = r12.moveToNext()     // Catch:{ all -> 0x0151 }
            if (r9 == 0) goto L_0x0157
            r9 = -1
            if (r4 == r9) goto L_0x010d
            int r10 = r12.getInt(r4)     // Catch:{ all -> 0x0151 }
            r18 = r10
            goto L_0x010f
        L_0x010d:
            r18 = 0
        L_0x010f:
            if (r7 == r9) goto L_0x0117
            int r10 = r12.getInt(r7)     // Catch:{ all -> 0x0151 }
            r15 = r10
            goto L_0x0118
        L_0x0117:
            r15 = 0
        L_0x0118:
            if (r0 != r9) goto L_0x0123
            long r13 = r12.getLong(r6)     // Catch:{ all -> 0x0151 }
            android.net.Uri r10 = android.content.ContentUris.withAppendedId(r11, r13)     // Catch:{ all -> 0x0151 }
            goto L_0x012b
        L_0x0123:
            long r13 = r12.getLong(r0)     // Catch:{ all -> 0x0151 }
            android.net.Uri r10 = android.content.ContentUris.withAppendedId(r3, r13)     // Catch:{ all -> 0x0151 }
        L_0x012b:
            if (r1 == r9) goto L_0x0134
            int r13 = r12.getInt(r1)     // Catch:{ all -> 0x0151 }
            r16 = r13
            goto L_0x0138
        L_0x0134:
            r13 = 400(0x190, float:5.6E-43)
            r16 = 400(0x190, float:5.6E-43)
        L_0x0138:
            if (r8 == r9) goto L_0x0143
            int r9 = r12.getInt(r8)     // Catch:{ all -> 0x0151 }
            if (r9 != r2) goto L_0x0143
            r17 = 1
            goto L_0x0145
        L_0x0143:
            r17 = 0
        L_0x0145:
            androidx.core.provider.FontsContractCompat$FontInfo r9 = new androidx.core.provider.FontsContractCompat$FontInfo     // Catch:{ all -> 0x0151 }
            r13 = r9
            r14 = r10
            r13.<init>(r14, r15, r16, r17, r18)     // Catch:{ all -> 0x0151 }
            r5.add(r9)     // Catch:{ all -> 0x0151 }
            goto L_0x00fd
        L_0x0151:
            r0 = move-exception
            r16 = r5
            goto L_0x016b
        L_0x0155:
            r5 = r16
        L_0x0157:
            if (r12 == 0) goto L_0x015c
            r12.close()
        L_0x015c:
            r0 = 0
            androidx.core.provider.FontsContractCompat$FontInfo[] r0 = new androidx.core.provider.FontsContractCompat.FontInfo[r0]
            java.lang.Object[] r0 = r5.toArray(r0)
            androidx.core.provider.FontsContractCompat$FontInfo[] r0 = (androidx.core.provider.FontsContractCompat.FontInfo[]) r0
            return r0
        L_0x0166:
            r0 = move-exception
            goto L_0x016b
        L_0x0168:
            r0 = move-exception
            r16 = r2
        L_0x016b:
            if (r12 == 0) goto L_0x0170
            r12.close()
        L_0x0170:
            goto L_0x0172
        L_0x0171:
            throw r0
        L_0x0172:
            goto L_0x0171
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.provider.FontsContractCompat.getFontFromProvider(android.content.Context, androidx.core.provider.FontRequest, java.lang.String, android.os.CancellationSignal):androidx.core.provider.FontsContractCompat$FontInfo[]");
    }
}
