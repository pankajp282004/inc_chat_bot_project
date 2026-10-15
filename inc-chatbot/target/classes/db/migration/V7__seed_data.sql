-- ================================================================
-- SEED DATA - V7
-- ================================================================

-- Users (passwords are BCrypt encoded)
-- admin / admin123
-- user1 / user123
-- viewer1 / viewer123
INSERT INTO users (username, password, role, email, is_active) VALUES
('admin',   '$2a$10$slYQmyNdgTY7NhN6MJF0BuKBjBo0OtNvZnhSIQMSZmraNhkOaXnwG', 'ADMIN',       'admin@paynways.com',   TRUE),
('user1',   '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1BReH.gpi',  'CONTRIBUTOR', 'user1@paynways.com',   TRUE),
('viewer1', '$2a$10$m/9TQy3szFYILOqmCo5T0.eXQ3i9JoBV.EpMrPpF5bCkBIjGMpqNO', 'VIEWER',      'viewer1@paynways.com', TRUE),
('approver1', '$2a$10$slYQmyNdgTY7NhN6MJF0BuKBjBo0OtNvZnhSIQMSZmraNhkOaXnwG', 'APPROVER',  'approver@paynways.com',TRUE)
ON DUPLICATE KEY UPDATE username = username;

-- ================================================================
-- Sample Incidents
-- ================================================================

INSERT INTO incidents (incident_no, task_no, app, description, status, last_comment, comments, rca, plan_solution, is_approved, created_by) VALUES
(
  'INC0027812747',
  'TASK00112233',
  'SPEI',
  'Payment transaction failed for payment type 16 (SPEI). Customers are unable to complete outbound SPEI transfers. Error code E_TXN_TIMEOUT returned by clearing house adapter.',
  'CLOSED',
  'Issue resolved after clearing the stuck queue entries and restarting the SPEI adapter service.',
  'Multiple users reported failed SPEI transactions from 09:00 to 11:30 AM. Error E_TXN_TIMEOUT appearing in logs. Clearing house adapter queue was stuck with 847 pending entries.',
  'Root cause: The SPEI clearing house adapter entered a deadlock state due to a database connection pool exhaustion. A long-running query from the batch reconciliation job held connections for 35+ minutes, starving the real-time transaction processor.',
  'Clear stuck queue entries from spei_queue table. Restart SPEI adapter service. Increase DB connection pool size from 10 to 25. Add timeout of 30s to reconciliation queries.',
  TRUE,
  'user1'
),
(
  'INC0027856890',
  'TASK00134567',
  'SPEI',
  'SPEI inbound payments not being credited to customer accounts. Payments are received from clearing house but not posted to core banking system.',
  'CLOSED',
  'Fixed by correcting the account mapping configuration and reprocessing failed messages from dead letter queue.',
  'About 150 inbound SPEI payments received but not credited. Core banking integration service throwing NullPointerException on account lookup. DLQ has 150 messages queued.',
  'Account mapping configuration file was corrupted during a deployment. The service was unable to map clearing house account identifiers to internal account numbers.',
  '1. Fix account_mapping.json configuration file\n2. Restart core banking integration service\n3. Reprocess 150 messages from dead letter queue using admin tool\n4. Verify each transaction is credited correctly',
  TRUE,
  'user1'
),
(
  'INC0027901234',
  'TASK00198765',
  'SPID',
  'SPID direct debit authorization failures. Type 3 direct debit mandates returning authorization error SPID-AUTH-403. Affects business customers with active standing orders.',
  'IN_PROGRESS',
  'Investigating with SPID network team. Temporary workaround in place for manual authorization.',
  'Started at 14:00. SPID authorization service returning 403 for all type 3 mandate checks. About 230 standing orders affected. Business customers calling support.',
  'Under investigation. Preliminary analysis shows the SPID network certificate expired at 13:58. Service is unable to authenticate to SPID authorization endpoint.',
  '1. Renew SPID network certificate (TLS client cert)\n2. Restart SPID authorization microservice\n3. Reprocess failed standing orders\n4. Set calendar reminder for cert renewal 30 days before expiry',
  FALSE,
  'user1'
),
(
  'INC0027945678',
  NULL,
  'SPEI',
  'Duplicate SPEI transactions being processed. Some customers see double charges. Transaction deduplication logic failing for concurrent requests.',
  'PENDING_SCRIPT',
  'DB script prepared to identify and void duplicate transactions. Awaiting approval from finance team.',
  'Approximately 45 duplicate transactions identified. Affecting customers who hit retry on slow connection. Deduplication key collision due to microsecond timestamp resolution.',
  'Deduplication logic uses timestamp + amount as unique key. Under high load, two transactions with same amount in same millisecond bypass dedup check. Race condition in distributed lock acquisition.',
  'Run duplicate_cleanup.sql script to void duplicate transactions and refund customers. Then deploy hotfix to use UUID-based idempotency keys instead of timestamp-based.',
  FALSE,
  'admin'
),
(
  'INC0028001122',
  'TASK00200001',
  'SPID',
  'SPID reports not generating for end-of-day reconciliation. Batch job fails silently. Finance team unable to complete daily reconciliation.',
  'OPEN',
  'Assigned to batch processing team for investigation.',
  'Daily reconciliation batch job scheduled at 23:00 did not produce any output files. Job shows completed status in scheduler but output directory is empty. No errors in application log.',
  NULL,
  NULL,
  FALSE,
  'user1'
)
ON DUPLICATE KEY UPDATE incident_no = incident_no;

