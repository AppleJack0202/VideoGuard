from fastapi import FastAPI

app = FastAPI(title="VideoGuard AI Service")


@app.get("/ai/health")
def health() -> dict[str, str]:
    return {"status": "ok"}

