[![Build Status](https://travis-ci.org/olszewskimichal/rssAggragator.svg?branch=master)](https://travis-ci.org/olszewskimichal/rssAggragator)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=pl.michal.olszewski:rss-aggregator)](https://sonarcloud.io/dashboard?id=pl.michal.olszewski%3Arss-aggregator)
[![codecov](https://codecov.io/gh/olszewskimichal/rssAggragator/branch/master/graph/badge.svg)](https://codecov.io/gh/olszewskimichal/rssAggragator)
# rssAggragator
Agregator RSS do śledzenia informacji na blogach

Aplikacja napisana z wykorzystaniem Spring Boota 1.5 + oraz bibiolteki Rome.

Aplikacja opiera się na API Restowym, aktualnie nie posiada żadnego czytelniejszego GUI.

* Tworzenie nowego Bloga
```
curl -i -X POST -H "Content-Type:application/json" -d "{ \"feedURL\" : \"http://feeds.feedburner.com/Baeldung/\" }" http://localhost:8082/api/v1/blogs
```
gdzie:
- feedURL = adres rss
Najważniejszym parametrem jest feedURL to na podstawie tego zostaną pobrane pozostałe informacje oraz zaktualizowane
