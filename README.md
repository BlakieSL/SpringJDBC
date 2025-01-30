# SpringJDBC_DAO Repository 🌱

A Spring-based project demonstrating robust database interactions **without** a full-fledged ORM, focusing on **plain JDBC** techniques and advanced usage of `JdbcTemplate`.  
*Bridging the power of raw SQL with Spring’s convenience for enterprise-grade persistence!*

---

## 📖 Overview

This repository showcases how to integrate **Spring** and plain **JDBC** in a modular fashion, emphasizing manual query construction, row mapping, and transactional boundaries. By leaning on **JdbcTemplate** instead of an ORM, it provides a clear view into the underlying SQL layer—ideal for projects requiring fine-grained control or a deeper understanding of raw data operations.

---

## ✨ Key Features

### 🧩 **Manual SQL & Row Mapping**
- **JdbcTemplate** with prepared statements, `KeyHolder` for auto-generated IDs  
- Custom `ResultSetExtractor` and `RowMapper` implementations  
- Handwritten SQL scripts defining associations (e.g., Author ↔ Book ↔ Library)  

### 🔄 **Flexible Data Retrieval & Aggregation**
- **Set**-based entity relationships manually stitched together via queries  
- Aggregate multiple rows into a single entity with partial merges (e.g., combining repeated library rows into one)  
- Fine-grained control over join operations for advanced performance tuning  

### 🧪 **Integration Testing with Testcontainers**
- **@SpringBootTest** + **@Testcontainers** for real DB integration tests  
- **SQL scripts** (`drop-schema.sql`, `create-schema.sql`) ensuring consistent test environments  
- Reproducible test data for verifying complex relationship queries  

### 🔧 **Transaction & Lifecycle Management**
- Use of **@Transactional** at the service or repository level for consistent commit/rollback boundaries  
- Clear separation of read-only queries vs. insert/update/delete operations  
- Managed error handling and logging with `Logger` to capture potential SQL exceptions  

---

## 🛠️ Built With
- **Spring Boot** & **JdbcTemplate** – Simplified data access without a full ORM  
- **SQL** – Hand-tuned queries for performance and clarity  
- **Testcontainers** – Automated, container-based testing environment  
- **Gradle** – Build, test, and dependency management
