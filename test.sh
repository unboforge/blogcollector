#!/bin/bash
# 환경 변수 로드 및 테스트 실행
export $(cat .env | xargs)
./gradlew test
