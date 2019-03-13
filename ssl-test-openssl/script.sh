#!/bin/bash -e

# 1 - INFRASTRUCTURE

cd root

echo "Deleting 'ca' directory";
rm -rf ca

echo "Creating 'ca' directory";
mkdir ca
cp openssl*.cnf ca/

cd ca

echo "Creating directories and base files"
mkdir certs crl newcerts private database

echo '01' > database/serial
echo '01' > crl/crlnumber
touch database/index.txt





# 2 GENERATE 'CA' - KEYS AND CERTS FOR CERTIFICATE AUTHORITY

# Using configuration file without asking questions
echo "Creating CA cert"
openssl req -new -x509 -nodes \
  -out certs/cacert.pem \
  -keyout private/cakey.pem \
  -config openssl.cnf \
  -subj "/C=CS/ST=Czech/L=Brno/O=My Company/OU=IT/CN=ca" \
  -set_serial 01 \
  -batch





# 3 GENERATE 'USERNAME' - KEYS AND CERTS FOR 'username'

# Generate private key of username
openssl genrsa -out newcerts/username_key.pem 2048

# Generate request for username
openssl req -utf8 -nameopt oneline,utf8 -new -key newcerts/username_key.pem \
  -out newcerts/username_req.pem \
  -reqexts usr_cert \
  -subj "/C=CS/ST=Czech/L=Brno/O=My Company/OU=IT/CN=username" \
  -config openssl.cnf -batch

# Generate certificate for username
openssl x509 -days 365 -req \
                       -CA certs/cacert.pem \
                       -CAkey private/cakey.pem \
                       -CAserial database/serial \
                       -in newcerts/username_req.pem \
                       -out newcerts/username.pem \
                       -extfile openssl.cnf -extensions usr_cert
                       

# Convert certificate to p12
openssl pkcs12 \
       -inkey newcerts/username_key.pem \
       -in newcerts/username.pem \
       -export -out newcerts/username.p12 \
       -passout pass:password





# 4 GENERATE 'USERNAME' - KEYS AND CERTS FOR 'username'

# Generate private key of username_revoked
openssl genrsa -out newcerts/username_revoked_key.pem 2048

# Generate request for username_revoked
openssl req -utf8 -nameopt oneline,utf8 -new -key newcerts/username_revoked_key.pem \
  -out newcerts/username_revoked_req.pem \
  -reqexts usr_cert \
  -subj "/C=CS/ST=Czech/L=Brno/O=My Company/OU=IT/CN=username_revoked" \
  -config openssl.cnf -batch

# Generate certificate for username_revoked
openssl x509 -days 365 -req \
                       -CA certs/cacert.pem \
                       -CAkey private/cakey.pem \
                       -CAserial database/serial \
                       -in newcerts/username_revoked_req.pem \
                       -out newcerts/username_revoked.pem \
                       -extfile openssl.cnf -extensions usr_cert


# Convert certificate to p12
openssl pkcs12 \
       -inkey newcerts/username_revoked_key.pem \
       -in newcerts/username_revoked.pem \
       -export -out newcerts/username_revoked.p12 \
       -passout pass:password


# 5 - REVOKE 'username_revoked' AND CHECK IT IS REVOKED


# Generate CRL
#openssl ca -gencrl -out crl/crl.pem -config openssl.cnf
openssl ca -revoke newcerts/username_revoked.pem -config openssl.cnf

openssl ca -gencrl \
  -config openssl.cnf \
  -out crl/crl.pem

# Need to concatenate CRL with the certificate CA to have a full chain
cat certs/cacert.pem crl/crl.pem > crl/crl_chain.pem

# This should be OK
echo
echo "Verifying if username.pem is OK. It should be."
openssl verify -crl_check -CAfile crl/crl_chain.pem newcerts/username.pem

# This should be revoked
echo
echo "Verifying if username.pem is OK. It should NOT be."
openssl verify -crl_check -CAfile crl/crl_chain.pem newcerts/username_revoked.pem

