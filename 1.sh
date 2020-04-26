#!/bin/bash

mvn clean install -DskipTests

mvn --projects backend spring-boot:run


