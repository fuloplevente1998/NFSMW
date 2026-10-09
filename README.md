# NFSMW HU/EN Android

A [codepdbh/nfsmw-android](https://github.com/codepdbh/nfsmw-android) közösségi port magyar–angol Android-felületének tesztváltozata.
Az eredeti v0.5.4 natív motorját változatlanul használja. **A Galaxy A57-en való működés még nincs igazolva.**

## Tesztváltozat

**[NFSMW HU/EN test1 APK letöltése](https://github.com/fuloplevente1998/NFSMW/raw/refs/heads/codex/hu-en-test1/test-apk/NFSMW-HU-EN-test1.apk)**

- Magyar és angol indítófelület, beállítások és érintős vezérlők.
- Megjegyzett nyelvválasztás az „Alkalmazás nyelve / App language” menüben.
- Külön telepíthető APK: `com.nfsmw.android.huen`, `0.5.4-hu-en-test1`.
- Angol játékbeli szöveg az alapbeállítás. **Magyar játékbeli fordítás még nincs benne.**
- A játékadatmappa közös az eredeti portéval; a mentések és beállítások külön alkalmazástárhelyen vannak.

A játékhoz a megfelelő Xbox 360 PAL spanyol kiadásból szükséges a `default.xex`, a teljes `NFS/` és `Movies/` mappa.
Az APK és a repó játékfájlokat nem tartalmaz. A PC-s telepítőfájlok nem megfelelőek ehhez a motorhoz.

[Telepítés, tesztelés és fordítás kész motorral](docs/CONTINUATION_HU.md) ·
[Források és eredeti szerzők](CREDITS.md) · [Eredeti Android-projekt leírása](README_UPSTREAM.md)

## Eredeti szerzők

A közvetlen Android-alap **[codepdbh](https://github.com/codepdbh)** és a
[nfsmw-android projekt](https://github.com/codepdbh/nfsmw-android) munkája.
Az előzmények között szerepel **StevensND**, **madelrandel-blip**, a **ReXGlue**, **Xenia**, **SDL** és **XenosRecomp** közössége.
Az eredeti Git-előzmények, szerzői fejlécek és komponenslicencek megmaradtak.
A részleteket és a pontos verziókat a [CREDITS.md](CREDITS.md) és a [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) tartalmazza.

Az átvett port és ezek a felületi módosítások **GPL-3.0** licencűek: [LICENSE](LICENSE).
A kezdeti, kód nélküli célrepó Apache-2.0 licencét történeti okból a `LICENSE.initial-Apache-2.0.txt` őrzi;
ez nem változtatja meg az átvett GPL-kód licencét.

## English

This is a Hungarian/English Android UI continuation of **codepdbh/nfsmw-android v0.5.4**.
The 11 native libraries and 20 runtime assets are unchanged; the engine was not rebuilt.
Six Android UI tests passed, and APK signing and ZIP alignment were verified.
Physical-device gameplay on the Galaxy A57 remains to be tested.
Hungarian in-game text is not included. See the credits and build instructions above.
