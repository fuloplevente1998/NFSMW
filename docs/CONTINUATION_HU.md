# NFSMW Android – HU/EN tesztverzió

Célrepó: [fuloplevente1998/NFSMW](https://github.com/fuloplevente1998/NFSMW).
Az eredeti szerzők és komponensek: [CREDITS.md](../CREDITS.md).

Alap: [codepdbh/nfsmw-android v0.5.4](https://github.com/codepdbh/nfsmw-android/releases/tag/v0.5.4),
`a7e6e4c3ca71064d0eea56d91fcc8aaf63418fc7`. A tesztverzió újraépíti az Android Java-felületét,
és a kiadott APK natív könyvtárait, valamint futásidejű shaderfájljait változatlanul használja.
Nem fordítja újra a motort, és nem igényel Xbox-fájlokat a fordításhoz.

## Elkészült

- Magyar és angol indítófelület, grafikai beállítások, érintős vezérlők és főbb hibaüzenetek.
- Az „Alkalmazás nyelve / App language” menüben váltható, megjegyzett nyelv.
  Első indításkor magyar telefonon magyar, más telefonon angol.
- A játék szövegének alapbeállítása angol. A játéknyelv és az Android-felület nyelve külön beállítás.
- Külön csomagazonosító: `com.nfsmw.android.huen`. Az eredeti APK mellé telepíthető.
  A mentések és beállítások saját alkalmazástárhelyen vannak; az eredeti mentéseket nem veszi át.
- A tesztverzió nem ajánlja fel az eredeti alkalmazás automatikus frissítéseit.
- Reprodukálható csomagolás a kész motorral és a végső APK fájljainak összehasonlító ellenőrzése.

## Mi hiányzik még?

A magyar játékbeli szöveg nincs benne. A megtalált PC-s 1.3 magyarítás nem tekinthető
Xbox 360-on működő fordításnak; külön fájlformátum- és megjelenítési vizsgálat szükséges hozzá.
A natív motor saját naplói és a shaderfordító belső hibái az eredeti nyelven is megjelenhetnek.

A Galaxy A57 kompatibilitását és sebességét valódi telefonon kell ellenőrizni.
A v0.5.4 leírása Xclipse BC4/BC5 javítást tartalmaz, de ez önmagában nem igazol A57-es működést.
Hivatkozások: [v0.5.4 kompatibilitás](compatibilidad-android-0.5.4.md),
[Xclipse vizsgálat](android-xclipse-diagnostic.md).
A korai Android-tervekben lévő „még nincs működő játék” állapot elavult;
a frissebb kiadási jegyzetek és a kód az irányadó.

A [madelrandel-blip/NFSMW-Recompiled kiadásai](https://github.com/madelrandel-blip/NFSMW-Recompiled/releases)
Windowsos csomagok. A megvizsgált 0.0.2 változásai főként az indítót, ISO-kibontást és
megjelenítési méretezést érintik. Az ISO-import későbbi bővítéséhez hasznos referencia;
nem helyettesíti a meglévő Android-motort.

## Játékfájlok és telepítés

A játékhoz a megfelelő Xbox 360 PAL spanyol kiadásból kellenek:

```text
nsfmw-androidevolved/
  default.xex
  NFS/       (teljes mappa)
  Movies/    (teljes mappa)
```

A támogatott `default.xex` SHA-256:
`aad15fc218d034de3131f746e0b763dd8049a40dd676efc87667fcaf4fc72694`.
Az APK játékadatokat nem tartalmaz. A Windows/PC telepítő ISO-ja nem megfelelő ehhez a motorhoz.

Telepítsd a teszt-APK-t, nyisd meg, és engedélyezd a kért fájlhozzáférést.
A játék mappáját az indítóban választhatod ki, vagy a fenti mappába másolhatod.
A `nsfmw-androidevolved` név az eredeti portban is így szerepel.
Ez a **közös külső játékadatmappa** az eredeti alkalmazáséval: importáláskor az ott lévő adatokat
lecserélheti. A mentések ettől elkülönülnek. A tesztverzió helyi tesztkulccsal aláírt APK.

## Telefonos teszt

1. Ellenőrizd a magyar–angol nyelvváltást és hogy újraindítás után is megmarad.
2. Válaszd ki a megfelelő játékfájlokat; nézd meg, hogy mindhárom szükséges elem ellenőrzése zöld-e.
3. Először natív megjelenítővel, 640×360 felbontással, 30 FPS korláttal próbáld.
4. Az első indítás grafikai előkészítése hosszabb lehet. Teszteld a főmenüt, majd egy versenyt.
5. Ellenőrizd a kormányt, gázt/féket, kéziféket, nitrót, kamera- és menügombokat, hangot és mentést.
6. Fekete kép, kilépés vagy grafikai hiba esetén ments jelentést az indítóból.
   Jegyezd fel az Android-verziót, a választott megjelenítőt és azt, hol jelentkezett a hiba.

## Fordítás kész motorral

JDK 17, Android SDK 35 és a projekt Gradle wrapperje szükséges. A script csak a pontos,
ellenőrzött v0.5.4 ARM64 APK-t fogadja el; a hiányzó SDL Java-kódot a SDK rögzített SDL-revíziójából tölti le.
A natív fordításhoz használt `sdk/`, NDK, generált guest-kód és `default.xex` itt nem szükséges.

```bash
python tools/prepare_android_prebuilt.py --apk /path/to/NFSMW-Android-Evolved-v0.5.4-arm64.apk
cd android
./gradlew -PreuseNative=true assembleRelease testReleaseUnitTest
cd ..
python tools/verify_android_prebuilt.py \
  --upstream-apk /path/to/NFSMW-Android-Evolved-v0.5.4-arm64.apk \
  --test-apk android/app/build/outputs/apk/release/app-release.apk
```

Az előkészítő scripthez opcionálisan `--sdl-zip /path/to/pinned-SDL.zip` adható.
Az `out/android-prebuilt/` csak helyi, figyelmen kívül hagyott buildanyag.
Az új Java DEX-hez a Gradle új `assets/dexopt/` profilokat készít, ezért ezeket nem vesszük át az eredeti APK-ból.
A motorhoz és a shaderekhez szükséges fájlok ellenőrzése bájtonként történik.

## Ellenőrzött eredmény – 2026-10-09

- `assembleRelease`: sikeres; a kiadási lint ellenőrzése lefutott.
- `testReleaseUnitTest`: 6 teszt, 0 hiba. Nyelvi alapbeállítás, mentett nyelv,
  angol játéknyelv, változatlan natív argumentumok, kétnyelvű indítóképernyő és
  a nyelvválasztó működése fájlhozzáférés nélkül, szimulált Android 15 környezetben.
- 11 natív könyvtár és 20 futásidejű asset változatlan az eredeti APK-hoz képest.
- APK v2 aláírás: érvényes. ZIP-illesztés: sikeres.
- Csomagazonosító: `com.nfsmw.android.huen`; verzió: `0.5.4-hu-en-test1`.
- SHA-256: `672b00d4f993d24851d07d87c27f276f5c2485ef8724f620cf7cf87f88833613`.

Fizikai telefonon és valódi játékadatokkal még nincs teszteredmény.
