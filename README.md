# microservice-baseline

A typical simple baseline for building good microservices

![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Cantara/microservice-baseline) ![Build Status](https://jenkins.quadim.ai/buildStatus/icon?job=microservice-baseline) - [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active)  [![Known Vulnerabilities](https://snyk.io/test/github/Cantara/microservice-baseline/badge.svg)](https://snyk.io/test/github/Cantara/microservice-baseline)

### Documentation
* https://wiki.cantara.no/display/architecture/Typical+micro+service+technology+stack+(java)


### Quick build and verify


```jshelllanguage
mvn clean install
java -jar target/microservice-baseline-0.1-SNAPSHOT.jar
wget http://localhost:8086/microservice-baseline/health
wget "http://localhost:8086/microservice-baseline/token?grant_type=client_credentials&grant_type=client_credentials&client_id=CLIENT_ID&client_secret=CLIENT_SECRET"
curl -X POST "http://localhost:8086/microservice-baseline/token?grant_type=client_credentials&grant_type=client_credentials&client_id=CLIENT_ID&client_secret=CLIENT_SECRET"
curl -X POST "http://localhost:8086/microservice-baseline/token?grant_type=authorization_code&code=mycode&redirect_uri=http://ocalhost:8086/microservice-baseline/oauth2&client_id=CLIENT_ID&client_secret=CLIENT_SECRET"
curl -i -H "Authorization: Bearer AsT5OjbzRn430zqMLgV3Ia" http://localhost:8086/microservice-baseline/verify
```
