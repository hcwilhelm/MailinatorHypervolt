CREATE TABLE IF NOT EXISTS mailbox_table(
    name TEXT NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_created_at on mailbox_table(created_at);

CREATE TABLE IF NOT EXISTS message_table(
    id UUID NOT NULL PRIMARY KEY,
    mailbox TEXT NOT NULL REFERENCES mailbox_table(name) ON DELETE CASCADE,
    sender TEXT NOT NULL,
    subject TEXT,
    message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_pagination on message_table(id, created_at);