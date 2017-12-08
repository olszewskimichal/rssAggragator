[![Build Status](https://travis-ci.org/olszewskimichal/rssAggragator.svg?branch=master)](https://travis-ci.org/olszewskimichal/rssAggragator)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=pl.michal.olszewski:rss-aggregator)](https://sonarcloud.io/dashboard?id=pl.michal.olszewski%3Arss-aggregator)
[![codecov](https://codecov.io/gh/olszewskimichal/rssAggragator/branch/master/graph/badge.svg)](https://codecov.io/gh/olszewskimichal/rssAggragator)
# rssAggragator
Agregator RSS do śledzenia informacji na blogach

Aplikacja napisana z wykorzystaniem Spring Boota 1.5 + oraz bibioteki Rome.
Aplikacja opiera się na API Restowym, aktualnie nie posiada żadnego czytelniejszego GUI.


### ENDPOINTY ###
* /api/v1/blogs - zwraca wszystkie blogi
```
GET /{id}  - szukanie blogu po id
```
```
GET /by-name/{name} - szukanie blogu po nazwie
```
```
POST /evictCache  - wyczyszczenie cache
```
```
DELETE /{id}  - usuwanie blogu po id
```
*/api/v1/items - pobiera najswieższe wpisy na blogach, domyslnie jest to 10 elementów ale jest możliwośc podania parametru limit
```
/api/v1/items?limit=50 - pobiera 50 wpisów
```
*/api/v1/refresh?blogId= - odswieża blog tzn sprawdza na żadanie czy nie pojawiły się nowe wpisy

### Tworzenie nowego Bloga ###
```
curl -i -X POST -H "Content-Type:application/json" -d "{ \"feedURL\" : \"http://feeds.feedburner.com/Baeldung/\" }" http://localhost:8082/api/v1/blogs
```
gdzie:
- feedURL = adres rss

### Uruchomienie ###
```
mvn clean package
java -jar target/*.jar  - podmienić * na rzeczywistą nazwe Jara
```
Domyślny profil z jakim jest uruchamiane to prod:
- port to 8082
- baza SQLServer
```
rssAggragator/src/main/resources/application-prod.properties
```



### Wykorzystane biblioteki ###
* SpringBoot 1.5+
* HikariCP
* ROME
* Lombok
* AssertJ
* Junit5
* Equalsverifier
* ThinJar
