🤖 Project Aria: The Autonomous "Rocky" AI
Project Aria is a highly advanced, autonomous backend AI agent built with Spring Boot and Spring AI. Powered by Groq's lightning-fast inference and the Llama 3.3 model, this agent features a Tri-Layer Memory architecture, dynamic tool execution, and an adaptive personality matrix inspired by JARVIS, TARS (Interstellar), and Rocky (Project Hail Mary).

✨ Key Features
🧠 Tri-Layer Memory Architecture:

Long-Term Semantic Memory: Uses PostgreSQL + pgvector to save and retrieve important facts via vector similarity search.

Short-Term History: Maintains a sliding chronological window of recent interactions.

Dynamic System Prompt: Injects real-time context, dates, and behavioral rules.

🛠️ Native ReAct Tool Calling:

Web Search: Integrates with Tavily API for real-time news and fact-checking.

Finance Engine: Scrapes live stock/index data from Yahoo Finance via custom REST clients.

Self-Correction: Dynamically updates its own personality matrix (Humor, Sarcasm, Honesty) based on user commands.

🎭 Adaptive Persona Matrix: Shifts dynamically between polite assistant (JARVIS), highly sarcastic companion (TARS), and enthusiastic researcher (Rocky) depending on the context of the conversation.

🗣️ Multilingual Foundation: Configured to detect and respond strictly in regional languages (Telugu, Tamil, English) without mixing contexts. (Fine-tuning in progress).

🛠️ Tech Stack
Java 21 & Spring Boot 3.3.x

Spring AI (1.0.0-M6): For LLM orchestration, ReAct loops, and functional tool-calling.

Database: PostgreSQL with pgvector extension.

LLM Engine: Meta Llama-3.3-70b-versatile (via Groq API for ultra-low latency).

External APIs: Tavily (Web Search), Yahoo Finance (Market Data).

🚀 Getting Started
Prerequisites
Java 21 and Maven installed.

PostgreSQL running locally or in Docker with the pgvector extension installed.

API Keys for Groq (set as OpenAI base URL) and Tavily.

Environment Setup
Create an application.properties or set the following environment variables:

Properties
# Groq API (Using Spring AI's OpenAI Client)
spring.ai.openai.api-key=YOUR_GROQ_API_KEY
spring.ai.openai.base-url=https://api.groq.com/openai
spring.ai.openai.chat.options.model=llama-3.3-70b-versatile
spring.ai.openai.chat.options.temperature=0.3

# PostgreSQL + pgvector config
spring.datasource.url=jdbc:postgresql://localhost:5432/ariadb
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# Web Search Tool
tavily.api.key=YOUR_TAVILY_API_KEY
Running the Application
Bash
./mvnw spring-boot:run
🧠 Memory & Noise Control
Aria uses a sophisticated quality-filter for memory retention.

Trivial greetings ("Hi", "Hello") are handled in short-term memory but dropped from long-term storage.

Substantial conversations (> 20 characters) are automatically vectorized and stored in PostgreSQL.

Similarity thresholds ensure the AI only retrieves past context when strictly relevant (Score > 0.75).

🗺️ Roadmap
[ ] Multilingual Fine-Tuning: Improve regional language token generation (Tamil/Telugu) to prevent model fallback to English.

[ ] Voice Interface: Integration with ElevenLabs TTS for native voice output.

[ ] Vision Capabilities: Adding multi-modal support for image analysis.

🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

“Logic circuits are fine, Boss. Ready for directives.”