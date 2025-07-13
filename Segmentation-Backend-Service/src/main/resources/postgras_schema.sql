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