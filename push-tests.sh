set -e

DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-"flangz"}
IMAGE_NAME=${IMAGE_NAME:-"nbank-tests"}
TAG=${TAG:-"v1.0"}
DOCKER_TOKEN=${DOCKER_TOKEN:-""}
if [ -z "$DOCKER_TOKEN" ]; then
  echo "Переменная DOCKER_TOKEN не задана."
  exit 1
fi

echo "Логинимся в Докер как $DOCKERHUB_USERNAME..."
echo "$DOCKER_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

FULL_TAG="$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
echo "Тег $FULL_TAG..."
docker tag "$IMAGE_NAME" "$FULL_TAG"

echo "Пушим $FULL_TAG в ДокерХаб"
docker push "$FULL_TAG"
