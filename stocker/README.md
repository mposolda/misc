P1

- Check if rateOfReturn work as expected. Is it correctly computed for S&P 500? Values computed are:
-- 23.1.2024 : 10.2 % , ratio CZK USD: (not known)
-- 29.1.2024 : 10.31 %, ratio CZK USD: 22.83
-- 2.2.2024  : 10.77 %, ratio CZK USD: 23.09
-- 19.2.2024 : 11,39 % ratio CZK USD: 23.64
-- 22.2.2024 : 10,7 % ratio CZK USD: 23.29
-- 29.2.2024 : 10,77 % ratio CZK USD: 23.35
-- 1.5.2024 : 10,22 % ratio CZK USD: 23.52

NOTE: It is maybe possible to get S&P 500 from this URL:
https://www.google.com/async/finance_wholepage_price_updates?ei=goTgZfGcJ9L7i-gP8euW8Ac&opi=89978449&sca_esv=7fdb6e941e7712e0&yv=3&cs=0&async=mids:%2Fm%2F016yss|%2Fm%2F0cqyw|%2Fm%2F02853rl|%2Fm%2F04zvfw|%2Fm%2F02pjjn9,currencies:,_fmt:jspb
it returns TXT file, which contains index value (TODO: Doublecheck in the future if it is really true)

- Figure S&P 500 and implement in RateOfReturnsManager (See TODO)

- Comparison RateOfReturns for MSCI stocks and RI?

- Display "Average purchase price" in the Transactions view in the first table.

- Tax from disposals in the "companies" and "summary" view

- Update from latest dividends from Lynx (activity statement)

- Add fees for dividends, taxes and other things (See screenshots for more details and delete those screenshots)

- Implement charts (and other info) for currencies

- Implement charts (and other info) for stocks including dividend gain

- Implement charts( and other info) for history of the portfolio

P2: 

- Rename FinhubHttpClient interface to something like "RESTDataClient" . And make it more clever (especially not require finnhub and fixxer to implement functionalities, which they don't support)

- Implement CandlesHistoryManager CZK methods

- Implement charts for currencies and stocks regarding CZK

- TODOS: others
 
