-- Function to auto-create monthly partitions
CREATE OR REPLACE FUNCTION create_monthly_partition(target_date DATE)
RETURNS VOID AS $$
DECLARE
    partition_name TEXT;
    start_date DATE;
    end_date DATE;
BEGIN
    start_date := DATE_TRUNC('month', target_date);
    end_date := start_date + INTERVAL '1 month';
    partition_name := 't_collected_data_' || TO_CHAR(start_date, 'YYYY_MM');

    IF NOT EXISTS (
        SELECT 1 FROM pg_tables
        WHERE tablename = partition_name AND schemaname = 'public'
    ) THEN
        EXECUTE format(
            'CREATE TABLE %I PARTITION OF t_collected_data FOR VALUES FROM (%L) TO (%L)',
            partition_name, start_date, end_date
        );
        RAISE NOTICE 'Created partition: %', partition_name;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Create partitions for the next 6 months from 2026-07 onward
DO $$
DECLARE
    i INTEGER;
BEGIN
    FOR i IN 0..5 LOOP
        PERFORM create_monthly_partition(('2026-07-01'::DATE + (i || ' months')::INTERVAL)::DATE);
    END LOOP;
END $$;
