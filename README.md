You can visit the site here - [venuehub.com](https://venuehub.vercel.app/)
## For running VenueHub in developemnt follow this [documentation](https://alikhan410.notion.site/Start-VenueHub-in-Development-a8bd5714c4934310963f579c10fd9c35)
## Introduction
- **Purpose**: This document aims to provide a comprehensive technical design for the Venuehub platform, detailing the implementation of each microservice, data design, security measures, performance metrics, and testing strategies.
- **Scope**: The scope of this document includes an overview of the Venuehub platform, which is designed as an event-based microservices architecture. The document covers the following aspects:
    - Microservices architecture and individual service responsibilities.
    - Data design and database choices.
    - Security measures and best practices.
    - Performance metrics and scalability strategies.
    - Testing strategies and tools used.
- **Definitions and Acronyms**: This section defines technical terms, acronyms, and abbreviations used throughout the document to ensure clarity and understanding. Key definitions include:
    - **RPS**: Requests Per Second
    - **TPS**: Transactions Per Second
    - **RabbitMQ**: A message broker that implements the Advanced Message Queuing Protocol (AMQP)
    - **Redis**: Remote Dictionary Server, an in-memory data structure store used for caching and rate limiting
    - **JMeter**: An open-source tool for performance testing and measuring response times and throughput
## System Architecture
## Overview
The Venuehub platform is designed as an event-based microservices architecture. Each service is responsible for a specific aspect of the system, ensuring modularity, scalability, and ease of maintenance. The key microservices include:

- **Auth Service**: Handles user authentication and authorization.
- **Venue Service**: Manages venue information and availability.
- **Image Service**: Responsible for storing and retrieving images
- **Booking Service**: Facilitates booking and reservation processes.
- **Job Service**: Manages background jobs and asynchronous tasks.
- **Payment Service**: Handles payment processing and integration.

These services communicate with each other asynchronously using RabbitMQ, ensuring decoupling and fault tolerance.

### Database and Caching Strategy
- **MySQL Database**:
    - MySQL is used for its decent latency and throughput, providing reliable and efficient transactional data handling for our services.
- **Redis for Caching and Rate Limiting**:
    - **Caching**: Redis is utilized for caching frequently accessed data, reducing database load and improving response times.
    - **Rate Limiting**: Provides efficient rate limiting capabilities, protecting backend services from excessive requests and ensuring system stability under varying loads.
### External APIs and Integrations:
List of external APIs:

- Stripe Payment Gateway API
### Infrastructure and Deployment
- **Kubernetes**:
    - **Usage**: Kubernetes is used in production for deploying and managing microservices.
    - **Service Discovery**: Utilizes Kubernetes' built-in service discovery and load balancing features to efficiently manage service-to-service communication.
    - **Orchestration**: Handles the orchestration of microservices, ensuring high availability, scalability, and fault tolerance.
- **Eureka Discovery**:
    - **Usage**: Eureka Discovery is used during the development phase to handle service registration and discovery.
    - **Development**: Facilitates easy service discovery and interaction in a development environment where services are frequently updated and tested.
- **Spring Gateway**:
    - **Usage**: Spring Gateway acts as a reverse proxy and API gateway.
    - **Routing**: Manages routing of requests to the appropriate microservices.
    - **API Management and Documentation**: Provides API management capabilities such as rate limiting, security, and monitoring. Also, exposes an Open API Documentation endpoint that includes documentation for all the services.
### Architecture Diagrams:
[﻿VenueHub architecture overview](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=bl0-QUs1w-pwMMYbgRu7KQ) 

[﻿RabbitMQ Setup](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=DuSClt1QZg3J9efYXZfitg) 

- **Microservices**:
    - [﻿Auth Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=rwh2XJsNpyd_pQIREeK8-g) 
    - [﻿Venue Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=4GgGaWISGcOnV8RNiMOqxQ) 
    - [﻿Image Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=NuNf5eXBoNTZP0nP-HrgNw) 
    - [﻿Booking Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=m8bF-g1r3kwlUIF12i6M_A) 
    - [﻿Job Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=H5TvUBjyeCMW9RacL7Z1YQ) 
    - [﻿Payment Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=IcOPQMjIKlhOj3flTHHm2Q) 
- **External APIs Sequence Diagram:**
    - [﻿Stripe Interaction](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=vlR--OD486IlhYiJUAuLXQ) 
## Detailed Design
### Microservice 1: Auth Service
- **Overview**: The Auth Service handles user authentication, profile management, and authorization. It provides endpoints for user/vendor registration, login, and role management.
- **Class Diagrams**: [`﻿Diagram depicting the classes involved in the Auth Service.`](https://lucid.app/documents/embedded/08686b00-ca8c-4384-a1ec-20c3ef8e7a41?invitationId=inv_3ee4dc99-a9ec-4ac8-b889-ecd14cab1146#) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Auth Service API Documentation
- **Data Model**: [﻿Data Model - Auth Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=7oVWRHiFpNCUNYFJIY0GDg) 
### Microservice 2: Venue Service
- **Overview**: The Venue Service handles venue management. It provides endpoints for adding, updating, deleting venues.
- **Class Diagrams**:  [`﻿Diagram depicting the classes involved in the Venue Service.`](https://lucid.app/documents/embedded/dd654d40-ae47-4d5b-b013-01cb3f5a3bc4) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Venue Service API Documentation
- **Data Model**: [﻿Data Model - Venue Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=icNTZ5qjSXVpDZyX_tEw8g) 
### Microservice 2: Image Service
- **Overview**: The Image Service stores the images associated with venues in disk. It acts as a local CDN.
- **Class Diagrams**:  [﻿`Diagram depicting the classes involved in the Image Service.`](https://lucid.app/documents/embedded/815c537a-f755-4889-9419-8ac411451c9e) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Venue Service API Documentation
- **Data Model**: [﻿Data Model - Image Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=vkxIcKeCHytZ38qXIBSxDg) 
### Microservice 3: Booking Service
- **Overview**: Manages the booking process, including availability checks and confirmations.
- **Class Diagrams**: [﻿` Diagram depicting the classes involved in the Booking Service.`](https://lucid.app/documents/embedded/93b864ed-0a4a-452f-845c-6093a12bd980) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Booking Service API Documentation
- **Data Model**:  [﻿Data Model - Booking Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=Rm2LswYDsCYX5Y1d74zn3g) 
### Microservice 4: Job Service
- **Overview**: The Job Services handles the Booking Jobs sent by the Booking Service and schedule them using Quartz.
- **Class Diagrams**: [﻿`Diagram depicting the classes involved in the Job Service.`](https://lucid.app/documents/embedded/c27a9bc3-2d8f-4a7f-ab48-a07050449ef6) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Job Service API Documentation
- **Data Flow**: [﻿Quartz Data Flow](https://s3.stackabuse.com/media/articles/guide-to-quartz-with-spring-boot-job-scheduling-and-automation-1.png) 
- **Data Mode:** [﻿Data Model - Job Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=DMVIN4qgfJz1Qd_ujjCWrA) 
### Microservice 4: Payment Service
- **Overview**: Payment Service handles the payment related task and performs validation using external Stripe API.
- **Class Diagrams**: [﻿`Diagram depicting the classes involved in the Payment Service.`](https://lucid.app/documents/embedded/992658c2-429f-4ade-9137-7a4166c0aaa2) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Payment Service API Documentation
- **Data Model**: [﻿Data Model - Payment Service](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=w06NFg8UjSqXa_EdDejzeg) 
## Security Considerations
- **Authentication Mechanisms**: User authentication involves submitting login credentials to the Auth Service, which validates them against the Auth Database. Upon successful authentication, the Auth Service issues a JWT token to the user; otherwise, it returns an appropriate error message.
- **Authorization Mechanisms**: Access to various services is controlled using JWT tokens. When a user makes a request, the Service validates the token using public keys provided by the Auth Service. If the token is verified, the Service performs Method Level Authorization to determine the user's permissions for the requested action.
- **Data Protection**: 
    - **Access Controls: Role-Based Access Control (RBAC)**: Our system employs Role-Based Access Control (RBAC) to enforce access restrictions to sensitive data. Upon user authentication, roles are assigned and encoded into a JWT (JSON Web Token). Subsequently, when a user makes a request to a service, the JWT containing role information is validated. This validation ensures that only authorized users, based on their roles, can access specific resources and perform designated actions within the system.
    - **Authentication and JWT Validation**: Authentication and JWT validation in our system are strengthened by RSA asymmetric encryption. The Auth Service securely manages the private key required for JWT decoding, while exposing public keys through endpoints for token validation by other services. This approach ensures the integrity and confidentiality of authentication processes, mitigating the risk of unauthorized access to sensitive information.
- **Auth Sequence Diagram**: [﻿JWT Authentication Flow](https://app.eraser.io/workspace/ptHDgAXUA0gm15nVxIY8?elements=ZTKxuiWpN6D-xg_4lIbQIA) 
## Performance Metrics
For detailed performance metrics, please refer to the [`﻿Performance Metrics Document.`](https://alikhan410.notion.site/Performance-Metrics-for-VenueHub-de5e571dd162409bba52df8ed053fb62?pvs=4) 

## Testing Strategy
- **Unit Testing**:
    - Tools and frameworks: Description of tools and frameworks used for unit testing. //TODO
    - Test cases: Examples of unit test cases. //TODO
- **Integration Testing**:
    - Tools and frameworks: Description of tools and frameworks used for integration testing. //TODO
    - Test cases: Examples of integration test cases. //TODO
- **Performance Testing**:
    - Tools and frameworks: Description of tools and frameworks used for performance testing. //TODO
    - Test cases: Examples of performance test cases.//TODO
## Deployment Plan
- **Deployment Steps**: Step-by-step guide for deploying the system.//TODO
- **Environments**: Description of different environments (development, staging, production).//TODO
- **Rollback Procedures**: Steps to rollback to a previous version in case of deployment issues.//TODO
## Maintenance and Support
- **Maintenance Guidelines**: Best practices for maintaining the system.//TODO
- **Support Resources**: Resources available for system support.//TODO
- **Update Procedures**: Procedures for applying updates to the system.//TODO
## TODO
- **Integration Test**: Add integration test in all services.
- **Frontend**: Integrate frontend with the APIs
- **Open Api Documentation**: Write open API documentation for all services
## Appendices
- **Glossary**: Definitions of terms used in the document.//TODO
- **References**: List of references used in the document.//TODO
- **Additional Diagrams and Documentation**: Any additional diagrams or documents that support the design.//TODO
