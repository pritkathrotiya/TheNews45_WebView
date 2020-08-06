package androidx.core.p003os;

import java.util.Locale;

/* renamed from: androidx.core.os.LocaleHelper */
final class LocaleHelper {
    static Locale forLanguageTag(String str) {
        String str2 = "-";
        if (str.contains(str2)) {
            String[] args = str.split(str2, -1);
            if (args.length > 2) {
                return new Locale(args[0], args[1], args[2]);
            }
            if (args.length > 1) {
                return new Locale(args[0], args[1]);
            }
            if (args.length == 1) {
                return new Locale(args[0]);
            }
        } else {
            String str3 = "_";
            if (!str.contains(str3)) {
                return new Locale(str);
            }
            String[] args2 = str.split(str3, -1);
            if (args2.length > 2) {
                return new Locale(args2[0], args2[1], args2[2]);
            }
            if (args2.length > 1) {
                return new Locale(args2[0], args2[1]);
            }
            if (args2.length == 1) {
                return new Locale(args2[0]);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Can not parse language tag: [");
        sb.append(str);
        sb.append("]");
        throw new IllegalArgumentException(sb.toString());
    }

    static String toLanguageTag(Locale locale) {
        StringBuilder buf = new StringBuilder();
        buf.append(locale.getLanguage());
        String country = locale.getCountry();
        if (country != null && !country.isEmpty()) {
            buf.append("-");
            buf.append(locale.getCountry());
        }
        return buf.toString();
    }

    private LocaleHelper() {
    }
}
