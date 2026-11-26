package com.smartintern.student

object Config {
    /**
     * Emulator  : http://10.0.2.2:8000/api/   (10.0.2.2 = your PC from the Android emulator)
     * Real phone: http://<PC-LAN-IP>:8000/api/ and start Laravel with: php artisan serve --host=0.0.0.0
     *             (also add that IP to res/xml/network_security_config.xml)
     */
    const val BASE_URL = "http://192.168.1.20:8000/api/"
}
