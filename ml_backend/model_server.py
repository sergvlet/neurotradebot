#!/usr/bin/env python3
# ml_backend/model_server.py

from flask import Flask, request, jsonify
from joblib import load
import pandas as pd
import os

app = Flask(__name__)

# Загружаем модели один раз при старте
BASE_DIR = os.path.dirname(__file__)
model_tp = load(os.path.join(BASE_DIR, "model_tp.joblib"))
model_sl = load(os.path.join(BASE_DIR, "model_sl.joblib"))

@app.route("/predict", methods=["POST"])
def predict():
    """
    Ожидает JSON вида:
    {
      "rsi": float,
      "bb_width": float,
      "atr": float,
      "body_ratio": float
    }
    Возвращает:
    {
      "tp": <float>,
      "sl": <float>
    }
    """
    payload = request.get_json()
    # Преобразуем в DataFrame одной строки
    df = pd.DataFrame([{
        "rsi": payload.get("rsi"),
        "bb_width": payload.get("bb_width"),
        "atr": payload.get("atr"),
        "body_ratio": payload.get("body_ratio")
    }])
    # Предсказываем
    tp = float(model_tp.predict(df)[0])
    sl = float(model_sl.predict(df)[0])
    return jsonify({"tp": tp, "sl": sl})

if __name__ == "__main__":
    # Запускаем на 0.0.0.0:5000
    app.run(host="0.0.0.0", port=5000)
