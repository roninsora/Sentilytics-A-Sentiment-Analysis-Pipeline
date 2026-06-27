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


    result = sentiment_pipeline(request.text)[0]

    raw_label = result['label'].upper()
    
    if raw_label in ['LABEL_0', 'NEG', 'NEGATIVE']:
        label = 'NEGATIVE'
    elif raw_label in ['LABEL_1', 'POS', 'POSITIVE']:
        label = 'POSITIVE'
    else:
        label = raw_label

    score = round(result['score'], 3)


    return {
        "sentiment": label,
        "score": score
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
