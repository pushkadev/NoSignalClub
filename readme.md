# 📟 NoSignalClub

> Forward WhatsApp & Telegram messages as SMS to any phone number — fully offline, no servers, open source.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)
![Language](https://img.shields.io/badge/Kotlin-Compose-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Offline](https://img.shields.io/badge/offline-100%25-00CC6A?style=flat-square)

---

## What it does

NoSignalClub listens for incoming WhatsApp and Telegram notifications and automatically forwards the message text as an SMS to a phone number you configure. Everything runs locally on your device — no servers, no accounts, no internet permission.

> ⚠️ Current version supports text messages only. Media forwarding is not supported.

---

## Use Cases

**📵 Digital Detox**
Leave your Android at home and walk around with a Nokia or any basic phone. NoSignalClub forwards incoming WhatsApp and Telegram messages to your feature phone as SMS — you stay reachable without carrying the internet in your pocket.

**📶 Internet only available in specific locations**
Your internet works only at home (via Wi-Fi or VPN). The app receives messages when connected and forwards them as SMS to a number that's outside 4G coverage. Useful anywhere mobile data is restricted or unavailable.

**🏢 Workplace restrictions**
Messengers are banned at your workplace but SMS is allowed. NoSignalClub lets you receive WhatsApp and Telegram message notifications as regular text messages.

---

## Features

- 📥 Intercepts incoming WhatsApp and Telegram notifications via `NotificationListenerService`
- 📤 Forwards message text to a configured number as SMS via Android `SmsManager`
- 🔐 100% offline — no backend, no external APIs, no third-party data sharing
- 🧩 Fully open source — read, audit, fork, or contribute
- 🛠 Build your own APK via Android Studio

---

## Install

**Option A — Download APK**
1. Download the latest APK from the [Releases](https://github.com/pushkadev/NoSignalClub/releases) page
2. Install on your Android device (you may need to allow installation from unknown sources)
3. Open the app and follow the setup instructions

**Option B — Build from source**
```bash
git clone git@github.com:pushkadev/NoSignalClub.git
```
1. Open the project in Android Studio: `File → Open → select project folder`
2. Build APK: `Build → Build APK(s)`
3. Or run directly on a connected device via `Run`

---

## Setup

1. Enter the target SMS number in **Settings** (e.g. `+4915...`)
2. Tap **Notification Access** → enable NoSignalClub in the system list
3. *(Optional)* Disable battery optimization for the app to keep the service alive
4. Tap **Start** on the home screen

---

## Permissions

| Permission | Purpose |
|---|---|
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Read incoming WhatsApp & Telegram notifications |
| `SEND_SMS` | Forward messages as SMS via your SIM card |
| Phone state *(if required)* | Detect call state to pause forwarding during calls |

All permissions are requested manually from the user. No permission is used for anything beyond its stated purpose.

---

## Architecture

- **`NotificationListenerService`** — monitors notifications from WhatsApp and Telegram
- **`SmsManager`** — sends SMS using the device's own SIM card
- **`DataStore`** — stores only two values locally: target number and enabled/disabled state
- No backend, no cloud sync, no third-party SDKs

---

## Privacy

- No data is sent to external servers
- No analytics, no trackers, no crash reporters
- Messages are never stored outside the device
- Source code is open and available for review

Full privacy policy: [pushkadev.github.io/NoSignalClub/privacy-policy.html](https://pushkadev.github.io/NoSignalClub/privacy-policy.html)

---

## Limitations

- Works only when WhatsApp / Telegram notifications are enabled
- Text messages only — no media forwarding
- Android only
- The app must be running in the background (disable battery optimization for best results)

---

## Roadmap

Have a feature request or found a bug? Open an [Issue](https://github.com/pushkadev/NoSignalClub/issues) — your feedback directly shapes what gets built next.

---

## Languages

The app is localized in 7 languages: 🇬🇧 English · 🇷🇺 Russian · 🇺🇦 Ukrainian · 🇩🇪 German · 🇫🇷 French · 🇪🇸 Spanish · 🇮🇹 Italian

---

## Contributing

Everyone is welcome to:
- Read and audit the source code
- Build their own APK
- Open an Issue with a question or suggestion
- Submit a Pull Request

---

## Disclaimer

The user is solely responsible for compliance with the laws of their country and the terms of service of the respective messengers. The author assumes no liability for any consequences of using this application.

---

## License

MIT © 2026 [pushkadev](https://github.com/pushkadev)