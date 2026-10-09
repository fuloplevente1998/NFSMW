package com.nfsmw.android;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;
import java.util.Locale;

/** App UI language only: never changes game files or the game's language cvar. */
final class AppLanguage {
    private static final String PREFS = "nfsmw_ui";
    private static final String KEY = "language";
    private AppLanguage() { }

    static String selected(Context context) {
        String code = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "");
        if ("en".equals(code) || "hu".equals(code)) return code;
        return "hu".equals(Locale.getDefault().getLanguage()) ? "hu" : "en";
    }

    static void select(Context context, String code) {
        if (!"en".equals(code) && !"hu".equals(code)) {
            throw new IllegalArgumentException("Unsupported app language: " + code);
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, code).apply();
    }

    static Context wrap(Context context) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocales(new LocaleList(Locale.forLanguageTag(selected(context))));
        return context.createConfigurationContext(config);
    }

    static String label(Context context) {
        return "hu".equals(selected(context)) ? "Magyar" : "English";
    }
}
