#!/bin/bash -ex

if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "1.x" && "${TRAVIS_REPO_SLUG}" == "sbt/website" ]]; then
  eval "$(ssh-agent -s)" #start the ssh agent
  openssl aes-256-cbc -K $encrypted_e29f8a2ff2bf_key -iv $encrypted_e29f8a2ff2bf_iv -in deploy_rsa.enc -out deploy_rsa -d
  chmod 600 deploy_rsa
  ssh-add deploy_rsa
  sbt ghpagesPushSite
fi
