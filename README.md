# Read Me first
1. Run script: `./startup.sh`
2. Double check with NoSQLWorkbench that `LOCK` table was created. If not, re-run.

# Scenarios to test
a. Re-election with active instance - DONE
  1. Instance-1 spins up and grabs the lock
  2. Instance-2 spins up and ignores lock because Instance-1 has it
  3. Instance-1 stops abruptly/randomly, Instance-2 grabs lock
  4. Instance-1 spins up and ignores lock because Instance-2 has it

b. Re-election on startup - DONE
  1. Instance 1 spins up and grabs lock
  2. Kill Instance 1
  3. Wait for 30 seconds
  4. Re-start Instance 1, should re-elect itself and continue executing tasks

c. Switching lock holders - IN PROGRESS
  1. Instance-1 spins up and grabs lock
  2. Instance-2 spins up and ignores lock because Instance-1 has it
  3. Send request to "leader switching" endpoint, specifying Instance-2 as new leader
  4. Instance-2 has lock, Instance-1 skips the jobs and Instance-2 does them
