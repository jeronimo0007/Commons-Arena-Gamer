# ArenaGamer — Common Service

Microsserviço **common**: operações autenticadas (staff ou cliente) via JWT. Faz parte da divisão da ArenaGamer API em 4 serviços independentes que compartilham o **mesmo banco de dados**.

- **Porta padrão:** `8082`
- **Swagger:** http://localhost:8082/swagger-ui.html

## Responsabilidades (`/api/v1/common/**`)

- Times (`/teams`) e ranks de time (`/teams/ranks`)
- Torneios (`/tournaments`)
- Presets/jogos (`/presets`)
- Wallet (`/wallet`)
- Assinaturas (`/subscriptions`)
- Jogadores (`/players`)
- **Upload de imagens** (`POST /api/v1/common/uploads`)

> Autenticação e perfil do usuário ficam no **arenagamer-auth**.

## Como rodar

```bash
cp .env.example .env   # mantenha FLYWAY_ENABLED=false
./mvnw spring-boot:run
```

Ou via Docker:

```bash
docker build -t arenagamer-common .
docker run --env-file .env -p 8082:8082 arenagamer-common
```

## Armazenamento de imagens (S3-compatible)

Imagens (logos/banners de time, etc.) são gravadas em bucket compatível com S3 (DigitalOcean Spaces, AWS S3, MinIO...). Configure via variáveis `STORAGE_*`. Para migrar de provedor, troque `STORAGE_ENDPOINT`/`STORAGE_REGION`/`STORAGE_BUCKET`.
