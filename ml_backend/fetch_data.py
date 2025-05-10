#!/usr/bin/env python3
import os
import sys
import logging
import pandas as pd
from binance.client import Client

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def fetch_ohlcv(symbol: str,
                interval: str = '1d',
                limit: int = 500,
                api_key: str = None,
                api_secret: str = None) -> pd.DataFrame:
    # Попробуем получить ключи из параметров, иначе из ENV
    api_key = api_key or os.getenv('BINANCE_API_KEY')
    api_secret = api_secret or os.getenv('BINANCE_API_SECRET')
    if not api_key or not api_secret:
        logger.error("Не заданы BINANCE_API_KEY и BINANCE_API_SECRET")
        sys.exit(1)

    client = Client(api_key, api_secret)
    logger.info(f"Запрашиваю {limit} баров для {symbol} @ {interval}")
    klines = client.get_klines(symbol=symbol, interval=interval, limit=limit)
    df = pd.DataFrame(klines, columns=[
        'open_time','open','high','low','close','volume',
        'close_time','quote_asset_volume','num_trades',
        'taker_buy_base','taker_buy_quote','ignore'
    ])
    df = df[['open_time','open','high','low','close','volume']].astype({
        'open_time': 'int64',
        'open':      'float64',
        'high':      'float64',
        'low':       'float64',
        'close':     'float64',
        'volume':    'float64'
    })
    df['open_time'] = pd.to_datetime(df['open_time'], unit='ms')
    return df

def main():
    if len(sys.argv) < 2:
        print("Usage: python fetch_data.py SYMBOL [INTERVAL] [LIMIT] [API_KEY] [API_SECRET]")
        sys.exit(1)

    symbol   = sys.argv[1]
    interval = sys.argv[2] if len(sys.argv) >= 3 else '1d'
    try:
        limit = int(sys.argv[3]) if len(sys.argv) >= 4 else 500
    except ValueError:
        logger.error("LIMIT должен быть целым числом")
        sys.exit(1)

    api_key = sys.argv[4] if len(sys.argv) >= 5 else None
    api_sec = sys.argv[5] if len(sys.argv) >= 6 else None

    df = fetch_ohlcv(symbol, interval, limit, api_key, api_sec)
    out = f"data_{symbol}_{interval}.csv"
    df.to_csv(out, index=False)
    logger.info(f"Сохранено в {out}")

if __name__ == "__main__":
    main()
