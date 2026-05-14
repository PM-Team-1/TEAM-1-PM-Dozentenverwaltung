## Voraussetzungen

- Docker installiert
- Mac oder Linux

## Anwendung starten

1. Maven build durchführen:

```bash
mvn clean package -DskipTests
```

2. Docker-Image bauen:

```bash
docker build -t lecturer-assignment-system .
```

3. Container starten:

```bash
docker run -p 8080:8080 lecturer-assignment-system
```

4. Anwendung im Browser öffnen:

http://localhost:8080

5. Anmelden:

Nutzername: `admin`
Passwort: `admin`