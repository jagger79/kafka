#!/bin/bash

# this will create new private key
openssl genrsa -out kafka.key 4096

# create public key
# first create configuration file for openssl to set-up mandatory extensions like 'X509v3 Key Usage' or 'X509v3 Subject Alternative Name'
cat > openssl.kafka.cnf <<EOF
[ req ]
default_bits        = 4096
distinguished_name  = v3_dir_sect
string_mask         = utf8only
#default_md          = sha256
default_md          = sha512

[v3_dir_sect]
C=CZ
O=Oskar a.s.
OU=IT
CN=ocp-kafka
L=Prague

[ v3_ca ]
basicConstraints=CA:FALSE
authorityKeyIdentifier = keyid,issuer
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @v3_ca_alt_names

[v3_ca_alt_names]
DNS.1 = localhost
DNS.2 = czcid1vr.oskarmobil.cz
DNS.3 = czcid1vr
EOF

years=20
# meaning 20 years - you can limit as you wish
days=$(($years*365))
openssl req -x509 -new -sha512 -verbose -extensions v3_ca -config openssl.kafka.cnf -key kafka.key -days $days -out kafka.pem

openssl x509 -in kafka.pem -text > kafka.pem.tmp
mv kafka.pem.tmp kafka.pem

openssl pkcs12 -export -nodes -inkey kafka.key -in kafka.pem -out kafka.p12
openssl pkcs12 -info -in kafka.p12
