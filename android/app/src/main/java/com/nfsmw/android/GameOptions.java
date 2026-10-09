package com.nfsmw.android;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * The graphics options of the launcher. Each one is a game cvar: GameActivity passes them on the command
 * line, which wins over nfsmw.toml. Values must be ones the cvar allows (nfsmw_ajustes_graficos.cpp and
 * friends), or the game ignores them.
 */
final class GameOptions {
    static final class Option {
        final String key;
        final int title;
        final String cvar;
        final String[] values;
        final int[] labels;
        final String defaultValue;

        Option(String key, int title, String cvar, String defaultValue, String[] values, int[] labels) {
            this.key = key;
            this.title = title;
            this.cvar = cvar;
            this.defaultValue = defaultValue;
            this.values = values;
            this.labels = labels;
        }

        String label(Context context, String value) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(value)) {
                    return context.getString(labels[i]);
                }
            }
            return value;
        }
        String[] labels(Context context) {
            String[] translated = new String[labels.length];
            for (int i = 0; i < labels.length; i++) translated[i] = context.getString(labels[i]);
            return translated;
        }
    }

    static final String RESOLUTION = "resolution";
    static final String FPS = "fps";
    static final String RENDERER = "renderer";

    static final Option[] ALL = {
            new Option(RENDERER, R.string.ui_renderer, "nfsmw_renderizador", "nativo",
                    new String[] {"nativo", "xenos"},
                    new int[] {R.string.ui_native, R.string.ui_compatibility_experimental}),
            new Option("gpu_stability", R.string.ui_graphics_stability, "nfsmw_consultas_oclusion", "auto",
                    new String[] {"auto", "off", "on"},
                    new int[] {R.string.ui_automatic_mali_protection, R.string.ui_maximum_compatibility_no_sun_flare, R.string.ui_full_effects_experimental}),
            new Option(RESOLUTION, R.string.ui_internal_resolution, "nfsmw_resolucion_interna", "1280x720",
                    new String[] {"640x360", "1024x576", "1280x720", "1920x1080"},
                    new int[] {R.string.ui_640_360_fastest, R.string.ui_1024_576_performance, R.string.ui_1280_720_balanced, R.string.ui_1920_1080_best_quality}),
            new Option(FPS, R.string.ui_frame_rate_limit, "nfsmw_limite_fps", "60",
                    new String[] {"30", "60", "90", "120"},
                    new int[] {R.string.ui_30_fps_battery_saver, R.string.ui_60_fps, R.string.ui_90_fps_experimental, R.string.ui_120_fps_experimental}),
            new Option("aa", R.string.ui_antialiasing, "nfsmw_antialiasing", "apagado",
                    new String[] {"apagado", "fxaa"},
                    new int[] {R.string.ui_off, R.string.ui_fxaa}),
            new Option("shadows", R.string.ui_shadows, "nfsmw_sombras_cada", "1",
                    new String[] {"1", "2"},
                    new int[] {R.string.ui_every_frame, R.string.ui_every_2_frames_faster}),
            new Option("car_reflections", R.string.ui_car_reflections, "nfsmw_cubemap_caras_max", "6",
                    new String[] {"6", "2", "1"},
                    new int[] {R.string.ui_high, R.string.ui_medium, R.string.ui_low_faster}),
            new Option("road_reflection", R.string.ui_wet_road_reflections, "nfsmw_reflejo_carretera", "true",
                    new String[] {"true", "false"},
                    new int[] {R.string.ui_on, R.string.ui_off_faster}),
            new Option("sky", R.string.ui_sky_glow, "nfsmw_resplandor_cielo", "natural",
                    new String[] {"original", "natural", "suave"},
                    new int[] {R.string.ui_original_xbox_360, R.string.ui_natural, R.string.ui_soft}),
            // The PAL discs carry the text of these ten languages (app/src/nfsmw_idioma.cpp). Speech and movies
            // stay in the disc's language.
            new Option("language", R.string.ui_game_text_language, "nfsmw_idioma", "0",
                    new String[] {"-1", "4", "0", "1", "2", "3", "5", "6", "7", "12", "13"},
                    new int[] {R.string.ui_disc_default, R.string.ui_spanish, R.string.ui_english, R.string.ui_french, R.string.ui_german, R.string.ui_italian, R.string.ui_dutch, R.string.ui_swedish, R.string.ui_danish, R.string.ui_polish, R.string.ui_finnish}),
            new Option("volume", R.string.ui_game_volume, "audio_ganancia_pct", "100",
                    new String[] {"100", "125", "150", "200"},
                    new int[] {R.string.ui_normal, R.string.ui_loud, R.string.ui_very_loud, R.string.ui_maximum_may_distort}),
            new Option("filter", R.string.ui_image_filter, "nfsmw_posproceso", "apagado",
                    new String[] {"apagado", "cine", "vivo", "calido", "frio", "sepia", "noir", "crt"},
                    new int[] {R.string.ui_no_filter, R.string.ui_cinema, R.string.ui_vivid, R.string.ui_warm, R.string.ui_cool, R.string.ui_sepia, R.string.ui_black_and_white, R.string.ui_crt}),
    };

    private static final String PREFS = "nfsmw_game";

    private GameOptions() {
    }

    static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static Option find(String key) {
        for (Option o : ALL) {
            if (o.key.equals(key)) {
                return o;
            }
        }
        throw new IllegalArgumentException(key);
    }

    static String get(Context context, String key) {
        Option o = find(key);
        String value = prefs(context).getString(key, o.defaultValue);
        for (String allowed : o.values) {
            if (allowed.equals(value)) {
                return value;
            }
        }
        return o.defaultValue;
    }

    static void set(Context context, String key, String value) {
        prefs(context).edit().putString(key, value).apply();
    }

    static List<String> arguments(Context context) {
        List<String> args = new ArrayList<>();
        for (Option o : ALL) {
            args.add("--" + o.cvar + "=" + get(context, o.key));
        }
        if ("xenos".equals(get(context, RENDERER))) {
            // Use ordinary descriptor sets and host framebuffers on older drivers.
            // Xenos also decompresses unsupported BC texture formats on the GPU.
            args.add("--vulkan_native_shader_features=false");
            args.add("--render_target_path_vulkan=fbo");
            args.add("--vulkan_require_geometry_shader=false");
            args.add("--vulkan_require_fill_mode_non_solid=false");
            args.add("--async_shader_compilation=false");
            args.add("--nfsmw_d3d_registros_nativo=false");
            args.add("--nfsmw_d3d_marcador=false");
            args.add("--nfsmw_d3d_marcador_registro=false");
            args.add("--nfsmw_d3d_efectos_nativo=false");
            args.add("--nfsmw_render_sin_mosaico=false");
            args.add("--nfsmw_material_nativo=false");
            args.add("--nfsmw_visible_nativo=false");
            args.add("--nfsmw_matrices_nativo=false");
            args.add("--nfsmw_eview_nativo=false");
            args.add("--nfsmw_escenario_nativo=false");
            args.add("--nfsmw_efecto_pasada_nativo=false");
            args.add("--nfsmw_pegamento_nativo=false");
        }
        GpuDrivers.arguments(context, args);
        return args;
    }
}
