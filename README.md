# FamilyShopper 🛒

**FamilyShopper** — современное Android-приложение для управления списками покупок внутри семьи (и не только покупок, можно обмениваться любыми списками, что вы захотите туда добавить). Проект построен на полностью декларативном интерфейсе (Jetpack Compose) и предлагает гибкие режимы работы: от полностью приватных локальных списков до общих облачных пространств с разграничением прав доступа.

## 📽 Демонстрация

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/2a0de842-743b-486a-bf47-85cf8b69b41e" width="200" alt="холст_1"></td>
    <td><img src="https://github.com/user-attachments/assets/302a412c-42a6-4ea5-ad0b-4b0b35ae8ca0" width="200" alt="холст_2"></td>
    <td><img src="https://github.com/user-attachments/assets/d0ec192c-53e6-4ddf-a6b2-4cdceb089383" width="200" alt="холст_3"></td>
    <td><img src="https://github.com/user-attachments/assets/44872845-443d-4075-a7b1-19eeb86bd1a2" width="200" alt="холст_4"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/2e1f15a5-ef05-47dc-ba93-b223819d8a59" width="200" alt="холст_5"></td>
    <td><img src="https://github.com/user-attachments/assets/6d2d3ea0-9e6b-477f-afdb-e1761efe3edf" width="200" alt="холст_6"></td>
    <td><img src="https://github.com/user-attachments/assets/bfede8ff-5dc2-4cc3-9db5-a06dc6b2b5fa" width="200" alt="холст_7"></td>    
    <td><img src="https://github.com/user-attachments/assets/1be9850e-810d-42c3-b6ee-71f9e4250ae3" width="200" alt="холст_8"></td>
  </tr>
</table>

## ✨ Основные возможности

### 🔐 Приватность и доступность (Privacy-friendly)
- **Без Google-сервисов:** Приложение не требует наличия Google Play Services или регистрации через Google-аккаунт. Это делает его доступным для любых Android-устройств (включая Huawei/Honor без GMS).
- **Анонимность:** Мы не собираем личные данные и не отслеживаем вашу активность.

### 🔐 Гибкое управление доступом
При создании списка вы можете выбрать один из 4 уровней приватности:
1. **Общий:** Все участники могут добавлять, изменять и удалять товары.
2. **Ограниченный:** Участники могут только отмечать товары как «купленные».
3. **Только просмотр:** Список доступен другим только для ознакомления.
4. **Частный:** Список не виден никому, кроме вас.

### 🌐 Гибридный режим работы
- **Offline:** Работайте локально без интернета. Данные сохраняются в базе Room. Для этого, в настройках, оставьте ключ хранилища пустым.
- **Online:** Синхронизируйте списки в реальном времени через Firebase для совместных походов в магазин.

### 📝 Умные покупки
- **Персональный словарь:** Создавайте базу своих любимых товаров, чтобы добавлять их в списки за пару кликов.
- **Детализация:** Добавляйте комментарии к каждому товару (бренд, вес, особые пожелания).

---

## 🛠 Технологический стек

Проект написан на **Kotlin** с использованием самого современного стека разработки:

*   **UI:** [Jetpack Compose](https://android.com) (100% декларативный интерфейс).
*   **Архитектура:** Clean Architecture + MVVM.
*   **DI:** [Koin](https://insert-koin.io) (Koin Compose, ViewModel).
*   **Сеть & Backend:** [Firebase](https://google.com) (Realtime Database, Auth), [Retrofit 2](https://github.io).
*   **Локальное хранение:** [Room Persistence Library](https://android.com).
*   **Навигация:** Compose Navigation.
*   **Сериализация:** Kotlinx Serialization.

---

## 📦 Основные зависимости

В проекте используются следующие библиотеки (из `libs.versions.toml`):
- **Core:** `androidx-core-ktx`, `lifecycle-runtime-ktx`
- **UI:** `Material3`, `Compose BOM`, `Material-icons-extended`
- **DI:** `Koin-bom`, `koin-compose-viewmodel`
- **Remote:** `Firebase-bom`, `Retrofit`, `Gson/Serialization`
- **Database:** `Room-runtime`, `Room-ktx`

---

## 🚀 Как запустить проект

1. **Клонируйте репозиторий:**
   ```bash
   git clone https://github.com/Igor-Abdulganeev/FamilyShopper
   ```

2. **Настройка Firebase:**
    - Создайте проект в [Firebase Console](https://console.firebase.google.com/).
    - Добавьте Android-приложение с вашим Package Name.
    - Скачайте файл `google-services.json` и положите его в папку `app/`.
    - Включите **Realtime Database** и примените правила, указанные ниже.
3. **Сборка и запуск:**
   Откройте проект в Android Studio и запустите сборку. Приложение готово к работе как в локальном, так и в сетевом режиме сразу после установки.
## ⚙️ Настройка Firebase Realtime Database

Приложение спроектировано для работы **без обязательной привязки к Google-сервисам**, чтобы обеспечить доступность для всех Android-устройств. Для корректной синхронизации установите в консоли Firebase следующие правила:

```json
{
  "rules": {
    "shared_data": {
      "$groupId": {
        ".read": "true",
        ".write": "true"
      }
    }
  }
}
```

*Эти правила позволяют пользователям взаимодействовать со списками по ID группы, не требуя авторизации через Google-аккаунт.*


---

## 📄 Лицензия
Этот проект распространяется под лицензией **GNU General Public License v3.0**.
- [Текст лицензии](https://www.gnu.org/licenses/gpl-3.0.html)

---
**Разработчик:** [Igor-Abdulganeev](https://github.com)  
**Связь:** [Telegram (@GorInIh)](https://t.me/GorInIh)
