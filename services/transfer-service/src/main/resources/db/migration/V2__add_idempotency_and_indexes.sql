ALTER TABLE transfers
    ADD COLUMN idempotency_key VARCHAR(100);

CREATE UNIQUE INDEX ux_transfers_idempotency_key
    ON transfers(idempotency_key)
    WHERE idempotency_key IS NOT NULL;

-- performans için
CREATE INDEX ix_transfers_from_account_id ON transfers(from_account_id);
CREATE INDEX ix_transfers_to_account_id ON transfers(to_account_id);
