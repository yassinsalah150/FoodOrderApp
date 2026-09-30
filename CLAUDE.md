# NETW704 Milestone 1 - food ordering app (Buyer/Seller)
- Package: com.yassin.foodorderapp
- Kotlin, minSdk 24, view binding (no findViewById)
- Firebase Auth (email/password) + Realtime Database only
- Google services plugin id "com.google.gms.google-services" version 4.5.0
- DB: /users/{uid} = { uid, name, phone, email, role }, role is "buyer" or "seller"
- Screens: LoginActivity (launcher), RegisterActivity, HomeActivity (profile view/edit)
- Explicit intents; clear the back stack after login
- Comment the code well; keep it simple (it is graded)
- Run ./gradlew assembleDebug to verify before finishing
