#!/bin/bash -ex

if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" && "${TRAVIS_REPO_SLUG}" == "sbt/website" ]]; then
  echo -e "Host github.com\n\tStrictHostKeyChecking no\nIdentityFile ~/.ssh/deploy_rsa\n" >> ~/.ssh/config
  openssl aes-256-cbc -pass "pass:$PRIV_KEY_SECRET" -in .travis/deploy_rsa.enc -out .travis/deploy_rsa -d -a
  chmod 600 .travis/deploy_rsa
  cp .travis/deploy_rsa ~/.ssh/
  sbt ghpagesPushSite
fi
