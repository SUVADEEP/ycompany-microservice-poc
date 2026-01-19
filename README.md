# Ycompany Insurance Microservices POC

A comprehensive microservices architecture for insurance claim management using Spring Boot, Spring Cloud Gateway, Temporal, and React.

## Architecture Overview

This project consists of the following microservices:

1. **API Gateway** (Port 8080) - Spring Cloud Gateway for routing, security, and monitoring
2. **Claim Service** (Port 8081) - Handles claim creation, document upload, and tracking
3. **Workflow Manager Service** (Port 8082) - Manages claim approval workflows and supervisor actions
4. **Claim UI** (Port 3000) - React-based web interface for customers and supervisors
5. **Temporal** (Port 7233) - Workflow orchestration engine
6. **PostgreSQL** (Port 5432) - Database for Temporal and claim data

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring Cloud Gateway 2023.0.0
- **Workflow**: Temporal 1.22.0
- **Database**: H2 (development), PostgreSQL (production)
- **Frontend**: React 18, Material-UI, Vite
- **Build Tool**: Maven 3.9
- **Java Version**: 17

## Features

### Claim Service
- Create new insurance claims
- Upload claim documents
- Track claim status
- View claim history
- Add comments to claims

### Workflow Manager Service
- Review claim details
- Approve or reject claims
- Assign supervisors to claims
- Monitor claim processing workflows

### Claim UI
- **Customer View**: Raise claims, track status, view history
- **Supervisor View**: Review claims, approve/reject, assign supervisors, add comments

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.9+
- Node.js 18+ and npm
- Docker and Docker Compose (for containerized deployment)
- Temporal Server (can be run via Docker)

### Local Development Setup

#### 1. Start Temporal Server

```bash
docker-compose up -d temporal postgresql
```

Or download and run Temporal locally:
```bash
# Download Temporal CLI
temporal server start-dev
```

#### 2. Build and Run Services

**Option A: Run all services with Docker Compose**
```bash
docker-compose up --build
```

**Option B: Run services individually**

Build the project:
```bash
mvn clean install
```

Start API Gateway:
```bash
cd api-gateway
mvn spring-boot:run
```

Start Claim Service:
```bash
cd claim-service
mvn spring-boot:run
```

Start Workflow Manager Service:
```bash
cd workflow-manager-service
mvn spring-boot:run
```

#### 3. Start UI

```bash
cd claim-ui
npm install
npm run dev
```

### Access Points

- **API Gateway**: http://localhost:8080
- **Claim Service**: http://localhost:8081
- **Workflow Manager Service**: http://localhost:8082
- **Claim UI**: http://localhost:3000
- **Temporal UI**: http://localhost:8088
- **H2 Console** (Claim Service): http://localhost:8081/h2-console

## API Endpoints

### Claim Service APIs (via Gateway: `/api/claims`)

- `POST /api/claims` - Create a new claim
- `GET /api/claims/{id}` - Get claim by ID
- `GET /api/claims/customer/{customerId}` - Get claims by customer
- `GET /api/claims` - Get all claims
- `PUT /api/claims/{id}` - Update claim
- `POST /api/claims/{id}/comments` - Add comment to claim
- `GET /api/claims/{id}/comments` - Get comments for claim
- `PATCH /api/claims/{id}/status?status={status}` - Update claim status

### Workflow Manager APIs (via Gateway: `/api/workflow`)

- `GET /api/workflow/claims/{id}` - Get claim details for review
- `POST /api/workflow/approve` - Approve or reject a claim
- `POST /api/workflow/claims/{id}/assign?supervisorId={id}` - Assign supervisor

## Example API Calls

### Create a Claim
```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "policyNumber": "POL123456",
    "claimType": "Auto",
    "description": "Car accident on highway",
    "claimAmount": 5000.00
  }'
```

### Approve a Claim
```bash
curl -X POST http://localhost:8080/api/workflow/approve \
  -H "Content-Type: application/json" \
  -d '{
    "claimId": 1,
    "supervisorId": "SUPER001",
    "decision": "APPROVED",
    "comments": "Approved after review"
  }'
```

## Project Structure

```
ycompany-microservice-poc/
├── api-gateway/              # Spring Cloud Gateway
├── claim-service/            # Claim management service
├── workflow-manager-service/ # Workflow orchestration service
├── common/                   # Shared DTOs and utilities
├── claim-ui/                 # React frontend
├── docker-compose.yml        # Docker orchestration
└── pom.xml                   # Parent POM
```

## Configuration

### Application Properties

Each service has its own `application.yml`:
- API Gateway: Routes configuration, rate limiting
- Claim Service: Database configuration, Temporal settings
- Workflow Manager: Service URLs, Temporal settings

### Environment Variables

- `SPRING_DATASOURCE_URL` - Database connection URL
- `TEMPORAL_SERVER_ADDRESS` - Temporal server address (default: localhost:7233)
- `CLAIM_SERVICE_URL` - Claim service URL for workflow manager

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -DskipTests
```

### Docker Build

```bash
docker-compose build
```

## Monitoring

- **Actuator Endpoints**: Available at `/actuator` on each service
- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus` (API Gateway)

## Workflow Process

1. Customer creates a claim via Claim Service
2. Claim Service initiates a Temporal workflow
3. Workflow waits for supervisor approval
4. Supervisor reviews claim via Workflow Manager Service
5. Supervisor approves/rejects, which signals the Temporal workflow
6. Workflow completes and updates claim status

## Future Enhancements

- Authentication and Authorization (Spring Security)
- Service Discovery (Eureka/Consul)
- Distributed Tracing (Zipkin/Jaeger)
- Message Queue (RabbitMQ/Kafka)
- File Storage Service (S3/MinIO)
- Email Notifications
- Advanced Analytics Dashboard

## License

This is a POC project for demonstration purposes.

## Contributing

This is a proof-of-concept project. For production use, additional considerations should be made for:
- Security (authentication, authorization, encryption)
- Scalability (load balancing, caching)
- Observability (logging, monitoring, tracing)
- Resilience (circuit breakers, retries, timeouts)

## commands

docker-compose up -d --build


docker-compose -f docker-compose.local.yml up -d

Start in detached mode (background):
docker-compose -f docker-compose.local.yml up -d
Start and view logs:
docker-compose -f docker-compose.local.yml up
Stop services:
docker-compose -f docker-compose.local.yml down
Stop and remove volumes (clean slate):
docker-compose -f docker-compose.local.yml down -v
View service status:
docker-compose -f docker-compose.local.yml ps
View logs:
docker-compose -f docker-compose.local.yml logs -f
View logs for a specific service:
docker-compose -f docker-compose.local.yml logs -f temporal