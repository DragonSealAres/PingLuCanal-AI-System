USE pinglu_ai;

SET @requirement_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'work_order'
    AND column_name = 'requirement'
);

SET @add_requirement_column_sql = IF(
  @requirement_column_exists = 0,
  'ALTER TABLE work_order ADD COLUMN requirement VARCHAR(1000) NULL COMMENT ''Handle requirement'' AFTER handler',
  'SELECT 1'
);

PREPARE add_requirement_column_statement FROM @add_requirement_column_sql;
EXECUTE add_requirement_column_statement;
DEALLOCATE PREPARE add_requirement_column_statement;
