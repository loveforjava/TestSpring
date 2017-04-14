    create table Discount (
        uuid raw(255) not null,
        name varchar2(255 char),
        fromDate date,
        toDate date,
        value float not null,
        primary key (uuid)
    );
    create table DiscountPerCounterparty (
        uuid raw(255) not null,
        fromDate date,
        toDate date,
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

    alter table Shipment add discount_per_counterparty_uuid RAW(255) null;
    alter table Shipment add lastModified timestamp not null;
    alter table Shipment
        add constraint FK_j48cpin264o2wfjiqspx7cj6m
        foreign key (discount_per_counterparty_uuid)
        references DiscountPerCounterparty;
    alter table Counterparty drop column discount;
    alter table Client drop column discount;
