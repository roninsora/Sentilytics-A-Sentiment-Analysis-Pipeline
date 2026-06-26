from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

app = FastAPI()

sentiment_pipeline = pipeline("sentiment-analysis")

class TextRequest(BaseModel):
    text: str

@app.post("/analyze")
def analyze_sentiment(request: TextRequest):
    if not request.text or not request.text.strip():
        return {"sentiment": "NEUTRAL", "score": 0.0}

    # Run sentiment analysis
    # Expected output from pipeline: [{'label': 'POSITIVE', 'score': 0.9998}]
    result = sentiment_pipeline(request.text)[0]

    label = result['label'].upper()
    score = round(result['score'], 3)

    # Transformers typically returns POSITIVE or NEGATIVE
    return {
        "sentiment": label,
        "score": score
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
