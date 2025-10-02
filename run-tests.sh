#!/bin/bash
IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

echo "Test build started"
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/reports"
mkdir -p "$TEST_OUTPUT_DIR/results"

echo "Test started"
docker run --rm \
  -v "C:/Users/semag/IdeaProjects/nbank-api-test/$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "C:/Users/semag/IdeaProjects/nbank-api-test/$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "C:/Users/semag/IdeaProjects/nbank-api-test/$TEST_OUTPUT_DIR/reports":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL="http://host.docker.internal:4111" \
  -e UIBASEURL="http://172.24.80.1:3000" \
$IMAGE_NAME

echo "Test finished"
echo "Log: $TEST_OUTPUT_DIR/logs/run.log"
echo "Test results: $TEST_OUTPUT_DIR/reports"
echo "Final report: $TEST_OUTPUT_DIR/report"