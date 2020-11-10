#!/bin/bash -ex

mkdir -p $HOME/.ssh/
echo -e "Host github.com\n\tStrictHostKeyChecking no\nIdentityFile ~/.ssh/deploy_rsa\n" >> ~/.ssh/config

gpg --quiet --batch --yes --decrypt --passphrase="$DEPLOY_KEY_PASSPHRASE" \
--output .github/deploy_rsa .github/deploy_sbt_website_rsa.gpg
chmod 600 .github/deploy_rsa
cp .github/deploy_rsa $HOME/.ssh/
sbt -v -Dsbt.website.detect_pdf ghpagesPushSite