-- ================================================================
-- Sample Solutions
-- ================================================================

INSERT INTO solutions (incident_id, solution_type, solution_text, version, created_by, is_active)
SELECT id, 'SQL',
'-- Step 1: Identify stuck queue entries
SELECT * FROM spei_queue WHERE status = ''PENDING'' AND created_at < DATE_SUB(NOW(), INTERVAL 2 HOUR);

-- Step 2: Move stuck entries to error state for reprocessing
UPDATE spei_queue
SET status = ''ERROR'', error_message = ''Manual reset - INC0027812747'', updated_at = NOW()
WHERE status = ''PENDING'' AND created_at < DATE_SUB(NOW(), INTERVAL 2 HOUR);

-- Step 3: Verify connection pool settings
SHOW VARIABLES LIKE ''max_connections'';
SHOW STATUS LIKE ''Threads_connected'';

-- Step 4: After adapter restart, reprocess
UPDATE spei_queue SET status = ''PENDING'', error_message = NULL WHERE status = ''ERROR'' AND created_at > DATE_SUB(NOW(), INTERVAL 6 HOUR);

-- Step 5: Confirm cleared
SELECT status, COUNT(*) FROM spei_queue GROUP BY status;',
1, 'admin', TRUE
FROM incidents WHERE incident_no = 'INC0027812747';

INSERT INTO solutions (incident_id, solution_type, solution_text, version, created_by, is_active)
SELECT id, 'STEPS',
'Resolution Steps for INC0027856890 - SPEI Inbound Credit Failure:

STEP 1: Verify the issue
  - SSH to core-banking-integration server
  - Check logs: tail -f /var/log/cbi/app.log | grep NullPointerException
  - Confirm DLQ count: Check RabbitMQ admin at http://rabbitmq:15672/#/queues

STEP 2: Fix configuration
  - Backup current config: cp /opt/cbi/config/account_mapping.json /opt/cbi/config/account_mapping.json.bak
  - Restore from git: git checkout HEAD -- config/account_mapping.json
  - Validate JSON: python3 -m json.tool account_mapping.json

STEP 3: Restart service
  - systemctl restart core-banking-integration
  - Wait 30 seconds
  - Check health: curl http://localhost:8080/actuator/health

STEP 4: Reprocess DLQ messages
  - Login to admin console: http://admin-panel:8090
  - Navigate to: Message Management > Dead Letter Queue
  - Filter by queue: spei.inbound.dlq
  - Select all 150 messages > Reprocess
  - Monitor: watch -n5 "rabbitmqctl list_queues name messages"

STEP 5: Verify credits
  - Run reconciliation query to confirm all 150 transactions credited
  - Notify finance team when complete',
1, 'user1', TRUE
FROM incidents WHERE incident_no = 'INC0027856890';

INSERT INTO solutions (incident_id, solution_type, solution_text, version, created_by, is_active)
SELECT id, 'SQL',
'-- DUPLICATE TRANSACTION CLEANUP SCRIPT
-- INC0027945678 - Run with DBA approval only

-- Step 1: Identify duplicates
SELECT
    customer_id,
    amount,
    transaction_date,
    COUNT(*) as duplicate_count,
    GROUP_CONCAT(id ORDER BY id) as transaction_ids
FROM spei_transactions
WHERE created_at >= ''2026-10-15 00:00:00''
  AND created_at < ''2026-10-16 00:00:00''
  AND status = ''COMPLETED''
GROUP BY customer_id, amount, DATE_FORMAT(created_at, ''%Y-%m-%d %H:%i'')
HAVING COUNT(*) > 1;

-- Step 2: Void duplicate transactions (keep first, void rest)
-- REVIEW THE ABOVE QUERY OUTPUT BEFORE RUNNING THIS
UPDATE spei_transactions t
INNER JOIN (
    SELECT MIN(id) as keep_id, customer_id, amount, DATE_FORMAT(created_at, ''%Y-%m-%d %H:%i'') as min_ts
    FROM spei_transactions
    WHERE created_at >= ''2026-10-15 00:00:00''
    GROUP BY customer_id, amount, DATE_FORMAT(created_at, ''%Y-%m-%d %H:%i'')
    HAVING COUNT(*) > 1
) d ON t.customer_id = d.customer_id AND t.amount = d.amount AND DATE_FORMAT(t.created_at, ''%Y-%m-%d %H:%i'') = d.min_ts
SET t.status = ''VOIDED'', t.void_reason = ''DUPLICATE - INC0027945678'', t.updated_at = NOW()
WHERE t.id != d.keep_id;

-- Step 3: Trigger refunds for voided transactions
-- (Handled by refund batch service automatically)',
1, 'admin', TRUE
FROM incidents WHERE incident_no = 'INC0027945678';
