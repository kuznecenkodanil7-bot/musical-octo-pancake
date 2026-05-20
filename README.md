# AI Assistant Fabric Mod для Minecraft 1.21.11

Минимальный Fabric-мод для Minecraft 1.21.11. Добавляет команду:

```mcfunction
/askai <вопрос>
```

Мод отправляет вопрос в OpenAI Responses API и выводит ответ в игровой чат.

## Что внутри

- Minecraft: `1.21.11`
- Fabric Loader: `0.18.1`
- Fabric API: `0.141.4+1.21.11`
- Yarn mappings: `1.21.11+build.5`
- Java: `21`
- Gradle для GitHub Actions: `9.2.1`

## Как собрать через GitHub

1. Создай новый репозиторий на GitHub.
2. Загрузи все файлы из этого архива в репозиторий.
3. Открой вкладку **Actions**.
4. Запусти workflow **Build Fabric Mod** вручную через **Run workflow** или сделай push в `main`.
5. После сборки открой завершённый workflow и скачай artifact `aiassistant-fabric-1.21.11`.
6. Внутри будет `.jar` мода.

## Как поставить в игру

1. Установи Fabric Loader для Minecraft 1.21.11.
2. Установи Fabric API для Minecraft 1.21.11.
3. Положи собранный `.jar` мода в папку `mods`.
4. Запусти игру.

## Как настроить API-ключ

При первом запуске мод создаст файл:

```text
.minecraft/config/aiassistant.properties
```

Открой его и замени:

```properties
openai.api_key=PASTE_YOUR_OPENAI_API_KEY_HERE
```

на свой ключ:

```properties
openai.api_key=sk-...
```

Либо можно задать переменную окружения:

```bash
OPENAI_API_KEY=sk-...
```

Переменная окружения имеет приоритет над файлом конфигурации.

## Команда в игре

```mcfunction
/askai как сделать кирку в майнкрафте?
```

## Как поменять модель

В файле `config/aiassistant.properties` измени строку:

```properties
openai.model=gpt-4.1-mini
```

## Как собрать локально

Нужны Java 21 и Gradle 9.2+.

```bash
gradle build
```

Готовый `.jar` будет в:

```text
build/libs/
```

## Как сделать zip повторно

Linux/macOS:

```bash
./scripts/pack.sh
```

Windows PowerShell:

```powershell
.\scripts\pack.ps1
```
