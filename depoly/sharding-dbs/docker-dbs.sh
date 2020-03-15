#!/usr/bin/env bash

docker run -p 3307:3306 -e MYSQL_ROOT_PASSWORD=123456 --name mysql5726 -d mysql:5.7.26
docker run -p 3308:3306 -e MYSQL_ROOT_PASSWORD=123456 --name mysql5726_1 -d mysql:5.7.26