CREATE TABLE customer_info (
	account_number VARCHAR(255) PRIMARY KEY,
	name VARCHAR(255)
);

CREATE TABLE portfolio (
  	id INT PRIMARY KEY,
    account_number VARCHAR(255),
    CONSTRAINT fk_customer_info
         		FOREIGN KEY(account_number)
         			REFERENCES customer_info(account_number)
);

CREATE TABLE security (
	id INT PRIMARY KEY,
	ticker_symbol VARCHAR(255),
	number_of_shares INTEGER,
	portFolio_id INTEGER,
	avg_buy_price DOUBLE PRECISION,
	CONSTRAINT fk_portfolio
		FOREIGN KEY(portFolio_id)
			REFERENCES portfolio(id)
	);

CREATE TABLE trade (
    id INT PRIMARY KEY,
    security_id INT,
    number_of_shares INTEGER,
    price DOUBLE PRECISION,
    portFolio_id INTEGER,
    trade_type VARCHAR(255),
	CONSTRAINT fk_security
		FOREIGN KEY(security_id)
			REFERENCES security(id)
	);

INSERT INTO customer_info(account_number, name) VALUES("account123", "Jon");
INSERT INTO portFolio(id, account_number) VALUES(123, "account123");
