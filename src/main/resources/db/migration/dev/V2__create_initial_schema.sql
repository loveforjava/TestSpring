-- 2017-04-08 13:29:38 INFO  SchemaExport:343 - HHH000227: Running hbm2ddl schema export
      create table Address (
        id number(19,0) not null,
        apartmentNumber varchar2(255 char),
        city varchar2(255 char),
        countryside number(1,0) not null,
        description varchar2(255 char),
        district varchar2(255 char),
        houseNumber varchar2(255 char),
        latitude number(19,2),
        longitude number(19,2),
        postcode varchar2(255 char),
        region varchar2(255 char),
        street varchar2(255 char),
        primary key (id)
    );
    create table BarcodeInnerNumber (
        id number(19,0) not null,
        innerNumber varchar2(8 char),
        status varchar2(255 char),
        postcode_pool_uuid raw(255),
        primary key (id)
    );
    create table City (
        id number(19,0) not null,
        name varchar2(255 char),
        country_id varchar(2),
        district_id number(19,0),
        region_id number(19,0),
        primary key (id)
    );
    create table Client (
        uuid raw(255) not null,
        bankAccount varchar2(255 char),
        bankCode varchar2(6 char),
        discount float not null,
        firstName varchar2(255 char),
        individual number(1,0) not null,
        lastName varchar2(255 char),
        middleName varchar2(255 char),
        name varchar2(255 char),
        sender number(1,0) not null,
        uniqueRegistrationNumber varchar2(255 char),
        address_id number(19,0),
        counterparty_uuid raw(255),
        phone_id number(19,0),
        primary key (uuid)
    );
    create table Counterparty (
        uuid raw(255) not null,
        description varchar2(255 char),
        discount float not null,
        name varchar2(255 char),
        postcodePool_uuid raw(255) not null,
        user_id number(19,0),
        primary key (uuid)
    );
    create table Country (
        ISO3166 varchar(2) not null,
        NAME varchar2(255 char),
        primary key (ISO3166)
    );
    create table CountrysidePostcode (
        id number(19,0) not null,
        postcode varchar2(255 char),
        primary key (id)
    );
    create table District (
        id number(19,0) not null,
        name varchar2(255 char),
        country_id varchar(2),
        region_id number(19,0),
        primary key (id)
    );
    create table Phone (
        id number(19,0) not null,
        phoneNumber varchar2(255 char),
        primary key (id)
    );
    create table PostOffice (
        id number(19,0) not null,
        name varchar2(255 char),
        address_id number(19,0),
        postcodePool_uuid raw(255) not null,
        primary key (id)
    );
    create table PostcodePool (
        uuid raw(255) not null,
        closed number(1,0) not null,
        postcode varchar2(5 char) not null,
        primary key (uuid)
    );
    create table Region (
        id number(19,0) not null,
        name varchar2(255 char),
        country_id varchar(2),
        primary key (id)
    );
    create table Shipment (
        uuid raw(255) not null,
        declaredPrice number(19,2),
        deliveryType varchar2(255 char),
        description varchar2(255 char),
        height float not null,
        length float not null,
        postPay number(19,2),
        price number(19,2),
        weight float not null,
        width float not null,
        barcodeInnerNumber_id number(19,0),
        recipient_uuid raw(255),
        sender_uuid raw(255),
        shipment_group_uuid raw(255),
        primary key (uuid)
    );
    create table ShipmentGroup (
        uuid raw(255) not null,
        name varchar2(255 char),
        counterparty_id raw(255),
        primary key (uuid)
    );
    create table ShipmentTrackingDetail (
        id number(19,0) not null,
        shipmentStatus varchar2(255 char),
        statusDate timestamp,
        post_office_id number(19,0),
        shipment_uuid raw(255),
        primary key (id)
    );
    create table TariffGrid (
        id number(19,0) not null,
        length float not null,
        price float not null,
        w2wVariation varchar2(255 char),
        weight float not null,
        primary key (id)
    );
    create table user_detail (
        id number(19,0) not null,
        password varchar2(255 char),
        token raw(255),
        username varchar2(255 char),
        primary key (id)
    );
    alter table BarcodeInnerNumber
        add constraint FK_fmkggernnn4t49jhcu4val0u9
        foreign key (postcode_pool_uuid)
        references PostcodePool;
    alter table City
        add constraint FK_m503bcpirmab9y40lg2ia9d54
        foreign key (country_id)
        references Country;
    alter table City
        add constraint FK_ckxqfvx9gq1vwpe58sr9gh8o4
        foreign key (district_id)
        references District;
    alter table City
        add constraint FK_i1tqr2ahrk7pblj3vb732a201
        foreign key (region_id)
        references Region;
    alter table Client
        add constraint FK_6nxjf59jdjxiysy7qke8l36j8
        foreign key (address_id)
        references Address;
    alter table Client
        add constraint FK_mxgmi8y810rgwttmkdh7u2xue
        foreign key (counterparty_uuid)
        references Counterparty;
    alter table Client
        add constraint FK_fym9dfxv7wpn9g673lnywxaay
        foreign key (phone_id)
        references Phone;
    alter table Counterparty
        add constraint FK_ktyso8q5giqb83yu7h4yi0x0l
        foreign key (postcodePool_uuid)
        references PostcodePool;
    alter table Counterparty
        add constraint FK_fosiykm4nxeqw0pohbd07swu
        foreign key (user_id)
        references user_detail;
    alter table District
        add constraint FK_t7gtl1v08iscivtslbrcjblrp
        foreign key (country_id)
        references Country;
    alter table District
        add constraint FK_r1dfpgvvaml104wrov0c7d9ug
        foreign key (region_id)
        references Region;
    alter table PostOffice
        add constraint FK_evkeifecdp6peclrwfbj3v903
        foreign key (address_id)
        references Address;
    alter table PostOffice
        add constraint FK_4xywkh70byte0vyr4b4i02cgb
        foreign key (postcodePool_uuid)
        references PostcodePool;
    alter table Region
        add constraint FK_pjpy658xasq2hkvb3dtldl1f7
        foreign key (country_id)
        references Country;
    alter table Shipment
        add constraint FK_38cgd3r97kyj2yhglypt6er3r
        foreign key (barcodeInnerNumber_id)
        references BarcodeInnerNumber;
    alter table Shipment
        add constraint FK_qjct0fecm4arxa9lg9dk9xy7r
        foreign key (recipient_uuid)
        references Client;
    alter table Shipment
        add constraint FK_ljwh5f48chw6h7l7xtu325vkx
        foreign key (sender_uuid)
        references Client;
    alter table Shipment
        add constraint FK_r4r03l1od9ugeo3u5av425nys
        foreign key (shipment_group_uuid)
        references ShipmentGroup;
    alter table ShipmentGroup
        add constraint FK_slw6n5nogv2y2pvi7xlgtigsf
        foreign key (counterparty_id)
        references Counterparty;
    alter table ShipmentTrackingDetail
        add constraint FK_78snxjcbn3k3uj156l6opxyp8
        foreign key (post_office_id)
        references PostOffice;
    alter table ShipmentTrackingDetail
        add constraint FK_cvx8r66ygh66c3yy27tk9q96g
        foreign key (shipment_uuid)
        references Shipment;
    create sequence hibernate_sequence;
-- 2017-04-08 13:29:41 INFO  SchemaExport:405 - HHH000230: Schema export complete
