@echo off
mvn clean package
mvn package -Plinux32
mvn package -Plinux64
mvn package -Pwindows
mvn package -Pmacosx