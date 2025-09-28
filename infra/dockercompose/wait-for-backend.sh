#!/bin/bash
for i in {1..30}; do
  echo "Waiting for backend..."
  if curl -s http://localhost:4111/actuator/health > /dev/null; then
    echo "Backend is up!"
    exit 0
  fi
  sleep 2
done
echo "Backend did not start in time"
exit 1
