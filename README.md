# RoutineBreaker — NeoForge Minecraft Mod Pack ⚡🎮

This repository contains a collection of small mods for **NeoForge** (latest Minecraft version) that:

- **Eliminates routine from the game** 🧹✨
- **Modifies hardcore gameplay to make it more enjoyable** 💀❤️

---

## 📦 Mods

### 🔥 **Hardcore**
- **shareddeath** 💔⚰️
  - If one player dies on the server, **all other players die as well** 😱

### 🛠️ **Regular**
- **informator** 🗺️🔥
  - Upon entering the **Nether**, announces the coordinates of the nearest **Nether Fortress** 🏰

- **witheraxehead** ⚔️💀
  - Killing a **Wither Skeleton with an axe** has a **100% chance** to drop a **Wither Skeleton skull** 🎯

- **toppacktoggle** 🎛️📦
  - Press **0** to remove/restore the top enabled **resource pack**. On first press, remembers the pack id and saves it to config. You can edit selected pack in `.minecraft\config\toppacktoggle.json`

---

## ⚙️ Installation

- Copy the desired `.jar` files into the `mods` folder 📁➡️✅

---

## 🏗️ Building

```bash
./gradlew buildAllMods
```

📦 **Artifacts are placed in:**

- `build/libs/hardcore` — hardcore mods 💀⚙️
- `build/libs/normal` — regular mods 🛠️✨