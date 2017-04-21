    alter table address add created timestamp null;
    alter table address add lastmodified timestamp null;

    alter table shipment add created timestamp null;
    alter table shipment add creator_id number(19,0) null;
    alter table shipment add lastmodifier_id number(19,0) null;

    alter table barcodeinnernumber add created timestamp null;

    alter table client add created timestamp null;
    alter table client add lastmodified timestamp null;
    alter table client add creator_id number(19,0) null;
    alter table client add lastmodifier_id number(19,0) null;

    alter table counterparty add created timestamp null;
    alter table counterparty add lastmodified timestamp null;
    alter table counterparty add creator_id number(19,0) null;
    alter table counterparty add lastmodifier_id number(19,0) null;

    alter table discount add created timestamp null;
    alter table discount add lastmodified timestamp null;

    alter table discountpercounterparty add created timestamp null;
    alter table discountpercounterparty add lastmodified timestamp null;
    alter table discountpercounterparty add creator_id number(19,0) null;
    alter table discountpercounterparty add lastmodifier_id number(19,0) null;

    alter table postcodepool add created timestamp null;
    alter table postcodepool add lastmodified timestamp null;

    alter table shipmentgroup add created timestamp null;
    alter table shipmentgroup add lastmodified timestamp null;
    alter table shipmentgroup add creator_id number(19,0) null;
    alter table shipmentgroup add lastmodifier_id number(19,0) null;

    alter table user_detail add created timestamp null;
    alter table user_detail add lastmodified timestamp null;

    alter table Shipment
        add constraint FK_rc3ufwtwwpk7ajk6bb8y9rnb0
        foreign key (creator_id)
        references user_detail;

    alter table Shipment
        add constraint FK_4uhelijx71p71qinadao93d1x
        foreign key (lastmodifier_id)
        references user_detail;

    alter table Client
        add constraint FK_l00vf0emaekmkr9a5xujqkgbn
        foreign key (creator_id)
        references user_detail;

    alter table Client
        add constraint FK_q8dpcctcbcpr05vx490qxn2i4
        foreign key (lastmodifier_id)
        references user_detail;

    alter table Counterparty
        add constraint FK_mxurfsj4qfdpapwks0qkas8iw
        foreign key (creator_id)
        references user_detail;

    alter table Counterparty
        add constraint FK_4kgc8c81hvk669qkj3qirc8ps
        foreign key (lastmodifier_id)
        references user_detail;

    alter table DiscountPerCounterparty
        add constraint FK_rmhoym5wir2w69sa1sshcm6ec
        foreign key (creator_id)
        references user_detail;

    alter table DiscountPerCounterparty
        add constraint FK_bsceyca4bf1gpbi7wq81hk9kv
        foreign key (lastmodifier_id)
        references user_detail;

    alter table ShipmentGroup
        add constraint FK_dvsikptg3wowwa2glp8fa5p96
        foreign key (creator_id)
        references user_detail;

    alter table ShipmentGroup
        add constraint FK_l7crgmk04rnck9ge69b1wk169
        foreign key (lastmodifier_id)
        references user_detail;

