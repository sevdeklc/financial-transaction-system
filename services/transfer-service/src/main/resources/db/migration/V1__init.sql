CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_accounts_client_id ON accounts(client_id);

CREATE TABLE transfers (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL UNIQUE,
    from_account_id UUID NOT NULL,
    to_account_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_transfers_from_account_id ON transfers(from_account_id);

