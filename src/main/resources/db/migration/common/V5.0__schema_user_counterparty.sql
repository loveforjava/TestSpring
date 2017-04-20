    alter table user_detail add counterparty_uuid raw(255) null;

    update user_detail
    set counterparty_uuid = (select uuid from counterparty where user_id = user_detail.id);

    alter table counterparty drop column user_id;

    alter table user_detail
        add constraint FK_82nek8t2c2t4ikuwlx5ee6gga
        foreign key (counterparty_uuid)
        references Counterparty;
