# Smart Internship — Student (Intern) app

Kotlin + Jetpack Compose. Talks to the Laravel API (`/api/...`) of the Smart Internship admin system.

## Open & run
1. Android Studio → **Open** → select this folder (`InternApp`). Let Gradle sync (JDK 17 is bundled).
2. Start Laravel (see the Laravel steps): `php artisan serve`
3. Run the app on the **emulator** (it reaches your PC at `http://10.0.2.2:8000`).
4. Demo login: `intern@example.com` / `password`  (or student ID `S1001`).

## Real phone
1. PC and phone on the same Wi-Fi. Find the PC IP (`ipconfig`), e.g. `192.168.1.20`.
2. `php artisan serve --host=0.0.0.0`
3. `Config.kt` → `BASE_URL = "http://192.168.1.20:8000/api/"`
4. `res/xml/network_security_config.xml` → put the same IP in a `<domain>` entry.
5. Allow PHP through Windows Firewall (private network) if the phone cannot connect.

## Production
Use HTTPS, then remove the cleartext `domain-config` block in `network_security_config.xml`.
