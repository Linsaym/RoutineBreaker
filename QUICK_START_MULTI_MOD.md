# Быстрый старт: Добавление нового мода в проект

## Шаги для добавления нового мода:

### 1. Создайте пакет и класс мода
```
src/main/java/com/yourmod/yourmod/YourMod.java
```

```java
package com.yourmod.yourmod;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(YourMod.MOD_ID)
public class YourMod {
    public static final String MOD_ID = "yourmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public YourMod() {
        LOGGER.info("YourMod загружен!");
    }
}
```

### 2. Добавьте в build.gradle (секция `mods`):
```groovy
"yourmod" {
    sourceSet(sourceSets.main)
}
```

### 3. Добавьте в src/main/templates/META-INF/neoforge.mods.toml:
```toml
[[mods]]
modId="yourmod"
version="${mod_version}"
displayName="Your Mod Name"
authors="${mod_authors}"
description='''Описание вашего мода.'''

[[dependencies.yourmod]]
    modId="neoforge"
    type="required"
    versionRange="[${neo_version},)"
    ordering="NONE"
    side="BOTH"

[[dependencies.yourmod]]
    modId="minecraft"
    type="required"
    versionRange="${minecraft_version_range}"
    ordering="NONE"
    side="BOTH"
```

### 4. Создайте папку ресурсов (если нужна):
```
src/main/resources/assets/yourmod/
```

**Готово!** Теперь ваш мод будет загружаться вместе с остальными модами.

## Важно помнить:
- ✅ MOD_ID должен быть уникальным
- ✅ MOD_ID должен совпадать с названием папки в assets (если используется)
- ✅ MOD_ID должен совпадать в трех местах: Java класс, build.gradle, neoforge.mods.toml

