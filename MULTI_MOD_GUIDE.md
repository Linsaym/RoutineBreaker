# Руководство по работе с несколькими модами в одном проекте

## Структура проекта

Для работы с несколькими модами в одном проекте нужно:

### 1. Организация пакетов Java

Каждый мод должен находиться в своем пакете:
```
src/main/java/com/
  ├── shareddeath/
  │   └── shareddeath/
  │       └── SharedDeathMod.java
  ├── toppacktoggle/
  │   └── toppacktoggle/
  │       └── TopPackToggleMod.java
  └── informator/
      └── informator/
          └── InformatorMod.java
```

### 2. Организация ресурсов

Каждый мод имеет свою папку в assets:
```
src/main/resources/assets/
  ├── shareddeath/
  │   └── lang/
  └── teleportationtables/
      ├── lang/
      └── models/
```

### 3. Настройка build.gradle

В секции `neoForge.mods` нужно определить привязку для каждого мода

### 4. Настройка neoforge.mods.toml

В файле `src/main/templates/META-INF/neoforge.mods.toml` нужно добавить секцию `[[mods]]` для каждого мода:
### 5. Аннотация @Mod в Java-классах

Каждый главный класс мода должен иметь уникальный MOD_ID:

```java
// SharedDeathMod.java
@Mod(SharedDeathMod.MOD_ID)
public class SharedDeathMod {
    public static final String MOD_ID = "shareddeath";
    // ...
}

// TeleportationTablesMod.java
@Mod(TeleportationTablesMod.MOD_ID)
public class TeleportationTablesMod {
    public static final String MOD_ID = "teleportationtables";
    // ...
}
```

### 6. gradle.properties

Свойства `mod_id`, `mod_name` и `mod_group_id` в `gradle.properties` используются только для генерации метаданных, но при работе с несколькими модами они не критичны, так как каждый мод имеет свои метаданные в `neoforge.mods.toml`.

## Преимущества такого подхода

1. ✅ Один проект для всех модов
2. ✅ Общий код можно вынести в отдельные классы
3. ✅ Одна сборка создает JAR с несколькими модами
4. ✅ Легче поддерживать общие зависимости

## Важные моменты

- **MOD_ID должен быть уникальным** для каждого мода
- **Папки в assets** должны совпадать с MOD_ID
- **Все моды** загружаются вместе при запуске игры
- **Зависимости между модами** можно настроить в neoforge.mods.toml

## Пример зависимости между модами

Если один мод зависит от другого:

```toml
[[dependencies.teleportationtables]]
    modId="shareddeath"
    type="required"  # или "optional"
    versionRange="[1.0.0,)"
    ordering="AFTER"  # загружается после shareddeath
    side="BOTH"
```

