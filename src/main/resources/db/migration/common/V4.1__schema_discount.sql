    create table Discount (
        uuid raw(255) not null,
        fromDate timestamp,
        name varchar2(255 char),
        toDate timestamp,
        value float not null,
        primary key (uuid)
    );
    create table DiscountPerCounterparty (
        uuid raw(255) not null,
        fromDate timestamp,
        toDate timestamp,
        counterparty_uuid raw(255),
        discount_uuid raw(255),
        primary key (uuid)
    );
    alter table DiscountPerCounterparty
        add constraint FK_oyf86iorox7a4prp1a83wnk67
        foreign key (counterparty_uuid)
        references Counterparty;
    alter table DiscountPerCounterparty
        add constraint FK_ir6wa9n1clkhlqf339g15as90
        foreign key (discount_uuid)
        references Discount;
