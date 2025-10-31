#!/bin/bash
# 환경 변수 로드 및 애플리케이션 실행
export $(cat .env | xargs)
./gradlew run
