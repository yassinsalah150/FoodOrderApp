# FoodOrderApp

NETW704 Milestone 1: a simple Android food ordering app where users register as either a **buyer** or a **seller**. It uses Kotlin, view binding, Firebase Authentication (email/password) and Firebase Realtime Database.

## How to run

1. Open the project in **Android Studio**.
2. Create a Firebase project with **Authentication** (Email/Password enabled) and a **Realtime Database**.
3. Register an Android app in Firebase with the package name `com.yassin.foodorderapp`.
4. Download `google-services.json` and put it in the `app/` folder.
5. Sync Gradle, then run the app on an emulator or device with **API 24 or higher**.

## Database structure

User profiles are stored in Realtime Database at `/users/{uid}`, where `{uid}` is the Firebase Auth user id:

```
users
 └── {uid}
      ├── uid:   "..."
      ├── name:  "..."
      ├── phone: "..."
      ├── email: "..."
      └── role:  "buyer" | "seller"
```

## Implemented features

- Email registration with a buyer/seller role
- Login with role-based redirect
- Auto-login when a user is already signed in
- Profile view and edit (name and phone)
- Logout
