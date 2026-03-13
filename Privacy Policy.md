# 📟 NoSignalClub

> Forward WhatsApp and Telegram notifications to SMS — so your feature phone never misses a message.

![Platform](https://img.shields.io/badge/platform-Android-green?style=flat-square&logo=android)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)
![Language](https://img.shields.io/badge/Kotlin-Compose-7F52FF?style=flat-square&logo=kotlin)

---

## What it does

NoSignalClub runs silently in the background and listens for incoming WhatsApp notifications. When one arrives, it immediately forwards the message content as an SMS to a phone number you configure — perfect for staying connected on a basic/feature phone with no internet.

**Use case:** You carry a Nokia or any button phone as your main SIM. Your Android sits at home. NoSignalClub bridges the gap.

---

## Features

- ✅ Forwards WhatsApp notifications to any SMS number
- ✅ Runs as a persistent background service
- ✅ No internet permission — everything stays on-device
- ✅ No accounts, no cloud, no tracking
- ✅ Localized in 7 languages: 🇬🇧 🇷🇺 🇺🇦 🇩🇪 🇫🇷 🇪🇸 🇮🇹

---

## Permissions

| Permission | Reason |
|---|---|
| `SEND_SMS` | Sends the forwarded message via your SIM card |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Reads incoming WhatsApp notification content |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Keeps the background service alive (optional) |

No data is ever sent to any server. See the full [Privacy Policy](https://pushkadev.github.io/NoSignalClub/privacy-policy.html).

---

## Setup

1. Install the app
2. Open **Settings** and enter the target SMS number (e.g. `+4915...`)
3. Tap **Notification Access** and enable NoSignalClub in the system list
4. (Optional) Disable battery optimization for the app
5. Hit **Start** on the home screen

---

## Build

```bash
git clone https://github.com/pushkadev/NoSignalClub.git
cd NoSignalClub
./gradlew assembleDebug
```

Requires Android Studio Hedgehog or later. Min SDK: 26.

---

## Privacy

NoSignalClub does not collect, store, or transmit any personal data.  
Full policy: [pushkadev.github.io/NoSignalClub/privacy-policy.html](https://pushkadev.github.io/NoSignalClub/privacy-policy.html)

---

## Contributing

Bug reports and suggestions welcome via [Issues](https://github.com/pushkadev/NoSignalClub/issues).  
Pull requests are open.

---

## License

MIT © 2026 pushkadev