<div align="center">
# 🔬 Sentilytics

**A Real-Time Sentiment Analysis Pipeline**

Built with Spring Boot & Hugging Face Transformers to ingest, analyze, and visualize sentiment from social media posts.

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.x-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)](https://fastapi.tiangolo.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Hugging Face](https://img.shields.io/badge/Hugging_Face-Transformers-FFD21E?style=for-the-badge&logo=huggingface&logoColor=black)](https://huggingface.co/docs/transformers/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)
 
---

[Features](#-features) •
[Architecture](#-architecture) •
[Getting Started](#-getting-started) •
[API Reference](#-api-reference) •
[Configuration](#-configuration) •
[Project Structure](#-project-structure)

</div>
---

## 📖 Overview

**Sentilytics** is a full-stack sentiment analysis pipeline that continuously ingests social media posts (Reddit & Twitter), runs them through a Hugging Face transformer model for sentiment classification, and persists the results in a PostgreSQL database. It exposes a rich REST API for on-demand text analysis, time-series sentiment breakdowns (hourly/daily), and aggregate analytics, all designed to power real-time dashboards.

The system is split into two cooperating services:

| Service | Role | Tech |
|---|---|---|
| **Spring Boot Backend** | Ingestion pipeline, scheduling, analytics, REST API, persistence | Java 25, Spring Boot 4.1, JPA, PostgreSQL |
| **Python ML Microservice** | Sentiment classification via Hugging Face `transformers` | Python, FastAPI, `distilbert-base-uncased-finetuned-sst-2-english` (default) |
 
---

## ✨ Features

- **🤖 ML-Powered Sentiment Analysis** — Leverages Hugging Face's `pipeline("sentiment-analysis")` for production-grade NLP classification (POSITIVE / NEGATIVE / NEUTRAL).
- **📡 Multi-Platform Ingestion** — Pluggable `SocialMediaClient` interface with built-in Reddit and Twitter simulation clients. Easily swap in live API clients.
- **⏱️ Scheduled Automated Fetching** — A configurable scheduler (`@Scheduled`) periodically fetches and analyzes posts for tracked keywords.
- **📊 Time-Series Analytics** — Hourly and daily sentiment breakdowns, aggregated with positive/negative/neutral counts and average confidence scores.
- **📈 Overall Statistics** — Per-keyword (or global) stats: total posts, sentiment distribution counts, percentages, and average scores.
- **✍️ Ad-Hoc Text Analysis** — POST any custom text to `/api/v1/analyze` and get instant sentiment classification + confidence score.
- **🔌 Manual Ingest Trigger** — Hit `/api/v1/ingest/trigger` to force an immediate ingestion cycle outside the scheduled interval.
- **🛡️ Input Validation** — Jakarta Bean Validation on all incoming DTOs with a global exception handler for clean error responses.
- **🗄️ Persistent Storage** — All analyzed social posts and ad-hoc analyses are persisted to PostgreSQL via Spring Data JPA.
---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 25, Python 3 |
| **Backend Framework** | Spring Boot 4.1.0 |
| **ML Framework** | Hugging Face Transformers |
| **ML API** | FastAPI + Uvicorn |
| **ORM** | Spring Data JPA (Hibernate) |
| **Database** | PostgreSQL |
| **HTTP Client** | Spring `RestClient` |
| **Validation** | Jakarta Bean Validation |
| **Object Mapping** | ModelMapper 3.0 |
| **Boilerplate Reduction** | Lombok |
| **Build Tool** | Maven |


---

## 🏗 Architecture

```text
┌─────────────────────────────────────────────────────────┐
│                    Spring Boot Backend                  │
│                                                         │
│  ┌──────────┐    ┌───────────────────┐    ┌──────────┐  │
│  │ Scheduler│───▶│ SentimentPipeline │────▶│ Analyzer│──┼──▶ Python ML API
│  └──────────┘    │     Service       │    │(RestClient)││    (FastAPI +
│                  └─────────┬─────────┘    └──────────┘  │     HuggingFace)
│                            │                            │
│  ┌──────────────┐          │                            │
│  │SocialMedia   │◀─────────┘                            │
│  │Clients       │                                       │
│  │ • Reddit Sim │     ┌──────────────┐                  │
│  │ • Twitter Sim│     │  Analytics   │                  │
│  └──────────────┘     │   Service    │                  │
│                       └──────┬───────┘                  │
│  ┌──────────────┐            │        ┌──────────────┐  │
│  │  REST API    │◀───────────┘        │  PostgreSQL  │  │
│  │ (Controller) │────────────────────▶│   Database   │  │
│  └──────────────┘                     └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Data Flow

1. **Ingestion** — The `Scheduler` fires at a configurable interval, iterating over tracked keywords. For each keyword, it calls `SentimentPipelineService.fetchAndAnalyze()`.
2. **Fetching** — The pipeline service delegates to all registered `SocialMediaClient` implementations (Reddit and Twitter simulators by default) to fetch raw posts.
3. **Deduplication** — Each fetched post is checked against the database by `postId` to avoid reprocessing.
4. **Analysis** — New posts are sent to the `PythonSentimentAnalyzer`, which makes an HTTP call to the Python FastAPI microservice running a Hugging Face transformer model.
5. **Persistence** — The classified post (with sentiment label and confidence score) is saved to PostgreSQL as a `SocialPost` entity.
6. **Serving** — The `SentimentController` exposes REST endpoints for querying analytics, recent posts, and ad-hoc analysis.
---

### 1. Clone the Repository

```bash
git clone https://github.com/roninsora/Sentilytics-A-Sentiment-Analysis-Pipeline.git
cd Sentilytics-A-Sentiment-Analysis-Pipeline
```

### 2. Set Up PostgreSQL

Create a database named `Sentilytics`:

```sql
CREATE DATABASE "Sentilytics";
```

> [!NOTE]
> The default configuration expects PostgreSQL running on `localhost:5432` with username `postgres`. Update `application.properties` if your setup differs (see [Configuration](#-configuration)).

### 3. Start the Python ML Microservice

```bash
cd python-ml-api
 
# Create and activate a virtual environment (recommended)
python -m venv .venv
source .venv/bin/activate   # On Windows: .venv\Scripts\activate
 
# Install dependencies
pip install -r requirements.txt
 
# Start the FastAPI server
python app.py
```

The ML API will start on **`http://localhost:8000`**. On first launch, it will download the default Hugging Face model (~250 MB).

> [!TIP]
> You can verify the ML service is working by sending a test request:
> ```bash
> curl -X POST http://localhost:8000/analyze \
>   -H "Content-Type: application/json" \
>   -d '{"text": "I love this product!"}'
> ```
> Expected response: `{"sentiment": "POSITIVE", "score": 0.999}`

### 4. Start the Spring Boot Backend

Open a **new terminal** in the project root:
```bash
# Using the Maven wrapper (no Maven installation needed)
./mvnw spring-boot:run
 
# Or with Maven installed
mvn spring-boot:run
```

The backend will start on **`http://localhost:8080`** and the scheduler will immediately begin ingesting posts.

> [!IMPORTANT]
> Make sure the Python ML API is running **before** starting the Spring Boot backend. The `PythonSentimentAnalyzer` calls `http://localhost:8000/analyze` and will gracefully degrade (returning `NEUTRAL` with score `0.0`) if the ML service is unavailable.

## 📡 API Reference

All endpoints are prefixed with `/api/v1/`.

### Analyze Custom Text

```http
POST /api/v1/analyze
```

Analyze any text input and get a sentiment classification. The analysis is also persisted to the database.

| Parameter | Type | Location | Required | Description |
|---|---|---|---|---|
| `text` | `string` | Body (JSON) | ✅ | The text to analyze (max 5,000 characters) |

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "This is absolutely fantastic! Best experience ever."}'
```

**Response:**
```json
{
  "label": "POSITIVE",
  "score": 0.998
}
```
```http
GET /api/v1/sentiment/hourly
```

Returns sentiment counts aggregated by hour.

| Parameter | Type | Location | Required | Default | Description |
|---|---|---|---|---|---|
| `keyword` | `string` | Query | No | `all` | Filter by keyword |
| `days` | `int` | Query | No | `1` | Number of days to look back (min: 1) |

**Response:**
```json
[
  {
    "date": "2026-06-27 14:00",
    "positiveCount": 12,
    "negativeCount": 8,
    "neutralCount": 5,
    "averageScore": 0.847
  }
]
```

---

### Daily Sentiment Breakdown

```http
GET /api/v1/sentiment/daily
```

Returns sentiment counts aggregated by day.

| Parameter | Type | Location | Required | Default | Description |
|---|---|---|---|---|---|
| `keyword` | `string` | Query | No | `all` | Filter by keyword |
| `days` | `int` | Query | No | `7` | Number of days to look back (min: 1) |

**Response:**
```json
[
  {
    "date": "2026-06-27",
    "positiveCount": 45,
    "negativeCount": 32,
    "neutralCount": 23,
    "averageScore": 0.762
  }
]
```

---

### Recent Posts

```http
GET /api/v1/sentiment/recent
```

Returns the most recently analyzed posts, newest first.

| Parameter | Type | Location | Required | Default | Description |
|---|---|---|---|---|---|
| `keyword` | `string` | Query | No | `all` | Filter by keyword |
| `limit` | `int` | Query | No | `50` | Max number of posts to return (min: 1) |

**Response:**
```json
[
  {
    "id": "a1b2c3d4-...",
    "platform": "TWITTER",
    "postId": "tw_abc123def456",
    "author": "@TechGuru42",
    "content": "Just tried Tesla and I'm absolutely blown away!",
    "sentiment": "POSITIVE",
    "score": 0.997,
    "keyword": "Tesla",
    "createdAt": "2026-06-27T13:22:10",
    "analyzedAt": "2026-06-27T13:45:03"
  }
]
```
 
---

### Overall Statistics

```http
GET /api/v1/sentiment/overall
```

Returns aggregate statistics for all analyzed posts, optionally filtered by keyword.

| Parameter | Type | Location | Required | Default | Description |
|---|---|---|---|---|---|
| `keyword` | `string` | Query | No | `all` | Filter by keyword |

**Response:**
```json
{
  "keyword": "Tesla",
  "totalPosts": 250,
  "positiveCount": 95,
  "negativeCount": 88,
  "neutralCount": 67,
  "average": 0.814,
  "positivePercentage": 38.0,
  "negativePercentage": 35.2,
  "neutralPercentage": 26.8
}
```
 
---

### Trigger Manual Ingestion

```http
POST /api/v1/ingest/trigger
```

Force an immediate ingestion cycle outside the scheduled interval.

| Parameter | Type | Location | Required | Default | Description |
|---|---|---|---|---|---|
| `keyword` | `string` | Query | No | `all` | Keyword to ingest (`all` runs all configured keywords) |

**Request:**
```bash
curl -X POST "http://localhost:8080/api/v1/ingest/trigger?keyword=Tesla"
```

**Response:**
```json
{
  "keyword": "Tesla",
  "newPosts": 10,
  "message": "Complete"
}
```

---

## 🔧 Configuration
### Sentiment Pipeline

| Property | Default | Description |
|---|---|---|
| `sentiment.keywords` | `Tesla,Apple,Google` | Comma-separated list of keywords to track |
| `sentiment.fetch-interval` | `60000` | Interval between scheduled ingestion runs (milliseconds) |
| `sentiment.posts-per-fetch` | `5` | Number of posts to fetch per keyword per platform per cycle |
| `python.ml.api.url` | `http://localhost:8000/analyze` | URL of the Python ML sentiment analysis endpoint |

### Example Custom Configuration

```properties
# Track different keywords
sentiment.keywords=Bitcoin,Ethereum,Solana
 
# Fetch every 30 seconds instead of every minute
sentiment.fetch-interval=30000
 
# Fetch 10 posts per keyword per cycle
sentiment.posts-per-fetch=10
 
# Point to a remote ML service
python.ml.api.url=http://ml-service.example.com/analyze
 
# Use 'update' for production to preserve data across restarts
spring.jpa.hibernate.ddl-auto=update
```
 
---

## 📁 Project Structure

```
Sentilytics/
├── python-ml-api/                          
│   ├── app.py                              
│   └── requirements.txt                    
│
├── src/main/java/roninsora/sentilytics/
│   ├── SentilyticsApplication.java         
│   │
│   ├── config/
│   │   └── MapperConfig.java               
│   │
│   ├── controllers/
│   │   └── SentimentController.java        
│   │
│   ├── exceptions/
│   │   └── GlobalExceptionHandler.java     
│   │
│   ├── mapper/
│   │   ├── Mapper.java                     
│   │   └── impl/
│   │       ├── AnalyzePostMapperImpl.java  
│   │       └── SocialPostMapperImpl.java   
│   │
│   ├── models/
│   │   ├── Dashboard.java                  
│   │   ├── Post.java                       
│   │   ├── SentimentResult.java            
│   │   ├── Stats.java                      
│   │   ├── dtos/
│   │   │   ├── AnalyzePostDTO.java         
│   │   │   └── SocialPostDTO.java          
│   │   └── entities/
│   │       ├── AnalyzePost.java            
│   │       └── SocialPost.java             
│   │
│   ├── repos/
│   │   ├── AnalyzePostRepo.java            
│   │   └── SocialPostRepo.java             
│   │
│   ├── schedulers/
│   │   └── Scheduler.java                  
│   │
│   └── services/
│       ├── AnalyticsService.java           
│       ├── SentimentAnalyzer.java          
│       ├── SentimentPipelineService.java   
│       ├── SocialMediaClient.java          
│       └── impl/
│           ├── PythonSentimentAnalyzer.java 
│           ├── RedditSimulation.java        
│           └── TwitterSimulation.java       
│
├── src/main/resources/
│   └── application.properties              
│
├── pom.xml                                 
├── mvnw / mvnw.cmd                         
└── LICENSE                                 
```
 
---
---

## 🔮 Future Enhancements

- [ ] **Live API Integrations** — Replace simulation clients with real Reddit and Twitter/X clients.
- [ ] **Frontend Dashboard** — Build a React or Next.js dashboard to visualize sentiment trends in real-time.
- [ ] **WebSocket Streaming** — Push new analysis results to connected clients in real-time.
- [ ] **Multi-Model Support** — Allow switching between different Hugging Face models or fine-tuned models.
- [ ] **Dockerization** — Add `docker-compose.yml` for one-command startup of all services (PostgreSQL, Python ML API, Spring Boot).
- [ ] **Authentication** — Secure the API with Spring Security + JWT.
- [ ] **Rate Limiting** — Add rate limiting to the public API endpoints.
---


## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
 
---
</div>