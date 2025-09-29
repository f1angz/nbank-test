#!/bin/bash
for i in {1..60}; do
  if curl -s http://localhost:4444/status | grep -q '"total":'; then
    echo "Selenoid is ready!"
    exit 0
  fi
  echo "Waiting for Selenoid..."
  sleep 2
done
echo "Selenoid did not start in time"
exit 1
