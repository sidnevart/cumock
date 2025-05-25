DO $$ BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'problems' AND column_name = 'topic'
  ) THEN
ALTER TABLE problems ADD COLUMN topic VARCHAR(100) NOT NULL DEFAULT '';
END IF;
END $$;
