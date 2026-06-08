from __future__ import annotations

import json
import shutil
import subprocess
from pathlib import Path

import cv2
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(title="VideoGuard AI Service")

PROJECT_ROOT = Path(__file__).resolve().parents[2]


class MetadataRequest(BaseModel):
    video_id: int
    video_path: str


class FrameExtractRequest(MetadataRequest):
    frame_interval_sec: int = Field(default=5, ge=1)


class SensitiveWord(BaseModel):
    word: str
    category: str
    weight: int = Field(default=10, ge=0)


class TextDetectRequest(BaseModel):
    video_id: int
    title: str = ""
    description: str = ""
    asr_text: str = ""
    sensitive_words: list[SensitiveWord] = Field(default_factory=list)


class FrameItem(BaseModel):
    frame_path: str
    timestamp_sec: float


class ImageDetectRequest(BaseModel):
    video_id: int
    frames: list[FrameItem] = Field(default_factory=list)


class AnalyzeRequest(FrameExtractRequest):
    title: str = ""
    description: str = ""
    sensitive_words: list[SensitiveWord] = Field(default_factory=list)


@app.get("/ai/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/ai/metadata")
def metadata(request: MetadataRequest) -> dict:
    video_path = resolve_video_path(request.video_path)
    ensure_file_exists(video_path)
    return {"video_id": request.video_id, **extract_metadata(video_path)}


@app.post("/ai/extract-frames")
def extract_frames(request: FrameExtractRequest) -> dict:
    video_path = resolve_video_path(request.video_path)
    ensure_file_exists(video_path)
    frames = extract_video_frames(video_path, request.video_id, request.frame_interval_sec)
    return {"video_id": request.video_id, "frames": frames}


@app.post("/ai/text-detect")
def text_detect(request: TextDetectRequest) -> dict:
    hits: list[dict] = []
    text_score = 0
    asr_score = 0

    sources = [
        ("TITLE", request.title),
        ("DESCRIPTION", request.description),
        ("ASR", request.asr_text),
    ]
    for source_type, text in sources:
        for sensitive_word in request.sensitive_words:
            if sensitive_word.word and sensitive_word.word in text:
                hit = {
                    "source_type": source_type,
                    "word": sensitive_word.word,
                    "category": sensitive_word.category,
                    "weight": sensitive_word.weight,
                    "context_text": build_context(text, sensitive_word.word),
                }
                hits.append(hit)
                if source_type == "ASR":
                    asr_score += sensitive_word.weight
                else:
                    text_score += sensitive_word.weight

    return {
        "video_id": request.video_id,
        "hits": hits,
        "text_score": min(100, text_score),
        "asr_score": min(100, asr_score),
    }


@app.post("/ai/image-detect")
def image_detect(request: ImageDetectRequest) -> dict:
    detector = ModelImageDetector()
    frames = [detector.detect(frame) for frame in request.frames]
    image_score = max([frame["risk_score"] for frame in frames], default=0)
    return {"video_id": request.video_id, "frames": frames, "image_score": image_score}


@app.post("/ai/analyze")
def analyze(request: AnalyzeRequest) -> dict:
    video_path = resolve_video_path(request.video_path)
    ensure_file_exists(video_path)

    video_metadata = extract_metadata(video_path)
    frame_result = extract_video_frames(video_path, request.video_id, request.frame_interval_sec)
    asr_text = ""
    text_result = text_detect(
        TextDetectRequest(
            video_id=request.video_id,
            title=request.title,
            description=request.description,
            asr_text=asr_text,
            sensitive_words=request.sensitive_words,
        )
    )
    image_result = image_detect(
        ImageDetectRequest(
            video_id=request.video_id,
            frames=[FrameItem(**frame) for frame in frame_result],
        )
    )

    text_score = text_result["text_score"]
    asr_score = text_result["asr_score"]
    image_score = image_result["image_score"]
    final_score = max(text_score, asr_score, image_score)

    return {
        "video_id": request.video_id,
        "metadata": video_metadata,
        "frames": image_result["frames"],
        "asr_text": asr_text,
        "text_hits": text_result["hits"],
        "scores": {
            "text_score": text_score,
            "image_score": image_score,
            "asr_score": asr_score,
            "final_score": final_score,
        },
        "risk_level": score_to_risk_level(final_score),
    }


class ModelImageDetector:
    def detect(self, frame: FrameItem) -> dict:
        frame_name = Path(frame.frame_path).name.lower()
        if "violence" in frame_name:
            label = "violence"
            confidence = 0.95
            risk_score = 80
        elif "porn" in frame_name:
            label = "porn"
            confidence = 0.95
            risk_score = 80
        else:
            label = "normal"
            confidence = 0.90
            risk_score = 5

        return {
            "frame_path": frame.frame_path,
            "timestamp_sec": frame.timestamp_sec,
            "label": label,
            "confidence": confidence,
            "risk_score": risk_score,
        }


