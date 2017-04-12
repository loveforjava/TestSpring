    create table ChangelogHistory (
        id number(19,0) not null,
        actionType varchar2(10 char) not null,
        changeDate timestamp not null,
        entity varchar2(36 char) not null,
        entity_uuid raw(255) not null,
        user_id number(19,0) not null,
        primary key (id)
    );
    create table ChangelogHistoryDetail (
        id number(19,0) not null,
        field varchar2(36 char) not null,
        newValue varchar2(255 char),
        oldValue varchar2(255 char),
        changelogHistory_id number(19,0) not null,
        primary key (id)
    );
    alter table ChangelogHistory
        add constraint FK_asqxy8w8qqw2c3snqo3d942ns
        foreign key (user_id)
        references user_detail;
    alter table ChangelogHistoryDetail
        add constraint FK_exnx9fa963hn9sp5mv3ci7caf
        foreign key (changelogHistory_id)
        references ChangelogHistory;
