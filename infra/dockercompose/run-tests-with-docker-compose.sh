#!/bin/bash
set -e

IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

echo ">>> Останавливаю предыдущее окружение"
docker compose down -v || true

echo ">>> docker pull браузеров для Selenoid"
json_file="./config/browsers.json"
if ! command -v jq &> /dev/null; then
  echo "jq is not installed. Please install jq and try again"
  exit 1
fi
images=$(jq -r '.. | objects | select(.image) | .image' "$json_file" | tr -d '\r')

while IFS= read -r image; do
  [ -z "$image" ] && continue
  echo "Pulling $image..."
  docker pull "$image"
done <<< "$images"

echo ">>> Запускаю окружение"
docker compose up -d

echo ">>> Жду пока backend поднимется..."
until curl -s http://localhost:4111/actuator/health | grep -q '"status":"UP"'; do
  sleep 2
done
echo "Backend готов"

echo ">>> Жду пока frontend поднимется..."
until curl -s http://localhost:80 > /dev/null; do
  sleep 2
done
echo "Frontend готов"

echo ">>> Собираю образ для тестов"
docker build -t $IMAGE_NAME -f ../../Dockerfile ../..

mkdir -p "$TEST_OUTPUT_DIR/logs" "$TEST_OUTPUT_DIR/reports" "$TEST_OUTPUT_DIR/results"

echo ">>> Запуск тестов (profile=$TEST_PROFILE)"
docker run --rm \
  -v "$(pwd)/$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$(pwd)/$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$(pwd)/$TEST_OUTPUT_DIR/reports":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL="http://backend:4111" \
  -e UIBASEURL="http://nginx" \
  --network nbank-network \
  $IMAGE_NAME

echo ">>> Тесты завершены"
echo "Лог: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты: $TEST_OUTPUT_DIR/results"
echo "Отчет: $TEST_OUTPUT_DIR/reports"

docker compose down -v
