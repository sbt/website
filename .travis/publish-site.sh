#!/bin/bash -ex

if [[ "${TRAVIS_PULL_REQUEST}" == "false" && ("${TRAVIS_BRANCH}" == "develop" || "${TRAVIS_BRANCH}" == "1.x-beta") && "${TRAVIS_REPO_SLUG}" == "sbt/website" ]]; then
  openssl version
  echo -e "Host github.com\n\tStrictHostKeyChecking no\nIdentityFile ~/.ssh/deploy_rsa\n" >> ~/.ssh/config
  openssl aes-256-cbc -K $encrypted_e29f8a2ff2bf_key -iv $encrypted_e29f8a2ff2bf_iv -in .travis/deploy_rsa.enc -out .travis/deploy_rsa -d
  chmod 600 .travis/deploy_rsa
  cp .travis/deploy_rsa ~/.ssh/
  /usr/bin/sbt ghpagesPushSite
fi
