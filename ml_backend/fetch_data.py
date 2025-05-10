#!/usr/bin/env python3
import sys
import pandas as pd
from binance.client import Client

def fetch_ohlcv(symbol: str, interval='1d', limit=500, api_key=None, api_secret=None):
    client = Client(api_key, api_secret)
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
        print("Usage: python fetch_data.py SYMBOL [INTERVAL] [LIMIT]")
        sys.exit(1)
    symbol   = sys.argv[1]
    interval = sys.argv[2] if len(sys.argv) >= 3 else '1d'
    limit    = int(sys.argv[3]) if len(sys.argv) >= 4 else 500

    print(f"Fetching {limit} bars for {symbol} at {interval} …")
    df = fetch_ohlcv(symbol, interval, limit)
    out = f"data_{symbol}_{interval}.csv"
    df.to_csv(out, index=False)
    print("Saved to", out)

if __name__ == "__main__":
    main()
