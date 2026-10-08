ALTER TABLE trendgauge_db.integrations
    MODIFY COLUMN client_secret TEXT NOT NULL COMMENT '暗号化済みクライアントシークレット';