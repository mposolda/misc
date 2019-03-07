# DELETE EXISTING STUFF IN "certs" DIRECTORY
cd certs
rm *

# GENERATE SERVER "localhost" CERTIFICATE AND PUT INTO KEYSTORES
keytool -genkey -alias localhost -keyalg RSA -keystore keycloak-server.jks -validity 10950 -keysize 2048

keytool -exportcert -keystore keycloak-server.jks -alias localhost -file localhost.crt
#keytool -importcert -keystore keycloak-jbrown.jks -alias localhost -file localhost.crt
keytool -importcert -keystore keycloak-bwilson.jks -alias localhost -file localhost.crt
rm localhost.crt

keytool -list -keystore keycloak-server.jks
#keytool -list -keystore keycloak-jbrown.jks
keytool -list -keystore keycloak-bwilson.jks

# GENERATE ROOT CERTIFICATE
openssl genrsa -out rootCA.key 2048
openssl req -x509 -new -nodes -key rootCA.key -days 1024 -out rootCA.pem

#IMPORT ROOT CA TO ALL KEYSTORES. IT NEEDS TO BE TRUSTED BY SERVER AND BY CLIENTS TOO
keytool -import -keystore keycloak-server.jks -file rootCA.pem -alias rootClient
#keytool -import -keystore keycloak-jbrown.jks -file rootCA.pem -alias rootClient
keytool -import -keystore keycloak-bwilson.jks -file rootCA.pem -alias rootClient

# GENERATE SIGNED REQUEST FOR BWILSON AND SIGN IT
keytool -genkey -alias bwilson -keyalg RSA -keystore keycloak-bwilson.jks -validity 10950
keytool -certreq -alias bwilson -keystore keycloak-bwilson.jks > keycloak-bwilson.careq
openssl x509 -req -in keycloak-bwilson.careq -CA rootCA.pem -CAkey rootCA.key -CAcreateserial -out keycloak-bwilson.crt -days 500
keytool -import -alias bwilson -keystore keycloak-bwilson.jks -file keycloak-bwilson.crt
rm keycloak-bwilson.careq
rm keycloak-bwilson.crt

# SAME FOR JBROWN
#keytool -genkey -alias jbrown -keyalg RSA -keystore keycloak-jbrown.jks -validity 10950
#keytool -certreq -alias jbrown -keystore keycloak-jbrown.jks > keycloak-jbrown.careq
#openssl x509 -req -in keycloak-jbrown.careq -CA rootCA.pem -CAkey rootCA.key -CAcreateserial -out keycloak-jbrown.crt -days 500
#keytool -import -alias jbrown -keystore keycloak-jbrown.jks -file keycloak-jbrown.crt
#rm keycloak-jbrown.careq
#rm keycloak-jbrown.crt

# GENERATE P12 CERTIFICATE FOR BWILSON TO BE IMPORTED INTO BROWSER
keytool -importkeystore -srckeystore keycloak-bwilson.jks -destkeystore keycloak-bwilson.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass secret -deststorepass secret -srcalias bwilson -destalias bwilson -srckeypass secret -destkeypass secret -noprompt



# DELETE SOME rootCA FILES
rm rootCA.key
rm rootCA.pem
rm rootCA.srl