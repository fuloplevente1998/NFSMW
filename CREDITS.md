# Credits / Felhasznált források

This repository continues existing community work. The HU/EN launcher changes do not claim authorship of the game engine or the original Android port.

| Project / author | Role in this version |
|---|---|
| [codepdbh/nfsmw-android](https://github.com/codepdbh/nfsmw-android) — **codepdbh** and contributors | Direct Android source base and unchanged native engine from [v0.5.4](https://github.com/codepdbh/nfsmw-android/releases/tag/v0.5.4), commit `a7e6e4c3ca71064d0eea56d91fcc8aaf63418fc7`. |
| [StevensND/nfsmw-nx](https://github.com/StevensND/nfsmw-nx) — **StevensND** and contributors | The recompilation and porting work underlying the Android project. |
| [madelrandel-blip/NFSMW-Recompiled](https://github.com/madelrandel-blip/NFSMW-Recompiled) — **madelrandel-blip** and contributors | Original recompilation project in the upstream lineage; Windows launcher reference. No Windows binaries were added to this APK. |
| [ReXGlue SDK](https://github.com/rexglue/rexglue-sdk) — **Tom Clay** and contributors; [Xenia](https://xenia.jp) — **Ben Vanik** and contributors | Xbox 360 recompilation tooling and runtime used by the existing engine. |
| [SDL](https://github.com/libsdl-org/SDL) — **Sam Lantinga** and SDL contributors | Android integration, Java sources pinned to `8bf3b7215ad9fc3deb583c6a3a37c6c67f2e24e4`; zlib license. |
| [XenosRecomp](https://github.com/hedge-dev/XenosRecomp) — **hedge-dev** and contributors | Shader translation used by the upstream project. |
| [fuloplevente1998/NFSMW](https://github.com/fuloplevente1998/NFSMW) — **fuloplevente1998** | HU/EN Android UI continuation, separate test package and packaging with the released engine. |

The imported project retains its **GPL-3.0** license in [LICENSE](LICENSE). Component-specific licenses and notices remain in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md), `sdk/LICENSE`, `shaders/LICENSE.md` and the Android asset notices. Existing copyright headers are preserved.

The upstream APK SHA-256 is `f2be67907cc69427e051fa34387d54af67f54cad8930f3154fce26bfc38124b6`. The build verification report records each reused native library and runtime asset. Only the Android UI, resource translations and packaging were changed; the native engine was not rebuilt.

Need for Speed: Most Wanted belongs to Electronic Arts. Game files and the PC Hungarian translation installer are not included in this repository or the test APK.
