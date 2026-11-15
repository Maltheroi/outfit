# Outfit

Tag billeder af dit tøj. En AI (Google Gemini) beskriver hvert stykke og sætter outfits sammen.

## Kør projektet
1. Start MySQL.
2. I IntelliJ under *Run → Edit Configurations → Environment variables*:
   ```
   GEMINI_API_KEY=din-nøgle;DB_USER=root;DB_PASSWORD=dit-password
   ```
   Gratis nøgle: aistudio.google.com → *Get API key*.
3. Kør `OutfitAiApplication` og åbn http://localhost:8080

## Endpoints
| Metode | URL | Hvad |
|---|---|---|
| GET | `/api/clothes` | Alt tøj |
| POST | `/api/clothes` | Upload billede (form-data: `image`) → AI beskriver → gemmes |
| GET | `/api/clothes/{id}/image` | Billedet |
| DELETE | `/api/clothes/{id}` | Slet |
| POST | `/api/outfit` | `{"occasion": "skole, 8 grader"}` → AI laver outfit |

Demo-billederne er fra Unsplash (gratis licens).
