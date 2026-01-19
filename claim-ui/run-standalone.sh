#!/bin/bash
# Script to run claim-ui in standalone mode

echo "Building claim-ui for standalone mode..."
docker build --build-arg NGINX_CONFIG=nginx-standalone.conf -t claim-ui:standalone .

echo "Starting claim-ui container..."
docker run -d \
  -p 3000:3000 \
  --name claim-ui \
  --add-host=host.docker.internal:host-gateway \
  claim-ui:standalone

echo "Claim UI is running at http://localhost:3000"
echo "Make sure API Gateway is running on http://localhost:8080"
echo ""
echo "To view logs: docker logs -f claim-ui"
echo "To stop: docker stop claim-ui && docker rm claim-ui"



