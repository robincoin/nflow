alter table nflow_archive_workflow drop constraint nflow_archive_workflow_uniq;

create index idx_workflow_archive_type on nflow_archive_workflow(type);
