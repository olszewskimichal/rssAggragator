[![Build Status](https://travis-ci.org/olszewskimichal/rssAggragator.svg?branch=master)](https://travis-ci.org/olszewskimichal/rssAggragator)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=pl.michal.olszewski:rss-aggregator)](https://sonarcloud.io/dashboard?id=pl.michal.olszewski%3Arss-aggregator)
[![codecov](https://codecov.io/gh/olszewskimichal/rssAggragator/branch/master/graph/badge.svg)](https://codecov.io/gh/olszewskimichal/rssAggragator)
# rssAggragator
Agregator RSS do śledzenia informacji na blogach

Aplikacja wielomodułowa napisana z wykorzystaniem Spring Boota 2.2 + oraz bibioteki Rome, opiera się na API Restowym, aktualnie nie posiada żadnego czytelniejszego GUI.


### ENDPOINTY ###
* /swagger-ui.html Dokumentacja API Swagger  
### Uruchomienie ###
```
mvn clean package
java -jar target/*.jar  - podmienić * na rzeczywistą nazwe Jara
```

### Wykorzystane biblioteki ###
* SpringBoot 2.2+
* ROME
* AssertJ
* Junit5
* Equalsverifier
