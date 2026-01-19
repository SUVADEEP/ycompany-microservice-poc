# API Gateway Docker Setup

## ✅ Status: Running

The API Gateway Docker container is now built and running successfully!

## Container Information

- **Image:** `api-gateway:latest`
- **Container Name:** `api-gateway`
- **Port:** `8080`
- **Status:** Running

## Quick Commands

### View Logs
```bash
docker logs api-gateway
docker logs -f api-gateway  # Follow logs
```

### Stop Container
```bash
docker stop api-gateway
```

### Start Container
```bash
docker start api-gateway
```

### Restart Container
```bash
docker restart api-gateway
```

### Remove Container
```bash
docker stop api-gateway
docker rm api-gateway
```

### Rebuild and Run
```bash
# Clean up
docker stop api-gateway 2>/dev/null
docker rm api-gateway 2>/dev/null
rm -rf api-gateway/target

# Rebuild
docker build -f api-gateway/Dockerfile -t api-gateway:latest .

# Run
docker run -d -p 8080:8080 --name api-gateway api-gateway:latest
```

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Endpoints

- **Health:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics
- **Info:** http://localhost:8080/actuator/info

## Routes Configured

1. **Claim Service:** `/api/claims/**` → `http://localhost:8081`
2. **Workflow Manager:** `/api/workflow/**` → `http://localhost:8082`
3. **Claim UI:** `/ui/**` → `http://localhost:3000`

## Dockerfile Details

- **Build Stage:** Uses `maven:3.9-eclipse-temurin-17` to build the project
- **Runtime Stage:** Uses `eclipse-temurin:17-jre` for running the application
- **Multi-stage build** for optimized image size

## Troubleshooting

### Container won't start
```bash
docker logs api-gateway
```

### Port already in use
```bash
# Find what's using port 8080
lsof -i :8080

# Or use a different port
docker run -d -p 8081:8080 --name api-gateway api-gateway:latest
```

### Rebuild from scratch
```bash
docker build --no-cache -f api-gateway/Dockerfile -t api-gateway:latest .
```


