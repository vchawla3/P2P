#!/bin/bash
# This is just used to configure a digital ocean ubuntu 16 droplet with java 8 for testing the program

sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install openjdk-8-jdk
apt-cache search jdk
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin
javac -version

