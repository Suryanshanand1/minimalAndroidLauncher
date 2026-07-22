# Minimal Launcher

A distraction-free, minimal Android home launcher. No icons, no colors, no clutter — just a clean text-based app list and a home screen with a clock and your favorite app shortcuts.

## Features

### Home Screen
- Digital clock with date
- Second clock with customizable timezone (set in Settings)
- User-configurable shortcut grid (apps you choose)
- Shortcuts launch the app on tap; long-press to remove

### App List
- Swipe right from home to access the full app list
- **Search bar** at the top — filter apps as you type
- Text-only list — no icons, no distractions
- Shows all installed apps with launcher support
- Sort alphabetically or by install date
- Settings gear icon in the app list header

### Shortcuts (Add/Remove)
- **Long-press** any app in the list → choose **Add to Home**
- **Long-press** a shortcut on the home screen → removes it
- Shortcuts are persisted across reboots

### Long-Press Menu (App List)
- **Add to Home** — add app as a home screen shortcut
- **App Info** — open system app settings
- **Uninstall** — open App Info screen to uninstall

### Settings
- **Theme** — System / Light / Dark (follows your preference)
- **Sort Order** — Alphabetical or by Install Date
- **Show UTC Clock** — toggle the second clock on the home screen
- **Show Date** — toggle the date display
- **Enable Icon Colors** — switch between greyscale and full-color icons
- **Clock Timezone** — pick from a dropdown of common timezones

### Default Launcher Prompt
- On first launch, a dialog asks if you want to set Minimal Launcher as your default home screen

### Theme
- Follows system dark/light mode automatically
- Light mode: white background, black text
- Dark mode: dark gray background, light gray text
- Icons rendered in greyscale by default (toggleable in Settings)

### Adaptive Layout
- Grid columns and icon sizes adjust automatically for phones, foldables, and tablets

## Requirements

- Android 5.0+ (API 21)
- No special permissions required

## Build

Open in Android Studio and build:

```
Build > Build Bundle(s) / APK(s) > Build APK
```

APK output: `app/build/outputs/apk/debug/MinimalLauncher-1.0-debug.apk`

## Setting as Default Launcher

1. Install the APK
2. On first launch, tap **"Set as default"** in the prompt
3. Select **Minimal Launcher** in the Home settings and confirm

Or manually:
1. Press the Home button
2. Select **Minimal Launcher** and tap **Always**