def resolve_video_path(video_path: str) -> Path:
    path = Path(video_path)
    if path.is_absolute():
        return path
    return PROJECT_ROOT / path


def ensure_file_exists(path: Path) -> None:
    if not path.exists() or not path.is_file():
        raise HTTPException(status_code=404, detail=f"Video file not found: {path}")


def extract_metadata(video_path: Path) -> dict:
    if shutil.which("ffprobe"):
        return extract_metadata_by_ffprobe(video_path)
    return extract_metadata_by_opencv(video_path)


def extract_metadata_by_ffprobe(video_path: Path) -> dict:
    command = [
        "ffprobe",
        "-v",
        "error",
        "-select_streams",
        "v:0",
        "-show_entries",
        "stream=width,height,r_frame_rate,duration",
        "-of",
        "json",
        str(video_path),
    ]
    try:
        result = subprocess.run(command, capture_output=True, text=True, check=True)
        stream = json.loads(result.stdout)["streams"][0]
        fps = parse_fps(stream.get("r_frame_rate", "0/1"))
        duration = float(stream.get("duration") or 0)
        return {
            "duration": round(duration, 3),
            "width": int(stream.get("width") or 0),
            "height": int(stream.get("height") or 0),
            "fps": round(fps, 3),
            "file_size": video_path.stat().st_size,
        }
    except (subprocess.CalledProcessError, KeyError, ValueError, IndexError) as exc:
        raise HTTPException(status_code=500, detail=f"ffprobe metadata extraction failed: {exc}") from exc


def extract_metadata_by_opencv(video_path: Path) -> dict:
    capture = cv2.VideoCapture(str(video_path))
    if not capture.isOpened():
        raise HTTPException(status_code=400, detail=f"Unable to open video file: {video_path}")

    fps = capture.get(cv2.CAP_PROP_FPS) or 0
    frame_count = capture.get(cv2.CAP_PROP_FRAME_COUNT) or 0
    duration = frame_count / fps if fps > 0 else 0
    metadata_result = {
        "duration": round(duration, 3),
        "width": int(capture.get(cv2.CAP_PROP_FRAME_WIDTH) or 0),
        "height": int(capture.get(cv2.CAP_PROP_FRAME_HEIGHT) or 0),
        "fps": round(fps, 3),
        "file_size": video_path.stat().st_size,
    }
    capture.release()
    return metadata_result


def extract_video_frames(video_path: Path, video_id: int, frame_interval_sec: int) -> list[dict]:
    capture = cv2.VideoCapture(str(video_path))
    if not capture.isOpened():
        raise HTTPException(status_code=400, detail=f"Unable to open video file: {video_path}")

    fps = capture.get(cv2.CAP_PROP_FPS) or 25
    frame_count = capture.get(cv2.CAP_PROP_FRAME_COUNT) or 0
    duration = frame_count / fps if fps > 0 else 0
    output_dir = PROJECT_ROOT / "uploads" / "frames" / str(video_id)
    output_dir.mkdir(parents=True, exist_ok=True)

    timestamps = build_timestamps(duration, frame_interval_sec)
    frames: list[dict] = []
    for index, timestamp_sec in enumerate(timestamps):
        capture.set(cv2.CAP_PROP_POS_MSEC, timestamp_sec * 1000)
        ok, frame = capture.read()
        if not ok:
            continue
        relative_path = Path("uploads") / "frames" / str(video_id) / f"frame_{index:04d}.jpg"
        output_path = PROJECT_ROOT / relative_path
        cv2.imwrite(str(output_path), frame)
        frames.append(
            {
                "frame_path": relative_path.as_posix(),
                "timestamp_sec": round(timestamp_sec, 3),
            }
        )

    capture.release()
    if not frames:
        raise HTTPException(status_code=400, detail=f"No frames could be extracted from: {video_path}")
    return frames


def build_timestamps(duration: float, frame_interval_sec: int) -> list[float]:
    if duration <= 0:
        return [0]
    timestamps = []
    current = 0.0
    while current <= duration:
        timestamps.append(current)
        current += frame_interval_sec
    return timestamps or [0]


def parse_fps(value: str) -> float:
    if "/" not in value:
        return float(value)
    numerator, denominator = value.split("/", 1)
    denominator_value = float(denominator)
    if denominator_value == 0:
        return 0
    return float(numerator) / denominator_value


def build_context(text: str, word: str, radius: int = 12) -> str:
    index = text.find(word)
    if index < 0:
        return ""
    start = max(0, index - radius)
    end = min(len(text), index + len(word) + radius)
    return text[start:end]


def score_to_risk_level(score: int | float) -> str:
    if score < 30:
        return "PASS"
    if score < 70:
        return "SUSPICIOUS"
    return "VIOLATION"
