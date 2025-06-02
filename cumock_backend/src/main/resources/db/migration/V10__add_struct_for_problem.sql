ALTER TABLE Problems
ADD COLUMN input_format TEXT,
ADD COLUMN output_format TEXT,
ADD COLUMN examples JSONB;