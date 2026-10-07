ALTER TABLE trendgauge_db.integrations
DROP COLUMN api_key,
    ADD COLUMN client_id VARCHAR(255) NOT NULL COMMENT 'クライアントID' AFTER contract_id,
    ADD COLUMN client_secret VARCHAR(255) NOT NULL COMMENT 'クライアントシークレット' AFTER client_id;