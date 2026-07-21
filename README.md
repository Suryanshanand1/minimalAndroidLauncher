# Minimal Launcher

A distraction-free, minimal Android home launcher. No icons, no colors, no clutter — just a clean text-based app list and a home screen with a clock and your favorite app shortcuts.

## Features

### Home Screen
- Digital clock with date
- User-configurable shortcut grid (apps you choose)

### App List
- Swipe right from home to access the full app list
- Text-only list — no icons, no distractions
- Shows all installed apps with launcher support

### Shortcuts (Add/Remove)
- **Long-press** any app in the list → choose **Add to Home**
- **Long-press** a shortcut on the home screen → removes it
- Shortcuts are persisted across reboots

### Long-Press Menu (App List)
- **Add to Home** — add app as a home screen shortcut
- **App Info** — open system app settings
- **Uninstall** — prompt to uninstall the app

### Theme
- Follows system dark/light mode automatically
- Light mode: white background, black text
- Dark mode: dark gray background, light gray text
- Icons rendered in greyscale for reduced visual noise

### Adaptive Layout
- Grid columns and icon sizes adjust automatically for phones, foldables, and tablets

## Requirements

- Android 5.0+ (API 21)
- No special permissions required

## Build

Open in Android Studio and build, or use Gradle:

```
gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/java/com/minimal/launcher/
├── MainActivity.kt          — Hosts ViewPager2 with two pages
├── LauncherPagerAdapter.kt  — Fragment adapter for swipe navigation
├── HomeFragment.kt          — Clock + shortcuts grid
├── AppListFragment.kt       — Full app list with long-press menu
├── AppAdapter.kt            — RecyclerView adapter for icon grid
└── TextAppAdapter.kt        — RecyclerView adapter for text-only list

app/src/main/res/layout/
├── activity_main.xml        — ViewPager2
├── fragment_home.xml        — Clock + shortcuts
├── fragment_app_list.xml    — App list
├── app_item.xml             — Icon + label (home shortcuts)
└── app_item_text.xml        — Text only (app list)
```

## Setting as Default Launcher

1. Install the APK
2. Press the Home button
3. Select **Minimal Launcher** and tap **Always**
