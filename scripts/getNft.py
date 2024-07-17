import requests
import json

# Replace <api_key> with your actual API key
url = "https://rpc.helius.xyz/?api-key=a9daec3e-c89d-41c3-a197-f7d7522fdfd7"

def get_asset():
    headers = {
        'Content-Type': 'application/json',
    }

    body = {
        "jsonrpc": "2.0",
        "id": "my-id",
        "method": "getAsset",
        "params": {
            "id": "HKDyTRikwSz5EkjXfEDw7ZPdZM4PNpM8UT2XFt9WtSq6"  # mint address of the NFT
        }
    }

    response = requests.post(url, headers=headers, data=json.dumps(body))
    response_data = response.json()
    result = response_data.get('result', None)

    print("Asset:", result)

get_asset()