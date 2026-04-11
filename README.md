# 🪨 Rocky AI — Personal AI Agent

> Inspired by Rocky from Project Hail Mary — brilliant, loyal, solves the impossible.

Rocky is a **personal AI companion** built using Java and Spring AI, designed to act as a **genius assistant, advisor, teacher, and friend**.

---

## 🚀 Features

* 💬 Conversational AI (LLM-powered via Groq)
* 🧠 Personality-driven responses (Rocky-style intelligence)
* 🔁 Context memory (session-based)
* ⚙️ REST API for interaction
* 🧩 Modular architecture (ready for tools & agents)

---

## 🧠 Personality

Rocky is a fusion of:

* **JARVIS** → Calm, witty, loyal
* **TARS** → Brutally honest, precise
* **R2-D2** → Resourceful, never gives up
* **Batman** → Strategic thinker
* **Rocky (Project Hail Mary)** → Solves impossible problems

---

## 🛠️ Tech Stack

* **Java 21**
* **Spring Boot 3.3.5**
* **Spring AI**
* **Groq API (Llama 3.3 70B)** — Free
* **REST APIs**

---

## ⚙️ Setup Instructions

### 1. Clone the repo

```bash
git clone https://github.com/Shan-1999/rocky-ai.git
cd rocky-ai
```

### 2. Add your Groq API key

Edit `application.properties`:

```properties
spring.ai.openai.api-key=YOUR_GROQ_KEY_HERE
spring.ai.openai.base-url=https://api.groq.com/openai
spring.ai.openai.chat.options.model=llama-3.3-70b-versatile
```

👉 Get free key from: https://console.groq.com

---

### 3. Run the app

```bash
.\mvnw.cmd spring-boot:run
```

---

## 📡 API Endpoints

### Chat with Rocky

```http
POST /aria/chat
```

Body:

```json
{
  "message": "Rocky, introduce yourself"
}
```

---

### Check Status

```http
GET /aria/status
```

---

### Clear Memory

```http
DELETE /aria/memory
```

---

## 🧭 Roadmap

### ✅ Phase 1 — Completed

* Core AI assistant
* Personality system
* REST API

### 🔜 Phase 2 — In Progress

* Persistent memory (PostgreSQL)

### 🔜 Phase 3 — Tools

* Web search
* Stock price checker
* Weather API

### 🔜 Phase 4 — Advanced

* Voice input/output
* WhatsApp integration
* Autonomous agent workflows

---

## 💡 Vision

Rocky is not just a chatbot.

It aims to become a **full AI companion** that can:

* Understand your life
* Help in decision-making
* Manage finances
* Act as a personal advisor

---

## 👨‍💻 Author

**Shanmuganathan**
🔗 https://github.com/Shan-1999

---

## ⭐ Support

If you like this project, give it a ⭐ on GitHub!
