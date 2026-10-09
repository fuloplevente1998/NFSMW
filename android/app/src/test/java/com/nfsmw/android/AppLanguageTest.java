package com.nfsmw.android;

import static org.junit.Assert.*;

import android.app.Application;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowEnvironment;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = Application.class, shadows = AppLanguageTest.DeniedStorage.class)
public class AppLanguageTest {
    // Robolectric 4.14 does not shadow this Android 15 permission API.
    @Implements(Environment.class)
    public static class DeniedStorage extends ShadowEnvironment {
        @Implementation(minSdk = 30)
        protected static boolean isExternalStorageManager() { return false; }
    }
    private Context context;

    @Before public void resetPreferences() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("nfsmw_ui", 0).edit().clear().commit();
        GameOptions.prefs(context).edit().clear().commit();
    }

    @Test public void deviceLanguageFallsBackToEnglishUnlessHungarian() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("hu-HU"));
            assertEquals("hu", AppLanguage.selected(context));
            Locale.setDefault(Locale.forLanguageTag("de-DE"));
            assertEquals("en", AppLanguage.selected(context));
        } finally { Locale.setDefault(original); }
    }

    @Test public void explicitLanguageSurvivesContextRecreation() {
        AppLanguage.select(context, "hu");
        Context first = AppLanguage.wrap(context);
        assertEquals("JÁTÉKFÁJLOK", first.getString(R.string.ui_game_files));
        assertEquals("hu", AppLanguage.selected(context.createConfigurationContext(
                context.getResources().getConfiguration())));
        AppLanguage.select(first, "en");
        Context second = AppLanguage.wrap(context);
        assertEquals("GAME FILES", second.getString(R.string.ui_game_files));
        assertEquals("Preparing graphics: 42%", second.getString(R.string.shader_progress, 42));
    }

    @Test public void translatingOptionsDoesNotChangeNativeArguments() {
        GameOptions.set(context, GameOptions.RENDERER, "xenos");
        GameOptions.set(context, "language", "1");
        List<String> before = GameOptions.arguments(context);
        AppLanguage.select(context, "hu");
        Context hungarian = AppLanguage.wrap(context);
        assertEquals("Natív", GameOptions.find(GameOptions.RENDERER).label(hungarian, "nativo"));
        assertEquals(before, GameOptions.arguments(hungarian));
        assertTrue(before.contains("--nfsmw_idioma=1"));
        assertTrue(before.contains("--nfsmw_renderizador=xenos"));
    }

    @Test public void newInstallDefaultsToEnglishGameText() {
        assertTrue(GameOptions.arguments(context).contains("--nfsmw_idioma=0"));
    }

    @Test public void launcherStartsWithoutGameFilesInBothLanguages() {
        for (String language : new String[]{"hu", "en"}) {
            AppLanguage.select(context, language);
            try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)) {
                MainActivity activity = controller.create().start().resume().get();
                View content = activity.findViewById(android.R.id.content);
                assertTrue(containsText(content, language.equals("hu") ? "JÁTÉKFÁJLOK" : "GAME FILES"));
                assertTrue(containsText(content, (language.equals("hu") ? "Magyar" : "English") + "  ›"));
            }
        }
    }

    @Test public void languageDialogWorksBeforeStoragePermissionIsGranted() {
        AppLanguage.select(context, "en");
        try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)) {
            MainActivity activity = controller.create().start().resume().get();
            View title = findText(activity.findViewById(android.R.id.content), "App language");
            assertNotNull(title);
            ((View) title.getParent()).performClick();
            AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
            assertNotNull(dialog);
            dialog.getListView().performItemClick(null, 1, 1);
            assertEquals("hu", AppLanguage.selected(context));
        }
    }

    private static boolean containsText(View view, String expected) {
        return findText(view, expected) != null;
    }

    private static View findText(View view, String expected) {
        if (view instanceof TextView && ((TextView) view).getText().toString().equals(expected)) return view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View match = findText(group.getChildAt(i), expected);
                if (match != null) return match;
            }
        }
        return null;
    }
}
