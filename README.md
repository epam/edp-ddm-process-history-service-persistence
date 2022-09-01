# process-history-service-persistence

This service interacts with the database of business process history (tasks and processes).

### Related components:
* `ddm-bpm-history-event-handler` - `bpms` plugin that pushes history events related to business processes and tasks to Kafka topic
* Kafka for receiving messages pushed by `ddm-bpm-history-event-handler`
* PostgreSQL database for data persistence

### Local development:
###### Prerequisites:
* Kafka is configured and running
* Database `process_history` is configured and running

###### Process history database setup:
1. Create database `process_history`
1. Run `initial-db-setup` script from the `citus` repository

###### Configuration:
1. Check `src/main/resources/application-local.yaml` and replace if needed:
    * data-platform.datasource... properties with actual values from local DB
    * data-platform.kafka.boostrap and audit.kafka.bootstrap with url of local Kafka

###### Steps:
1. (Optional) Package application into jar file with `mvn clean package`
1. Add `--spring.profiles.active=local` to application run arguments
1. Run application with your favourite IDE or via `java -jar ...` with jar file, created above

###### Additional information
All properties, related to other third-party services, not mentioned above (dso, keycloak, ceph) are mocked in `application-local.yaml` (like keycloak.realm=realm), to check such integrations, mock values must be replaced with real ones.

### License
process-history-service-persistence is Open Source software released under the Apache 2.0 license.
