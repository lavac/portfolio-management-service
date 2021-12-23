# API Routes documentation

## Assumptions:
* Portfolio has been generated automatically when user creates an account.
* APIs are taking portfolioId also as an input but not taking userId (just to keep it simple), but we can extend it to take userId as well if needed
* Current price of any share is considered as 100, assuming currency is rupee and not storing or displaying it anywhere explicitly.

## API Routes
* BaseUrl - https://portfolio-management-service.herokuapp.com

### add-trade
* URL - https://portfolio-management-service.herokuapp.com/portfolio/{portFolioId}/securities/{tickerSymbol}/trades
* portFolioId and tickerSymbol are mandatory path variables:
*  Request body: 

    {
    "tradeType": "String",
    "pricePerShare": Double,
    "numberOfShares": Integer
    }
  
All fields in the request body are mandatory

* Response body

    {
    "id": Integer,
    "tradeType": String,
    "price": Double,
    "numberOfShares": Integer
    }
  
#### Validation Notes:    
* tradeType should be either BUY/SELL (Case sensitive) 
* pricePerShare should not be less than 1.0, have configured 1.0 as MINIMUM_SHARE_PRICE
* numberOfShares should be greater than 0

#### Possible exception:
* When invalid(value/format) input is received
* If portFolioId is not found in the database (use 123 as an id since it is already present in Database)
* When first trade is of type SELL for a security (Cannot SELL any stock before buying it)
* When the number of shares requested to SELL is more than the existing number of shares for any security 

#### Business conditions
* When the trade request is for existing security and tradeType is BUY then update the numberOfShares and average_buy_price of that security
* When the trade request is for existing security and tradeType is SELL then update the numberOfShares of that security, average_buy_price does not change
* When the trade request is for new security and tradeType is BUY, it creates new security against the portfolio passed in the request
* When the trade request is for new security and tradeType is SELL, throws an exception since it is an invalid request
* Security will be identified using tickerSymbol and portfolioId 

### update-trade
* URL - https://portfolio-management-service.herokuapp.com/portfolio/{portFolioId}/trades/{tradeId}
* portFolioId and tradeId are mandatory path variables
*  Request body:
   {
    "tradeType": "String",
    "pricePerShare": Double,
    "numberOfShares": Integer
    }

#### Validation Notes:    
* All fields validations are same as add-trade API validation rules but fields are not mandatory in update request.
* Considered as bad request if portfolioId or tradeId is not present in the database 

#### Business conditions
##### update tradeType - 
* BUY to SELL - Reverts BUY trade and recovers pre-existing security state (wth pre-existing averagePrice and numberOfShares)
  and places SELL trade only if that security had that many number of shares before otherwise throws exception
* SELL to BUY - reverts the SELL trade and recovers pre-existing security state (pre-existing numberOfShares)
 and places BUY as new trade and updates averagePrice and numberOfShares of that security

##### update pricePerShare - 
* Updates averagePrice of the security if the tradeType is BUY

##### update numberOfShares - 
* Updates averagePrice of the security if the tradeType is BUY
* Validates if security has those many number of shares to SELL if the tradeType is SELL, otherwise throws exception

##### update multiple fields in one request
* All business validations are in place before updating the trade

### remove-trade
* URL - https://portfolio-management-service.herokuapp.com/portfolio/{portFolioId}/trades/{tradeId}

* portFolioId and tradeId are mandatory path variables

#### Validation Notes:
* Considered as bad request if portfolioId or tradeId is not present in the database 

#### Business conditions
* Trade of a security will be removed from the portfolio reverting the changes it had when it was added.
* In some cases trade cannot be reverted directly. 
  for example, trade1 is of type BUY, trade2 is of type SELL, we cannot revert trade1 directly since that is sold partially/completely as part of trade2

### fetch aggregated-securities
* URL - https://portfolio-management-service.herokuapp.com/portfolio/{portFolioId}/aggregated-securities

* portFolioId is a mandatory path variable

* This API fetches list of securities with their tickeySymbol and trades for requested portfolioId

* Example response:
    [
        {
            "tickerSymbol": "WIPRO",
            "trades": [
                {
                    "id": 258,
                    "tradeType": "BUY",
                    "price": 50.0,
                    "numberOfShares": 8
                },
                {
                    "id": 259,
                    "tradeType": "BUY",
                    "price": 50.0,
                    "numberOfShares": 8
                }
            ]
        }, 
        ....
    ]


### fetch portfolio
* URL - https://portfolio-management-service.herokuapp.com/portfolio/{portFolioId}
* portFolioId is a mandatory path variable
* This API fetches list of securities with their tickerSymbol, averageBuyPrice and totalShares

* Example response:
    [
        {
            "tickerSymbol": "WIPRO",
            "averageBuyPrice": 50.0,
            "shares": 16
        },
        ....
    ]

### fetch returns
* URL - https://portfolio-management-service.herokuapp.com/portfolio/{portfolioId}/returns
* portFolioId is a mandatory path variable
* This API gives returns(profit/loss) of the portfolio, this value can be negative in case of loss.

* Response is a Double Value

## Note
* One customer and portfolio has been created already, use 123 as portfolioId to test other APIs


  









