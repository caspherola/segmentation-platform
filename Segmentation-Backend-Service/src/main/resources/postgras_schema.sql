-- Create database schema (run this first)
CREATE DATABASE pipeline_config_db;

-- Connect to the database and run this DDL
CREATE TABLE IF NOT EXISTS pipeline_configuration (
    partition_key VARCHAR(255) PRIMARY KEY,
    config_data JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_pipeline_config_created_at ON pipeline_configuration(created_at);
CREATE INDEX IF NOT EXISTS idx_pipeline_config_updated_at ON pipeline_configuration(updated_at);

CREATE TABLE IF NOT EXISTS rule_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    input_event_type VARCHAR(255) NOT NULL,
    expression TEXT NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rule_parameters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_definition_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('number', 'string', 'boolean')),
    default_value TEXT,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rule_definition_id) REFERENCES rule_definitions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_rule_definitions_rule_id ON rule_definitions(rule_id);
CREATE INDEX IF NOT EXISTS idx_rule_definitions_enabled ON rule_definitions(enabled);
CREATE INDEX IF NOT EXISTS idx_rule_definitions_input_event_type ON rule_definitions(input_event_type);
CREATE INDEX IF NOT EXISTS idx_rule_parameters_rule_definition_id ON rule_parameters(rule_definition_id);

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    topic_name VARCHAR(200) NOT NULL,
    schema TEXT NOT NULL
);

-- Add indexes for better performance
CREATE INDEX idx_events_event_type ON events(event_type);
CREATE INDEX idx_events_topic_name ON events(topic_name);
CREATE INDEX idx_events_event_type_topic_name ON events(event_type, topic_name);



DROP table events;
-- Create events table to store event schemas
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    topic_name VARCHAR(200) NOT NULL,
    schema TEXT NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_events_event_type ON events(event_type);
CREATE INDEX idx_events_topic_name ON events(topic_name);

-- Insert event schemas in Spark SQL JSON struct format
INSERT INTO events (event_type, topic_name, schema) VALUES
('transaction', 'user_transactions','{"type":"struct","fields":[{"name":"amount","type":"double","nullable":true,"metadata":{}},{"name":"category","type":"string","nullable":true,"metadata":{}},{"name":"currency","type":"string","nullable":true,"metadata":{}},{"name":"description","type":"string","nullable":true,"metadata":{}},{"name":"location","type":"string","nullable":true,"metadata":{}},{"name":"merchant","type":"string","nullable":true,"metadata":{}},{"name":"timestamp","type":"string","nullable":true,"metadata":{}},{"name":"transactionId","type":"string","nullable":true,"metadata":{}},{"name":"transactionType","type":"string","nullable":true,"metadata":{}},{"name":"userId","type":"string","nullable":true,"metadata":{}}]}'),

('user_onboarding', 'user_onboarding_data', '{"type":"struct","fields":[{"name":"userId","type":"string","nullable":false,"metadata":{}},{"name":"step","type":"string","nullable":false,"metadata":{}},{"name":"timestamp","type":"string","nullable":false,"metadata":{}},{"name":"completionStatus","type":"string","nullable":false,"metadata":{}},{"name":"deviceInfo","type":"string","nullable":true,"metadata":{}},{"name":"ipAddress","type":"string","nullable":true,"metadata":{}},{"name":"referralSource","type":"string","nullable":true,"metadata":{}}]}'),

('affluence_score_update', 'user_affluence_score_update', '{"type":"struct","fields":[{"name":"userId","type":"string","nullable":false,"metadata":{}},{"name":"previousScore","type":"integer","nullable":true,"metadata":{}},{"name":"newScore","type":"integer","nullable":false,"metadata":{}},{"name":"scoreChange","type":"integer","nullable":false,"metadata":{}},{"name":"timestamp","type":"string","nullable":false,"metadata":{}},{"name":"factors","type":{"type":"array","elementType":"string","containsNull":true},"nullable":true,"metadata":{}},{"name":"confidenceLevel","type":"string","nullable":false,"metadata":{}}]}'),

('at_risk_user', 'user_at_risk', '{"type":"struct","fields":[{"name":"userId","type":"string","nullable":false,"metadata":{}},{"name":"riskScore","type":"integer","nullable":false,"metadata":{}},{"name":"riskLevel","type":"string","nullable":false,"metadata":{}},{"name":"reasons","type":{"type":"array","elementType":"string","containsNull":true},"nullable":false,"metadata":{}},{"name":"timestamp","type":"string","nullable":false,"metadata":{}},{"name":"recommendedActions","type":{"type":"array","elementType":"string","containsNull":true},"nullable":true,"metadata":{}},{"name":"alertId","type":"string","nullable":false,"metadata":{}}]}'),

('brand_affinity_score', 'user_brand_affinity_score', '{"type":"struct","fields":[{"name":"userId","type":"string","nullable":false,"metadata":{}},{"name":"brandName","type":"string","nullable":false,"metadata":{}},{"name":"affinityScore","type":"double","nullable":false,"metadata":{}},{"name":"previousScore","type":"double","nullable":true,"metadata":{}},{"name":"scoreChange","type":"double","nullable":false,"metadata":{}},{"name":"timestamp","type":"string","nullable":false,"metadata":{}},{"name":"interactionCount","type":"integer","nullable":false,"metadata":{}},{"name":"lastInteraction","type":"string","nullable":true,"metadata":{}},{"name":"category","type":"string","nullable":true,"metadata":{}}]}');

-- Query examples
-- Get all event schemas
SELECT event_type, topic_name FROM events ORDER BY event_type;

-- Get specific event schema
SELECT schema FROM events WHERE event_type = 'transaction';

-- Get schema by topic name
SELECT event_type, schema FROM events WHERE topic_name = 'user_transactions';

-- Verify all schemas are inserted
SELECT
    event_type,
    topic_name,
    CASE
        WHEN LENGTH(schema) > 100 THEN LEFT(schema, 100) || '...'
        ELSE schema
    END as schema_preview
FROM events
ORDER BY event_type;