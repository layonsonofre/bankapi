DROP PROCEDURE IF EXISTS PROC_ADD_COLUMN;
DELIMITER $$
CREATE PROCEDURE PROC_ADD_COLUMN(IN tableName VARCHAR(64), IN columnName VARCHAR(64), IN extras TEXT(65535))
BEGIN
    IF NOT EXISTS(
        SELECT * FROM information_schema.columns
        WHERE
            table_schema = DATABASE()     AND
            table_name   = tableName      AND
            column_name  = columnName)
    THEN
        SET @query = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', columnName, ' ', extras, ';');
        PREPARE stmt FROM @query;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER;