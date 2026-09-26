# Lovable: AI-Powered Website Builder Platform

Lovable is an advanced, AI-driven backend engine that translates natural language prompts into fully functional, production-ready React web applications. Built with Java 21 and Spring Boot, this platform leverages Spring AI and Project Reactor to stream LLM-generated code in real-time, intelligently parsing and saving it into a scalable MinIO object storage system.

## 🚀 Key Features

* **Real-Time AI Generation (SSE):** Utilizes Server-Sent Events to stream LLM responses back to the client natively using Project Reactor (`Flux`).
* **Intelligent Context Injection:** Custom Spring AI `StreamAdvisor` components (e.g., `FileTreeContextAdvisor`) dynamically inject the current project's file-tree context into the LLM prompt to ensure contextual accuracy and prevent redundant code generation.
* **Robust File Parsing & Storage:** Intercepts LLM outputs via Regex/AST parsing, extracts `<file>` and `<message>` XML tags, and persists the raw code into a highly scalable MinIO (S3-compatible) storage architecture.
* **Template Initialization:** Automatically scaffolds new user projects by copying base React/Vite/Tailwind templates across MinIO buckets.
* **Subscription & Billing:** End-to-end Stripe integration featuring checkout sessions, customer billing portals, and secure webhook deserialization for managing user tiers and project limits.
* **Stateless Security:** Implements Spring Security with custom JWT filters and Role-Based Access Control (RBAC) via method-level `@PreAuthorize` annotations.

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot (WebMVC, Data JPA, Security)
* **AI Integration:** Spring AI, OpenRouter API (GPT/Claude)
* **Database:** PostgreSQL with `pgvector` extension
* **Object Storage:** MinIO 
* **Payments:** Stripe SDK
* **Mapping & Tooling:** MapStruct, Lombok, Maven

---


```bash
docker-compose -f services.docker-compose.yml up -d
