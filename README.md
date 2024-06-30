<p><a target="_blank" href="https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg" id="edit-in-eraser-github-link"><img alt="Edit in Eraser" src="https://firebasestorage.googleapis.com/v0/b/second-petal-295822.appspot.com/o/images%2Fgithub%2FOpen%20in%20Eraser.svg?alt=media&amp;token=968381c8-a7e7-472a-8ed6-4a6626da5501"></a></p>

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
[﻿VenueHub architecture overview](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=duumHAPqIydoajLTmkv2aw) 

[﻿RabbitMQ Setup](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=Wgm5KHWCIlpX-YypuzDOnQ) 

- **Microservices**:
    - [﻿Auth Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=N0lkhqt1xkFxtv2ZFDpgpg) 
    - [﻿Venue Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=9S75dMzmbsRk0wCvRgrs5Q) 
    - [﻿Booking Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=R_pztQW3Y4pZpPArwvMEnA) 
    - [﻿Job Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=XHJ0cMyQH20SGLJQDSZSrw) 
    - [﻿Payment Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=cMpdGE7B7D2a5FnebOKQrA) 
- **External APIs Sequence Diagram:**
    - [﻿Stripe Interaction](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=bHWJqfume7QgHaS5D3zWZw)  
## Detailed Design
### Microservice 1: Auth Service
- **Overview**: The Auth Service handles user authentication, profile management, and authorization. It provides endpoints for user/vendor registration, login, and role management.
- **Class Diagrams**: [`﻿Diagram depicting the classes involved in the Auth Service.`](https://lucid.app/documents/embedded/08686b00-ca8c-4384-a1ec-20c3ef8e7a41?invitationId=inv_3ee4dc99-a9ec-4ac8-b889-ecd14cab1146#) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Auth Service API Documentation
- **Data Model**: [﻿Data Model - Auth Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=2W1QnB3sYLZB4hY-QIpoVA) 
### Microservice 2: Venue Service
- **Overview**: The Venue Service handles venue management. It provides endpoints for adding, updating, deleting venues.
- **Class Diagrams**:  [`﻿Diagram depicting the classes involved in the Venue Service.`](https://lucid.app/documents/embedded/dd654d40-ae47-4d5b-b013-01cb3f5a3bc4) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Venue Service API Documentation
- **Data Model**: [﻿Data Model - Venue Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=cRIv8ZRrLmCk5R4TrnPLEw) 
### Microservice 3: Booking Service
- **Overview**: Manages the booking process, including availability checks and confirmations.
- **Class Diagrams**: [﻿` Diagram depicting the classes involved in the Booking Service.`](https://lucid.app/documents/embedded/93b864ed-0a4a-452f-845c-6093a12bd980) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Booking Service API Documentation
- **Data Model**:  [﻿Data Model - Booking Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=A8kybkjvpftN8JMKb2e3vA) 
### Microservice 4: Job Service
- **Overview**: The Job Services handles the Booking Jobs sent by the Booking Service and schedule them using Quartz.
- **Class Diagrams**: [﻿`Diagram depicting the classes involved in the Job Service.`](https://lucid.app/documents/embedded/c27a9bc3-2d8f-4a7f-ab48-a07050449ef6) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Job Service API Documentation
- **Data Flow**: [﻿Quartz Data Flow](https://s3.stackabuse.com/media/articles/guide-to-quartz-with-spring-boot-job-scheduling-and-automation-1.png) 
- **Data Mode:** [﻿Data Model - Job Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=yD0RO4_ouNvQX8De0nfsGQ) 
### Microservice 4: Payment Service
- **Overview**: Payment Service handles the payment related task and performs validation using external Stripe API.
- **Class Diagrams**: [﻿`Diagram depicting the classes involved in the Payment Service.`](https://lucid.app/documents/embedded/992658c2-429f-4ade-9137-7a4166c0aaa2) 
- **Method Descriptions**: Detailed method descriptions and API documentation can be found at the Payment Service API Documentation
- **Data Model**: [﻿Data Model - Payment Service](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=MmOQ9y4SlHRKJ9MC0GZxpw) 
## Security Considerations
- **Authentication Mechanisms**: User authentication involves submitting login credentials to the Auth Service, which validates them against the Auth Database. Upon successful authentication, the Auth Service issues a JWT token to the user; otherwise, it returns an appropriate error message.
- **Authorization Mechanisms**: Access to various services is controlled using JWT tokens. When a user makes a request, the Service validates the token using public keys provided by the Auth Service. If the token is verified, the Service performs Method Level Authorization to determine the user's permissions for the requested action.
- **Data Protection**: 
    - **Access Controls: Role-Based Access Control (RBAC)**: Our system employs Role-Based Access Control (RBAC) to enforce access restrictions to sensitive data. Upon user authentication, roles are assigned and encoded into a JWT (JSON Web Token). Subsequently, when a user makes a request to a service, the JWT containing role information is validated. This validation ensures that only authorized users, based on their roles, can access specific resources and perform designated actions within the system.
    - **Authentication and JWT Validation**: Authentication and JWT validation in our system are strengthened by RSA asymmetric encryption. The Auth Service securely manages the private key required for JWT decoding, while exposing public keys through endpoints for token validation by other services. This approach ensures the integrity and confidentiality of authentication processes, mitigating the risk of unauthorized access to sensitive information.
- **Auth Sequence Diagram**: [﻿JWT Authentication Flow](https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg?elements=ub9UYuC6tC2Lf_QEUjAMdQ) 
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
- **Open Api Documentation**: Write open API documentation for all services
## Appendices
- **Glossary**: Definitions of terms used in the document.//TODO
- **References**: List of references used in the document.//TODO
- **Additional Diagrams and Documentation**: Any additional diagrams or documents that support the design.//TODO



<!-- eraser-additional-content -->
## Diagrams
<!-- eraser-additional-files -->
<a href="/README-sequence-diagram-1.eraserdiagram" data-element-id="QPswH5uUSnLj1Khk3YPLG"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----3459bbc87ad04d152fce638d3c1b9990.png" alt="" data-element-id="QPswH5uUSnLj1Khk3YPLG" /></a>
<a href="/README-sequence-diagram-2.eraserdiagram" data-element-id="4mX0x56R6qO4KnQ9WfAOL"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----912a5079d42adecbcc510bb9a6918891.png" alt="" data-element-id="4mX0x56R6qO4KnQ9WfAOL" /></a>
<a href="/README-entity-relationship-3.eraserdiagram" data-element-id="vab2d5Sngm6XjCGvUpFPY"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----d7ff2168846a1cff1e176cb8809eb91c.png" alt="" data-element-id="vab2d5Sngm6XjCGvUpFPY" /></a>
<a href="/README-entity-relationship-4.eraserdiagram" data-element-id="AgfmHyKVo0_qmveQO2ObS"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----f6fcdda1061c2be5c280322e30fe43a1.png" alt="" data-element-id="AgfmHyKVo0_qmveQO2ObS" /></a>
<a href="/README-entity-relationship-5.eraserdiagram" data-element-id="qlGgOQN9rTI69frgMSNPq"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----cf70cb5d2a267426c0106fd0f2d32cb1.png" alt="" data-element-id="qlGgOQN9rTI69frgMSNPq" /></a>
<a href="/README-entity-relationship-6.eraserdiagram" data-element-id="VVJjj6Vf5zXvXkjEwliSZ"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----70215f0e4d056a32df297dc3c20aaf2f.png" alt="" data-element-id="VVJjj6Vf5zXvXkjEwliSZ" /></a>
<a href="/README-entity-relationship-7.eraserdiagram" data-element-id="aXcYekJEgN5zXK8DB_m9S"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----14bc22659369fadf20944e2d16bf53f5.png" alt="" data-element-id="aXcYekJEgN5zXK8DB_m9S" /></a>
<a href="/README-flowchart-8.eraserdiagram" data-element-id="oSjrun_ikaxhGNEC2PAND"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----d9c008cfed02e22cfcb016ce2132ab8b.png" alt="" data-element-id="oSjrun_ikaxhGNEC2PAND" /></a>
<a href="/README-flowchart-9.eraserdiagram" data-element-id="EP3jjvZFLVYRP7s3oHX8z"><img src="/.eraser/3CTdIKRpLWBRUgDMOeFg___wJ55CLjSu3NBmCKdvb9ZUlWqu763___---diagram----3643d2cdfa15da5df1b06da505cfcfef.png" alt="" data-element-id="EP3jjvZFLVYRP7s3oHX8z" /></a>
<!-- end-eraser-additional-files -->
<!-- end-eraser-additional-content -->
<!--- Eraser file: https://app.eraser.io/workspace/3CTdIKRpLWBRUgDMOeFg --->