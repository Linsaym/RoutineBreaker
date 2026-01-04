# Инструкции по сборке модов

## Сборка всех модов

Для сборки всех модов в отдельные JAR файлы выполните:

```bash
./gradlew buildAllMods
```

или

```bash
gradlew.bat buildAllMods
```

## Сборка отдельных модов

Для сборки конкретного мода используйте:

```bash
# Сборка мода Shared Death
./gradlew jarShareddeath

# Сборка мода Informator
./gradlew jarInformator
```

## Результат сборки

После успешной сборки JAR файлы будут находиться в папке `build/libs/`:

- `shareddeath-1.0.0.jar` - мод Shared Death
- `informator-1.0.0.jar` - мод Informator

## Структура JAR файлов

Каждый JAR файл содержит:
- Классы мода (только классы соответствующего пакета)
- Ресурсы мода
- `META-INF/neoforge.mods.toml` с метаданными мода

## Добавление нового мода

Для добавления нового мода в сборку:

1. Создайте Java класс мода в соответствующем пакете
2. Создайте шаблон TOML: `src/main/templates/META-INF/neoforge.mods.toml.<modid>`
3. Добавьте конфигурацию мода в `build.gradle` в массив `modsToBuild`:

```groovy
[
    modId: 'yourmodid',
    packagePrefix: 'com.yourmod',
    displayName: 'Your Mod Name',
    description: 'Описание вашего мода.'
]
```

4. Добавьте мод в секцию `mods` в `neoForge.mods`:

```groovy
"yourmodid" {
    sourceSet(sourceSets.main)
}
```

После этого выполните `./gradlew buildAllMods` для сборки всех модов, включая новый.


