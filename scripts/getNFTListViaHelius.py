import requests
import json

# Replace <api_key> with your actual API key
url = "https://mainnet.helius-rpc.com/?api-key=a9daec3e-c89d-41c3-a197-f7d7522fdfd7"

def get_assets_by_owner():
    headers = {
        'Content-Type': 'application/json',
    }

    body = {
        "jsonrpc": "2.0",
        "id": "my-id",
        "method": "getAssetsByOwner",
        "params": {
            "ownerAddress": "3W1bVgaXPnhbbU8pWuSjBt7EZCcZ49XY9rzERt8U4piH",
            "page": 1,  # Starts at 1
            "limit": 1000,
        }
    }

    response = requests.post(url, headers=headers, data=json.dumps(body))
    response_data = response.json()
    result = response_data.get('result', None)

    if result:
        items = result.get('items', [])
        for item in items:
            content = item.get('content', {})
            files = content.get('files', [])
            if files:
                cdn_uri = files[0].get('cdn_uri', 'No CDN URI found')
                print("CDN URI:", cdn_uri)
            else:
                print("No files found for item")
    else:
        print("No result found")


get_assets_by_owner()


# json data
# {
#       "interface":"V1_NFT",
#       "id":"Hwu3bZmFdkDsBWx5EtNHLc1JmK3YGrXYUcREdMGzCdFY",
#       "content":{
#          "$schema":"https://schema.metaplex.com/nft1.0.json",
#          "json_uri":"https://www.hi-hi.vip/json/4000w0326.json",
#          "files":[
#             {
#                "uri":"https://img.hi-hi.vip/json/img/4000w1.gif",
#                "cdn_uri":"https://cdn.helius-rpc.com/cdn-cgi/image//https://img.hi-hi.vip/json/img/4000w1.gif",
#                "mime":"image/gif"
#             }
#          ],
#          "metadata":{
#             "attributes":[
#                {
#                   "value":"https://4000W.io",
#                   "trait_type":"Website"
#                },
#                {
#                   "value":"True",
#                   "trait_type":"Verified"
#                },
#                {
#                   "value":"4,000+ W",
#                   "trait_type":"Amount"
#                },
#                {
#                   "value":"35 minutes!",
#                   "trait_type":"Time Left"
#                }
#             ],
#             "description":"Random Drop event! https://4000W.io",
#             "name":"4000 W Drop 4000W.io",
#             "symbol":"wwW",
#             "token_standard":"NonFungible"
#          },
#          "links":{
#             "external_url":"https://4000W.io",
#             "image":"https://img.hi-hi.vip/json/img/4000w1.gif"
#          }
#       },
#       "authorities":[
#                {
#                   "address":"6ncxsJk6L7TzWu4XiQDB72ut8qGQ6JUr8hPEniujNNeF",
#                   "scopes":[
#                      "full"
#                   ]
#                }
#             ],
#             "compression":{
#                "eligible":false,
#                "compressed":true,
#                "data_hash":"DMAkYte5csSRmbsytTG7AZRXEY4rQUC3WtQwcnFqc76B",
#                "creator_hash":"8zFw7HxBPaE8WM6KwDXpnATFscuNMCV9LP4bvBBnXqmc",
#                "asset_hash":"CunAZQZGoHHVY8EaupYkBRhEKtrbxWVpdNpFYRcwpJ4E",
#                "tree":"AxM84SgtLjS51ffA9DucZpGZc3xKDF7H4zU7U6hJQYbR",
#                "seq":219970,
#                "leaf_id":219837
#             },
#             "grouping":[
#                {
#                   "group_key":"collection",
#                   "group_value":"HKDyTRikwSz5EkjXfEDw7ZPdZM4PNpM8UT2XFt9WtSq6"
#                }
#             ],
#             "royalty":{
#                "royalty_model":"creators",
#                "target":"None",
#                "percent":0.0,
#                "basis_points":0,
#                "primary_sale_happened":false,
#                "locked":false
#             },
#             "creators":[
#                {
#                   "address":"G4phxh4Q81HY7kbHs4RMCofVAWLLHjy58yBvUrhEb4yv",
#                   "share":100,
#                   "verified":true
#                }
#             ],
#             "ownership":{
#                "frozen":false,
#                "delegated":true,
#                "delegate":"G4phxh4Q81HY7kbHs4RMCofVAWLLHjy58yBvUrhEb4yv",
#                "ownership_model":"single",
#                "owner":"3W1bVgaXPnhbbU8pWuSjBt7EZCcZ49XY9rzERt8U4piH"
#             },
#             "supply":{
#                "print_max_supply":0,
#                "print_current_supply":0,
#                "edition_nonce":0
#             },
#             "mutable":true,
#             "burnt":false
#          },