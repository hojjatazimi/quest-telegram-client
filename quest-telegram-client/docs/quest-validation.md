# Quest Validation Checklist

Use this checklist before merging user-facing UI or input changes. Record findings in the PR description or a follow-up issue.

## Required Device Pass

- Device: Meta Quest 3
- Build variant: `fakeDebug`
- Install path: Android Studio Run or `adb install -r app/build/outputs/apk/fake/debug/app-fake-debug.apk`

## Smoke Flow

- Launches from App Library > Unknown Sources as TeleQuest.
- Login screen text is readable in headset.
- Phone field accepts input from headset keyboard.
- Fake code flow reaches the chat list.
- Chat list rows are comfortable to target with controller.
- Chat list rows are comfortable to target with hand input.
- Opening a chat shows the two-pane conversation surface.
- Message composer accepts text and sends a fake message.
- Back navigation returns to the inbox.
- Logout returns to the login screen.

## Visual And Ergonomic Notes

- Text readability:
- Panel comfort:
- Controller focus behavior:
- Hand tracking behavior:
- Keyboard/input ergonomics:
- Launcher icon/name:
- High-confidence fixes needed before TDLib work:
