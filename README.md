About
=====
This is a simple example webserver written using java 20, using Jetty embedded server (version 11). 

This should be a good starting point for implementing a webservice in java.

Features:
 * HTTPS, with TLS certificates loading from `keystore/self_signed.keystore`
 * Serves directory `/.well-known/acme-challenge/` to complete acme-challenge with webroot approach (to avoid downtime running certbot in standalone).
 * Builds with maven and stages distribution in `dis/` with `compile-distribution.ps1`
 * Blazingly fast

How to run
==========
1. Build with maven
2. Inside `res/` run `keytool -genkey -keyalg RSA -alias self_signed -keystore self_signed.keystore -storepass my_password123 -dname "CN=127.0.0.1" -noprompt` in order to get a keystore
3. Run Main.java

How to deploy
=============
1. Build with maven
2. Run `compile-distribution.ps1`
3. Copy your keystore to `dis/webapp/res/`
3. Copy `dis/webapp/` to your production server
4. Run `simple-jetty-example-tls.jar` in your preferred way (could be systemd)
5. (Optional) Configure nginx to expose your service to the internet trough port 443
6. (optional) Replace keystore with one generated using certificates from certbot and letsencrypt

License
=======
You can do with this as you want.