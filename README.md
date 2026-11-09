# LeanType: SMS OTP + Russian next-word fork

Это неофициальный форк исходного кода LeanType. Репозиторий публикуется «как есть» как личная модификация, без поддержки, гарантий и планов на отдельный продукт.

## Что добавлено

1. **SMS OTP**
   - приём SMS через `RECEIVE_SMS`;
   - показ кода в панели подсказок;
   - TTL кода — 120 секунд;
   - существующий OTP-код не сохраняется в историю буфера обмена.

2. **Русские контекстные подсказки**
   - локальная модель следующего слова Stage 3.2;
   - объединение curated-подсказок и n-граммной модели;
   - персональная история и существующая автокоррекция;
   - всё работает офлайн, без сетевых запросов.

Остальные функции и оформление — из LeanType. Оформление iPhone из этапов 4.0–4.2 в этой публикации удалено; используется стандартный дизайн LeanType.

## Сборка

Требования: JDK 17, Android SDK с compileSdk 36, Android NDK 28.0.13004108.

```bash
./gradlew :app:assembleOfflineDebug
```

Windows:

```powershell
.\gradlew.bat :app:assembleOfflineDebug
```

Готовый APK обычно находится здесь:

```text
app/build/outputs/apk/offline/debug/
```

Сборка использует debug-подпись и не требует `keystore.properties`.

## Готовый APK

В корне подготовлен файл `LeanType-4.3.1-offline-debug.apk` (не коммитится, добавлен в `.gitignore`):

- SHA-256: `536CC228A73901D5976306D8DFE0DB5CD5A6B846CF97E47EB9A608AD7CE1B7F6`
- versionCode: `4315`
- versionName: `4.3.1`

Его можно отдельно прикрепить к GitHub Release.

## Установка

Установите APK через файловый менеджер Android или Android Studio. Для установки поверх предыдущей сборки может потребоваться совместимый `versionCode` и та же подпись.

SMS OTP включается в настройках LeanType. Приложение запрашивает разрешение `RECEIVE_SMS` только для этой функции.

## Лицензии и авторство

- LeanType: [LeanBitLab/LeanType](https://github.com/LeanBitLab/LeanType), GPL-3.0.
- Основа: HeliBoard / OpenBoard / AOSP LatinIME; сведения об авторстве сохранены в исходниках и лицензионных файлах.
- Русская n-граммная модель является производной от предложений Tatoeba (CC BY 2.0 FR). Подробности: [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) и [MODEL_CARD.md](tools/prediction/models/MODEL_CARD.md).

Проект распространяется по лицензии GPL-3.0; см. [LICENSE](LICENSE). Сторонние лицензии находятся в `LICENSE-Apache-2.0` и `LICENSE-CC-BY-SA-4.0`.

## Ограничения

Это личная публикация исходников, а не официальный релиз LeanType. Совместимость с конкретными устройствами, поддержка пользователей и регулярные обновления не гарантируются.
