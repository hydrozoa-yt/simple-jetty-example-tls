About
============
This is a simple example webserver written using java 20, using Jetty embedded server (version 11). 

This should be a good starting place for implementing a webservice in java.

Features:
 * HTTPS, with TLS certificates loading from `keystore/self_signed.keystore`
 * Serves directory `/.well-known/acme-challenge/` to complete acme-challenge with webroot approach (to avoid downtime running certbot in standalone).
 * Builds with maven and stages distribution in `dis/` with `compile-distribution.ps1`

License
=======
You can do with this as you want.