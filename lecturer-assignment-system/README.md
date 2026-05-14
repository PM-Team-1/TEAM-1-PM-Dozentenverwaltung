## Voraussetzungen

- Docker installiert
- Mac oder Linux

## Anwendung starten

1. Docker-Image laden:

```bash
docker load -i lecturer-assignment-system.tar
```

2. Container starten:

```bash
docker run -p 8080:8080 lecturer-assignment-system
```

3. Anwendung im Browser öffnen:

http://localhost:8080