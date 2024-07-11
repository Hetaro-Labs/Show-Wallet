import requests

# Token IDs on CoinGecko
token_ids = [
    'solana',
    'usd-coin',
    'tether',
    'serum',
    'raydium'
]

# Convert the list of token IDs into a comma-separated string
token_ids_str = ','.join(token_ids)

# CoinGecko API endpoint
url = f"https://api.coingecko.com/api/v3/simple/price?ids={token_ids_str}&vs_currencies=usd"

# Make a request to the CoinGecko API
response = requests.get(url)

# Check if the request was successful
if response.status_code == 200:
    prices = response.json()
    print(prices)
else:
    print(f"Failed to fetch token prices: {response.status_code}")