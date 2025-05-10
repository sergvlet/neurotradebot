#!/usr/bin/env python3
import sys
import logging
import pandas as pd
from xgboost import XGBRegressor
from sklearn.model_selection import train_test_split
from joblib import dump

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# --- индикаторы (ваш код) ---
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
    df['rsi']         = compute_rsi(df['close'])
    _, _, bb_w        = compute_bollinger(df['close'])
    df['bb_width']    = bb_w
    df['atr']         = compute_atr(df)
    df['body_ratio']  = compute_body_ratio(df)
    return df.dropna().reset_index(drop=True)
# --- /индикаторы ---

def main(path_csv: str):
    logger.info(f"Читаю CSV {path_csv}")
    df = pd.read_csv(path_csv, parse_dates=['open_time'])

    # Если целевых нет — создаём как % изменения close→next_close
    if 'tp_percent' not in df.columns or 'sl_percent' not in df.columns:
        logger.info("Добавляю tp_percent/sl_percent автоматически по next-bar return")
        df['next_close']   = df['close'].shift(-1)
        df['pct_change']   = (df['next_close'] / df['close'] - 1) * 100
        # TP = положительный ход, SL = отрицательный (отрицательное значение)
        df['tp_percent']   = df['pct_change'].clip(lower=0)
        df['sl_percent']   = df['pct_change'].clip(upper=0).abs()
        df = df.dropna(subset=['tp_percent','sl_percent']).reset_index(drop=True)

    feats = prepare_features(df)
    X     = feats[['rsi', 'bb_width', 'atr', 'body_ratio']]
    y_tp  = feats['tp_percent']
    y_sl  = feats['sl_percent']

    # train/test split
    X_train, X_test, y_tp_train, y_tp_test, y_sl_train, y_sl_test = train_test_split(
        X, y_tp, y_sl, test_size=0.2, random_state=42
    )

    # обучаем
    logger.info("Training TP model…")
    model_tp = XGBRegressor(tree_method='hist', random_state=42)
    model_tp.fit(X_train, y_tp_train)

    logger.info("Training SL model…")
    model_sl = XGBRegressor(tree_method='hist', random_state=42)
    model_sl.fit(X_train, y_sl_train)

    # сохраняем
    dump(model_tp, 'model_tp.joblib')
    dump(model_sl, 'model_sl.joblib')
    logger.info("Models saved as model_tp.joblib and model_sl.joblib")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python train_model.py path/to/data.csv")
        sys.exit(1)
    main(sys.argv[1])
