#!/usr/bin/env python3
# ml_backend/train_model.py

import sys
import pandas as pd
from xgboost import XGBRegressor
from sklearn.model_selection import train_test_split
from joblib import dump

def compute_rsi(series: pd.Series, period: int = 14) -> pd.Series:
    delta = series.diff()
    gain = (delta.where(delta > 0, 0)).rolling(period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(period).mean()
    rs = gain / loss
    return 100 - (100 / (1 + rs))

def compute_bollinger(closes: pd.Series, period: int = 20, width: float = 2.0):
    sma = closes.rolling(period).mean()
    std = closes.rolling(period).std()
    upper = sma + width * std
    lower = sma - width * std
    bb_width = (upper - lower) / sma * 100
    return upper, lower, bb_width

def compute_atr(df: pd.DataFrame, period: int = 14) -> pd.Series:
    high_low        = df['high'] - df['low']
    high_prev_close = (df['high'] - df['close'].shift()).abs()
    low_prev_close  = (df['low']  - df['close'].shift()).abs()
    tr = pd.concat([high_low, high_prev_close, low_prev_close], axis=1).max(axis=1)
    return tr.rolling(period).mean()

def compute_body_ratio(df: pd.DataFrame) -> pd.Series:
    body = (df['close'] - df['open']).abs()
    rng  = df['high'] - df['low']
    return (body / rng * 100).fillna(0)

def prepare_features(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy().reset_index(drop=True)
    df['rsi']        = compute_rsi(df['close'])
    _, _, bb_w       = compute_bollinger(df['close'])
    df['bb_width']   = bb_w
    df['atr']        = compute_atr(df)
    df['body_ratio'] = compute_body_ratio(df)
    # Убираем строки с NaN (первые period и target-строки)
    return df.dropna().reset_index(drop=True)

def main(path_csv: str):
    # 1) Читаем исходный CSV с OHLCV
    df = pd.read_csv(path_csv, parse_dates=['open_time'])

    # 2) Генерируем таргеты по следующей свече:
    #    tp_percent = процент роста до максимума следующей свечи
    #    sl_percent = процент снижения до минимума следующей свечи
    df['next_high']  = df['high'].shift(-1)
    df['next_low']   = df['low'].shift(-1)
    df['tp_percent'] = (df['next_high'] / df['close'] - 1) * 100
    df['sl_percent'] = (df['next_low']  / df['close'] - 1) * 100
    # Убираем последнюю строку, где next_* = NaN
    df = df.dropna(subset=['tp_percent', 'sl_percent'])

    # 3) Строим признаки
    feats = prepare_features(df)
    X     = feats[['rsi', 'bb_width', 'atr', 'body_ratio']]
    y_tp  = feats['tp_percent']
    y_sl  = feats['sl_percent']

    # 4) Разбиваем на train/test
    X_train, X_test, y_tp_train, y_tp_test, y_sl_train, y_sl_test = train_test_split(
        X, y_tp, y_sl, test_size=0.2, random_state=42
    )

    # 5) Обучаем модели
    print("Training TP model…")
    model_tp = XGBRegressor(tree_method='hist', random_state=42)
    model_tp.fit(X_train, y_tp_train)
    print("Training SL model…")
    model_sl = XGBRegressor(tree_method='hist', random_state=42)
    model_sl.fit(X_train, y_sl_train)

    # 6) Сохраняем результаты
    dump(model_tp, 'model_tp.joblib')
    dump(model_sl, 'model_sl.joblib')
    print("Models saved as model_tp.joblib and model_sl.joblib")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python train_model.py path/to/data.csv")
        sys.exit(1)
    main(sys.argv[1])
