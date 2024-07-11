import requests
import json

# Replace with your desired RPC endpoint
rpc_endpoint = "https://api.mainnet-beta.solana.com"

# wallet_address is the publicKey
wallet_address = "EjAX2KePXZEZEaADMVc5UT2SQDvBYfoP1Jyx7frignFX"

# Prepare the JSON-RPC request payload
payload = {
    "jsonrpc": "2.0",
    "id": 1,
    "method": "getTokenAccountsByOwner",
    "params": [
        "EjAX2KePXZEZEaADMVc5UT2SQDvBYfoP1Jyx7frignFX",
        {
            "programId": "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
        },
        {
            "encoding": "jsonParsed"
        }
    ]
}

# Make the request
response = requests.post(rpc_endpoint, headers={"Content-Type": "application/json"}, data=json.dumps(payload))

# Parse the response
if response.status_code == 200:
    result = response.json()
    print(result)
    if "result" in result:
        token_accounts = result["result"]["value"]
        for account in token_accounts:
            print(f"Token Account: {account['pubkey']}")
            print(f"Mint: {account['account']['data']['parsed']['info']['mint']}")
            print(f"Token Amount: {account['account']['data']['parsed']['info']['tokenAmount']['uiAmountString']}")
            print("------")
    else:
        print("Error: No result found in response.")
else:
    print(f"Error: {response.status_code} - {response.text}")