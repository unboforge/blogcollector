#!/bin/bash

echo "🛑 서버 중지 중..."

# EngineMain 프로세스 찾기
PID=$(ps aux | grep "io.ktor.server.netty.EngineMain" | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "❌ 실행 중인 서버를 찾을 수 없습니다."
    exit 1
fi

echo "🔍 발견된 프로세스: PID $PID"

# 프로세스 종료
kill $PID

# 종료 확인 (최대 10초 대기)
for i in {1..10}; do
    if ! ps -p $PID > /dev/null 2>&1; then
        echo "✅ 서버가 정상적으로 종료되었습니다."
        exit 0
    fi
    echo "⏳ 종료 대기 중... ($i/10)"
    sleep 1
done

# 강제 종료
echo "⚠️  정상 종료 실패. 강제 종료 중..."
kill -9 $PID

if ! ps -p $PID > /dev/null 2>&1; then
    echo "✅ 서버가 강제 종료되었습니다."
else
    echo "❌ 서버 종료 실패"
    exit 1
fi
